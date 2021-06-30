package edu.osu.cse5234.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "CUSTOMER_ORDER")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "CUSTOMER_ORDER_ID_FK")
	private List<LineItem> lineItems;
	
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	
	@Column(name = "CUSTOMER_EMAIL")
	private String emailAddress;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "SHIPPING_INFO_ID_FK")
	private ShippingInfo shippingInfo;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "PAYMENT_INFO_ID_FK")
	private PaymentInfo paymentInfo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public ShippingInfo getShippingInfo() {
		return shippingInfo;
	}

	public void setShippingInfo(ShippingInfo shippingInfo) {
		this.shippingInfo = shippingInfo;
	}

	public PaymentInfo getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(PaymentInfo paymentInfo) {
		this.paymentInfo = paymentInfo;
	}
	
	public Order() {
		this.lineItems = new ArrayList<LineItem>();
	}

	public List<LineItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

}