package ca.digitalcave.parts.resource.mobile;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

		final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
		try {
			final List<String> terms = Collections.emptyList();
			final List<Category> search = sqlSession.getMapper(PartsMapper.class).search(terms);
			
			return new WriterRepresentation(MediaType.APPLICATION_JSON) {
				@Override
				public void write(Writer w) throws IOException {
					final JsonGenerator g = application.getJsonFactory().createJsonGenerator(w);
					g.writeStartObject();
					g.writeBooleanField("success", true);
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
		final Form form = new Form(entity);
		final String dk = form.getFirstValue("dk");
		if (dk != null) {
			try {
				final DigiKeyClient client = new DigiKeyClient();
				final List<Attribute> attributes = client.parse(dk);
				
				attributes.add(new Attribute("Minimum Stock", "0"));
				final Attribute dkAttr = new Attribute("URL", "URL");
				dkAttr.setHref(dk);
				attributes.add(dkAttr);
				if (attributes.size() > 0) {
					String quantityInStock = form.getFirstValue("qty","0");
					if (StringUtils.isBlank(quantityInStock)) quantityInStock = "0";
					attributes.add(new Attribute("Quantity In Stock", quantityInStock));
					
					final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
					try {
						final PartsMapper mapper = sqlSession.getMapper(PartsMapper.class);
						short partId = mapper.newPartId();
						for (Attribute attribute : attributes) {
							if (attribute.getValue().length() > 255) attribute.setValue(attribute.getValue().substring(0, 255));	//Prevent DB field overflow
							attribute.setPartId(partId);
							mapper.insert(attribute);
						}
						sqlSession.commit();
						
					} finally {
						sqlSession.close();
					}
				}
				redirectSeeOther("index.html");
				return new EmptyRepresentation();
			} catch (Exception e) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
			}
		}
		
		final ArrayList<String> terms = new ArrayList<String>();
		final String keywords = form.getFirstValue("keywords");
		if (keywords != null) {
			for (String term : Arrays.asList(keywords.split("\\s+"))) {
				terms.add(StringEscapeUtils.escapeSql(term.toLowerCase()));
			}
		}
		final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
		try {
			final PartsMapper mapper = sqlSession.getMapper(PartsMapper.class);
			getResponseAttributes().put("categories", mapper.search(terms));
			final Template template = application.getFmConfig().getTemplate("index.ftl");
			template.setOutputEncoding("UTF-8");
			return new TemplateRepresentation(template, getResponseAttributes(), MediaType.TEXT_HTML);
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			sqlSession.close();
		}
	}
}
