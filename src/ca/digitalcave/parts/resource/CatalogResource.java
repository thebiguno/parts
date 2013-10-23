package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Category;
import ca.digitalcave.parts.model.Family;

public class HierarchyResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser(); 

		final SqlSession sqlSession = application.getSqlFactory().openSession();
		try {
			final String q = getQuery().getFirstValue("q", "");
			final List<String> terms = Arrays.asList(q.split(" "));
			final List<Category> search = sqlSession.getMapper(PartsMapper.class).selectHierarchy(account.getId(), terms);
			final boolean tree = getQuery().getFirst("node") != null;
			
			return new WriterRepresentation(MediaType.APPLICATION_JSON) {
				@Override
				public void write(Writer w) throws IOException {
					final JsonGenerator g = application.getJsonFactory().createJsonGenerator(w);
					g.writeStartObject();
					g.writeBooleanField("success", true);
					if (tree) {
						// this is for the desktop client
						g.writeArrayFieldStart("children");
						for (Category category : search) {
							g.writeStartObject();
							g.writeStringField("name", category.getName());
							g.writeStringField("category", category.getName());
							g.writeArrayFieldStart("children");
							for (Family family : category.getFamilies()) {
								g.writeStartObject();
								g.writeStringField("name", family.getName());
								g.writeBooleanField("leaf", true);
								g.writeStringField("category", category.getName());
								g.writeStringField("family", family.getName());
								g.writeEndObject();
							}
							g.writeEndArray();
							g.writeEndObject();
						}
						g.writeEndArray();
					} else {
						// this is for the mobile client
						g.writeArrayFieldStart("data");
						for (Category category : search) {
							for (Family family : category.getFamilies()) {
								g.writeStartObject();
								g.writeStringField("category", category.getName());
								g.writeStringField("family", family.getName());
								g.writeEndObject();
							}
						}
						g.writeEndArray();
					}
					g.writeEndObject();
					g.flush();
				}
			};
			
			
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser(); 
		
		final SqlSession sql = application.getSqlFactory().openSession();
		try {
			final JsonParser p = application.getJsonFactory().createJsonParser(entity.getReader());
			final JsonNode node = p.readValueAsTree();
			p.close();
			
			final JsonNode categoryId = node.get("category");
			if (categoryId == null) {
				final Category category = new Category();
				category.setAccount(account);
				category.setName(node.get("name").getTextValue());
				sql.getMapper(PartsMapper.class).insertCategory(category);
				
				final JSONObject result = new JSONObject();
				result.put("success", true);
				result.put("category", category.getId());
				return new JsonRepresentation(result);
			} else {
				final Family family = new Family();
				family.setCategory(new Category(categoryId.getIntValue()));
				family.setName(node.get("name").getTextValue());
				sql.getMapper(PartsMapper.class).insertFamily(family);
				
				final JSONObject result = new JSONObject();
				result.put("success", true);
				result.put("category", family.getCategory().getId());
				result.put("family", family.getId());
				return new JsonRepresentation(result);
			}
		} catch (Exception e) {
			throw new ResourceException(e);
		} finally {
			sql.close();
		}
	}
}
