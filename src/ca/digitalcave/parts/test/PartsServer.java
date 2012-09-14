package ca.digitalcave.parts.test;

import java.io.File;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class PartsServer {

	public static void main(String[] args) throws Exception {
		final String webapp = "WebContent";
		final String contextPath = "/parts";
		
		final Server server = new Server();
		
		final SelectChannelConnector c1 = new SelectChannelConnector();
		c1.setPort(8686);
		server.addConnector(c1);
		
		final URL warUrl = new File(webapp).toURI().toURL(); 
		final WebAppContext context = new WebAppContext(warUrl.toExternalForm(), contextPath);
		context.setClassLoader(PartsServer.class.getClassLoader());

		server.setHandler(context);
		server.start();
		server.join();
	}
}
