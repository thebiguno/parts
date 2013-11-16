package ca.digitalcave.parts;

import java.security.Key;
import java.sql.Blob;
import java.sql.Connection;
import java.util.Locale;
import java.util.Properties;

import javax.crypto.spec.PBEKeySpec;

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
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.engine.application.Encoder;
import org.restlet.resource.ClientResource;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.service.StatusService;

import ca.digitalcave.moss.crypto.Crypto;
import ca.digitalcave.moss.crypto.Crypto.Algorithm;
import ca.digitalcave.moss.crypto.Crypto.CryptoException;
import ca.digitalcave.moss.restlet.CookieAuthenticator;
import ca.digitalcave.parts.data.BlobTypeHandler;
import ca.digitalcave.parts.resource.AttributeResource;
import ca.digitalcave.parts.resource.AttributesResource;
import ca.digitalcave.parts.resource.CategoriesResource;
import ca.digitalcave.parts.resource.CsvResource;
import ca.digitalcave.parts.resource.DefaultResource;
import ca.digitalcave.parts.resource.DigikeyResource;
import ca.digitalcave.parts.resource.IndexResource;
import ca.digitalcave.parts.resource.PartResource;
import ca.digitalcave.parts.resource.PartsResource;
import ca.digitalcave.parts.security.PartsVerifier;

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
	private final PartsVerifier verifier = new PartsVerifier();
	
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
		dataSource.setMinPoolSize(1);
		dataSource.setInitialPoolSize(1);
		dataSource.setMaxPoolSize(10);
		
		// set up mybatis
		final Environment environment = new Environment("prod", new JdbcTransactionFactory(), dataSource);
		final org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration(environment);
		config.getTypeHandlerRegistry().register(Blob.class, new BlobTypeHandler());
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
	
	public PartsVerifier getVerifier() {
		return verifier;
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
		try {
			final byte[] salt = {14, -43, -91, -67, 85, 55, 44, -115};

			final Key key = Crypto.recoverKey(Algorithm.AES_128, new PBEKeySpec("password".toCharArray(), salt, 8, 128));
			
			final Router categoriesRouter = new Router(getContext());
			categoriesRouter.attach("", CategoriesResource.class);
			categoriesRouter.attach("/{category}", CategoriesResource.class);
			categoriesRouter.attach("/{category}/parts", PartsResource.class);
			categoriesRouter.attach("/{category}/parts/{part}", PartResource.class);
			categoriesRouter.attach("/{category}/parts/{part}/attributes", AttributesResource.class);
			categoriesRouter.attach("/{category}/parts/{part}/attributes/{attribute}", AttributeResource.class);
			
			final CookieAuthenticator categegoryAuth = new CookieAuthenticator(getContext(), false, key);
			categegoryAuth.setAllowRemember(true);
			categegoryAuth.setVerifier(getVerifier());
			categegoryAuth.setNext(categoriesRouter);
			
			final Router importRouter = new Router(getContext());
			importRouter.attach("/digikey", DigikeyResource.class);
			importRouter.attach("/csv", CsvResource.class);
	
			final CookieAuthenticator importAuth = new CookieAuthenticator(getContext(), false, key);
			importAuth.setAllowRemember(true);
			importAuth.setVerifier(getVerifier());
			importAuth.setNext(importRouter);
			
			final Router publicRouter = new Router(getContext());
			publicRouter.attach("", new Redirector(getContext(), "index.html", Redirector.MODE_CLIENT_TEMPORARY));
			publicRouter.attach("/", new Redirector(getContext(), "index.html", Redirector.MODE_CLIENT_TEMPORARY));
			publicRouter.attach("/index", IndexResource.class);
			publicRouter.attach("/categories", categegoryAuth);
			publicRouter.attach("/import", importAuth);
			publicRouter.attachDefault(DefaultResource.class).setMatchingMode(Template.MODE_STARTS_WITH);
	
			final CookieAuthenticator optionalAuth = new CookieAuthenticator(getContext(), true, key);
			optionalAuth.setAllowRemember(true);
			optionalAuth.setVerifier(getVerifier());
			optionalAuth.setNext(publicRouter);
	
			final Encoder encoder = new Encoder(getContext(), false, true, getEncoderService());
			encoder.setNext(optionalAuth);
	
			return encoder;
		} catch (CryptoException e) {
			throw new RuntimeException(e);
		}
	}
}
