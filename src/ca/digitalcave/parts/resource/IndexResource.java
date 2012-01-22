package ca.digitalcave.parts.resource;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ibatis.session.SqlSession;
import org.htmlparser.Parser;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.ParserException;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Attribute;

public class IndexResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();

		return new TemplateRepresentation("index.ftl", application.getFmConfig(), getResponseAttributes(), MediaType.TEXT_HTML);
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Form form = new Form(entity);
		final String dk = form.getFirstValue("dk");
		if (dk != null) {
			final DigiKeyVisitor visitor = new DigiKeyVisitor();
			final ConnectionManager connectionManager = Page.getConnectionManager();
			try {
				final Parser parser = new Parser(connectionManager.openConnection(dk));
				parser.visitAllNodesWith(visitor);
			} catch (ParserException e) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
			}
			
			final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
			try {
				final PartsMapper mapper = sqlSession.getMapper(PartsMapper.class);
				int partId = mapper.newPartId();
				for (Attribute attribute : visitor.getAttributes()) {
					attribute.setPartId(partId);
					mapper.insert(attribute);
				}
			} finally {
				sqlSession.close();
			}
		}
		
		final ArrayList<String> terms = new ArrayList<String>();
		final String keywords = form.getFirstValue("keywords");
		if (keywords != null) {
			for (String term : Arrays.asList(keywords.split("\\s+"))) {
				terms.add(StringEscapeUtils.escapeSql(term));
			}
		}
		final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
		try {
			final PartsMapper mapper = sqlSession.getMapper(PartsMapper.class);
			getResponseAttributes().put("categories", mapper.search(terms));
			return new TemplateRepresentation("index.ftl", application.getFmConfig(), getResponseAttributes(), MediaType.TEXT_HTML);
		} finally {
			sqlSession.close();
		}
	}
}
