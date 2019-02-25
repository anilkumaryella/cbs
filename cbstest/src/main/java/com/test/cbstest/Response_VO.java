package com.test.cbstest;


public class Response_VO {

	private String responseCode;
	private String responseMsg;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public Response_VO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Response_VO(String responseCode, String responseMsg) {
		super();
		this.responseCode = responseCode;
		this.responseMsg = responseMsg;
	}

	@Override
	public String toString() {
		return "Response_VO [responseCode=" + responseCode + ", responseMsg=" + responseMsg + "]";
	}

}
