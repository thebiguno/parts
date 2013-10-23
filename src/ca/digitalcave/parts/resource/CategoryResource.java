package ca.digitalcave.parts.resource;

import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Account;

public class CategoryResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation put(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();
		// TODO verify category or family belongs to this account
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject result = new JSONObject();
			result.put("success", true);
			
			final int categoryId = Integer.parseInt((String) getRequestAttributes().get("category"));

			sql.getMapper(PartsMapper.class).updateCategory(categoryId, entity.getText());
			return new JsonRepresentation(result);
		} catch (Exception e) {
			throw new ResourceException(e);
		} finally {
			sql.close();
		}
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();
		// TODO verify category or family belongs to this account
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject result = new JSONObject();
			result.put("success", true);
			
			final String categoryId = getQuery().getFirstValue("category");
			sql.getMapper(PartsMapper.class).deleteCategory(Integer.parseInt(categoryId));
			return new JsonRepresentation(result);
		} catch (Exception e) {
			throw new ResourceException(e);
		} finally {
			sql.close();
		}
	}
}
