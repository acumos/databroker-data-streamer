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

import javax.servlet.http.HttpServletRequest;

import org.acumos.streamer.common.DataStreamerUtil;
import org.acumos.streamer.common.JsonResponse;
import org.acumos.streamer.exception.DataStreamerException;
import org.acumos.streamer.service.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RestConsumerServiceImpl implements RestConsumerService {
	
	private static final String SUCCESS = "success";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private ConsumerService aConsumerService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private DataStreamerUtil dataStreamerUtil;
	


	@Override
	public ResponseEntity<JsonResponse> operateData(String authorization, String feedAuthorization, String catalogKey, String fileName,
			InputStream attachedFiles) {
		String user = dataStreamerUtil.getRemoteUser(request);
		
		try {
			String response = aConsumerService.operateData(user, authorization, feedAuthorization, fileName, catalogKey, attachedFiles);
			
			if (response.equals(SUCCESS)){
					return new ResponseEntity<JsonResponse>(new JsonResponse("") ,HttpStatus.OK);
				}
				
		} catch (DataStreamerException e) {
			logger.info("RestConsumerService::operateData()::There was error in response from RestConsumerService: operateData . operate data returned:" + e.getMessage());
			return new ResponseEntity<JsonResponse>(new JsonResponse(e.getMessage()) ,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JsonResponse>(new JsonResponse("") ,HttpStatus.NO_CONTENT);
	}

}
