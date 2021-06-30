package edu.osu.cse5234.model;

import javax.persistence.*;

@Entity
@Table(name = "PAYMENT_INFO")
public class PaymentInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "CARD_NUM")
	private String creditCardNumber;
	
	@Column(name = "EXP_DATE")
	private String expirationDate;
	
	@Column(name = "CVV")
	private String cvvCode;
	
	@Column(name = "HOLDER_NAME")
	private String cardHolderName;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCreditCardNumber() {
		return creditCardNumber;
	}
	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}
	
	public String getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public String getCvvCode() {
		return cvvCode;
	}
	
	public void setCvvCode(String cvvCode) {
		this.cvvCode = cvvCode;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}
	
	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}
}
