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
public class AttackHandler implements SOAPHandler<SOAPMessageContext> {
;

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


		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		try {
			if (!outboundElement) {
				System.out.println("----------------------------");
				System.out.println(".-----ATACK HEADER -------");
				System.out.println("----------------------------");
				System.out.println("When receiving the SOAP message from the mediator, try to change the content...");

				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();

				QName operation = (QName) smc.get(MessageContext.WSDL_OPERATION);
				if (operation.getLocalPart().equals("buyProduct")) {
					NodeList children = sb.getFirstChild().getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						Node argument = (Node) children.item(i);
						System.out.println("--------------");
						System.out.println("STARTING ATTACK on " + argument);
						System.out.println("--------------");
						argument.setTextContent("200");
						System.out.println("--------------");
						System.out.println("PurchaseId changed to 200");
						System.out.println("--------------");
						
						/*if (argument.getNodeName().equals("productId")) {
							System.out.println("--------------");
							System.out.println("productID found");
							System.out.println("--------------");
							if(argument.getValue().equals("XPTOATTACK")){
								argument = (Node) children.item(i+1);
								argument.setTextContent("200");
								System.out.println("--------------");
								System.out.println("Quantity changed to 200");
								System.out.println("--------------");
								msg.saveChanges();
							
							}*/
						/*	System.out.println("--------------");
							System.out.println(argument.getTextContent());
							System.out.println("--------------");*/
							//String secretArgument = argument.getTextContent();
							
							



						}
					}
				msg.saveChanges();
				}
			
			
			} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("ATTACK HEADER DEU ERRO");
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