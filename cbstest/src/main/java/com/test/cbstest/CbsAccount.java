package com.test.cbstest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="CBS_ACCOUNT")
@IdClass(CbsAccountPK.class)
public class CbsAccount {
	
	@Id
	@Column(name="ID")
	private int id;

	@Column(name="MOBILE_NO")
	private String mobileNo;
	
	@Column(name="ACCOUNT_NO")
	private String accountNo;
	
	@Column(name="BAL")
	private String bal;
	
	@Column(name="STATUS")
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBal() {
		return bal;
	}

	public void setBal(String bal) {
		this.bal = bal;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CbsAccount [id=" + id + ", mobileNo=" + mobileNo + ", accountNo=" + accountNo + ", bal=" + bal
				+ ", status=" + status + "]";
	}
	
	
}
