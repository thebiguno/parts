package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
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
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Part;
import freemarker.template.Template;


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
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlFactory().openSession();
		try {
			final PartsMapper mapper = sqlSession.getMapper(PartsMapper.class);
			final String part = (String) getRequestAttributes().get("part");
			final short partId;
			if ("new".equals(part)) {
				partId = mapper.newPartId();
			} else {
				partId = Short.parseShort(part);
			}

			final ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			final Form form = new Form(entity);
			final String[] names = form.getValuesArray("name");
			final String[] values = form.getValuesArray("value");
			final String[] hrefs = form.getValuesArray("href");
			
			boolean category = false;
			boolean family = false;
			boolean mpn = false;
			
			for (short i = 0; i < names.length; i++) {
				final Attribute attribute = new Attribute();
				attribute.setPartId(partId);
				attribute.setName(names[i]);
				attribute.setValue(values[i]);
				if (hrefs[i].trim().length() > 0) attribute.setHref(hrefs[i]);
				attribute.setSort(i);
				attributes.add(attribute);
				
				// required attributes
				if ("Category".equals(names[i])) {
					if (values[i].trim().length() == 0) {
						attribute.setValue("Undefined");
					}
					category = true;
				}
				if ("Family".equals(names[i])) {
					if (values[i].trim().length() == 0) {
						attribute.setValue("Undefined");
					}
					family = true;
				}
				if ("Manufacturer Part Number".equals(names[i])) {
					if (values[i].trim().length() == 0) {
						attribute.setValue("Undefined");
					}
					mpn = true;
				}
			}
			
			if (attributes.size() > 0) {
				if (!category) {
					final Attribute attribute = new Attribute("Category", "Undefined");
					attribute.setPartId(partId);
					attributes.add(attribute);
				}
				if (!family) {
					final Attribute attribute = new Attribute("Family", "Undefined");
					attribute.setPartId(partId);
					attributes.add(attribute);
				}
				if (!mpn) {
					final Attribute attribute = new Attribute("Manufacturer Part Number", "Undefined");
					attribute.setPartId(partId);
					attributes.add(attribute);
				}
			}
			
			mapper.remove(partId);
			for (Attribute attribute : attributes) {
				mapper.insert(attribute);
			}
			
			sqlSession.commit();
			
			redirectSeeOther("" + partId);
			return new EmptyRepresentation();
		} finally {
			sqlSession.close();
		}	
	}
	
	@Override
	protected Representation put(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlFactory().openSession();
		try {
			final Form form = new Form(entity);
			final int partId = Integer.parseInt((String) getRequestAttributes().get("part"));
			final PartsMapper mapper = sqlSession.getMapper(PartsMapper.class);
			final int delta = form.getFirstValue("minus.x") != null ? -1 : 1;
			final List<Attribute> attributesByPart = mapper.attributesByPart(partId);
			final Part part = new Part();
			part.setId(partId);
			part.setAttributes(attributesByPart);
			final Attribute quantity = part.findAttribute("Quantity In Stock");
			final Attribute category = part.findAttribute("Category");
			final Attribute family = part.findAttribute("Family");
			final Short q = (short) (Short.parseShort(quantity.getValue()) + delta);
			if (q >= 0) {
				mapper.setQuantity(partId, Short.toString(q));
				sqlSession.commit();
			}
			
			redirectSeeOther("../parts/" + category.getValue() + "/" + family.getValue());
			return new EmptyRepresentation();
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlFactory().openSession();
		try {
			final String part = (String) getRequestAttributes().get("part");
			final short partId = Short.parseShort(part);
			final PartsMapper mapper = sqlSession.getMapper(PartsMapper.class);
			mapper.remove(partId);
			sqlSession.commit();
			
			redirectSeeOther("../index.html");
			return new EmptyRepresentation();
		} finally {
			sqlSession.close();
		}
	}
	
}
