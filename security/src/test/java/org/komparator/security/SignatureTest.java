package org.komparator.security;

//provides helper methods to print byte[]
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static org.junit.Assert.*;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;

import org.junit.BeforeClass;
import org.junit.Test;

/** Test suite to show how the Java Security API can be used for digests. */
public class SignatureTest {

	final static String CERTIFICATE = "example.cer";
	
	final static String KEYSTORE = "example.jks";
	final static String KEYSTORE_PASSWORD = "1nsecure";
	
	final static String KEY_ALIAS = "example";
	final static String KEY_PASSWORD = "ins3cur3";
	
	private final static String plainText = "This is the plain text!";
	private final static byte[] plainBytes = plainText.getBytes();
	private final static String FUNC_HASH = "SHA256withRSA";
	private static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";

	static PrivateKey privateKey;
	static PublicKey  publicKey;
	
	byte[] digest;
	
    @BeforeClass
    public static void oneTimeSetUp() throws UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
        privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE, KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
        publicKey = CertUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey();

    }

	@Test
	public void testSignatureObject() throws Exception {
		System.out.print("TEST ");
		System.out.print(FUNC_HASH);
		System.out.println(" digital signature");

		System.out.print("Text: ");
		System.out.println(plainText);
		System.out.print("Bytes: ");
		System.out.println(printHexBinary(plainBytes));

		// make digital signature
		System.out.println("Signing, using makeDigitalSignature");
		byte[] cipherDigest = CryptoUtil.makeDigitalSignature(plainBytes, privateKey);

		// verify the signature
		System.out.println("Verifying, using verifyDigitalSignature");
		boolean result = CryptoUtil.verifyDigitalSignature(plainBytes, cipherDigest,publicKey);
		System.out.println("Signature is " + (result ? "right" : "wrong"));

		assertTrue(result);
	}

		@Test
		public void testSignatureObjectFail() throws Exception {
			System.out.print("TEST ");
			System.out.print(FUNC_HASH);
			System.out.println(" digital signature");

			System.out.print("Text: ");
			System.out.println(plainText);
			System.out.print("Bytes: ");
			System.out.println(printHexBinary(plainBytes));

			// make digital signature
			System.out.println("Signing, using makeDigitalSignature");
			byte[] cipherDigest = CryptoUtil.makeDigitalSignature(plainBytes, privateKey);

			// data modification ...
			plainBytes[3] = 12;
			System.out.println("Tampered bytes: (look closely around the 7th hex character)");
			System.out.println(printHexBinary(plainBytes));
			System.out.println("      ^");

			// verify the signature
			System.out.println("Verifying, using redigestDecipherCompare");
			boolean result = CryptoUtil.verifyDigitalSignature(plainBytes, cipherDigest, publicKey);
			System.out.println("Signature is " + (result ? "right" : "wrong, which is supposed"));

			assertFalse(result);
		}


}
