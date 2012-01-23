package ca.digitalcave.parts.resource;

import java.net.URLDecoder;

import org.apache.ibatis.session.SqlSession;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
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
			final String category = URLDecoder.decode((String) getRequestAttributes().get("category"), "UTF-8");
			final String family = URLDecoder.decode((String) getRequestAttributes().get("family"), "UTF-8");
			
			getResponseAttributes().put("category", category);
			getResponseAttributes().put("family", family);
			getResponseAttributes().put("parts", sqlSession.getMapper(PartsMapper.class).partsByFamily(category, family));
			return new TemplateRepresentation("family.ftl", application.getFmConfig(), getResponseAttributes(), MediaType.TEXT_HTML);
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			sqlSession.close();
		}
	}
}
