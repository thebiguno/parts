package ca.digitalcave.parts.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
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
import ca.digitalcave.parts.digi.DigiKeyClient;
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Category;
import ca.digitalcave.parts.model.Family;
import ca.digitalcave.parts.model.Part;
import freemarker.template.Template;

public class DigikeyResource extends ServerResource {

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Form form = new Form(entity);
		final String dk = form.getFirstValue("dk");
		if (dk != null) {
			try {
				final DigiKeyClient client = new DigiKeyClient();
				final List<Attribute> attributes = client.parse(dk);

				final Attribute dkAttr = new Attribute("URL", "URL");
				dkAttr.setHref(dk);
				attributes.add(dkAttr);
				if (attributes.size() > 0) {
					final SqlSession sqlSession = application.getSqlFactory().openSession();
					try {
						final PartsMapper mapper = sqlSession.getMapper(PartsMapper.class);
						final Attribute catAttr = Attribute.remove("Category", attributes);
						Category category = mapper.selectCategory(catAttr.getValue());
						if (category == null) {
							category = new Category();
							category.setName(catAttr.getName());
							mapper.insertCategory(category);
						} 
						final Attribute famAttr = Attribute.remove("Family", attributes);
						Family family = mapper.selectFamily(category, famAttr.getValue());
						if (family == null) {
							family = new Family();
							family.setCategory(category);
							family.setName(famAttr.getValue());
							mapper.insertFamily(family);
						}
						final Part part = new Part();
						part.setFamily(family);
						final Attribute mpnAttr = Attribute.remove("Manufacturer Part Number", attributes);
						final Attribute descAttr = Attribute.remove("Description", attributes);
						final Attribute notesAttr = Attribute.remove("Notes", attributes);
						part.setNumber(mpnAttr.getValue());
						part.setDescription(descAttr.getValue());
						part.setNotes(notesAttr.getValue());
						part.setAvailable(Integer.parseInt(form.getFirstValue("qty", "0")));
						part.setMinimum(Integer.parseInt(form.getFirstValue("qty", "0")));

						for (Attribute attribute : attributes) {
							if (attribute.getValue().length() > 255) attribute.setValue(attribute.getValue().substring(0, 255));	//Prevent DB field overflow
							attribute.setPart(part);
							mapper.insertAttribute(attribute);
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
		final SqlSession sqlSession = application.getSqlFactory().openSession();
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
	}}