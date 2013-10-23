package ca.digitalcave.parts;

import java.sql.Connection;
import java.util.Locale;
import java.util.Properties;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.codehaus.jackson.JsonFactory;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.engine.application.Encoder;
import org.restlet.resource.ClientResource;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.routing.Variable;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.service.StatusService;

import ca.digitalcave.parts.resource.CategoryResource;
import ca.digitalcave.parts.resource.DefaultResource;
import ca.digitalcave.parts.resource.IndexResource;
import ca.digitalcave.parts.resource.PartResource;
import ca.digitalcave.parts.resource.PartsResource;
import ca.digitalcave.parts.security.CookieVerifier;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

public class PartsApplication extends Application {

	private final Properties properties = new Properties();
	private final JsonFactory jsonFactory = new JsonFactory();
	private final Configuration fmConfig = new Configuration();
	private ComboPooledDataSource dataSource;
	private SqlSessionFactory sqlFactory;
	
	public PartsApplication() {
		setStatusService(new StatusService());

		getMetadataService().setDefaultCharacterSet(CharacterSet.UTF_8);
		getMetadataService().setDefaultLanguage(Language.ENGLISH);
		getMetadataService().addExtension("html", MediaType.TEXT_HTML);
		getMetadataService().addExtension("json", MediaType.APPLICATION_JAVASCRIPT);
		getTunnelService().setEnabled(true);
		getTunnelService().setExtensionsTunnel(true);
		getTunnelService().setMethodTunnel(true);
		getTunnelService().setMethodParameter("method");
	}
	
	public synchronized void start() throws Exception {
		final ClientResource propertiesResource = new ClientResource("war:///WEB-INF/config.properties");
		properties.load(propertiesResource.get().getReader());
		
		// set up database
		dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(properties.getProperty("jdbc.driver"));
		dataSource.setJdbcUrl(properties.getProperty("jdbc.url"));
		dataSource.setUser(properties.getProperty("jdbc.user"));
		dataSource.setPassword(properties.getProperty("jdbc.password"));
		
		// set up mybatis
		final Environment environment = new Environment("prod", new JdbcTransactionFactory(), dataSource);
		final org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration(environment);
		config.addMappers("ca.digitalcave.parts.data");
		sqlFactory = new SqlSessionFactoryBuilder().build(config);
		
		// schema migration
		final Connection c = dataSource.getConnection();
		try {
			final DatabaseConnection dbc = new JdbcConnection(c);
			final ClassLoaderResourceAccessor ra = new ClassLoaderResourceAccessor(getClass().getClassLoader());
			final Liquibase l = new Liquibase("ca/digitalcave/parts/data/migrate.sql", ra, dbc);
			l.update("all");
		} finally {
			c.close();
		}
		
		// set up freemarker
		final Object servletContext = getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		fmConfig.setServletContextForTemplateLoading(servletContext, "/");
		fmConfig.setDefaultEncoding("UTF-8");
		fmConfig.setLocalizedLookup(true);
		fmConfig.setLocale(Locale.ENGLISH);
		fmConfig.setTemplateUpdateDelay(0);
		fmConfig.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		fmConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		
		super.start();
	}
	
	public synchronized void stop() throws Exception {
		dataSource.close();
		dataSource = null;
		sqlFactory = null;
		
		super.stop();
	}
	
	public JsonFactory getJsonFactory() {
		return jsonFactory;
	}
	
	public SqlSessionFactory getSqlFactory() {
		return sqlFactory;
	}
	
	public Configuration getFmConfig() {
		return fmConfig;
	}

	public Properties getConfig() {
		return properties;
	}

	@Override  
	public Restlet createInboundRoot() {

		final Router catalogRouter = new Router(getContext());
		catalogRouter.attach("/categories", CategoryResource.class);
		catalogRouter.attach("/categories/{category}", CategoryResource.class).getTemplate().getVariables().put("category", new Variable(Variable.TYPE_DIGIT));
		catalogRouter.attach("/parts", PartsResource.class);
		catalogRouter.attach("/parts/{part}", PartResource.class);
//		catalogRouter.attach("/attachment{id}", AttachmentResource.class);
		
		final ChallengeAuthenticator authenticator = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "Parts");
		authenticator.setVerifier(new CookieVerifier(this));
		authenticator.setNext(catalogRouter);

		final Router publicRouter = new Router(getContext());
		publicRouter.attach("", new Redirector(getContext(), "index.html", Redirector.MODE_CLIENT_TEMPORARY));
		publicRouter.attach("/", new Redirector(getContext(), "index.html", Redirector.MODE_CLIENT_TEMPORARY));
		publicRouter.attach("/index", IndexResource.class);
		publicRouter.attach("/catalog", catalogRouter);
		
		publicRouter.attachDefault(DefaultResource.class).setMatchingMode(Template.MODE_STARTS_WITH);

		final Encoder encoder = new Encoder(getContext(), false, true, getEncoderService());
		encoder.setNext(publicRouter);

		return encoder;
	}
}
