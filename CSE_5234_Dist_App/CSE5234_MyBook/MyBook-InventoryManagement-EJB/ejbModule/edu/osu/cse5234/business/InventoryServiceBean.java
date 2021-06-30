package edu.osu.cse5234.business;

import edu.osu.cse5234.business.view.Inventory;
import edu.osu.cse5234.business.view.InventoryService;
import edu.osu.cse5234.business.view.Item;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.*;

/**
 * Session Bean implementation class InventoryServiceBean
 */
@Stateless
public class InventoryServiceBean implements InventoryService {

    /**
     * Default constructor. 
     */
    public InventoryServiceBean() {
        // TODO Auto-generated constructor stub
    }
    String MY_QUERY = "Select i from Item i";
    @PersistenceContext
    EntityManager entityManager;

    public EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	@Override
    public Inventory getAvailableInventory() {
		Inventory inventory = new Inventory();
		List<Item> items = entityManager.createQuery(MY_QUERY, Item.class).getResultList();
		inventory.setItems(items);
		return inventory;
	}
	@Override
	public boolean validateQuantity(List<Item> items) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean updateInventory(List<Item> items) {
		// TODO Auto-generated method stub
		return true;
	}

}
