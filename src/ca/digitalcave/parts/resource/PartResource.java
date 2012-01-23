package ca.digitalcave.parts.resource;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
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
			final int partId = Integer.parseInt((String) getRequestAttributes().get("part"));
			final List<Attribute> attributes = sqlSession.getMapper(PartsMapper.class).attributesByPart(partId);
			getResponseAttributes().put("attributes", attributes);
			for (Attribute attribute : attributes) {
				if ("Manufacturer Part Number".equals(attribute.getName())) {
					getResponseAttributes().put("title", attribute.getValue());
					break;
				}
			}
			final Template template = application.getFmConfig().getTemplate("part.ftl");
			template.setOutputEncoding("UTF-8");
			return new TemplateRepresentation(template, getResponseAttributes(), MediaType.TEXT_HTML);
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			sqlSession.close();
		}
	}
	
	// TODO
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
		try {
			final int partId = Integer.parseInt((String) getRequestAttributes().get("part"));
			
//			final Form form = new Form(entity);
//			final String name = form.getFirstValue("name");
//			final String action = form.getFirstValue("action");
//			final String newValue = form.getFirstValue("newvalue");
//			
//			if ("delete".equals(action)) {
//				node.setProperty(name, (Value) null);
//			} else if ("update_name".equals(action)) {
//				final Value value = node.getProperty(name).getValue();
//				node.setProperty(newValue, value);
//				node.setProperty(name, (Value) null);
//			} else if ("update_value".equals(action)) {
//				node.setProperty(name, newValue);
//			}
			
			return new EmptyRepresentation();
		} finally {
			sqlSession.close();
		}	
	}
	
}
