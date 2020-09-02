package com.docomodigital.exerciseapi.dal.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the constant_enum database table.
 * 
 */
@Entity
@Table(name="constant_enum")
@NamedQuery(name="ConstantEnum.findAll", query="SELECT c FROM ConstantEnum c")
public class ConstantEnum implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="enum_code")
	private Integer enumCode;

	private Boolean disabled;

	@Column(name="enum_desc")
	private String enumDesc;

	@Column(name="enum_label")
	private String enumLabel;

	@Column(name="enum_name")
	private String enumName;

	@Column(name="enum_type")
	private String enumType;

	public ConstantEnum() {
	    //
	}
	
	public ConstantEnum(Integer code) {
	    this.enumCode = code;
	}

	public Integer getEnumCode() {
		return this.enumCode;
	}

	public void setEnumCode(Integer enumCode) {
		this.enumCode = enumCode;
	}

	public Boolean getDisabled() {
		return this.disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public String getEnumDesc() {
		return this.enumDesc;
	}

	public void setEnumDesc(String enumDesc) {
		this.enumDesc = enumDesc;
	}

	public String getEnumLabel() {
		return this.enumLabel;
	}

	public void setEnumLabel(String enumLabel) {
		this.enumLabel = enumLabel;
	}

	public String getEnumName() {
		return this.enumName;
	}

	public void setEnumName(String enumName) {
		this.enumName = enumName;
	}

	public String getEnumType() {
		return this.enumType;
	}

	public void setEnumType(String enumType) {
		this.enumType = enumType;
	}

}