/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.streamer.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acumos.streamer.model.ErrorModel;

/**
 * This class serves as a custom exception. 
 * @author am375y
 *
 */
public class DataStreamerException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private int _code = Status.INTERNAL_SERVER_ERROR.getStatusCode();;
	
	/**
	 * Default constructor
	 */
	public DataStreamerException() {
		super();
	}

	/**
	 * @param Message
	 * Custom message is going to display
	 */
	public DataStreamerException(String Message) {
		super(Message);
	}
	
	public DataStreamerException(String Message, int code) {
		super(Message);
		this._code = code;
	}

	public Response toResponse() {
		ErrorModel error = new ErrorModel().message(this.getMessage());
		return Response.status(Status.fromStatusCode(_code)).entity(error).build();
	}
}
