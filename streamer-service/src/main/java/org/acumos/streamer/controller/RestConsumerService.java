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

package org.acumos.streamer.controller;

import java.io.InputStream;

import org.acumos.streamer.common.JsonResponse;
import org.springframework.http.ResponseEntity;

public interface RestConsumerService {
	
	/**
	 * Does data operation based on the data received from the files through the 
	 * datastreamer project.
	 * @param authorization
	 * 				authorization header details
	 * @param feedAuthorization
	 * 				username and password from authorization
	 * @param catalogKey
	 * 				catalogKey to get the details of the catalog object
	 * @param fileName
	 * 				filename of where the results to be published 
	 * @param attachedFiles
	 * 				the files that are attached as part of the request
	 * @return status of the service after the operation
	 */
	public ResponseEntity<JsonResponse> operateData(String authorization, String feedAuthorization, 
			String catalogKey, String fileName,
			InputStream attachedFiles);

}
