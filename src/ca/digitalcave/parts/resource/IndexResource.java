package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.util.UUID;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.restlet.Request;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.xml.SaxRepresentation;
import org.restlet.ext.xml.XmlWriter;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.xml.sax.SAXException;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.AccountMapper;
import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.security.PasswordUtil;


public class IndexResource extends ServerResource {

	final String mobile = "android|blackberry|iphone|ipod|iemobile|opera mobile|palmos|webos|googlebot-mobile";

	@Override
	protected void doInit() throws ResourceException {
		final Variant variant = new Variant(MediaType.TEXT_HTML);
		getVariants().add(variant);
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
		if (getClientInfo().getAgent().matches(mobile)) {
			getResponse().setLocationRef("m.html");
		} else {
			getResponse().setLocationRef("d.html");
		}
		return new EmptyRepresentation();
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final ChallengeResponse cr = getRequest().getChallengeResponse();
		final String action = cr.getParameters().getFirstValue("action");
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final StringRepresentation failure = new StringRepresentation("{success:false}");
			if ("login".equals(action)) {
				final Account account = (Account) getClientInfo().getUser();
				if (account == null) return failure;
				
				// TODO forced password change would set an activation key onto the account and return that in the response
			} else if ("impersonate".equals(action)) {
				if (cr.getParameters().getFirstValue("authenticator") == null) return failure; 
			} else if ("enrole".equals(action)) {
				final Account account = new Account();
				account.setIdentifier(cr.getIdentifier());
				account.setEmail(cr.getParameters().getFirstValue("email"));
				account.setFirstName(cr.getParameters().getFirstValue("firstName"));
				account.setLastName(cr.getParameters().getFirstValue("lastName"));
				account.setActivationKey(UUID.randomUUID().toString());
				
				try {
					sql.getMapper(AccountMapper.class).insert(account);
					sendEmail(account);
				} catch (PersistenceException e) {
					return failure;
				}
			} else if ("reset".equals(action)) {
				final Account account = sql.getMapper(AccountMapper.class).select(cr.getIdentifier());
				account.setActivationKey(UUID.randomUUID().toString());
				sql.getMapper(AccountMapper.class).updateActivationKey(account);
			} else if ("activate".equals(action)) {
				final Account account = sql.getMapper(AccountMapper.class).select(cr.getIdentifier());
				final String password = new String(cr.getSecret());
				if (PasswordUtil.strength(password) < 30) {
					return failure;
				}
				account.setSecretString(PasswordUtil.sha1(1, PasswordUtil.randomSalt(8), password));
				sql.getMapper(AccountMapper.class).updateSecret(account);
			}
		} finally {
			sql.close();
		}
		return new StringRepresentation("{success:true}");
	}
	
	private void sendEmail(final Account account) {
		final Request request = new Request(Method.GET, "smtp://localhost"); // TODO
		SaxRepresentation entity = new SaxRepresentation() {
			@Override
			public void write(XmlWriter w) throws IOException {
				try {
				w.startDocument();
					w.startElement("email");
					w.startElement("head");
					w.dataElement("subject", "Account activation");
					w.dataElement("from", "donotreply@example.com");
					w.dataElement("to", account.getEmail());
					w.endElement("head");
					w.startElement("body");
					w.characters("Here is the activation key you requested: ");
					w.characters(account.getActivationKey());
					w.characters("\n");
					w.characters("If you did not request this activation key please ignore this email.\n");
					w.endElement("body");
					w.endElement("email");
					w.endDocument();
				} catch (SAXException e) {
					throw new IOException(e);
				}
			}
		};
		entity.setCharacterSet(CharacterSet.ISO_8859_1);
		request.setEntity(entity);
		new ClientResource(request).handle();
	}
}
