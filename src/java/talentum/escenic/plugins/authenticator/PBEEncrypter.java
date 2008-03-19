package talentum.escenic.plugins.authenticator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import neo.util.Base64;

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

	public static String encrypt(String s) throws Exception {

		// Encrypt the string
		byte[] ciphertext = getPBECipher(Cipher.ENCRYPT_MODE).doFinal(
				s.getBytes());

		return new String(Base64.encode(ciphertext));

	}

	public static String decrypt(String s) throws Exception {

		// Decrypt the string
		byte[] ciphertext = getPBECipher(Cipher.DECRYPT_MODE).doFinal(
				Base64.decode(s.toCharArray()));

		return new String(ciphertext);

	}

}
