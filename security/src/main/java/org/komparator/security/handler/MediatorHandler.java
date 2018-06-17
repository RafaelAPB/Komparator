package org.komparator.security.handler;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CertUtil;
import org.komparator.security.CryptoUtil;
import org.w3c.dom.NodeList;

/**
 * This SOAPHandler shows how to set/get values from headers in inbound/outbound
 * SOAP messages.
 *
 * A header is created in an outbound message and is read on an inbound message.
 *
 * The value that is read from the header is placed in a SOAP message context
 * property that can be accessed by other handlers or by the application.
 */
public class MediatorHandler implements SOAPHandler<SOAPMessageContext> {

	final static String CERTIFICATE = "A45_Mediator.cer";
	final static String KEY_ALIAS = "A45_Mediator";
	final static String KEYSTORE = "A45_Mediator.jks";
	final static String PASSWORD = "BvDJb0AN";

	//
	// Handler interface implementation
	//

	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("AddHeaderHandler: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				System.out.println("----------------------------");
				System.out.println(".-----MEDIATOR HEADER OUTBOUND-------");
				System.out.println("----------------------------");
				System.out.println("Writing header in outbound SOAP message...");

				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				QName operation = (QName) smc.get(MessageContext.WSDL_OPERATION);
				if (operation.getLocalPart().equals("buyCart")) {
					NodeList children = sb.getFirstChild().getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						Node argument = (Node) children.item(i);

						
						if (argument.getNodeName().equals("creditCardNr")) {
							System.out.println("--------------");
							System.out.println(argument.getTextContent());
							System.out.println("--------------");
							String secretArgument = argument.getTextContent();
							
							PublicKey publicKey = CertUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey();
							byte[] cipheredArgument = CryptoUtil.asymCipher(secretArgument.getBytes(), publicKey);

							String encodedSecretArgument = Base64.getEncoder().encodeToString(cipheredArgument);
							System.out.println("--------------");
							System.out.println(encodedSecretArgument);
							System.out.println("--------------");

							argument.setTextContent(encodedSecretArgument);
							msg.saveChanges();

						}
					}
				}
			} else {
				System.out.println("----------------------------");
				System.out.println("---- MEDIATOR HEADER INBOUND----");
				System.out.println("----------------------------");
				System.out.println("Reading header in inbound SOAP message...");

				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				QName operation = (QName) smc.get(MessageContext.WSDL_OPERATION);
				if (operation.getLocalPart().equals("buyCart")) {
					NodeList children = sb.getFirstChild().getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						Node argument = (Node) children.item(i);
						if (argument.getNodeName().equals("creditCardNr")) {
							String secretArgument = argument.getTextContent();
							System.out.println("--------------");
							System.out.println(secretArgument);
							System.out.println("--------------");
							byte[] bytes = Base64.getDecoder().decode(secretArgument.getBytes());
							PrivateKey privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE,
									PASSWORD.toCharArray(), KEY_ALIAS, PASSWORD.toCharArray());
							byte[] cipheredArgument = CryptoUtil.asymDecipher(bytes, privateKey);
							System.out.println("--------------");
							System.out.println(new String(cipheredArgument, StandardCharsets.US_ASCII));
							System.out.println("--------------");
							argument.setTextContent(new String(cipheredArgument, StandardCharsets.US_ASCII));
							msg.saveChanges();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}

}