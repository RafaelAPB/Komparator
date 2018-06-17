package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.Result;
import org.komparator.mediator.ws.ShoppingResultView;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;

public class updateShopTest extends BaseIT {
	private ItemIdView item = new ItemIdView();

	@Override
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
		super.setUp();
		item.setProductId("X1");
		item.setSupplierId(SUP1_URL);
	}

	@Test
	public void sucess() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {

		try {
			MediatorClient ligacao = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
			item.setProductId("X1");
			item.setSupplierId(SUP1_URL);
			mediatorClient.addToCart("cart1", item, 1);

			ShoppingResultView shop = mediatorClient.buyCart("cart1", "4024007102923926");

			assertEquals(shop.getId(), "1");
			assertEquals(shop.getResult(), Result.COMPLETE);
			assertEquals(shop.getTotalPrice(), 10);
			assertEquals(shop.getPurchasedItems().size(), 1);
			assertEquals(shop.getDroppedItems().size(), 0);

			assertEquals(mediatorClient.shopHistory().size(), ligacao.shopHistory().size());
			assertEquals(mediatorClient.shopHistory().get(0).getId(), ligacao.shopHistory().get(0).getId());
			assertEquals(mediatorClient.shopHistory().get(0).getDroppedItems().size(),
					ligacao.shopHistory().get(0).getDroppedItems().size());
			assertEquals(mediatorClient.shopHistory().get(0).getPurchasedItems().size(),
					ligacao.shopHistory().get(0).getPurchasedItems().size());
			assertEquals(mediatorClient.shopHistory().get(0).getResult(), ligacao.shopHistory().get(0).getResult());
			assertEquals(mediatorClient.shopHistory().get(0).getTotalPrice(),
					ligacao.shopHistory().get(0).getTotalPrice());

		} catch (MediatorClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
