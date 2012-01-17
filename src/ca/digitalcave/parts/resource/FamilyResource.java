package ca.digitalcave.parts.resource;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;


public class FamilyResource extends ServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Repository repository = application.getRepository();
		Session session = null;
		try {
			session = repository.login();
			final String category = (String) getRequestAttributes().get("category");
			final String family = (String) getRequestAttributes().get("family");
			final Node node = session.getNode(String.format("/parts/$1/$2", category, family));
			getResponseAttributes().put("family", node);
			return new TemplateRepresentation("family.ftl", application.getFmConfig(), getResponseAttributes(), MediaType.TEXT_HTML);
		} catch (PathNotFoundException e) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (RepositoryException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			if (session != null) session.logout();
		}
	}
}
