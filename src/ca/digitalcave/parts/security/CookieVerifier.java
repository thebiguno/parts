package ca.digitalcave.parts.security;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.apache.ibatis.session.SqlSession;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.CookieSetting;
import org.restlet.security.Verifier;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Account;

public class CookieVerifier implements Verifier {

	private final PartsApplication application;
	private static final String COOOKIE_NAME = "_auth";
	private final static Map<String, Semaphore> loginAttempts = Collections.synchronizedMap(new WeakHashMap<String, Semaphore>());
	
	public CookieVerifier(PartsApplication application) {
		this.application = application;
	}
	
	@Override
	public int verify(Request request, Response response) {
		final String[] values = request.getCookies().getValuesArray(COOOKIE_NAME);
		for (String value : values) {
			try {
				final Properties p = new Properties();
				p.load(new StringReader(CryptoUtil.decrypt(value)));
				final String uid = p.getProperty("uid");
				final String password = p.getProperty("password");
				request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.CUSTOM, uid, password));
			} catch (Throwable e) {
				;
			}
		}
		
		if (request.getChallengeResponse() == null) {
			return RESULT_MISSING;
		} else {
			final boolean validSecret = checkSecret(request);
			
			if (validSecret) {
			} else {
				final String identifier = request.getChallengeResponse().getIdentifier();
				// remove the cookie
				setCookie(request, response, null, null);
				
				// delay 1.5 seconds
				try {
					if (loginAttempts.get(identifier) == null) {
						loginAttempts.put(identifier, new Semaphore(1));
					}
					final Semaphore semaphore = loginAttempts.get(identifier);
					semaphore.acquire();
					Thread.sleep(1500);
					semaphore.release();
				} catch (InterruptedException e) {
					;
				}
			}
			return validSecret ? RESULT_VALID : RESULT_INVALID;
		}
	}
	
	private boolean checkSecret(Request request) {
		final String identifier = request.getChallengeResponse().getIdentifier();
		final char[] secret = request.getChallengeResponse().getSecret();
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final Account account = sql.getMapper(PartsMapper.class).selectAccount(identifier);
			if (account == null) {
				return false;
			}
			if ((secret == null) || account.getSecret() == null) {
				return (secret == account.getSecret());
			} else {
				final String stored = new String(account.getSecret());
				if (stored.startsWith("SHA1:")) {
					return PasswordUtil.verify(stored, new String(secret));
				} else {
					return stored.equals(new String(secret));
				}
			}
		} finally {
			sql.close();
		}
	}
	
	public void setCookie(Request request, Response response, String identifier, String password) {
		try {
			final StringWriter w = new StringWriter();
			final Properties p = new Properties();
			p.put("uid", identifier);
			p.put("pwd", password);
			p.store(w, null);
			w.close();
			
			final CookieSetting c = new CookieSetting(COOOKIE_NAME, CryptoUtil.encrypt(w.toString()));
			c.setPath("/");
			c.setMaxAge(identifier == null ? 0 : -1);
			response.getCookieSettings().add(c);
		} catch (Exception e) {
			application.getLogger().log(Level.WARNING, "Unable to set cookie", e);
		}
		
	}
}
