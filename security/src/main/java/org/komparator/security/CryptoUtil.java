package org.komparator.security;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.xml.soap.SOAPBody;

import org.w3c.dom.Node;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

public class CryptoUtil {

	private final static String ASYM_CIPHER = "RSA/ECB/PKCS1PADDING";

	private final static String FUNC_HASH = "SHA256withRSA";

	private final static String PASSWORD = "BvDJb0AN";
	
	private static HashMap<String,String> certificados = new HashMap<String, String>();

	public static byte[] asymCipherWithPrivate(byte[] dados, PrivateKey privateKey) throws Exception {

		Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] cipherBytes = cipher.doFinal(dados);
		return cipherBytes;

	}
	
	public static byte[] asymDecipherWithPublic(byte[] dadosCifrados, PublicKey publicKey) throws Exception {

		Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		byte[] dados = cipher.doFinal(dadosCifrados);
		return dados;
	}
	public static byte[] asymCipher(byte[] dados, PublicKey publicKey) throws Exception {

		Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherBytes = cipher.doFinal(dados);
		return cipherBytes;

	}

	public static byte[] asymDecipher(byte[] dadosCifrados, PrivateKey privateKey) throws Exception {

		Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] dados = cipher.doFinal(dadosCifrados);
		return dados;
	}

	// If something goes wrong (InvalidKeyException, SignatureException or
	// NoSuchAlgorithmException) returns false
	public static byte[] makeDigitalSignature(byte[] bytes, PrivateKey privateKey) throws Exception {
		byte[] signature = CertUtil.makeDigitalSignature(FUNC_HASH, privateKey, bytes);
		return signature;
	}

	// If something goes wrong (InvalidKeyException, SignatureException or
	// NoSuchAlgorithmException) returns false
	public static boolean verifyDigitalSignature(byte[] bytes, byte[] cipherDigest, PublicKey publicKey)
			throws Exception {
		return CertUtil.verifyDigitalSignature(FUNC_HASH, publicKey, bytes, cipherDigest);

	}

	public static byte[] SOAPMessageBytes(SOAPBody body) throws Exception {

		String string = ((Node) body).getTextContent();

		byte[] msgByteArray = parseBase64Binary(string);
		return msgByteArray;
	}

	public static PublicKey getPublicKey(String name) throws Exception {
		String certificate = getCertificateCA(name); 
		return CertUtil.getX509CertificateFromResource(certificate).getPublicKey();
	}

	public static String getCertificateCA(String name) throws Exception {
		try {
			
			if (certificados.containsKey(name))	{
				return certificados.get(name);
			} else	{
				CAClient client = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
				String certificate = client.getCertificate(name);
				if (certificate == null) {
					System.out.println("not found certificate");
					throw new Exception();
				}
				String fileName = name + ".cer";
				certificados.put(fileName, certificate);
				return fileName;
			}

		} catch (CAClientException e) {

			e.printStackTrace();
		} catch (IOException e) {
			System.out.print("Couldn't write Certificates in files ");
			System.out.println(e);
			System.out.println("Continue normal processing...");

		}
		return null;
	}

	public static PrivateKey getPrivateKey(String name) { // TODO: At the moment
															// returns
		// Mediator's private key. Maybe add a
		// switch
		PrivateKey privateKey = null;
		String alias = name.toLowerCase();
		name = name + ".jks";

		try {
			return privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(name, PASSWORD.toCharArray(), alias,
					PASSWORD.toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException e) {
			System.out.println("Error getting private key from KeyStore ");
			e.printStackTrace();
		}

		catch (FileNotFoundException e) {
			System.out.println("Keystore doesn't exist.");
			e.printStackTrace();
		}
		return privateKey;
	}

	private static void writeFile(String fileName, String result) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

		bw.write(result);
		bw.close();
	}

}
