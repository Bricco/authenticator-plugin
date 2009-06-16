package talentum.escenic.plugins.authenticator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class PBEEncrypter {

	private static Cipher getPBECipher(int mode) throws Exception {

		PBEKeySpec pbeKeySpec;
		PBEParameterSpec pbeParamSpec;
		SecretKeyFactory keyFac;

		// Salt
		byte[] salt = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
				(byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };

		// Iteration count
		int count = 20;

		// Create PBE parameter set
		pbeParamSpec = new PBEParameterSpec(salt, count);

		pbeKeySpec = new PBEKeySpec("MySuperflyEncryptionString".toCharArray());
		keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

		// Create PBE Cipher
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		// Initialize PBE Cipher with key and parameters
		pbeCipher.init(mode, pbeKey, pbeParamSpec);

		return pbeCipher;
	}

	/**
	 * Encrypts a String using PBE (Password Based Encryption) and
	 * encodes it using the characters abcdefghijklmnop.
	 * 
	 * @param s String to encrypt
	 * @return an encrypted String
	 * @throws Exception if anything goes wrong
	 */
	public static String encrypt(String s) throws Exception {

		byte[] ciphertext = getPBECipher(Cipher.ENCRYPT_MODE).doFinal(s.getBytes());

		char[] encoded = new char[ciphertext.length * 2];

		for(int i = 0; i < ciphertext.length; i++) {
			byte b = ciphertext[i];
			encoded[i * 2] = (char)('a' + ((b >> 4) & 0xf));
			encoded[i * 2 + 1] = (char)('a' + (b & 0xf));
		}

		return new String(encoded);
	}

	/**
	 * Decrypts a String which has been encrypted with encrypt(String).
	 * 
	 * @param s String to decrypt
	 * @return a decrypted String
	 * @throws Exception if anything goes wrong
	 */
	public static String decrypt(String s) throws Exception {

		char[] encoded = s.toCharArray();

		byte[] ciphertext = new byte[encoded.length / 2];

		for(int i = 0; i < ciphertext.length; i++) {
			byte b = (byte)((encoded[i * 2] - 'a') << 4);
			b |= (byte)(encoded[i * 2 + 1] - 'a');
			ciphertext[i] = b;
		}

		byte[] cleartext = getPBECipher(Cipher.DECRYPT_MODE).doFinal(ciphertext);

		return new String(cleartext);
	}
}
