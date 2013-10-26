package ca.digitalcave.parts.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.logging.Level;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Attribute;

public class AttributeResource extends ServerResource {

	@Override
	protected Representation get() throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = new Account(0); // TODO implement auth 

		final long attribute = Long.parseLong((String) getRequestAttributes().get("attribute"));
		
		final SqlSession sql = application.getSqlFactory().openSession(false);
		try {
			final Attribute attr = sql.getMapper(PartsMapper.class).selectAttribute(account.getId(), attribute);
			final Representation result = new OutputRepresentation(new MediaType(attr.getMimeType())) {
				@Override
				public void write(OutputStream os) throws IOException {
					try {
						Streams.copy(attr.getData().getBinaryStream(), os, true);
						sql.commit();
						sql.close();
					} catch (SQLException e) {
						throw new IOException(e);
					}
				}
			};
			result.setDisposition(new Disposition(Disposition.TYPE_INLINE));
			result.getDisposition().setFilename(attr.getValue());
			result.getDisposition().setSize(attr.getData().length());
			result.getDisposition().setCreationDate(attr.getCreatedAt());
			result.getDisposition().setModificationDate(attr.getModifiedAt());
			return result;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}
	
	@Override
	protected Representation post(Representation entity) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = new Account(0); // TODO implement auth 

		final long attribute = Long.parseLong((String) getRequestAttributes().get("attribute"));
		
		final SqlSession sql = application.getSqlFactory().openSession(false);
		try {
			Attribute attr = sql.getMapper(PartsMapper.class).selectAttribute(account.getId(), attribute);
			attr.setId(attribute);
			final FileItemIterator i = new RestletFileUpload().getItemIterator(entity);
			while (i.hasNext()) {
				final FileItemStream item = i.next();
				final String name = item.getFieldName();
				if (item.isFormField() && "url".equals(name)) {
					attr.setHref(Streams.asString(item.openStream()));
					if (attr.getHref().trim().length() == 0) attr.setHref(null);
				} else if ("file".equals(name)) {
					if (item.getName() == null || item.getName().trim().length() == 0) {
						attr.setValue(item.getName());
						attr.setData(null);
						attr.setMimeType(null);
					} else {
						attr.setMimeType(item.getContentType());
					}
					
					sql.getMapper(PartsMapper.class).updateAttribute(account.getId(), attr);
					sql.commit();
					
					attr = sql.getMapper(PartsMapper.class).selectAttribute(account.getId(), attribute);
					if (attr.getData() != null) {
						attr.getData().truncate(1);
						Streams.copy(item.openStream(), attr.getData().setBinaryStream(1), true);
					}
					sql.commit();
				}
			}
			return new ExtResponseRepresentation();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());
		} finally {
			sql.close();
		}
	}
	
	
	@Override
	protected Representation put(Representation entity) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = new Account(0); // TODO implement auth 
		
		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final JSONObject object = new JSONObject(entity.getText());
			final Attribute attr = new Attribute();
			attr.setId(Long.parseLong((String) getRequestAttributes().get("attribute")));
			attr.setName(object.optString("name", ""));
			attr.setValue(object.optString("value",""));
			sql.getMapper(PartsMapper.class).updateAttribute(account.getId(), attr);
			return new ExtResponseRepresentation();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());

		} finally {
			sql.close();
		}
	}
	
	@Override
	protected Representation delete() throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = new Account(0); // TODO implement auth 

		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final String attr = (String) getRequestAttributes().get("attribute");
			final long partId = Long.parseLong(attr);
			sql.getMapper(PartsMapper.class).deletePart(account.getId(), partId);
			return new ExtResponseRepresentation();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());
		} finally {
			sql.close();
		}
	}}
