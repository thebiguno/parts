package ca.digitalcave.parts.security;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.restlet.engine.util.Base64;

public class CryptoUtil {

	private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

	public static void main(String[] args) throws Exception {
		final Key key = createKey("password".toCharArray());
		decrypt(key, encrypt(key, "message"));
	}
	
	/**
	 * Generates an AES key from a password
	 */
	public static Key createKey(char[] password) {
		try {
			byte[] salt = new byte[8];
			SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
			return createKey(new PBEKeySpec(password, salt, 65536, 256));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Key createKey(PBEKeySpec keySpec) throws InvalidKeySpecException {
		try {
			final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
			return new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), "AES");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Given an AES key and plain-text, returns an 16 byte IV concatenated with cipher-text.
	 */
	public static byte[] encrypt(Key key, byte[] bytes) throws Exception {
		final Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
		final AlgorithmParameters p = c.getParameters();
		
		final byte[] iv = p.getParameterSpec(IvParameterSpec.class).getIV();
		final byte[] out = c.doFinal(bytes);

		final byte[] result = new byte[out.length + iv.length];
		System.arraycopy(iv, 0, result, 0, iv.length);
		System.arraycopy(out, 0, result, iv.length, out.length);

		return result;
	}
	
	/**
	 * Given an AES key and a 16 byte IV concatenated with cipher-text, returns plain-text.
	 */
	public static byte[] decrypt(Key key, byte[] bytes) throws Exception {
		final byte[] iv = new byte[16];
		final byte[] in = new byte[bytes.length - iv.length];
		System.arraycopy(bytes, 0, iv, 0, iv.length);
		System.arraycopy(bytes, iv.length, in, 0, in.length);

		final Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return c.doFinal(in);
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