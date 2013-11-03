package ca.digitalcave.parts.security;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.restlet.engine.util.Base64;

public class CryptoUtil {

	private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final String CIPHER_ALGORITHM = "AES";

	public static void main(String[] args) throws Exception {
		final Key key = createKey("password".toCharArray());
		decrypt(key, encrypt(key, "message"));
		decrypt(key, encrypt(key, "message"));
	}
	
	/**
	 * Generates an AES key from a password with random salt
	 */
	public static Key createKey(char[] password) {
		try {
			byte[] salt = new byte[128];
			SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
			return createKey(new PBEKeySpec(password, salt, 8, 128));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Key createKey(PBEKeySpec keySpec) throws InvalidKeySpecException {
		try {
			final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
			final SecretKeySpec result =  new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), "AES");
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static byte[] encrypt(Key key, byte[] bytes) throws Exception {
		final Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
		return c.doFinal(bytes);
	}
	
	public static byte[] decrypt(Key key, byte[] bytes) throws Exception {
		final Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, key);
		return c.doFinal(bytes);
	}
	
	public static String encrypt(Key key, String string) throws Exception {
		byte[] in = string.getBytes("UTF-8");
		byte[] out = encrypt(key, in);
		return Base64.encode(out, false);
	}
	
	public static String decrypt(Key key, String string) throws Exception {
		byte[] in = Base64.decode(string);
		byte[] out = decrypt(key, in);
		return new String(out, "UTF-8");
	}
}