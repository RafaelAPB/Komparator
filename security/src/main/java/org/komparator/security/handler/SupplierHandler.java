package org.komparator.security.handler;

import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CryptoUtil;
import org.komparator.security.SecuritySingleton;
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
public class SupplierHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String CONTEXT_PROPERTY = "my.property";

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
				System.out.println("	----HEADER OUTBOUND----");
				System.out.println("----------------------------");
				System.out.println("Writing header in outbound SOAP message...");

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();

				// add header
				SOAPHeader sh = se.getHeader();
				SOAPBody sb = se.getBody();

				if (sh == null) {
					sh = se.addHeader();
				}

				// add header element (name, namespace prefix, namespace)
				Name digests = se.createName("messageDigest", "d", "http://demo");

				// Name body = se.createName("body", "d", "http://demo");

				SOAPHeaderElement digestElem = sh.addHeaderElement(digests);

				// SOAPBodyElement bodyElem = sb.addBodyElement(body);

				//////////////////////////// Signature//////////////////////////////////////

				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				msg.writeTo(byteOut);
				// cipher message with symmetric key

				String namewho = SecuritySingleton.getInstance().getName();

				PrivateKey privateKey = CryptoUtil.getPrivateKey(namewho);
				System.out.println(privateKey.toString());

				byte[] cipherdigest = CryptoUtil.makeDigitalSignature(byteOut.toByteArray(), privateKey);

				String encodedSecretArgument = Base64.getEncoder().encodeToString(cipherdigest);

				digestElem.setTextContent(encodedSecretArgument);
				msg.saveChanges();
				
			} else {
				System.out.println("----------------------------");
				System.out.println("	----HEADER INBOUND----");
				System.out.println("----------------------------");
				System.out.println("Reading header in inbound SOAP message...");

				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();

				SOAPHeader sh = se.getHeader();
				SOAPBody sb = se.getBody();

				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}
				String url = (String) smc.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
				String name = SecuritySingleton.getInstance().getUDDI(url);

				//////////////////////////////////////////////
				NodeList children = sh.getFirstChild().getChildNodes();

				for (int i = 0; i < children.getLength(); i++) {
					Node argument = (Node) children.item(i);
					if (argument.getNodeName().equals("messageDigest")) {
						String secretArgument = argument.getTextContent();
						byte[] before = Base64.getDecoder().decode(secretArgument.getBytes());
						argument.getParentNode().removeChild(argument);
						msg.saveChanges();

						ByteArrayOutputStream out = new ByteArrayOutputStream();
						msg.writeTo(out);

						// Maybe requires save changes infinity times
						if (!CryptoUtil.verifyDigitalSignature(before, out.toByteArray(),
								CryptoUtil.getPublicKey(name))) {
							throw new RuntimeException("attack");
						}
					}
				}
				return true;

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