package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
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
import ca.digitalcave.parts.model.Attribute;

public class AttributesResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();

		final Integer categoryId = Integer.parseInt((String) getRequestAttributes().get("category"));
		final Integer partId = Integer.parseInt((String) getRequestAttributes().get("part"));
		
		return new WriterRepresentation(MediaType.APPLICATION_JSON) {
			@Override
			public void write(Writer writer) throws IOException {
				final JsonGenerator g = application.getJsonFactory().createJsonGenerator(writer);

				final SqlSession sql = application.getSqlFactory().openSession(true);
				try {
					g.writeStartObject();
					g.writeBooleanField("success", true);
					g.writeArrayFieldStart("data");
					
					sql.getMapper(PartsMapper.class).selectAttributes(account.getId(), partId, new ResultHandler() {
						@Override
						public void handleResult(ResultContext ctx) {
							try {
								final Attribute attribute = (Attribute) ctx.getResultObject();
								
								g.writeStartObject();
								g.writeNumberField("category", categoryId);
								g.writeNumberField("part", partId);
								g.writeNumberField("id", attribute.getId());
								g.writeStringField("name", attribute.getName());
								g.writeStringField("value", attribute.getValue());
								
								if (attribute.getHref() != null) {
									g.writeStringField("icon", "img/document-bookmark.png");
									g.writeStringField("href", attribute.getHref());
								} else if (attribute.getMimeType() != null) {
									g.writeStringField("icon", getIcon(attribute.getMimeType()));
									g.writeStringField("href", "categories/" + categoryId + "/parts/" + partId + "/attributes/" + attribute.getId());
								}
								
								g.writeEndObject();
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
						
					});
					
					g.writeEndArray();
					g.writeEndObject();
					g.flush();
				} finally {
					sql.close();
				}
			}
		};
	}
	
	private String getIcon(String mediaType) {
		if ("text/html".equals(mediaType)) {
			return "img/document-globe.png";
		} else if (mediaType.startsWith("text/")) {
			return "img/document-text.png";
		} else if (mediaType.startsWith("image/")) {
			return "img/document-image.png";
		} else if (mediaType.equals("application/pdf")) {
			return "img/document-pdf.png";
		} else {
			return "img/document.png";
		}
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject resultAttr = new JSONObject();
			final JSONObject result = new JSONObject();
			result.put("success", true);
			result.put("attribute", resultAttr);
			
			final Attribute attr = new Attribute();
			attr.setPart(Integer.parseInt((String) getRequestAttributes().get("part")));
			attr.setName("");
			attr.setValue("");
			final int ct = sql.getMapper(PartsMapper.class).insertAttribute(account.getId(), attr);
			if (ct == 0) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			
			resultAttr.put("id", attr.getId());
			resultAttr.put("name", attr.getName());
			resultAttr.put("value", attr.getValue());
			return new JsonRepresentation(result);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());
		} finally {
			sql.close();
		}
	}
}
