package ca.digitalcave.parts.security;

import java.security.AlgorithmParameters;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.restlet.engine.util.Base64;

public class CryptoUtil {

	private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final char[] PASSWORD = "_,AwDIu\\:kXLPo.zZ8Jo61x.n{qVi(9".toCharArray();
	private static final int ITERATIONS = 8;
	private static final int KEY_LENGTH = 128; // 256 not available without export-restrictions
	
	private static byte[] salt = new byte[] { 55,127,40,117,111,81,82,89 };
	private static Key key;

	public static void main(String[] args) throws Exception {
		System.out.println(new String(decrypt(encrypt("test".getBytes()))));
	}
	
	private static void init() throws Exception {
		if (key == null) {
			final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
			final PBEKeySpec keySpec = new PBEKeySpec(PASSWORD, salt, ITERATIONS, KEY_LENGTH);
			final Key tmp = keyFactory.generateSecret(keySpec);
			key = new SecretKeySpec(tmp.getEncoded(), "AES");
		}
	}
	
	public static String encrypt(String string) throws Exception {
		byte[] in = string.getBytes("UTF-8");
		byte[] out = encrypt(in);
		return Base64.encode(out, false);
	}
	
	public static byte[] encrypt(byte[] bytes) throws Exception {
		init();
		
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

	public static String decrypt(String string) throws Exception {
		byte[] in = Base64.decode(string);
		byte[] out = decrypt(in);
		return new String(out, "UTF-8");
	}

	public static byte[] decrypt(byte[] bytes) throws Exception {
		init();
		
		// recover the iv
		final byte[] iv = new byte[16];
		System.arraycopy(bytes, 0, iv, 0, iv.length);

		// recover the cyphertext
		final byte[] in = new byte[bytes.length - iv.length];
		System.arraycopy(bytes, iv.length, in, 0, in.length);

		final Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return c.doFinal(in);
	}
}