package com.docomodigital.exerciseapi.dal.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the payment_transaction database table.
 * 
 */
@Entity
@Table(name="payment_transaction")
@NamedQuery(name="PaymentTransaction.findAll", query="SELECT p FROM PaymentTransaction p")
public class PaymentTransaction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_transaction_id_seq_generator")
    @SequenceGenerator(name="payment_transaction_id_seq_generator", sequenceName = "payment_transaction_id_seq")
	private Integer id;

	private double amount;

	@Column(name="phone_number")
	private String phoneNumber;

	@Column(name="product_id")
	private String productId;

	@Column(name="transaction_begin")
	private OffsetDateTime transactionBegin;

	@Column(name="transaction_end")
	private OffsetDateTime transactionEnd;

	@Column(name="transaction_id")
	private String transactionId;

	public PaymentTransaction() {
	    //
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public OffsetDateTime getTransactionBegin() {
		return this.transactionBegin;
	}

	public void setTransactionBegin(OffsetDateTime transactionBegin) {
		this.transactionBegin = transactionBegin;
	}

	public OffsetDateTime getTransactionEnd() {
		return this.transactionEnd;
	}

	public void setTransactionEnd(OffsetDateTime transactionEnd) {
		this.transactionEnd = transactionEnd;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}