package org.komparator.security;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;


import javax.crypto.*;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class CryptoUtilTest {

    // static members
	final static String CERTIFICATE = "example.cer";
	
	final static String KEYSTORE = "example.jks";
	final static String KEYSTORE_PASSWORD = "1nsecure";
	
	final static String KEY_ALIAS = "example";
	final static String KEY_PASSWORD = "ins3cur3";
	
	private final static String plainText = "This is the plain text!";
	private final static byte[] plainBytes = plainText.getBytes();
	
	static PrivateKey privateKey;
	static PublicKey  publicKey;
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() throws UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
        privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE, KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
        publicKey = CertUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey();

    }

    @AfterClass
    public static void oneTimeTearDown() {
        // runs once after all tests in the suite
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() {
        // runs before each test
    }

    @After
    public void tearDown() {
        // runs after each test
    }

    // tests
    @Test
    public void success() throws Exception {
        System.out.println("ENCRYPTING... " + plainText + " or in bytes " + printHexBinary(plainBytes));
        byte[] encrypted = CryptoUtil.asymCipher (plainBytes,publicKey);
        System.out.println(printHexBinary(encrypted));
        System.out.println("---DECRYPTING---");
        byte[] decrypted = CryptoUtil.asymDecipher(encrypted, privateKey);
        System.out.println(printHexBinary(decrypted));
        System.out.println("---NEW PLAIN TEXT---");
        String newPlainText = new String(decrypted);
        System.out.println(newPlainText);
        assertEquals(plainText,newPlainText);
        // assertEquals(expected, actual);
        // if the assert fails, the test fails
    }

}
