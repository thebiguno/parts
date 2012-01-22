package ca.digitalcave.parts.resource;

import org.apache.ibatis.session.SqlSession;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;


public class FamilyResource extends ServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
		try {
			final String category = (String) getRequestAttributes().get("category");
			final String family = (String) getRequestAttributes().get("family");
			
			getResponseAttributes().put("family", sqlSession.getMapper(PartsMapper.class).partsByFamily(category, family));
			return new TemplateRepresentation("family.ftl", application.getFmConfig(), getResponseAttributes(), MediaType.TEXT_HTML);
		} finally {
			sqlSession.close();
		}
	}
}
