package ca.digitalcave.parts.resource.mobile;

import java.net.URLDecoder;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Part;


public class FamilyResourceMobile extends ServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final SqlSession sqlSession = application.getSqlSessionFactory().openSession();
		try {
			final String category = URLDecoder.decode((String) getRequestAttributes().get("category"), "UTF-8");
			final String family = URLDecoder.decode((String) getRequestAttributes().get("family"), "UTF-8");
			
			List<Part> parts = sqlSession.getMapper(PartsMapper.class).partsByFamily(category, family);
			
			final JSONObject result = new JSONObject();
			for (Part part : parts) {
				final Attribute partNumber = part.findAttribute("Manufacturer Part Number");
				final Attribute description = part.findAttribute("Description");
				final Attribute notes = part.findAttribute("Notes");
				JSONObject partObj = new JSONObject();
				partObj.put("part", partNumber != null ? partNumber.getValue() : null);
				partObj.put("description", description != null ? description.getValue() : null);
				partObj.put("notes", notes != null ? notes.getValue() : null);
				final StringBuilder datasheets = new StringBuilder();
				for (Attribute attribute : part.findAttributes("Datasheet")) {
					datasheets.append("<a href='").append(attribute.getHref()).append("' target='_blank'>").append(attribute.getValue()).append("</a><br/>");
				}
				partObj.put("datasheets", datasheets.toString());
				result.append("data", partObj);
			}

			return new JsonRepresentation(result);
		} catch (Exception e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		} finally {
			sqlSession.close();
		}
	}
}
