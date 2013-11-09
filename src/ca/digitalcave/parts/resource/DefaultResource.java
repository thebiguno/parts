package ca.digitalcave.parts.resource;

import ca.digitalcave.moss.restlet.AbstractFreemarkerResource;
import ca.digitalcave.parts.PartsApplication;
import freemarker.template.Configuration;

public class DefaultResource extends AbstractFreemarkerResource {

	@Override
	protected Configuration getFreemarkerConfig() {
		return ((PartsApplication) getApplication()).getFmConfig();
	}
}
