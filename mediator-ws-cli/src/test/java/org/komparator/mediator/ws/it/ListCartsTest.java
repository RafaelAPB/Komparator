package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;

public class ListCartsTest extends BaseIT {

	@Test
	public void success() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception,
			NotEnoughItems_Exception {
		ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
		mediatorClient.addToCart("carrinho", item, 1);
		mediatorClient.addToCart("car", item, 1);
		mediatorClient.addToCart("carrao", item, 1);
		List<CartView> cv = mediatorClient.listCarts();
		assertEquals(cv.get(0).getCartId(), "carrao");
		assertEquals(cv.get(1).getCartId(), "car");
		assertEquals(cv.get(2).getCartId(), "carrinho");

	}

}
