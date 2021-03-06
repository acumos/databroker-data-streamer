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

import java.util.Map;

public interface PublisherService {
	
	/**
	 * Publishes the data received from the predictor to the data router.
	 * @param catalogKey 
	 * 				catalogKey of the catalogObject used to get those catalog details
	 * @param feedAuthorization 
	 * 				feedAuthorization is the user name and password
	 * @param filePath
	 * 				path of the file where it has to be published
	 * @param publisherUrl
	 * 				publisherUrl of the catalogObject
	 * @param metaDataMap
	 * 				metaData of the file to be published
	 * @return String publishId
	 */
	public String publish(String catalogKey, String feedAuthorization, String filePath, 
			String publisherUrl, Map<String, String> metaDataMap);

}
