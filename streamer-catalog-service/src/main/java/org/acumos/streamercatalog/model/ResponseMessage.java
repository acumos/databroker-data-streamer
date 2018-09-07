package org.acumos.streamercatalog.model;

import org.json.JSONObject;

public class ResponseMessage {
	private int code;
	private String message;
	private JSONObject data;
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param i the code to set
	 */
	public void setCode(int i) {
		this.code = i;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the data
	 */
	public JSONObject getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(JSONObject data) {
		this.data = data;
	}
	

}
