package ca.digitalcave.parts.security;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.security.User;
import org.restlet.security.Verifier;

import aspekt.AspektApplication;
import aspekt.db.maps.AccountMapper;
import aspekt.util.LocalizationUtil;

public class PartsVerifier implements Verifier {

	private AspektApplication aspekt;
	
	public PartsVerifier(AspektApplication aspekt) {
		this.aspekt = aspekt;
	}
	
	@Override
	public int verify(Request request, Response response) {
		final Cookie l = request.getCookies().getFirst("logout");
		if (l != null) {
			// remove logout token
			final CookieSetting s = new CookieSetting("logout", "1");
			s.setPath("/");
			s.setMaxAge(0); // discard immediately
			response.getCookieSettings().add(s);
			
			return RESULT_STALE;
		}
		
		if (request.getChallengeResponse() == null) {
			return RESULT_MISSING;
		} else if (request.getChallengeResponse().getScheme().equals(ChallengeScheme.HTTP_BASIC)) {
			if (check(request)) {
				return RESULT_VALID;
			} else {
				return RESULT_INVALID;
			}
		} else {
			return RESULT_UNSUPPORTED;
		}
	}
	
	public boolean check(Request request) {
		final String ldapUrl = aspekt.getProperties().getProperty("ldap.url");
		final String jaasModule = aspekt.getProperties().getProperty("jaas.module");

		if (ldapUrl != null) {
			return checkLdap(request);
		} else if (jaasModule != null) {
			return checkJaas(request);
		} else {
			return checkDb(request);
		}
	}

	private boolean checkJaas(Request request) {
		final String identifier = request.getChallengeResponse().getIdentifier();
		final char[] secret = request.getChallengeResponse().getSecret();

		final String module = aspekt.getProperties().getProperty("jaas.module");
		final String principalClassName = aspekt.getProperties().getProperty("jaas.principal", "com.sun.security.auth.UserPrincipal");

		try {
			final LoginContext loginContext = new LoginContext(module, new CallbackHandler() {
				@Override
				public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
					for (int i = 0; i < callbacks.length; i++) {
						if (callbacks[i] instanceof NameCallback) {
							((NameCallback) callbacks[i]).setName(identifier);
						} else if (callbacks[i] instanceof PasswordCallback) {
							((PasswordCallback) callbacks[i]).setPassword(secret);
						}
					}
				}
			});
			loginContext.login();
			
			for (Principal p : loginContext.getSubject().getPrincipals()) {
				if (p.getClass().getName().equals(principalClassName)) {
					final String uid = p.getName();
					
					insertUser(identifier);
					findUser(uid, request);
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean checkLdap(Request request) {
		final String identifier = request.getChallengeResponse().getIdentifier();
		final char[] secret = request.getChallengeResponse().getSecret();

		final String url = aspekt.getProperties().getProperty("ldap.url");
		final String mode = aspekt.getProperties().getProperty("ldap.mode", "search");
		final Hashtable<String, Object> env = new Hashtable<String, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		if ("bind+search".equals(mode)) {
			// bind as the configured dn
			final String bind = aspekt.getProperties().getProperty("ldap.bind");
			final String[] bindParts = bind.split(":");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, bindParts[0]);
			env.put(Context.SECURITY_CREDENTIALS, bindParts[1]);
		} else if ("bind".equals(mode)) {
			// bind as the user
			final Pattern p = Pattern.compile("[^:]*://[^/]*/(.*)");
			final Matcher m = p.matcher(url);
			if (m.find()) {
				final String path = m.group(1);
				final String dn = "uid=" + identifier + "," + path;
				env.put(Context.SECURITY_AUTHENTICATION, "simple");
				env.put(Context.SECURITY_PRINCIPAL, dn);
				env.put(Context.SECURITY_CREDENTIALS, new String(secret));
			}
		} else {
			env.put(Context.SECURITY_AUTHENTICATION, "none");
		}
		try {
			final DirContext ctx = new InitialDirContext(env);
			final BasicAttributes match = new BasicAttributes();
			match.put(new BasicAttribute("uid", identifier));
			final String[] attrs = new String[] { "uid", "displayName", "initials", "mail" }; 
			
			final NamingEnumeration<SearchResult> e = ctx.search("", match, attrs);
			if (e.hasMore()) {
				final SearchResult r = e.next();
				if ("bind".equals(mode) == false) {
					r.setRelative(false);
					final String dn = r.getName();
					ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
					ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
					ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, new String(secret));
					ctx.lookup("");
				}

				insertUser(identifier);
				findUser(identifier, request);
				
				final AspektUser user = (AspektUser) request.getClientInfo().getUser();
				
				// overwrite local information with information from ldap
				final String mail = (String) r.getAttributes().get("mail").get();
				final String displayName = (String) r.getAttributes().get("displayName").get();
				final String initials = (String) r.getAttributes().get("initials").get();
				user.setEmail(mail);
				user.setDisplayName(displayName);
				user.setInitials(initials);
				
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean checkDb(Request request) {
		final String identifier = request.getChallengeResponse().getIdentifier();
		final char[] secret = request.getChallengeResponse().getSecret();
		
		findUser(identifier, request);
		final User user = request.getClientInfo().getUser();
		if (user == null) {
			return false;
		}
		if ((secret == null) || (user.getSecret() == null)) {
            // check if both are null
            return (secret == user.getSecret());
		} else {
			final String secretAsString = new String(secret);
			final String storedSecret = new String(user.getSecret());
			if (storedSecret.startsWith("OBF:")) {
				return PasswordUtil.deobfuscate(storedSecret).equals(secretAsString);
			} else if (storedSecret.startsWith("MD2:")
					|| storedSecret.startsWith("MD5:")
					|| storedSecret.startsWith("SHA-1:") 
					|| storedSecret.startsWith("SHA-256:")
					|| storedSecret.startsWith("SHA-512:")) {
				return PasswordUtil.verify(storedSecret, secretAsString.getBytes()); 
			} else {
				return secretAsString.equals(storedSecret);
			}
		}
	}

	public void findUser(String identifier, Request request) {
		final SqlSession session = aspekt.getSqlSessionFactory().openSession();
		try {
			final AccountMapper account = session.getMapper(AccountMapper.class);
			final Map<String, Object> user = account.authenticate(identifier.toLowerCase());

			if (user == null) return;
			
			final String uid = (String) user.get("uid");
			final String secret = (String) user.get("credentials");
			final String displayName = (String) user.get("display_name");
			final String initials = (String) user.get("initials");
			final String mail = (String) user.get("mail");
			final String tz = (String) user.get("tz");
			final String l = (String) user.get("locale");
			final TimeZone timeZone = tz == null ? TimeZone.getDefault() : TimeZone.getTimeZone(tz);
			final Locale locale = LocalizationUtil.parseLocale(l);
			request.getClientInfo().setUser(new AspektUser(uid, secret, displayName, initials, mail, timeZone, locale));
		} finally {
			session.close();
		}
	}
	
	/**
	 * Used to auto-create a user the first time they log in
	 * This is only used when using LDAP or JAAS for authentication
	 */
	public void insertUser(String identifier) {
		final SqlSession session = aspekt.getSqlSessionFactory().openSession();
		try {
			final HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("uid", identifier);
			params.put("tz", TimeZone.getDefault().toString());
			params.put("locale", LocalizationUtil.getLocale(Request.getCurrent().getClientInfo().getPreferredLanguage(Arrays.asList(LocalizationUtil.AVAILABLE_LANGUAGES))));
		} catch (PersistenceException e) {
			if (e.getCause() instanceof SQLException) {
				if (((SQLException) e.getCause()).getSQLState().startsWith("23")) {
					return;
				}
			}
			throw e;
		} finally {
			session.close();
		}
	}

}
