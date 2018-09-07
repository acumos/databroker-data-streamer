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

import javax.ws.rs.Produces;
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

@Api(value = "CMLP Streamer")
@Controller
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON)
public class RestConsumerController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	RestConsumerServiceImpl service;
	
	@RequestMapping(value = "{catalogKey}/{fileName}", method = RequestMethod.PUT)
	@ApiOperation(value = "Responds back with scoring result", notes = "Returns a No content successful message after publishing the data recieved and scoring perfromed after using the same data.", response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""), @ApiResponse(code = 400, message = "Bad Payload"),
			@ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	@ResponseBody
	public Response operateData(@RequestHeader("Authorization") String authorization,
			@RequestParam("feedAuth") String feedAuthorization, @PathVariable("catalogKey") String catalogKey,
			@PathVariable("fileName") String fileName,
			@Multipart(value = "file", type = "application/octet-stream") InputStream attachedFiles) {
		
		logger.debug("in operateData in RestConsumerCOntroller");
		
		return service.operateData(authorization, feedAuthorization, catalogKey, fileName, attachedFiles);
	}

}
