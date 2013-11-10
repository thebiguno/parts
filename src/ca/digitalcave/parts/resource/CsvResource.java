package ca.digitalcave.parts.resource;

import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.logging.Level;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.ibatis.session.SqlSession;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Attribute;

public class CsvResource extends ServerResource {

	@Override
	protected Representation post(Representation entity) throws ResourceException {
		final PartsApplication application = (PartsApplication) getApplication();
		final Account account = (Account) getClientInfo().getUser();
		
		LinkedList<Attribute> attributes = new LinkedList<Attribute>();
		String lastId = null;
		
		final SqlSession sql = application.getSqlFactory().openSession(false);
		try {
			final FileItemIterator i = new RestletFileUpload().getItemIterator(entity);
			while (i.hasNext()) {
				final FileItemStream item = i.next();
				final CSVParser p = new CSVParser(new InputStreamReader(item.openStream()), CSVFormat.DEFAULT);
				for (CSVRecord r : p) {
					final String id = r.get(0);
					
					if (id.equals(lastId) == false && attributes.size() > 0) {
						DigikeyResource.insertPart(sql, attributes, account.getId());
						attributes.clear();
					}
					
					String name = r.get(1);
					String value = r.get(2);
					String href = r.get(3);
					if ("null".equalsIgnoreCase(name)) name = null;
					if ("null".equalsIgnoreCase(value)) value = null;
					if ("null".equalsIgnoreCase(href)) href = null;
					attributes.add(new Attribute(name, value, href));
					lastId = id;
				}
				p.close();
			}
			sql.commit();
			return new ExtResponseRepresentation();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, null, e);
			return new ExtResponseRepresentation(e.getMessage());
		} finally {
			sql.close();
		}
	}
}
