package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.codehaus.jackson.JsonGenerator;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Part;


public class PartResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlFactory().openSession();
		try {
			final String part = (String) getRequestAttributes().get("part");
			final int partId = Integer.parseInt(part);
			return new WriterRepresentation(variant.getMediaType()) {
				@Override
				public void write(Writer w) throws IOException {
					final JsonGenerator g = application.getJsonFactory().createJsonGenerator(w);
					g.writeStartObject();
					g.writeBooleanField("success", true);
					g.writeArrayFieldStart("data");
					sqlSession.getMapper(PartsMapper.class).selectAttributes(partId, new ResultHandler() {
						@Override
						public void handleResult(ResultContext ctx) {
							final Attribute attribute = (Attribute) ctx.getResultObject();
							try {
								g.writeStartObject();
								g.writeNumberField("sort", attribute.getSort());
								g.writeStringField("name", attribute.getName());
								g.writeStringField("value", attribute.getValue());
								g.writeStringField("href", attribute.getHref());
								g.writeEndObject();
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					});
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
	protected Representation put(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = new Account(0); // TODO implement auth 
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject object = new JSONObject(entity.getText());
			final Part part = new Part();
			part.setId(Integer.parseInt((String) getRequestAttributes().get("part")));
			part.setNumber(object.optString("number", ""));
			part.setDescription(object.optString("description",""));
			part.setNotes(object.optString("notes", ""));
			part.setCategory(object.getInt("category"));
			part.setAvailable(object.optInt("available", 0));
			part.setMinimum(object.optInt("minimum", 0));
			sql.getMapper(PartsMapper.class).updatePart(account.getId(), part);
			return new StringRepresentation("{\"success\":true}");
		} catch (Exception e) {
			throw new ResourceException(e);
		} finally {
			sql.close();
		}
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = new Account(0); // TODO implement auth 

		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			// TODO id and category from URI
			final String part = (String) getRequestAttributes().get("part");
			final int partId = Integer.parseInt(part);
			sql.getMapper(PartsMapper.class).deletePart(account.getId(), partId);
			return new StringRepresentation("{\"success\":true}");
		} finally {
			sql.close();
		}
	}
	
}
