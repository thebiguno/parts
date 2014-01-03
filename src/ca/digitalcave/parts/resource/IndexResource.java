package ca.digitalcave.parts.resource;

import org.apache.ibatis.session.SqlSession;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import ca.digitalcave.moss.restlet.CookieAuthInterceptResource;
import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.AccountMapper;
import ca.digitalcave.parts.model.Account;


public class IndexResource extends CookieAuthInterceptResource {

	final String mobile = ".*android.*|.*blackberry.*|.*iphone.*|.*ipod.*|.*iemobile.*|.*opera mobile.*|.*palmos.*|.*webos.*|.*googlebot-mobile.*";

	@Override
	protected void doInit() throws ResourceException {
		final Variant variant = new Variant(MediaType.TEXT_HTML);
		getVariants().add(variant);
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
		if (getClientInfo().getAgent().toLowerCase().matches(mobile)) {
			getResponse().setLocationRef("m.html");
		} else {
			getResponse().setLocationRef("d.html");
		}
		return new EmptyRepresentation();
	}
	
	@Override
	protected String insertUser(User user, String activationKey) {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sql = application.getSqlFactory().openSession(true);
		final Account account = new Account();
		try {
			account.setIdentifier(user.getIdentifier());
			account.setEmail(user.getIdentifier());
			account.setFirstName(user.getFirstName());
			account.setLastName(user.getLastName());
			account.setActivationKey(activationKey);
			
			sql.getMapper(AccountMapper.class).insert(account);
			return user.getEmail();
		} catch (Throwable e) {
			return null;
		} finally {
			sql.close();
		}
	}

	@Override
	protected String updateActivationKey(String identifier, String activationKey) {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			sql.getMapper(AccountMapper.class).updateActivationKey(identifier, activationKey);
			return identifier;
		}
		catch (Throwable e){
			return null;
		}
		finally {
			sql.close();
		}
	}

	@Override
	protected void updateSecret(String activationKey, String hash) {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			sql.getMapper(AccountMapper.class).updateSecret(activationKey, hash);
		} finally {
			sql.close();
		}
	}

	@Override
	protected boolean isAllowRegister() {
		return true;
	}

	@Override
	protected boolean isAllowReset() {
		return true;
	}
}
