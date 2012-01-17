package ca.digitalcave.parts;

import java.util.Locale;
import java.util.Properties;

import javax.jcr.Repository;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.application.Encoder;
import org.restlet.resource.Directory;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.service.StatusService;

import ca.digitalcave.parts.resource.FamilyResource;
import ca.digitalcave.parts.resource.PartResource;
import ca.digitalcave.parts.resource.SearchResource;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

public class PartsApplication extends Application {

	private final Properties properties = new Properties();
	private final Configuration fmConfig = new Configuration();
	private RepositoryImpl repository;
	
	public PartsApplication() {
		setStatusService(new StatusService());

		getMetadataService().setDefaultCharacterSet(CharacterSet.UTF_8);
		getMetadataService().setDefaultLanguage(Language.ENGLISH);
		getMetadataService().addExtension("html", MediaType.TEXT_HTML);
		getTunnelService().setEnabled(true);
		getTunnelService().setExtensionsTunnel(true);
		getTunnelService().setMethodTunnel(true);
		getTunnelService().setMethodParameter("method");
	}
	
	public synchronized void start() throws Exception {
		// called using reflection to avoid adding servlet api to lib
		final Object servletContext = getContext().getServerDispatcher().getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		final String repositoryPath = (String) servletContext.getClass().getMethod("getRealPath", String.class).invoke(servletContext, "WEB-INF/jackrabbit");

		// static binding to jackrabbit
		final RepositoryConfig config = RepositoryConfig.create(
				Context.getCurrent().getClientDispatcher().handle(new Request(Method.GET, "war:///WEB-INF/jackrabbit/repository.xml")).getEntity().getStream(),
				repositoryPath);
		repository = RepositoryImpl.create(config);
		
		fmConfig.setServletContextForTemplateLoading(servletContext, "WEB-INF/ftl/");
		fmConfig.setDefaultEncoding("UTF-8");
		fmConfig.setLocalizedLookup(true);
		fmConfig.setLocale(Locale.ENGLISH);
		fmConfig.setTemplateUpdateDelay(0);
		fmConfig.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		fmConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		
		super.start();
	}
	
	public synchronized void stop() throws Exception {
		repository.shutdown();
		repository = null;
		
		super.stop();
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	public Configuration getFmConfig() {
		return fmConfig;
	}

	public Properties getProperties() {
		return properties;
	}

	@Override  
	public Restlet createRoot() {
		final Router publicRouter = new Router(getContext());
		
		publicRouter.attach("", new Redirector(getContext(), "/", Redirector.MODE_CLIENT_TEMPORARY));
//		publicRouter.attach("/", IndexResource.class);
		publicRouter.attach("/search", SearchResource.class);
		publicRouter.attach("/parts/{category}/{family}", FamilyResource.class);
		publicRouter.attach("/parts/{category}/{family}/{part}", PartResource.class);
		publicRouter.attach("/media", new Directory(getContext(), "war:///media"));

		final Encoder encoder = new Encoder(getContext());
		encoder.setNext(publicRouter);
		return encoder;
	}
}
