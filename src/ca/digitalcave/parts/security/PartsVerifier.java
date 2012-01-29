package ca.digitalcave.parts.security;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeScheme;
import org.restlet.security.Verifier;

import ca.digitalcave.parts.PartsApplication;

public class PartsVerifier implements Verifier {

	private PartsApplication application;
	
	public PartsVerifier(PartsApplication aspekt) {
		this.application = aspekt;
	}
	
	public int verify(Request request, Response response) {
		if (request.getChallengeResponse() == null) {
			return RESULT_MISSING;
		} else if (request.getChallengeResponse().getScheme().equals(ChallengeScheme.HTTP_BASIC)) {
			if (check(request)) {
				return RESULT_VALID;
			} else {
				return RESULT_INVALID;
			}
		} else {
			return RESULT_UNSUPPORTED;
		}
	}
	
	public boolean check(Request request) {
		// TODO
		return true;
	}
}
