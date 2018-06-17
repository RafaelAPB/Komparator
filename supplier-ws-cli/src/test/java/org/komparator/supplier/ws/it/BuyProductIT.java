package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.PurchaseView;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
		client.clear();
		ProductView product = new ProductView();
		product.setId("X1");
		product.setDesc("Basketball");
		product.setPrice(10);
		product.setQuantity(10);
		client.createProduct(product);

	}

	@After
	public void tearDown() {
		client.clear();
	}

	@Test(expected = BadProductId_Exception.class)
	public void BuyProductNullIdTest()
			throws BadQuantity_Exception, InsufficientQuantity_Exception, BadProductId_Exception {
		client.buyProduct(null, 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void BuyProductEmptyIdTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void BuyProductWhitespaceIdTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(" ", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void BuyProductTabTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\t", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void BuyProductNewlineIdTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\n", 5);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void BuyProductQuantityNegativeTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", -10);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void BuyProductQuantity0Test()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", 0);
	}
	// main tests

	@Test
	public void BuyProductSuccessTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String pId = client.buyProduct("X1", 10);
		List<PurchaseView> pview = client.listPurchases();
		for (PurchaseView p : pview) {
			if (p.getId().equals(pId)) {
				assertEquals("X1", p.getProductId());
			}
		}
		assertEquals(0, client.getProduct("X1").getQuantity());
	}

/*	@Test
	public void attackTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception, BadProduct_Exception {
		ProductView product = new ProductView();
		product.setId("XPTOATTACK");
		product.setDesc("TESTE");
		product.setPrice(10);
		product.setQuantity(10);
		client.createProduct(product);
		client.buyProduct("XPTOATTACK", 1);
		
	}*/
	@Test(expected = InsufficientQuantity_Exception.class)
	public void BuyProductQuantityTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", 15);
	}

	@Test(expected = BadProductId_Exception.class)
	public void BuyProductTextIdTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X2", 5);
	}

}
