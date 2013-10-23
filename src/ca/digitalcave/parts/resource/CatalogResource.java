package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
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
import ca.digitalcave.parts.model.Family;

public class CatalogResource extends ServerResource {

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
			final List<Category> search = sqlSession.getMapper(PartsMapper.class).selectTree(account.getId(), Arrays.asList(terms));
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
							g.writeNumberField("category", category.getId());
							g.writeStringField("icon", "img/category.png");
							g.writeArrayFieldStart("children");
							for (Family family : category.getFamilies()) {
								g.writeStartObject();
								g.writeStringField("name", family.getName());
								g.writeBooleanField("leaf", true);
								g.writeNumberField("category", category.getId());
								g.writeNumberField("family", family.getId());
								g.writeStringField("icon", "img/category.png");
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
//		final Account account = (Account) getClientInfo().getUser(); 
		final Account account = new Account(0);
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject resultNode = new JSONObject();
			final JSONObject result = new JSONObject();
			result.put("success", true);
			result.put("node", resultNode);
			
			final String categoryId = getQuery().getFirstValue("category");
			if (categoryId != null) {
				// TODO verify category belongs to this account
				final Family family = new Family();
				family.setName("Untitled");
				family.setCategory(new Category(Integer.parseInt(categoryId)));
				sql.getMapper(PartsMapper.class).insertFamily(family);
				
				resultNode.put("category", family.getCategory().getId());
				resultNode.put("family", family.getId());
				resultNode.put("name", family.getName());
				resultNode.put("icon", "img/category.png");
			} else {
				final Category category = new Category();
				category.setName("Untitled");
				category.setAccount(account);
				sql.getMapper(PartsMapper.class).insertCategory(category);
				
				resultNode.put("category", category.getId());
				resultNode.put("name", category.getName());
				resultNode.put("leaf", true);
				resultNode.put("icon", "img/category.png");
			}
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
		final Account account = (Account) getClientInfo().getUser();
		// TODO verify category or family belongs to this account
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject result = new JSONObject();
			result.put("success", true);
			
			final String categoryId = getQuery().getFirstValue("category");
			final String familyId = getQuery().getFirstValue("family");
			
			if (familyId == null || familyId.trim().length() == 0) {
				sql.getMapper(PartsMapper.class).updateCategory(Integer.parseInt(categoryId), entity.getText());
			} else {
				sql.getMapper(PartsMapper.class).updateFamily(Integer.parseInt(familyId), entity.getText());
			}
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
			final String familyId = getQuery().getFirstValue("family");
			if (familyId == null) {
				sql.getMapper(PartsMapper.class).deleteCategory(Integer.parseInt(categoryId));
			} else {
				sql.getMapper(PartsMapper.class).deleteFamily(Integer.parseInt(familyId));
			}
			return new JsonRepresentation(result);
		} catch (Exception e) {
			throw new ResourceException(e);
		} finally {
			sql.close();
		}
	}
}
