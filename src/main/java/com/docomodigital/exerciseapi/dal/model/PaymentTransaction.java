package com.docomodigital.exerciseapi.dal.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

	@Column(name="updated_at")
	private OffsetDateTime updatedAt;

	@Column(name="created_at")
	private OffsetDateTime createdAt;

	@Column(name="failure_message")
	private String failureMessage;

	private String kind;

	private String msisdn;

	@Column(name="order_id")
	private String orderId;

	@Column(name="phone_number")
	private String phoneNumber;

	@Column(name="product_id")
	private String productId;

	@Column(name="refund_reason")
	private String refundReason;

	private String status;

	@Column(name="transaction_id")
	private String transactionId;

	//uni-directional many-to-one association to ConstantEnum
	@ManyToOne
	@JoinColumn(name="currency")
	private ConstantEnum constantEnum;

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

	public OffsetDateTime getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public OffsetDateTime getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getFailureMessage() {
		return this.failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public String getKind() {
		return this.kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getMsisdn() {
		return this.msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
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

	public String getRefundReason() {
		return this.refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public ConstantEnum getConstantEnum() {
		return this.constantEnum;
	}

	public void setConstantEnum(ConstantEnum constantEnum) {
		this.constantEnum = constantEnum;
	}

}