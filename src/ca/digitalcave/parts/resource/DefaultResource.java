package ca.digitalcave.parts.resource;

import java.util.Date;
import java.util.HashMap;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;

public class DefaultResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		getVariants().add(new Variant(MediaType.APPLICATION_JAVASCRIPT));
		getVariants().add(new Variant(MediaType.IMAGE_GIF));
		getVariants().add(new Variant(MediaType.IMAGE_PNG));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication aspekt = (PartsApplication) getApplication();
		final String path = new Reference(getRootRef(), getOriginalRef()).getRemainingPart(true, false);
				
		if (path.startsWith("WEB-INF")) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		
		boolean transform = variant.getMediaType().equals(MediaType.TEXT_HTML) 
					|| variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT); 
		
		if (transform) {
			final HashMap<String, Object> dataModel = new HashMap<String, Object>();
			dataModel.put("user", getClientInfo().getUser());
			dataModel.put("requestAttributes", getRequestAttributes());
			final TemplateRepresentation entity = new TemplateRepresentation(path, aspekt.getFmConfig(), dataModel, variant.getMediaType());
			if (entity.getTemplate() == null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			entity.setModificationDate(new Date());
			return entity;
		} else {
			final Request request = new Request(Method.GET, new Reference("war://" + path));
			request.getConditions().setUnmodifiedSince(getRequest().getConditions().getUnmodifiedSince());
			getContext().getClientDispatcher().handle(request, getResponse());
			return getResponseEntity();
		}
	}

}
