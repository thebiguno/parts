package ca.digitalcave.parts.resource;

import java.util.logging.Level;

import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Part;


public class PartResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	@Override
	protected Representation put(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject object = new JSONObject(entity.getText());
			final Part part = new Part();
			final int category = Integer.parseInt((String) getRequestAttributes().get("part"));
			part.setId(Integer.parseInt((String) getRequestAttributes().get("part")));
			part.setNumber(object.optString("number", ""));
			part.setDescription(object.optString("description",""));
			part.setNotes(object.optString("notes", ""));
			part.setCategory(object.optInt("category", category));
			part.setAvailable(object.optInt("available", 0));
			part.setMinimum(object.optInt("minimum", 0));
			sql.getMapper(PartsMapper.class).updatePart(account.getId(), part);
			return new ExtResponseRepresentation();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());
		} finally {
			sql.close();
		}
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();

		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final String part = (String) getRequestAttributes().get("part");
			final int partId = Integer.parseInt(part);
			sql.getMapper(PartsMapper.class).deletePart(account.getId(), partId);
			return new ExtResponseRepresentation();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());
		} finally {
			sql.close();
		}
	}
	
}
