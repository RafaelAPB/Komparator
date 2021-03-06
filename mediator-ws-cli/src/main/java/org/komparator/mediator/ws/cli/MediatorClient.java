package org.komparator.mediator.ws.cli;

import java.util.List;

import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.MediatorPortType;
import org.komparator.mediator.ws.MediatorService;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.ShoppingResultView;

/**
 * Client.
 *
 * Adds easier endpoint address configuration and UDDI lookup capability to the
 * frontendType generated by wsimport.
 */
public class MediatorClient implements MediatorPortType {

	// TODO uncomment after generate-sources
	/** WS service */
	MediatorService service = null;

	// TODO uncomment after generate-sources
	/**
	 * WS frontend (frontend type is the interface, frontend is the
	 * implementation)
	 */
	FrontEndMediatorClient frontend = null;

	/** UDDI server URL */
	private String uddiURL = null;

	/** WS name */
	private String wsName = null;

	/** WS endpoint address */
	private String wsURL = null; // default value is defined inside WSDL

	public String getWsURL() {
		return wsURL;
	}

	/** output option **/
	private boolean verbose = false;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided web service URL */
	public MediatorClient(String wsURL) throws MediatorClientException {
		frontend = new FrontEndMediatorClient(wsURL);
	}

	/** constructor with provided UDDI location and name */
	public MediatorClient(String uddiURL, String wsName) throws MediatorClientException {
		frontend = new FrontEndMediatorClient(uddiURL, wsName);
	}

	@Override
	public void clear() {
		frontend.clear();
	}

	@Override
	public void imAlive() {
		frontend.imAlive();
	}

	@Override
	public void updateShopHistory(ShoppingResultView result) {
		frontend.updateShopHistory(result);

	}

	@Override
	public String ping(String arg0) {
		return frontend.ping(arg0);
	}

	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		return frontend.searchItems(descText);
	}

	@Override
	public List<CartView> listCarts() {
		return frontend.listCarts();
	}

	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		return frontend.getItems(productId);
	}

	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr)
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		return frontend.buyCart(cartId, creditCardNr);
	}

	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		frontend.addToCart(cartId, itemId, itemQty);
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		return frontend.shopHistory();
	}

	@Override
	public void updateCart(CartView result, int opcao) {
		frontend.updateCart(result, opcao);

	}

}