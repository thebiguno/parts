package ca.digitalcave.parts.resource;

import org.json.JSONObject;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.Verifier;

import ca.digitalcave.parts.PartsApplication;


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
		
		try {
			final JSONObject credentials = new JSONObject(entity.getText());
			String identifier = credentials.getString("identifier");
			String secret = credentials.getString("secret");
			getRequest().setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_COOKIE, identifier, secret.toCharArray()));
			int verify = application.getVerifier().verify(getRequest(), getResponse());
			if (verify == Verifier.RESULT_VALID) {
				application.getVerifier().setCookie(getRequest(), getResponse(), identifier, secret);
				return new ExtResponseRepresentation();
			} else {
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
		} catch (Exception e) {
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
 		}
	}
}
