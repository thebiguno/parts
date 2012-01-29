package ca.digitalcave.parts.security;

import java.util.Properties;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.security.User;
import org.restlet.security.Verifier;

import ca.digitalcave.parts.PartsApplication;

public class PartsVerifier implements Verifier {

	private PartsApplication application;
	
	public PartsVerifier(PartsApplication application) {
		this.application = application;
	}
	
	public int verify(Request request, Response response) {
		final Request passwdRequest = new Request(Method.GET, "war:///WEB-INF/passwd.properties");
		final Response passwdResponse = application.getContext().getClientDispatcher().handle(passwdRequest);

		if (passwdResponse.getStatus() == Status.CLIENT_ERROR_NOT_FOUND) {
			// admin party!
			return RESULT_VALID;
		} else {
			if (request.getChallengeResponse() == null) {
				return RESULT_MISSING;
			} else if (request.getChallengeResponse().getScheme().equals(ChallengeScheme.HTTP_BASIC)) {
				final Properties passwd = new Properties();
				try {
					passwd.load(passwdResponse.getEntity().getStream());
				} catch (Exception e) {
					;
				}

				final String identifier = request.getChallengeResponse().getIdentifier();
				final String secret = new String(request.getChallengeResponse().getSecret());
				final String storedSecret = passwd.getProperty(identifier);

				request.getClientInfo().setUser(new User(identifier));

				if (storedSecret.startsWith("OBF:")) {
					return PasswordUtil.deobfuscate(storedSecret).equals(secret) ? RESULT_VALID : RESULT_INVALID;
				} else if (storedSecret.startsWith("MD2:")
						|| storedSecret.startsWith("MD5:")
						|| storedSecret.startsWith("SHA-1:") 
						|| storedSecret.startsWith("SHA-256:")
						|| storedSecret.startsWith("SHA-512:")) {
					return PasswordUtil.verify(storedSecret, secret.getBytes()) ? RESULT_VALID : RESULT_INVALID; 
				} else {
					return secret.equals(storedSecret) ? RESULT_VALID : RESULT_INVALID;
				}
			} else {
				return RESULT_UNSUPPORTED;
			}
		}
	}
}
