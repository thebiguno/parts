package ca.digitalcave.parts;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Locale;
import java.util.Properties;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.engine.application.Encoder;
import org.restlet.resource.Directory;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.service.StatusService;

import ca.digitalcave.parts.resource.FamilyResource;
import ca.digitalcave.parts.resource.IndexResource;
import ca.digitalcave.parts.resource.PartResource;
import ca.digitalcave.parts.security.PartsVerifier;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

public class PartsApplication extends Application {

	private final Properties properties = new Properties();
	private final Configuration fmConfig = new Configuration();
	private EmbeddedDataSource dataSource;
	private SqlSessionFactory sqlSessionFactory;
	
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
		final String databaseName = (String) servletContext.getClass().getMethod("getRealPath", String.class).invoke(servletContext, "WEB-INF/db");

		// set up derby
		dataSource = new EmbeddedDataSource();
		dataSource.setDatabaseName(databaseName);
		dataSource.setCreateDatabase("create");
		
		// set up mybatis
		final Environment environment = new Environment("prod", new JdbcTransactionFactory(), dataSource);
		final org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration(environment);
		config.addMappers("ca.digitalcave.parts");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
		
		// set up freemarker
		fmConfig.setServletContextForTemplateLoading(servletContext, "WEB-INF/ftl/");
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
		dataSource.setShutdownDatabase("shutdown");
		dataSource.getConnection().close();
		dataSource = null;
		
		sqlSessionFactory = null;
		
		super.stop();
	}
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
	
	public Configuration getFmConfig() {
		return fmConfig;
	}

	public Properties getProperties() {
		return properties;
	}

	@Override  
	public Restlet createRoot() {

		final Router privateRouter = new Router(getContext());
		privateRouter.attach("/index", IndexResource.class);
		privateRouter.attach("/parts/{category}/{family}", FamilyResource.class);
		privateRouter.attach("/parts/{part}", PartResource.class);
		
		final ChallengeAuthenticator authenticator = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "Parts");
		authenticator.setVerifier(new PartsVerifier(this));
		authenticator.setNext(privateRouter);

		final Router publicRouter = new Router(getContext());
		privateRouter.attach("", new Redirector(getContext(), "index.html", Redirector.MODE_CLIENT_TEMPORARY));
		privateRouter.attach("/", new Redirector(getContext(), "index.html", Redirector.MODE_CLIENT_TEMPORARY));
		publicRouter.attach("/media", new Directory(getContext(), "war:///media"));
		publicRouter.attachDefault(authenticator);

		final Encoder encoder = new Encoder(getContext());
		encoder.setNext(publicRouter);

		return encoder;
	}
}
