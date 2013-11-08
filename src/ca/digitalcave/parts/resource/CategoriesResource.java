package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.apache.ibatis.session.SqlSession;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.json.JSONObject;
import org.json.JSONTokener;
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
			final boolean required = "true".equals(getQuery().getFirstValue("required"));
			final String[] terms = q.trim().length() > 0 ? q.split(" ") : new String[0];
			final List<Category> categories = sqlSession.getMapper(PartsMapper.class).selectCategories(account.getId(), Arrays.asList(terms), required);
			final Category root = Category.buildTree(categories);
			
			return new WriterRepresentation(MediaType.APPLICATION_JSON) {
				@Override
				public void write(Writer w) throws IOException {
					final JsonGenerator g = application.getJsonFactory().createJsonGenerator(w);
					g.writeStartObject();
					g.writeBooleanField("success", true);
					g.writeArrayFieldStart("children");
					for (Category child : root.getChildren()) {
						write(child, g);
					}
					g.writeEndArray();
					g.writeEndObject();
					g.flush();
				}
				
				public void write(Category category, JsonGenerator g) throws JsonGenerationException, IOException {
					g.writeStartObject();
					g.writeNumberField("id", category.getId());
					g.writeStringField("icon", "img/category.png");
					g.writeStringField("name", category.getName());
					g.writeArrayFieldStart("children");
					for (Category child : category.getChildren()) {
						write(child, g);
					}
					g.writeEndArray();
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
		final Account account = new Account(0); // TODO implement auth
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject resultNode = new JSONObject();
			final JSONObject result = new JSONObject();
			result.put("success", true);
			result.put("node", resultNode);
			
			final int parentId = Integer.parseInt((String) getRequestAttributes().get("category"));
			final Category category = new Category();
			category.setName("Untitled");
			category.setParentId(parentId == 0 ? null : parentId);
			final int ct = sql.getMapper(PartsMapper.class).insertCategory(account.getId(), category);
			if (ct == 0) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			
			resultNode.put("id", category.getId());
			resultNode.put("name", category.getName());
			resultNode.put("children", Collections.EMPTY_LIST);
			resultNode.put("icon", "img/category.png");
			return new JsonRepresentation(result);
		} catch (Exception e) {
			throw new ResourceException(e);
		} finally {
			sql.close();
		}
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
			final JSONObject object = new JSONObject(entity.getText());
			if (object.has("name")) {
				int ct = sql.getMapper(PartsMapper.class).updateCategory(account.getId(), categoryId, object.getString("name"));
				if (ct == 0) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			} else if (object.has("parent")) {
				int ct = sql.getMapper(PartsMapper.class).moveCategory(account.getId(), categoryId, object.getInt("parent"));
				if (ct == 0) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			} else {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			return new JsonRepresentation(result);
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
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());
		} finally {
			sql.close();
		}
	}
}
