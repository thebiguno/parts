package ca.digitalcave.parts.resource;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;


public class PartResource extends ServerResource {

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
			final String part = (String) getRequestAttributes().get("part");
			final Node node = session.getNode(String.format("/parts/$1/$2/$3", category, family, part));
			getResponseAttributes().put("part", node); 
			return new TemplateRepresentation("part.ftl", application.getFmConfig(), getResponseAttributes(), MediaType.TEXT_HTML);
		} catch (PathNotFoundException e) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (RepositoryException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			if (session != null) session.logout();
		}
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Repository repository = application.getRepository();
		Session session = null;
		try {
			session = repository.login();
			final String category = (String) getRequestAttributes().get("category");
			final String family = (String) getRequestAttributes().get("family");
			final String part = (String) getRequestAttributes().get("part");
			final Node node = session.getNode(String.format("/parts/$1/$2/$3", category, family, part));
			
			final Form form = new Form(entity);
			final String name = form.getFirstValue("name");
			final String action = form.getFirstValue("action");
			final String newValue = form.getFirstValue("newvalue");
			if ("delete".equals(action)) {
				node.setProperty(name, (Value) null);
			} else if ("update_name".equals(action)) {
				final Value value = node.getProperty(name).getValue();
				node.setProperty(newValue, value);
				node.setProperty(name, (Value) null);
			} else if ("update_value".equals(action)) {
				node.setProperty(name, newValue);
			}
			
			return new EmptyRepresentation();
		} catch (PathNotFoundException e) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (RepositoryException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			if (session != null) session.logout();
		}	
	}
	
}
