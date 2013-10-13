package ca.digitalcave.parts.resource.mobile;

import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.codehaus.jackson.JsonGenerator;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Part;


public class FamilyResource extends ServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlFactory().openSession();
		try {
			String category = URLDecoder.decode((String) getRequestAttributes().get("category"), "UTF-8");
			String family = URLDecoder.decode((String) getRequestAttributes().get("family"), "UTF-8");
			if ("*".equals(category)) category = null;
			if ("*".equals(family)) family = null;
			
			final List<Part> parts = sqlSession.getMapper(PartsMapper.class).partsByFamily(category, family);
			
			return new WriterRepresentation(MediaType.APPLICATION_JSON) {
				@Override
				public void write(Writer w) throws IOException {
					final JsonGenerator g = application.getJsonFactory().createJsonGenerator(w);
					g.writeStartObject();
					g.writeBooleanField("success", true);
					g.writeArrayFieldStart("data");
					for (Part part : parts) {
						g.writeStartObject();
						final Attribute partNumber = part.findAttribute("Manufacturer Part Number");
						final Attribute description = part.findAttribute("Description");
						final Attribute notes = part.findAttribute("Notes");
						final Attribute quantity = part.findAttribute("Quantity In Stock");
						if (partNumber != null) g.writeStringField("part", partNumber.getValue());
						if (description != null) g.writeStringField("description", description.getValue());
						if (notes != null) g.writeStringField("notes", notes.getValue());
						if (quantity != null) g.writeNumberField("quantity", Integer.parseInt(quantity.getValue()));
						final StringBuilder datasheets = new StringBuilder();
						for (Attribute attribute : part.findAttributes("Datasheet")) {
							datasheets.append("<a href='").append(attribute.getHref()).append("' target='_blank'>").append(attribute.getValue()).append("</a><br/>");
						}
						g.writeStringField("datasheets", datasheets.toString());
						g.writeEndObject();
					}
					g.writeEndArray();
					g.writeEndObject();
					g.close();
				}
			};
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			sqlSession.close();
		}
	}
}
