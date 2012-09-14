package ca.digitalcave.parts.resource.mobile;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Category;
import ca.digitalcave.parts.model.Family;

public class IndexResourceMobile extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();

		final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
		try {
			final List<String> terms = Collections.emptyList();
			final List<Category> categories = sqlSession.getMapper(PartsMapper.class).search(terms);

			final JSONObject result = new JSONObject();
			for (Category category : categories) {
				for (Family family : category.getFamilies()) {
					JSONObject familyObj = new JSONObject();
					familyObj.put("category", category.getName());
					familyObj.put("family", family.getName());
					result.append("data", familyObj);
				}
			}

			return new JsonRepresentation(result);
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			sqlSession.close();
		}
	}
}
