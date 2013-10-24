package ca.digitalcave.parts.resource;

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
import ca.digitalcave.parts.model.Account;

public class CategoryResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation put(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
//		final Account account = (Account) getClientInfo().getUser();
		final Account account = new Account(0); // TODO implement auth
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject result = new JSONObject();
			result.put("success", true);
			
			final int categoryId = Integer.parseInt((String) getRequestAttributes().get("category"));

			int ct = sql.getMapper(PartsMapper.class).updateCategory(account.getId(), categoryId, entity.getText());
			if (ct == 0) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
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
		//final Account account = (Account) getClientInfo().getUser();
		final Account account = new Account(0); // TODO implement auth
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject result = new JSONObject();
			result.put("success", true);
			
			final int categoryId = Integer.parseInt((String) getRequestAttributes().get("category"));
			final int ct = sql.getMapper(PartsMapper.class).deleteCategory(account.getId(), categoryId);
			if (ct == 0) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			return new JsonRepresentation(result);
		} catch (Exception e) {
			throw new ResourceException(e);
		} finally {
			sql.close();
		}
	}
}
