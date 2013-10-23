package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
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

public class CategoriesResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		//final Account account = (Account) getClientInfo().getUser(); 
		final Account account = new Account(0); // TODO implement auth

		final SqlSession sqlSession = application.getSqlFactory().openSession();
		try {
			final String q = getQuery().getFirstValue("q", "");
			final String[] terms = q.trim().length() > 0 ? q.split(" ") : new String[0];
			final List<Category> categories = sqlSession.getMapper(PartsMapper.class).selectCategories(account.getId(), Arrays.asList(terms));
			final Category root = Category.buildTree(categories);
			
			return new WriterRepresentation(MediaType.APPLICATION_JSON) {
				@Override
				public void write(Writer w) throws IOException {
					final JsonGenerator g = application.getJsonFactory().createJsonGenerator(w);
					g.writeStartObject();
					g.writeBooleanField("success", true);
					write(root, g);
					g.writeEndObject();
					g.flush();
				}
				
				public void write(Category category, JsonGenerator g) throws JsonGenerationException, IOException {
					g.writeStartObject();
					g.writeNumberField("category", category.getId());
					g.writeStringField("icon", "img/category.png");
					g.writeStringField("name", category.getName());
					if (category.getChildren().size() > 0) {
						g.writeArrayFieldStart("children");
						for (Category child : category.getChildren()) {
							write(child, g);
						}
						g.writeEndArray();
					} else {
						g.writeBooleanField("leaf", true);
					}
					g.writeEndObject();
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
//		final Account account = (Account) getClientInfo().getUser(); 
		final Account account = new Account(0);
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject resultNode = new JSONObject();
			final JSONObject result = new JSONObject();
			result.put("success", true);
			result.put("node", resultNode);
			
			final String parentId = getQuery().getFirstValue("category");
			
			final Category category = new Category();
			category.setName("Untitled");
			category.setParentId(parentId == null ? null : Integer.parseInt(parentId));
			category.setAccount(account);
			sql.getMapper(PartsMapper.class).insertCategory(category);
			
			resultNode.put("category", category.getId());
			resultNode.put("name", category.getName());
			resultNode.put("leaf", true);
			resultNode.put("icon", "img/category.png");
			return new JsonRepresentation(result);
		} catch (Exception e) {
			throw new ResourceException(e);
		} finally {
			sql.close();
		}
	}
}
