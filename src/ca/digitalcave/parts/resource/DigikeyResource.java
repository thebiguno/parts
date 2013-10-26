package ca.digitalcave.parts.resource;

import java.util.List;
import java.util.logging.Level;

import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.digi.DigiKeyClient;
import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Category;
import ca.digitalcave.parts.model.Part;

public class DigikeyResource extends ServerResource {

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();
		
		final JSONObject object = new JSONObject(entity);
		final SqlSession sql = application.getSqlFactory().openSession(false);
		try {
			final DigiKeyClient client = new DigiKeyClient();
			final List<Attribute> attributes = client.parse(object.getString("url"));

			if (attributes.size() > 0) {
				attributes.add(new Attribute("URL", "URL", object.getString("url")));
				attributes.add(new Attribute("Quantity Available", object.optString("qty", "0")));
				attributes.add(new Attribute("Minimum Quantity", object.optString("min", "0")));
				insertPart(sql, attributes, account.getId());
				sql.commit();
			}
			return new ExtResponseRepresentation();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());
		} finally {
			sql.close();
		}
	}
	
	public static void insertPart(SqlSession sql, List<Attribute> attributes, int account) {
		final PartsMapper mapper = sql.getMapper(PartsMapper.class);
		final Attribute catAttr = Attribute.remove("Category", attributes);
		final Attribute famAttr = Attribute.remove("Family", attributes);
		final List<Category> categories = mapper.selectDigikey(account, catAttr.getValue(), famAttr.getValue());
		if (categories.size() == 0) {
			final Category category = new Category();
			category.setName(catAttr.getValue());
			mapper.insertCategory(account, category);
			categories.add(category);
		} 
		if (categories.size() == 1) {
			final Category category = new Category();
			category.setParentId(categories.get(0).getId());
			category.setName(famAttr.getValue());
			mapper.insertCategory(account, category);
			categories.add(category);
		}
		final Part part = new Part();
		part.setCategory(categories.get(1).getId());
		final Attribute num = Attribute.remove("Manufacturer Part Number", attributes);
		if (num != null) part.setNumber(num.getValue());
		final Attribute desc = Attribute.remove("Description", attributes);
		if (desc != null) part.setDescription(desc.getValue());
		final Attribute notes = Attribute.remove("Notes", attributes);
		if (notes != null) part.setNotes(notes.getValue());
		final Attribute qty = Attribute.remove("Quantity Available", attributes);
		
		part.setAvailable(0);
		if (qty != null) part.setAvailable(Integer.parseInt(qty.getValue()));
		final Attribute min = Attribute.remove("Minimum Quantity", attributes);
		part.setMinimum(0);
		if (min != null) part.setMinimum(Integer.parseInt(min.getValue()));
		
		mapper.insertPart(account, part);

		for (Attribute attribute : attributes) {
			if (attribute.getValue().length() > 255) attribute.setValue(attribute.getValue().substring(0, 255));	//Prevent DB field overflow
			attribute.setPart(part.getId());
			mapper.insertAttribute(account, attribute);
		}
	}
}
