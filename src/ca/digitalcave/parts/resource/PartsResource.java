package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
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
import ca.digitalcave.parts.model.Part;


public class PartsResource extends ServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final String category = getQuery().getFirstValue("category","");
		final Integer c = category.trim().length() == 0 ? null : Integer.parseInt(category);
		
		final String q = getQuery().getFirstValue("q", "");
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
					sql.getMapper(PartsMapper.class).selectParts(c, Arrays.asList(terms), new ResultHandler() {
						@Override
						public void handleResult(ResultContext ctx) {
							try {
								final Part part = (Part) ctx.getResultObject();
								g.writeStartObject();
								g.writeStringField("number", part.getNumber());
								g.writeStringField("description", part.getDescription());
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
}
