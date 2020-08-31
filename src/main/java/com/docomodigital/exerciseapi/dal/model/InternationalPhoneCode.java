package com.docomodigital.exerciseapi.dal.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the international_phone_codes database table.
 * 
 */
@Entity
@Table(name="international_phone_codes")
@NamedQuery(name="InternationalPhoneCode.findAll", query="SELECT i FROM InternationalPhoneCode i")
public class InternationalPhoneCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column(name="area_code")
	private String areaCode;

	private String country;

	//uni-directional many-to-one association to ConstantEnum
	@ManyToOne
	@JoinColumn(name="currency")
	private ConstantEnum constantEnum;

	public InternationalPhoneCode() {
	    //
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAreaCode() {
		return this.areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public ConstantEnum getConstantEnum() {
		return this.constantEnum;
	}

	public void setConstantEnum(ConstantEnum constantEnum) {
		this.constantEnum = constantEnum;
	}

}