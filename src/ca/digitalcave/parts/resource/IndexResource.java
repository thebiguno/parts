package ca.digitalcave.parts.resource;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;


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
}
