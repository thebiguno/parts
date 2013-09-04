package ca.digitalcave.parts;

import java.sql.Connection;
import java.sql.Statement;
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
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.engine.application.Encoder;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.security.Authenticator;
import org.restlet.service.StatusService;

import ca.digitalcave.parts.resource.DefaultResource;
import ca.digitalcave.parts.resource.mobile.CatalogResource;
import ca.digitalcave.parts.resource.mobile.FamilyResource;

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
		getTunnelService().setEnabled(true);
		getTunnelService().setExtensionsTunnel(true);
		getTunnelService().setMethodTunnel(true);
		getTunnelService().setMethodParameter("method");
	}
	
	public synchronized void start() throws Exception {
		// called using reflection to avoid adding servlet api to lib
		final Object servletContext = getContext().getServerDispatcher().getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
//		final String databaseName = (String) servletContext.getClass().getMethod("getRealPath", String.class).invoke(servletContext, "WEB-INF/db");

		// set up derby
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
		
		final Connection c = dataSource.getConnection();
		try {
			final DatabaseConnection dbc = new JdbcConnection(c);
			final ClassLoaderResourceAccessor ra = new ClassLoaderResourceAccessor(getClass().getClassLoader());
			final Liquibase l = new Liquibase("/recipe/data/migrate.sql", ra, dbc);
			l.update("all");
		} finally {
			c.close();
		}
		
		// set up freemarker
		fmConfig.setServletContextForTemplateLoading(servletContext, "/");
		fmConfig.setDefaultEncoding("UTF-8");
		fmConfig.setLocalizedLookup(true);
		fmConfig.setLocale(Locale.ENGLISH);
		fmConfig.setTemplateUpdateDelay(0);
		fmConfig.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		fmConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		
		// install the schema
		final Connection connection = dataSource.getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
//			statement.execute("delete from attribute");
			statement.execute("create table attribute (part_id smallint, name varchar(255), value varchar(255), href varchar(255), sort smallint)");
		} catch (Exception e) {
			; 
		} finally {
			if (statement != null) statement.close();
			connection.close();
		}
		
		super.start();
	}
	
	public synchronized void stop() throws Exception {
		// shutdown derby
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

		final Router dataRouter = new Router(getContext());
		dataRouter.attach("/", CatalogResource.class);
		dataRouter.attach("/{category}/{family}", FamilyResource.class);
//		datamRouter.attach("/{part}", PartResource.class);
		
//		final ChallengeAuthenticator authenticator = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "Parts");
//		authenticator.setVerifier(new PartsVerifier(this));
//		authenticator.setNext(privateRouter);

		final Router publicRouter = new Router(getContext());
//		privateRouter.attach("", new Redirector(getContext(), "index.html", Redirector.MODE_CLIENT_TEMPORARY));
//		privateRouter.attach("/", new Redirector(getContext(), "index.html", Redirector.MODE_CLIENT_TEMPORARY));
		publicRouter.attach("/data", dataRouter);
		publicRouter.attach("/datasheets", new Directory(getContext(), "war:///datasheets"));
		
		publicRouter.attachDefault(DefaultResource.class).setMatchingMode(Template.MODE_STARTS_WITH);

		final Encoder encoder = new Encoder(getContext(), false, true, getEncoderService());
		encoder.setNext(publicRouter);

		return encoder;
	}
}
