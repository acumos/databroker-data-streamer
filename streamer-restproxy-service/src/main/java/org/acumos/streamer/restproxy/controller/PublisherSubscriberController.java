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

import java.lang.invoke.MethodHandles;

import javax.ws.rs.core.MediaType;

import org.acumos.streamer.restproxy.model.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Streamer Catalog")
@SuppressWarnings("rawtypes")
@Controller
@RequestMapping(value = "v2/pubsubservice", produces = MediaType.TEXT_PLAIN)
public class PublisherSubscriberController {
	
private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

private static final String RESPOND_WITH_STATUS = "Respond with status";
private static final String RETURNS_A_JSON_OBJECT_WITH_STRING_PROVIDING_INFO="Returns the status of the messages posted.";
private static final String CREATED="Created";
private static final String BAD_REQUEST ="Bad Request";
private static final String UNAUTHORIZED ="Unauthorized";
private static final String FORBIDDEN ="Forbidden";
private static final String SERVICE_NOT_AVAILABLE  ="Service not available";
private static final String UNEXPECTED_RUNTIME_ERROR ="Unexpected Runtime error";
private static final String TOPIC ="Topic";
private static final String AUTHORIZATION ="Authorization";
private static final String RESPOND_WITH_MESSAGES="Respond back with mesaages";
private static final String RETURNS_JSON_OBJECT_WITH_MSG_DETAILS="Returns Json Object With Message Details";
private static final String OK="ok";
private static final String GROUP_NAME="groupName"; 
private static final String GROUP_ID="groupId";

	@Autowired
	PublisherSubscriberServiceImpl service;
	
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = RESPOND_WITH_STATUS , notes = RETURNS_A_JSON_OBJECT_WITH_STRING_PROVIDING_INFO, response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = CREATED),
			@ApiResponse(code = 400, message = BAD_REQUEST), 
			@ApiResponse(code = 401, message = UNAUTHORIZED),
			@ApiResponse(code = 403, message = FORBIDDEN),
			@ApiResponse(code = 404, message = SERVICE_NOT_AVAILABLE),
			@ApiResponse(code = 500, message = UNEXPECTED_RUNTIME_ERROR) })
	@ResponseBody
	public ResponseEntity sendMessageToKafkaTopic(@RequestHeader(AUTHORIZATION) String authorization,@RequestHeader(TOPIC) String topic,
			@RequestBody String msgs) {
		
		logger.info("Sending messages to the Kafka Topic:");
		
		return service.postMsgsToKafka(msgs,topic );
	}
	
	@RequestMapping(value = "/{groupName}/{groupId}", method = RequestMethod.GET)
	@ApiOperation(value = RESPOND_WITH_MESSAGES, notes = RETURNS_JSON_OBJECT_WITH_MSG_DETAILS, response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = OK), @ApiResponse(code = 400, message = BAD_REQUEST),
			@ApiResponse(code = 401, message = UNAUTHORIZED), @ApiResponse(code = 403, message = FORBIDDEN),
			@ApiResponse(code = 404, message = SERVICE_NOT_AVAILABLE),
			@ApiResponse(code = 500, message = UNEXPECTED_RUNTIME_ERROR) })
	@ResponseBody
	public ResponseEntity recieveMessageFromKafkaTopic(@RequestHeader(AUTHORIZATION) String authorization,@RequestHeader(TOPIC) String topic,
			@PathVariable(GROUP_NAME) String groupName,@PathVariable(GROUP_ID) String groupId) {
		
		logger.info("Getting Messages from existing Topic from Kafka Queue:" +topic );
		
		return service.getMsgsFromKafka(authorization, topic, groupName, groupId);
	}

}
