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

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import org.acumos.streamer.exception.DataStreamerException;

public interface ConsumerService {
	
	/**
	 * Does receive data operation based on the data received from the 
	 * files through the attachment.
	 * @param user
	 * @param authorization
	 * @param feedAuthorization
	 * @param attachedFiles
	 * @return String publishId
	 */
	public String recieveData(String user, String authorization, String feedAuthorization, 
			MultipartBody attachedFiles);
	
	/**
	 * Does delete data operation based on the data received from the 
	 * files through the attachment.
	 * @param user
	 * @param authorization
	 * @param feedAuthorization
	 * @param attachedFiles
	 * @return String publishId
	 */
	
	public String deleteData(String user, String authorization, String feedAuthorization, 
			MultipartBody attachedFiles);
	
	/**
	 * Does data operations based on the data received from the files through the attachment.
	 * @param user
	 * @param authorization
	 * @param feedAuthorization
	 * @param path
	 * @param catalogKey
	 * @param attachedFiles
	 * @return String publishId
	 * @throws DataStreamerException
	 */
	
	public String operateData(String user, String authorization, String feedAuthorization,
			String path, String catalogKey, InputStream attachedFiles)  
			throws DataStreamerException;
}
