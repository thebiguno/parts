package ca.digitalcave.parts.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.util.Properties;
import java.util.logging.Level;

import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Method;
import org.restlet.engine.util.Base64;
import org.restlet.security.ChallengeAuthenticator;

public class CookieAuthenticator extends ChallengeAuthenticator {

	private final String cookieName;
	private final int maxCookieAge;
	private final String loginPath;
	private final String logoutPath;
	private final Key key;

	public CookieAuthenticator(Context context, boolean optional, Key key) {
		this(context, optional, key, "_auth", 0, "/index", "/index");
	}
	public CookieAuthenticator(Context context, boolean optional, Key key, String cookieName, int maxCookieAge, String loginPath, String logoutPath) {
		super(context, optional, ChallengeScheme.HTTP_COOKIE, null);
		this.key = key;
		this.cookieName = cookieName;
		this.maxCookieAge = maxCookieAge;
		this.loginPath = loginPath;
		this.logoutPath = logoutPath;
	}

	@Override
	protected boolean authenticate(Request request, Response response) {
		final Cookie cookie = request.getCookies().getFirst(cookieName);

		if (cookie != null) {
			request.setChallengeResponse(parse(cookie.getValue()));
		}

		return super.authenticate(request, response);
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		if (isLoggingIn(request, response)) {
			login(request, response);
		} else if (isLoggingOut(request, response)) {
			return logout(request, response);
		}

		return super.beforeHandle(request, response);
	}
	
	@Override
	protected int authenticated(Request request, Response response) {
		final CookieSetting credentialsCookie = getCredentialsCookie(request, response);
		credentialsCookie.setValue(format(request.getChallengeResponse()));
		credentialsCookie.setMaxAge(maxCookieAge);
		return super.authenticated(request, response);
	}

	public ChallengeResponse parse(String encoded) {
		try {
			final byte[] bytes = CryptoUtil.decrypt(key, Base64.decode(encoded));
			final Properties p = new Properties();
			p.load(new ByteArrayInputStream(bytes));

			final ChallengeResponse result = new ChallengeResponse(getScheme());
			result.setRawValue(encoded);
			result.setTimeIssued(Long.parseLong(p.getProperty("issued")));
			result.setIdentifier(p.getProperty("identifier"));
			result.setSecret(p.getProperty("secret"));
			result.getParameters().set("expiry", p.getProperty("expiry"));
			result.getParameters().set("impersonate", p.getProperty("impersonate"));
			return result;
		} catch (Exception e) {
			getLogger().log(Level.INFO, "Unable to decrypt cookie credentials", e);
			return null;
		}
	}
	public String format(ChallengeResponse challengeResponse) {
		try {
			final Properties p = new Properties();
			p.setProperty("issued", Long.toString(challengeResponse.getTimeIssued()));
			p.setProperty("identifier", challengeResponse.getIdentifier());
			p.setProperty("secret", new String(challengeResponse.getSecret()));
			p.setProperty("expiry", challengeResponse.getParameters().getFirstValue("expiry"));
			p.setProperty("impersonate", challengeResponse.getParameters().getFirstValue("impersonate"));

			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			p.store(out, null);
			return Base64.encode(CryptoUtil.encrypt(key, out.toByteArray()), false);
		} catch (Exception e) {
			getLogger().log(Level.INFO, "Unable to decrypt cookie credentials", e);
			return null;
		}
	}

	protected boolean isLoggingIn(Request request, Response response) {
		return loginPath != null
				&& loginPath.equals(request.getResourceRef().getRemainingPart(false, false))
				&& Method.POST.equals(request.getMethod());
	}

	protected boolean isLoggingOut(Request request, Response response) {
		return logoutPath != null
				&& logoutPath.equals(request.getResourceRef().getRemainingPart(false, false))
				&& Method.DELETE.equals(request.getMethod());
	}

	protected void login(Request request, Response response) {
		try {
			final JSONObject object = new JSONObject(request.getEntityAsText()); 
			final ChallengeResponse cr = new ChallengeResponse(getScheme(), object.getString("identifier"), object.getString("secret"));
			cr.getParameters().add("impersonate", object.optString("impersonate"));
			request.setChallengeResponse(cr);
		} catch (Exception e) {
			;
		}
	}

	protected int logout(Request request, Response response) {
		request.setChallengeResponse(null);
		final CookieSetting credentialsCookie = getCredentialsCookie(request, response);
		credentialsCookie.setMaxAge(0);
		return STOP;
	}

	protected CookieSetting getCredentialsCookie(Request request,
			Response response) {
		CookieSetting credentialsCookie = response.getCookieSettings().getFirst(cookieName);

		if (credentialsCookie == null) {
			credentialsCookie = new CookieSetting(cookieName, null);
			credentialsCookie.setAccessRestricted(true);
			// authCookie.setVersion(1);

			if (request.getRootRef() != null) {
				String p = request.getRootRef().getPath();
				credentialsCookie.setPath(p == null ? "/" : p);
			} else {
				// authCookie.setPath("/");
			}

			response.getCookieSettings().add(credentialsCookie);
		}

		return credentialsCookie;
	}
}
