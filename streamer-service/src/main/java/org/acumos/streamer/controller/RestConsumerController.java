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
import java.lang.invoke.MethodHandles;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acumos.streamer.model.ResponseMessage;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Consumer")
@Controller
@RequestMapping(value = "/v2/consumer", produces = MediaType.APPLICATION_JSON)
public class RestConsumerController {

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	private static final String FILE = "file";

	private static final String FILE_NAME = "fileName";

	private static final String CATALOG_KEY = "catalogKey";

	private static final String FEED_AUTH = "feedAuth";

	private static final String AUTHORIZATION = "Authorization";

	private static final String UNEXPECTED_RUNTIME_ERROR = "Unexpected Runtime error";

	private static final String BAD_PAYLOAD = "Bad Payload";

	private static final String SERVICE_NOT_AVAILABLE = "Service not available";

	private static final String RETURNS_A_NO_CONTENT_SUCCESSFUL_MESSAGE_AFTER_PUBLISHING_DATA_AND_SCORING_PERFROMED_USING_SAME_DATA = "Returns a No content successful message after publishing the data recieved and scoring perfromed after using the same data.";

	private static final String RESPONDS_BACK_WITH_SCORING_RESULT = "Responds back with scoring result";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	RestConsumerServiceImpl service;
	
	@RequestMapping(value = "{catalogKey}/{fileName}", method = RequestMethod.PUT)
	@ApiOperation(value = RESPONDS_BACK_WITH_SCORING_RESULT, notes = RETURNS_A_NO_CONTENT_SUCCESSFUL_MESSAGE_AFTER_PUBLISHING_DATA_AND_SCORING_PERFROMED_USING_SAME_DATA, response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""), @ApiResponse(code = 400, message = BAD_PAYLOAD),
			@ApiResponse(code = 404, message = SERVICE_NOT_AVAILABLE),
			@ApiResponse(code = 500, message = UNEXPECTED_RUNTIME_ERROR) })
	@ResponseBody
	public Response operateData(@RequestHeader(AUTHORIZATION) String authorization,
			@RequestParam(FEED_AUTH) String feedAuthorization, @PathVariable(CATALOG_KEY) String catalogKey,
			@PathVariable(FILE_NAME) String fileName,
			@Multipart(value = FILE, type = APPLICATION_OCTET_STREAM) InputStream attachedFiles) {
		
		logger.debug("in operateData in RestConsumerCOntroller");
		
		return service.operateData(authorization, feedAuthorization, catalogKey, fileName, attachedFiles);
	}

}
