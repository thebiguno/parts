package ca.digitalcave.parts.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtil {

	public static void main(String[] args) {
		if (args.length == 0) {
			final byte[] salt = randomSalt(2);
			final byte[] test = "password".getBytes();
	
			long start = System.currentTimeMillis();
			System.out.println(hash("MD5", 0, salt, test));
			System.out.println(System.currentTimeMillis() - start);
	
			start = System.currentTimeMillis();
			System.out.println(hash("SHA-1", 0, salt, test));
			System.out.println(System.currentTimeMillis() - start);
	
			start = System.currentTimeMillis();
			System.out.println(hash("SHA-1", 255, salt, test));
			System.out.println(System.currentTimeMillis() - start);
	
			start = System.currentTimeMillis();
			System.out.println(hash("SHA-1", 32767, salt, test));
			System.out.println(System.currentTimeMillis() - start);
	
			start = System.currentTimeMillis();
			System.out.println(hash("SHA-1", 65535, salt, test));
			System.out.println(System.currentTimeMillis() - start);
	
			start = System.currentTimeMillis();
			System.out.println(hash("SHA-1", 16777215, salt, test));
			System.out.println(System.currentTimeMillis() - start);
		} else {
			if (args[0].startsWith("OBF:")) {
				System.out.println(recover(args[0]));
			} else {
				final byte[] salt = randomSalt(2);
				final String algorithm;
				final int iterations;
				if (args.length > 1) {
					algorithm = args[1];
				} else {
					algorithm = "SHA-1";
				}
				if (args.length > 2) {
					iterations = Integer.parseInt(args[2]);
				} else {
					iterations = 0;
				}
				System.out.println(obfuscate(args[0]));
				System.out.println(hash(algorithm, iterations, salt, args[0].getBytes()));
			}
		}
	}
	
	
	/**
	 * Returns a code with the given strength scale:
	 * <ul>
	 * <li>0-19 very weak</li>
	 * <li>20-39 weak</li>
	 * <li>40-59 is mediocre</li>
	 * <li>60-79 is decently strong</li>
	 * <li>80+ is strong</li>
	 * <li>@param password</li>
	 * @return
	 */
	public static int strength(String password) {
		double factor = 0;

		// encourage using multiple classes
		if (password.matches(".*[a-z].*")) factor += 2.6;
		if (password.matches(".*[A-Z].*")) factor += 2.6;
		if (password.matches(".*[0-9].*")) factor += 1.0;
		if (password.matches(".*[ ].*")) factor += 0.1;
		if (password.matches(".*[^a-zA-Z0-9].*")) factor += 3.2;

		return Math.max(100, password.length() * (int) Math.round(factor));
	}
	
	/**
	 * The resulting string is 40 characters for the hash + 5 for the algorithm + salt + iteration
	 */
	public static String sha1(int iterations, byte[] salt, byte[] message) {
		return hash("SHA-1", 0, salt, message);
	}
	
	/**
	 * The resulting string is 64 characters for the hash + 7 for the algorithm + salt + iteration
	 */
	public static String sha256(int iterations, byte[] salt, byte[] message) {
		return hash("SHA-256", iterations, salt, message);
	}
	
	/**
	 * Returns a string in the format "algorithm:iterations:salt:hash".
	 */
	public static String hash(String algorithm, int iterations, byte[] salt, byte[] message) {
		try {
			final MessageDigest digest = MessageDigest.getInstance(algorithm);
			
			digest.update(message);
			digest.update(salt);
			
			for (int i = 0; i < iterations; i++) {
				digest.update(digest.digest());
				digest.update(message);
				digest.update(salt);
			}
			
			final StringBuilder sb = new StringBuilder();
			sb.append(algorithm);
			sb.append(":");
			sb.append(Integer.toString(iterations, 16));
			sb.append(":");
			sb.append(encode(salt));
			sb.append(":");
			sb.append(encode(digest.digest()));
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean verify(String hash, byte[] message) {
		final int a = hash.indexOf(':');
    	final int b = hash.indexOf(':', a + 1);
    	final int c = hash.indexOf(':', b + 1);
    	final String algorithm = hash.substring(0, a);
    	final int iterations = Integer.parseInt(hash.substring(a + 1, b), 16);
    	final byte[] salt = decode(hash.substring(b + 1, c));
    	final String calc = hash(algorithm, iterations, salt, message);
    	return hash.equalsIgnoreCase(calc);
	}
	
    public static String encode(byte[] bytes) {
    	final int base = 16;
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int bi = 0xff & bytes[i];
            int c = '0' + (bi/base) % base;
            if (c > '9') 
            	c = 'a' + (c - '0' - 10);
            buf.append((char) c);
            c = '0' + bi % base;
            if (c > '9')
                c = 'a' + (c - '0' - 10);
            buf.append((char) c);
        }
        return buf.toString();
    }
    
    public static byte[] decode(String encoded) {
    	final char[] hex = encoded.toCharArray();
    	int length = hex.length / 2;
    	byte[] raw = new byte[length];
    	for (int i = 0; i < length; i++) {
    		int high = Character.digit(hex[i * 2], 16);
    		int low = Character.digit(hex[i * 2 + 1], 16);
    		int value = (high << 4) | low;
    		if (value > 127)
    			value -= 256;
    		raw[i] = (byte) value;
    	}
    	return raw;
    }
    
    public static byte[] randomSalt() {
    	return randomSalt(2);
    }
    
    public static byte[] randomSalt(int bytes) {
		final byte[] salt = new byte[bytes];
		final SecureRandom r = new SecureRandom();
		r.nextBytes(salt);
		return salt;
    }
    
    public static String recover(String s) {
    	if (s.startsWith("OBF:")) {
    		return deobfuscate(s);
    	} else {
    		return s;
    	}
    }
    public static String obfuscate(String s) {
        final StringBuilder sb = new StringBuilder();
        byte[] b = s.getBytes();
        
        sb.append("OBF:");
        for (int i = 0; i < b.length; i++) {
            final byte b1 = b[i];
            final byte b2 = b[s.length() - (i+1)];
            final int i1 = 127 + b1 + b2;
            final int i2 = 127 + b1 - b2;
            final int i0 = i1 * 256 + i2;
            final String x = Integer.toString(i0,36);

            switch (x.length()) {
              case 1:
              case 2:
              case 3:
            	  sb.append('0');
              default: 
            	  sb.append(x);
            }
        }
        return sb.toString();
    }
    
    public static String deobfuscate(String s) {
        if (s.startsWith("OBF:"))
            s = s.substring(4);
        
        final byte[] b = new byte[s.length()/2];
        int l = 0;
        for (int i = 0; i < s.length(); i += 4) {
            final String x = s.substring(i, i+4);
            final int i0 = Integer.parseInt(x, 36);
            final int i1 = (i0 / 256);
            final int i2 = (i0 % 256);
            b[l++] = (byte) ((i1 + i2 - 254) / 2);
        }

        return new String(b, 0, l);
    }

}