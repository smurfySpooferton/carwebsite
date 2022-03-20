package com.udacity.pricing;

import com.udacity.pricing.api.PricingController;
import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.service.PricingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testResponse;
	@Autowired
	private PricingService pricingService;
	@Autowired
	private PricingController pricingController;

	@Test
	public void contextLoads() {
		assertNotNull(pricingController);
		assertNotNull(pricingService);
	}

	@Test
	public void testPrice(){
		//Test Response codes (404 and 200) and ensure that price will stay the same during app context.
		ResponseEntity<Price> response = testResponse.getForEntity("http://localhost:" + port + "/services/price?vehicleId=1", Price.class );
		assertEquals(HttpStatus.OK, response.getStatusCode());
		Price price1 = response.getBody();
		response = testResponse.getForEntity("http://localhost:" + port + "/services/price?vehicleId=1", Price.class );
		Price price2 = response.getBody();
		assertEquals (price1, price2);

		//Max count or vehicles is 20; as configured in PricingService
		response = testResponse.getForEntity("http://localhost:" + port + "/services/price?vehicleId=21", Price.class );
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
}
