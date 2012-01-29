package ca.digitalcave.parts.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Attribute;
import freemarker.template.Template;


public class PartResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
		try {
			final List<Attribute> attributes;
			final String part = (String) getRequestAttributes().get("part");
			if ("new".equals(part)) {
				attributes = new ArrayList<Attribute>();
				attributes.add(new Attribute("Category", ""));
				attributes.add(new Attribute("Family", ""));
				attributes.add(new Attribute("Manufacturer", ""));
				attributes.add(new Attribute("Manufacturer Part Number", ""));
				attributes.add(new Attribute("Description", ""));
				attributes.add(new Attribute("Quantity In Stock", "0"));
				getResponseAttributes().put("title", "New Part");
			} else {
				final short partId = Short.parseShort(part);
				attributes = sqlSession.getMapper(PartsMapper.class).attributesByPart(partId);
				for (Attribute attribute : attributes) {
					if ("Manufacturer Part Number".equals(attribute.getName())) {
						getResponseAttributes().put("title", attribute.getValue());
						break;
					}
				}
			}
			getResponseAttributes().put("part", part);
			getResponseAttributes().put("attributes", attributes);
			final Template template = application.getFmConfig().getTemplate("part.ftl");
			template.setOutputEncoding("UTF-8");
			return new TemplateRepresentation(template, getResponseAttributes(), MediaType.TEXT_HTML);
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
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
	
}
