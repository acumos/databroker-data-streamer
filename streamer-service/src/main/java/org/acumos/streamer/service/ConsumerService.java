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

package org.acumos.streamer.service;

import java.io.InputStream;

import org.acumos.streamer.exception.DataStreamerException;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

public interface ConsumerService {
	
	/**
	 * Does receive data operation based on the data received from the 
	 * files through the attachment.
	 * @param user
	 * 			user from the authHeader	
	 * @param authorization
	 * 			authorization header value	
	 * @param feedAuthorization
	 * 			feedAuthorization with username and password
	 * @param attachedFiles
	 * 			files which are attached along with the request
	 * @return String publishId
	 */
	public String receiveData(String user, String authorization, String feedAuthorization, 
			MultipartBody attachedFiles);
	
	/**
	 * Does delete data operation based on the data received from the 
	 * files through the attachment.
	 * @param user
	 * 				user from the authHeader
	 * @param authorization
	 * 				authorization header value
	 * @param feedAuthorization
	 * 				feedAuthorization with username and password
	 * @param attachedFiles
	 * 				files which are attached along with the request
	 * @return String publishId
	 */
	
	public String deleteData(String user, String authorization, String feedAuthorization, 
			MultipartBody attachedFiles);
	
	/**
	 * Does data operations based on the data received from the files through the attachment.
	 * @param user
	 * 				user from the authHeader
	 * @param authorization
	 * 				authorization header value
	 * @param feedAuthorization
	 * 				feedAuthorization with username and password
	 * @param path
	 * 				fileid to get the filename
	 * @param catalogKey
	 * 				catalogKey for retrieving catalog details
	 * @param attachedFiles
	 * 				files which are attached along with the request
	 * @return String publishId
	 * @throws DataStreamerException
	 * 				Custom Exception for the application.
	 */
	
	public String operateData(String user, String authorization, String feedAuthorization,
			String path, String catalogKey, InputStream attachedFiles)  
			throws DataStreamerException;
}
