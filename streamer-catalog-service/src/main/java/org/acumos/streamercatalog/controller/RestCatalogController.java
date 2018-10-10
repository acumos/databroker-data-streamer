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
package org.acumos.streamercatalog.controller;

import java.lang.invoke.MethodHandles;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acumos.streamercatalog.model.CatalogObject;
import org.acumos.streamercatalog.model.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Streamer Catalog")
@Controller
@SuppressWarnings("rawtypes")
@RequestMapping(value = "v2/streamers", produces = MediaType.APPLICATION_JSON)
public class RestCatalogController {
	
	private static final String RESPOND_TO_A_NEW_STREAMER_URL = "Respond a new streamer URL";
	private static final String RETURNS_A_JSON_OBJECT_WITH_STRING_PROVIDING_INFO="Returns a JSON object with a string providing info about new streamer.";
	private static final String CREATED="Created";
	private static final String BAD_REQUEST ="Bad Request";
	private static final String UNAUTHORIZED ="Unauthorized";
	private static final String FORBIDDEN ="Forbidden";
	private static final String SERVICE_NOT_AVAILABLE  ="Service not available";
	private static final String UNEXPECTED_RUNTIME_ERROR ="Unexpected Runtime error";
	private static final String AUTHORIZATION ="Authorization";
	private static final String RESPOND_UPDATED_STREAMER="Respond a updated streamer";
	private static final String JSON_OBJECT_WITH_INFO_ABOUT_UPDATED_STREAMER= "Returns a JSON object with a string providing info about updated streamer";
	private static final String STREAMER_KEY= "streamerKey";
	private static final String UPDATED="Updated";
	private static final String RETURNS_JSON_OBJECT_WITH_STREAMER_DETAILS="Returns a JSON object with streamer details";
	private static final String RESPOND_A_STREAMER_OBJECT="Respond a streamer object";
	private static final String OK="OK";
	private static final String MODE="mode";
	private static final String RESPOND_LIST_OF_STREAMER="Respond a list of streamer";
	private static final String RETURNS_JSON_OBJECT_WITH_LIST_OF_STREAMERS_QUERY_PARAM="Returns a JSON object with a list of streamers according to the query params, if provided.";
	private static final String TEXT_SEARCH="textSearch";
	private static final String CATEGORY="category";
	private static final String RESPOND_NO_CONTENT ="Respond no content";
	private static final String RETURNS_NO_CONTENT_AFTER_DELETING_STREAMER="Returns No content after deleting a streamer corrsponding to provided key.Uses 'world' if a name is not specified";
	private static final String NO_CONTENT="No Content";
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private RestCatalogServiceImpl service;
	
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = RESPOND_TO_A_NEW_STREAMER_URL , notes = RETURNS_A_JSON_OBJECT_WITH_STRING_PROVIDING_INFO, response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = CREATED),
			@ApiResponse(code = 400, message = BAD_REQUEST), 
			@ApiResponse(code = 401, message = UNAUTHORIZED),
			@ApiResponse(code = 403, message = FORBIDDEN),
			@ApiResponse(code = 404, message = SERVICE_NOT_AVAILABLE),
			@ApiResponse(code = 500, message = UNEXPECTED_RUNTIME_ERROR) })
	@ResponseBody
	public ResponseEntity saveCatalog(@RequestHeader(AUTHORIZATION) String authorization,
			@RequestBody CatalogObject objCatalog) {
		logger.info("Saving new catalog object:");
		return service.saveCatalog(authorization, objCatalog);
	}

	
	@RequestMapping(value = "/{streamerKey}", method = RequestMethod.PUT)
	@ApiOperation(value = RESPOND_UPDATED_STREAMER, notes = JSON_OBJECT_WITH_INFO_ABOUT_UPDATED_STREAMER, response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = UPDATED),
			@ApiResponse(code = 400, message = BAD_REQUEST), @ApiResponse(code = 401, message = UNAUTHORIZED),
			@ApiResponse(code = 403, message = FORBIDDEN),
			@ApiResponse(code = 404, message = SERVICE_NOT_AVAILABLE),
			@ApiResponse(code = 500, message = UNEXPECTED_RUNTIME_ERROR) })
	@ResponseBody
	public ResponseEntity updateCatalog(@RequestHeader(AUTHORIZATION) String authorization,
			@PathVariable(STREAMER_KEY) String catalogKey, @RequestBody CatalogObject objCatalog) {
		
		logger.info("Updating an existing catalogObject:" +catalogKey);
		
		return service.updateCatalog(authorization, catalogKey, objCatalog);
	}

	
	@RequestMapping(value = "/{streamerKey}", method = RequestMethod.GET)
	@ApiOperation(value = RESPOND_A_STREAMER_OBJECT, notes = RETURNS_JSON_OBJECT_WITH_STREAMER_DETAILS, response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = OK), @ApiResponse(code = 400, message = BAD_REQUEST),
			@ApiResponse(code = 401, message = UNAUTHORIZED), @ApiResponse(code = 403, message = FORBIDDEN),
			@ApiResponse(code = 404, message = SERVICE_NOT_AVAILABLE),
			@ApiResponse(code = 500, message = UNEXPECTED_RUNTIME_ERROR) })
	@ResponseBody
	public ResponseEntity getCatalog(@RequestHeader(AUTHORIZATION) String authorization,
			@PathVariable(STREAMER_KEY) String catalogKey, @RequestParam(MODE) String mode) {
		
		logger.info("Getting details of an existing catalogObject in particular mode:" +catalogKey +mode);
		
		return service.getCatalog(authorization, catalogKey, mode);
	}

	
	@Produces({ MediaType.APPLICATION_JSON })
	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = RESPOND_LIST_OF_STREAMER, notes = RETURNS_JSON_OBJECT_WITH_LIST_OF_STREAMERS_QUERY_PARAM, response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = OK), @ApiResponse(code = 400, message = BAD_REQUEST),
			@ApiResponse(code = 401, message = UNAUTHORIZED), @ApiResponse(code = 403, message = FORBIDDEN),
			@ApiResponse(code = 404, message = SERVICE_NOT_AVAILABLE),
			@ApiResponse(code = 500, message = UNEXPECTED_RUNTIME_ERROR) })
	@ResponseBody
	public ResponseEntity getCatalogs(@RequestHeader(AUTHORIZATION) String authorization,
			@RequestParam(value = CATEGORY, required = false) String category, @RequestParam(value =TEXT_SEARCH,required = false) String textSearch) {
		
		logger.info("Getting details of an existing catalogObject(s) in particular category or textsearch:" +category +textSearch);
		
		return service.getCatalogs(authorization, category, textSearch);
	}
	
	@RequestMapping(value ="/{streamerKey}", method = RequestMethod.DELETE)
	@ApiOperation(value = RESPOND_NO_CONTENT, notes = RETURNS_NO_CONTENT_AFTER_DELETING_STREAMER, response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 204, message = NO_CONTENT),
			@ApiResponse(code = 400, message = "BAD_REQUEST"), @ApiResponse(code = 401, message = UNAUTHORIZED),
			@ApiResponse(code = 403, message = FORBIDDEN),
			@ApiResponse(code = 404, message = SERVICE_NOT_AVAILABLE),
			@ApiResponse(code = 500, message = UNEXPECTED_RUNTIME_ERROR) })
	@ResponseBody
	public ResponseEntity deleteCatalog(@RequestHeader(AUTHORIZATION) String authorization,
			@PathVariable(STREAMER_KEY) String catalogKey) {
		
		logger.info("Deleting an existing catalogObject:" +catalogKey);
		
		return service.deleteCatalog(authorization, catalogKey);
	}

}
