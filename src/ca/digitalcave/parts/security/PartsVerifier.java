package ca.digitalcave.parts.security;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Semaphore;

import org.apache.ibatis.session.SqlSession;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Verifier;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Account;

public class PartsVerifier implements Verifier {

	private final PartsApplication application;
	private final static Map<String, Semaphore> loginAttempts = Collections.synchronizedMap(new WeakHashMap<String, Semaphore>());
	
	public PartsVerifier(PartsApplication application) {
		this.application = application;
	}
	
	@Override
	public int verify(Request request, Response response) {
		if (request.getChallengeResponse() == null) {
			return RESULT_MISSING;
		} else {
			final boolean validSecret = checkSecret(request);
			
			if (!validSecret) {
				final String identifier = request.getChallengeResponse().getIdentifier();
				
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
	
	public boolean checkSecret(Request request) {
		final String identifier = request.getChallengeResponse().getIdentifier();
		final char[] secret = request.getChallengeResponse().getSecret();
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final Account account = sql.getMapper(PartsMapper.class).selectAccount(identifier);
			
			if (account == null) {
				return false;
			}
			final boolean result;
			if ((secret == null) || account.getSecret() == null) {
				result = (secret == account.getSecret());
			} else {
				final String stored = new String(account.getSecret());
				if (stored.startsWith("SHA1:")) {
					result = PasswordUtil.verify(stored, new String(secret));
				} else {
					result = stored.equals(new String(secret));
				}
			}
			
			if (result) {
				request.getClientInfo().setUser(account);
			}
			return result;
		} finally {
			sql.close();
		}
	}
}
