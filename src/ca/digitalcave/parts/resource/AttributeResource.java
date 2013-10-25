package ca.digitalcave.parts.resource;

import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Attribute;

public class AttributeResource extends ServerResource {

//	@Override
//	protected Representation get() throws ResourceException {
//		
//	}
	
	
	@Override
	protected Representation put(Representation entity) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = new Account(0); // TODO implement auth 
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject object = new JSONObject(entity.getText());
			final Attribute attr = new Attribute();
			attr.setId(Long.parseLong((String) getRequestAttributes().get("attribute")));
			attr.setName(object.optString("name", ""));
			attr.setValue(object.optString("value",""));
			sql.getMapper(PartsMapper.class).updateAttribute(account.getId(), attr);
			return new StringRepresentation("{\"success\":true}");
		} catch (Exception e) {
			throw new ResourceException(e);
		} finally {
			sql.close();
		}
	}
	
	@Override
	protected Representation delete() throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = new Account(0); // TODO implement auth 

		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final String attr = (String) getRequestAttributes().get("attribute");
			final long partId = Long.parseLong(attr);
			sql.getMapper(PartsMapper.class).deletePart(account.getId(), partId);
			return new StringRepresentation("{\"success\":true}");
		} finally {
			sql.close();
		}
	}}
