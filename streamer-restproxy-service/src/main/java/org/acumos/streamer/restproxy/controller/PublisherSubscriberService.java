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
package org.acumos.streamer.restproxy.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("rawtypes")
public interface PublisherSubscriberService {
	
	/**
	 * Posts Messages to Kafka Topic.
	 * @param authorization
	 * 				basic authorization header value
	 * @param topic
	 * 				kafka topic where the messages have to be posted
	 * @return ResponseEntity
	 */
	public ResponseEntity postMsgsToKafka(String authorization,String topic);
	
	/**
	 * Saves catalog object to the database.
	 * @param user 
	 * 				user details from the request
	 * @param authorization
	 * 				basic authorization header value
	 * @param topic
	 * 				kafka topic where the messages have to be posted
	 * @param groupName
	 * 				kafka group name
	 * @param groupId
	 * 				kafka groupId
	 * @return ResponseEntity
	 */
	public ResponseEntity getMsgsFromKafka(String authorization,String topic,String groupName, String groupId);

}
