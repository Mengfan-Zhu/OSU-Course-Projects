package edu.osu.cse5234.business.view;

import java.util.List;
import java.io.Serializable; 

public class Inventory implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4741276659145606965L;
	private List<Item> items;

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
}
