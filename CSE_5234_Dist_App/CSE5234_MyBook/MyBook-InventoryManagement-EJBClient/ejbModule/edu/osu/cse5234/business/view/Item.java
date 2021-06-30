package edu.osu.cse5234.business.view;
import java.io.Serializable;

import javax.persistence.*;



@Entity
@Table (name="ITEM") 
public class Item implements Serializable{
	public Item() {
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4385992098997286072L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private int id;
	@Column(name="ITEM_NUMBER")
	private int itemNumber;
	@Column(name="NAME")
	private String name;
	@Column(name="DESCRIPTION")
	private String description;
	@Column(name="UNIT_PRICE")
	private String price;
	@Column(name="AVAILABLE_QUANTITY")
	private int quantity;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPrice() {
		return price;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(int itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
