package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.codehaus.jackson.JsonGenerator;
import org.restlet.Application;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.ext.xml.SaxRepresentation;
import org.restlet.ext.xml.XmlWriter;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.representation.WriterRepresentation;
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
		
		final HashMap<String, Object> result = new HashMap<String, Object>();
		final HashMap<String, Object> errors = new HashMap<String, Object>();
		result.put("success", true);
		try {
			if ("login".equals(action)) {
				final Account account = (Account) getClientInfo().getUser();
				if (account == null) {
					result.put("success", false);
					result.put("msg", "Invalid Credentials");
				}
				
				// TODO forced password change would set an activation key onto the account and return that in the response
			} else if ("impersonate".equals(action)) {
				if (cr.getParameters().getFirstValue("authenticator") == null) {
					result.put("success", false);
					result.put("msg", "Not Permitted");
				}
			} else if ("enrole".equals(action)) {
				final Account account = new Account();
				account.setIdentifier(cr.getIdentifier());
				account.setEmail(cr.getParameters().getFirstValue("email"));
				account.setFirstName(cr.getParameters().getFirstValue("firstName"));
				account.setLastName(cr.getParameters().getFirstValue("lastName"));
				account.setActivationKey(UUID.randomUUID().toString());
				
				try {
					sql.getMapper(AccountMapper.class).insert(account);
				} catch (PersistenceException e) {
					result.put("success", false);
					result.put("msg", "Unable to create account");
				}
				sendEmail(account.getEmail(), account.getActivationKey());
			} else if ("reset".equals(action)) {
				final String activationKey = UUID.randomUUID().toString();
				sql.getMapper(AccountMapper.class).updateActivationKey(cr.getIdentifier(), activationKey);
				sendEmail(cr.getParameters().getFirstValue("email"), activationKey);
			} else if ("activate".equals(action)) {
				final String password = new String(cr.getSecret());
				if (PasswordUtil.strength(password) < 30) {
					result.put("success", false);
					errors.put("secret", "Not strong enough");
					result.put("msg", "Unable to activate account");
				}
				// TODO additional policies could be enforced here such as dictionary words or password history
				final String hash = PasswordUtil.sha1(1, PasswordUtil.randomSalt(8), password);
				sql.getMapper(AccountMapper.class).updateSecret(cr.getIdentifier(), hash);
			}
			sql.commit();
		} finally {
			sql.close();
		}
		return new WriterRepresentation(MediaType.APPLICATION_JSON) {
			@Override
			public void write(Writer w) throws IOException {
				final JsonGenerator g = application.getJsonFactory().createJsonGenerator(w);
				g.writeStartObject();
				g.writeBooleanField("success", (Boolean) result.get("success"));
				if (result.containsKey("msg")) g.writeStringField("msg", (String) result.get("msg"));
				if (result.containsKey("key")) g.writeStringField("key", (String) result.get("key"));
				g.writeObjectFieldStart("errors");
				if (result.containsKey("secret")) g.writeStringField("secret", (String) result.get("secret"));
				g.writeEndObject();
				g.writeEndObject();
				g.flush();
			}
		};
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		return new StringRepresentation("{success:true}");
	}
	
	private void sendEmail(final String email, final String activationKey) {
		System.out.println(activationKey);
		if (true) return;
		
		final PartsApplication application = (PartsApplication) Application.getCurrent();
		
		final Properties config = application.getConfig();
		final SaxRepresentation entity = new SaxRepresentation() {
			@Override
			public void write(XmlWriter w) throws IOException {
				try {
					w.startDocument();
					w.startElement("email");
					w.startElement("head");
					w.dataElement("subject", "Account activation");
					w.dataElement("from", config.getProperty("mail.smtp.from"));
					w.dataElement("to", email);
					w.endElement("head");
					w.startElement("body");
					w.characters("Here is the activation key you requested: ");
					w.characters(activationKey);
					w.characters("\nIf you did not request this activation key please ignore this email.\n");
					w.endElement("body");
					w.endElement("email");
					w.endDocument();
				} catch (SAXException e) {
					throw new IOException(e);
				}
			}
		};
		entity.setCharacterSet(CharacterSet.ISO_8859_1);
		final String url = "smtp://" + config.getProperty("mail.smtp.host") + ":" + config.getProperty("mail.smtp.port");
		final Request request = new Request(Method.POST, url);
		request.setEntity(entity);
		if ("true".equals(config.getProperty("mail.smtp.auth"))) {
			final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.SMTP_PLAIN, config.getProperty("mail.smtp.username"), config.getProperty("mail.smtp.password"));
			request.setChallengeResponse(cr);
		}
		
		final Client client = new Client(getContext().createChildContext(), Protocol.SMTP);
		client.getContext().getParameters().set("startTls", application.getConfig().getProperty("mail.smtp.starttls.enable", "false"));
		client.handle(request);
	}
}
