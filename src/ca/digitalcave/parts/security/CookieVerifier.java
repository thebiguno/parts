package ca.digitalcave.parts.security;

import java.io.StringReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.ibatis.session.SqlSession;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.engine.util.Base64;
import org.restlet.security.Verifier;

import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.PartsMapper;
import ca.digitalcave.parts.model.Account;

public class CookieVerifier implements Verifier {

	private final PartsApplication application;
	private static final String NAME = "_auth";
	private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final char[] PASSWORD = "SCMNIixaKm2VQ8KvrrxtlvYu3I8hw3A".toCharArray();
	private static final int ITERATION = 32;
	private Verifier verifier;
	
	public CookieVerifier(PartsApplication application) {
		this.application = application;
	}
	
	@Override
	public int verify(Request request, Response response) {
		final String[] values = request.getCookies().getValuesArray(NAME);
		for (String value : values) {
			try {
				final Properties p = new Properties();
				p.load(new StringReader(decrypt(value)));
				final String uid = p.getProperty("uid");
				final String password = p.getProperty("password");
				final Date expiry = new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse(p.getProperty("expiry"));
				if (expiry.after(new Date())) {
					request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.CUSTOM, uid, password.toCharArray()));
					break;
				}
			} catch (Throwable e) {
				;
			}
		}
		
		if (request.getChallengeResponse() == null) {
			return RESULT_MISSING;
		} else {
			final SqlSession sql = application.getSqlFactory().openSession(true);
			try {
				final Account account = sql.getMapper(PartsMapper.class).selectAccount(request.getChallengeResponse().getIdentifier());
				if (account.verifyCredentials(request.getChallengeResponse().getSecret()) {
					return RESULT_VALID;
				} else {
					return RESULT_INVALID;
				}
			} finally {
				sql.close();
			}
		}
	}
	

	private static String encrypt(String value) throws Exception {
		// generate some random salt
		if (value == null) return null;
		final byte[] salt = new byte[16];
		SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
		
		final int iterations = ITERATION;

		final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		final PBEKeySpec keySpec = new PBEKeySpec(PASSWORD, salt, iterations, 256);
		final Key tmp = keyFactory.generateSecret(keySpec);
		final Key key = new SecretKeySpec(tmp.getEncoded(), "AES");

		final Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
		final AlgorithmParameters p = c.getParameters();

		final byte[] iv = p.getParameterSpec(IvParameterSpec.class).getIV();
		final byte[] out = c.doFinal(value.getBytes("UTF-8"));

		final StringBuilder sb = new StringBuilder();
		sb.append(iterations);
		sb.append(":");
		sb.append(encode(salt));
		sb.append(":");
		sb.append(encode(iv));
		sb.append(":");
		sb.append(encode(out));

		return sb.toString();
	}
	
	private static String decrypt(String value) throws Exception {
		final String[] parts = value.split(":");
		
		final int iterations = Integer.parseInt(parts[0]);
		final byte[] salt = decode(parts[1]);
		final byte[] iv = decode(parts[2]);
		final byte[] in = decode(parts[3]);

		final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		final PBEKeySpec keySpec = new PBEKeySpec(PASSWORD, salt, iterations, 256);
		final Key tmp = keyFactory.generateSecret(keySpec);
		final Key key = new SecretKeySpec(tmp.getEncoded(), "AES");

		final Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return new String(c.doFinal(in), "UTF-8");
	}
	
	public static String getPasswordHash(String password) {
		try {
			final MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(Charset.forName("UTF-8").encode(CharBuffer.wrap(password)));
			return Base64.encode(md.digest(), false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String encode(byte[] bytes) {
		return Base64.encode(bytes, false);
	}
	private static byte[] decode(String encoded) {
		return Base64.decode(encoded);
	}

}
