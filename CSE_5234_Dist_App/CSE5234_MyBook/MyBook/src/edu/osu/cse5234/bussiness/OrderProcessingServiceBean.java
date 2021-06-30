package edu.osu.cse5234.bussiness;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.*;
import javax.ws.rs.client.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;

import edu.osu.cse5234.business.view.Inventory;
import edu.osu.cse5234.business.view.InventoryService;
import edu.osu.cse5234.business.view.Item;
import edu.osu.cse5234.model.LineItem;
import edu.osu.cse5234.model.Order;
import edu.osu.cse5234.util.ServiceLocator;

/**
 * Session Bean implementation class OrderProcessingServiceBean
 */
@Stateless
@LocalBean
@Resource(name="jms/emailQCF", lookup="jms/emailQCF", type=ConnectionFactory.class) 
public class OrderProcessingServiceBean {
	
	@PersistenceContext
	private EntityManager entityManager;
	private static String SHIPPING_URI = "http://localhost:9080/UPS/services/shipping";
	/**
	 * for message
	 */
	@Inject
	@JMSConnectionFactory("java:comp/env/jms/emailQCF")
	private JMSContext jmsContext;

	@Resource(lookup="jms/emailQ")
	private Queue queue;

    /**
     * Default constructor. 
     */
    public OrderProcessingServiceBean() {
        // TODO Auto-generated constructor stub
    }
    
    public String processOrder(Order order) {
    	if(!validateItemAvailability(order)) {
    		return "Error";
    	}    	
    	entityManager.persist(order);
    	entityManager.flush();
    	// shipping
		Client client = ClientBuilder.newClient();
		WebTarget baseTarget = client.target(SHIPPING_URI);
				
		JsonObject requestJson = Json.createObjectBuilder()
		.add("Organization", "MyBook LLC.")
		.add("OrderRefId", 123)
		.add("ItemsCount", 8)
		.add("Zip", "43210")
		.build();

		JsonObject responseJson = baseTarget.path("/initiate")
		.request(MediaType.APPLICATION_JSON)
		.post(Entity.json(requestJson), JsonObject.class);

		System.out.println("UPS accepted request? " +   
		     responseJson.getBoolean("Accepted"));
		System.out.println("Shipping Reference Number: " + 
		     responseJson.getInt("ShippingReferenceNumber"));

		client.close();
		// message
		notifyUser(order.getEmailAddress());

    	return UUID.randomUUID().toString();
    }
    
    public boolean validateItemAvailability(Order order) {
    	InventoryService inventoryService = ServiceLocator.getInventoryService();
    	Inventory inventory = inventoryService.getAvailableInventory();
    	
    	List<LineItem> lineItems = order.getLineItems(); 
    	List<Item> items = inventory.getItems();
    	
    	for(LineItem lineItem : lineItems) {
    		int itemNumber = lineItem.getItemNumber();
    		for(Item item : items) {
    			int itemNumberInventory = item.getItemNumber();
    			if(itemNumber == itemNumberInventory) {
    				if(item.getQuantity() < lineItem.getQuantity()) {
    					return false;
    				}
    			}
    		}
    	}
    	return true;
    }
    private void notifyUser(String customerEmail) {

    	String message = customerEmail + ":" +
    	       "Your order was successfully submitted. " + 
    	     	"You will hear from us when items are shipped. " + 
    	      	new Date();

    	System.out.println("Sending message: " + message);
    	jmsContext.createProducer().send(queue, message);
    	System.out.println("Message Sent!");
    	}

}
