package ca.digitalcave.parts.resource;

import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.htmlparser.Parser;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TagFindingVisitor;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;

public class IndexResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();

		return new TemplateRepresentation("search.ftl", application.getFmConfig(), getResponseAttributes(), MediaType.TEXT_HTML);
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final Form form = new Form(entity);
		final String keywords = form.getFirstValue("keywords");
		final String dk = form.getFirstValue("dk");
		if (dk != null) {
			final ConnectionManager connectionManager = Page.getConnectionManager();
			final Parser parser = new Parser(connectionManager.openConnection(dk));
			
			final DigiKeyVisitor visitor = new DigiKeyVisitor();
			try {
				parser.visitAllNodesWith(visitor);
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		final PartsApplication application = (PartsApplication) getApplication();
		final Repository repository = application.getRepository();
		Session session = null;
		try {
			session = repository.login();
			
			final String sql = "select * from [nt:unstructured] as node where contains(node.*, $keywords)";
			final Query query = session.getWorkspace().getQueryManager().createQuery(sql, Query.JCR_SQL2);
			query.bindValue("keywords", session.getValueFactory().createValue(keywords));
			final QueryResult queryResults = query.execute();
			getResponseAttributes().put("categories", queryResults.getNodes());
			return new TemplateRepresentation("search.ftl", application.getFmConfig(), getResponseAttributes(), MediaType.TEXT_HTML);
		} catch (PathNotFoundException e) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (RepositoryException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			if (session != null) session.logout();
		}
	}
}
