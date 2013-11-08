package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.logging.Level;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
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
import ca.digitalcave.parts.model.Part;


public class PartsResource extends ServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();
		
		final Integer categoryId = Integer.parseInt((String) getRequestAttributes().get("category"));
		
		final String q = getQuery().getFirstValue("q", "");
		final boolean required = "true".equals(getQuery().getFirstValue("required"));
		final String[] terms = q.trim().length() > 0 ? q.split(" ") : new String[0];

		return new WriterRepresentation(MediaType.APPLICATION_JSON) {
			@Override
			public void write(Writer w) throws IOException {
				final JsonGenerator g = application.getJsonFactory().createJsonGenerator(w);
				g.writeStartObject();
				g.writeBooleanField("success", true);
				g.writeArrayFieldStart("data");
				
				final SqlSession sql = application.getSqlFactory().openSession();
				try {
					sql.getMapper(PartsMapper.class).selectParts(account.getId(), categoryId == 0 ? null : categoryId, Arrays.asList(terms), required, new ResultHandler() {
						@Override
						public void handleResult(ResultContext ctx) {
							try {
								final Part part = (Part) ctx.getResultObject();
								g.writeStartObject();
								g.writeNumberField("id", part.getId());
								g.writeStringField("group", part.getGroup());
								g.writeNumberField("category", part.getCategory());
								g.writeStringField("number", part.getNumber());
								g.writeStringField("description", part.getDescription());
								if (part.getNotes() != null) g.writeStringField("notes", part.getNotes());
								g.writeNumberField("available", part.getAvailable());
								g.writeNumberField("minimum", part.getMinimum());
								g.writeEndObject();
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					});
					g.writeEndArray();
					g.writeEndObject();
					g.flush();
				} catch (Exception e) {
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
				} finally {
					sql.close();
				}
			}
		};
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject resultPart = new JSONObject();
			final JSONObject result = new JSONObject();
			result.put("success", true);
			result.put("part", resultPart);
			
			final Part part = new Part();
			part.setCategory(Integer.parseInt((String) getRequestAttributes().get("category")));
			part.setNumber("");
			part.setAvailable(0);
			part.setMinimum(0);
			part.setDescription("");
			part.setNotes("");
			final int ct = sql.getMapper(PartsMapper.class).insertPart(account.getId(), part);
			if (ct == 0) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			
			resultPart.put("id", part.getId());
			resultPart.put("number", part.getNumber());
			resultPart.put("category", part.getCategory());
			resultPart.put("available", part.getAvailable());
			resultPart.put("minimum", part.getMinimum());
			resultPart.put("description", part.getDescription());
			resultPart.put("notes", part.getNotes());
			return new JsonRepresentation(result);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());
		} finally {
			sql.close();
		}
	}
}
