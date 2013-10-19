package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.codehaus.jackson.JsonGenerator;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.digi.DigiKeyClient;
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Category;
import ca.digitalcave.parts.model.Family;
import freemarker.template.Template;

public class CatalogResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();

		final SqlSession sqlSession = application.getSqlFactory().openSession();
		try {
			final String q = getQuery().getFirstValue("q", "");
			final List<String> terms = Arrays.asList(q.split(" "));
			final List<Category> search = sqlSession.getMapper(PartsMapper.class).search(terms);
			final boolean tree = getQuery().getFirst("node") != null;
			
			return new WriterRepresentation(MediaType.APPLICATION_JSON) {
				@Override
				public void write(Writer w) throws IOException {
					final JsonGenerator g = application.getJsonFactory().createJsonGenerator(w);
					g.writeStartObject();
					g.writeBooleanField("success", true);
					if (tree) {
						g.writeArrayFieldStart("children");
						for (Category category : search) {
							g.writeStartObject();
							g.writeStringField("name", category.getName());
							g.writeStringField("category", category.getName());
							g.writeStringField("family",  "*");
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
}
