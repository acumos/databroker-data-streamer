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


import java.net.URI;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;

import org.acumos.streamercatalog.common.DataStreamerCatalogUtil;
import org.acumos.streamercatalog.exception.DataStreamerException;
import org.acumos.streamercatalog.model.CatalogObject;
import org.acumos.streamercatalog.model.RelativeModel;
import org.acumos.streamercatalog.model.ResponseMessage;
import org.acumos.streamercatalog.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RestCatalogServiceImpl implements RestCatalogService {

	private static final String MSG_ROUTER = "MsgRouter";

	private static Logger log = LoggerFactory.getLogger(RestCatalogServiceImpl.class);

	@Autowired
	private CatalogService aCatalogService;

	@Autowired
	private HttpServletRequest request;

	@Override
	public Response saveCatalog(String authorization, String codeCloudAuthorization, CatalogObject objCatalog) {
		ResponseMessage aResponseMessage = new ResponseMessage();

		String user = DataStreamerCatalogUtil.getRemoteUser(request);
		log.info("RestCatalogServiceImpl::saveCatalog::the request has been itiated by user: " + user);
		
		try {
			
			DataStreamerCatalogUtil.validateRequest(user, codeCloudAuthorization, objCatalog);
			
		} catch (Exception e) {
			aResponseMessage.setCode(400);
			aResponseMessage.setMessage(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(aResponseMessage).build();
		}

		try {
			log.info("RestCatalogServiceImpl::saveCatalog()::intiating save request.");
			
			String url = request.getRequestURL().toString();
			
			String catalogKey = aCatalogService.saveCatalog(user, authorization, codeCloudAuthorization, objCatalog);
			log.info("RestCatalogServiceImpl::saveCatalog()::catalog key generated is: " + catalogKey);
			
			if(objCatalog.getCategory().equalsIgnoreCase(MSG_ROUTER)) {
				
				URI location = new URI(url + objCatalog.getCatalogKey());
				return Response.status(Status.CREATED).entity("{\"subscriberUrl\":\"" +  objCatalog.getSubscriberUrl()+ "\",\"catalogKey\":\""+  objCatalog.getCatalogKey()+"\"}").contentLocation(location).build();
			}
			
			String subscriberUrl = DataStreamerCatalogUtil.getEnv("streamer_base_url",
					DataStreamerCatalogUtil.getComponentPropertyValue("streamer_base_url")) + catalogKey;
			log.info("RestCatalogServiceImpl::saveCatalog()::generated subscriber URL is: " + subscriberUrl);
			
			URI location = new URI(url + catalogKey);
			
			return Response.status(Status.CREATED).entity("{\"subscriberUrl\":\"" +  objCatalog.getSubscriberUrl()+ "\",\"catalogKey\":\""+  objCatalog.getCatalogKey()+"\"}").contentLocation(location).build();
			
		} catch (Exception e) {
			log.error("Error in saveCatalog:",e);
			aResponseMessage.setCode(500);
			aResponseMessage.setMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(aResponseMessage).build();
		} 
	}

	@Override
	public Response updateCatalog(String authorization, String codeCloudAuthorization, String catalogKey,
			CatalogObject objCatalog) {
		ResponseMessage aResponseMessage = new ResponseMessage();

		String user = DataStreamerCatalogUtil.getRemoteUser(request);
		log.info("RestCatalogServiceImpl::updateCatalog()::the request has been intiated by user: " + user);
		String url = request.getRequestURL().toString();
		
		try {
			if (!(user != null)) {
				throw new DataStreamerException("Please provide a valid credentials", 400);
			}

			if (!(objCatalog.getModelKey() != null) && objCatalog.getModelKey().isEmpty()) {
				throw new DataStreamerException("Please provide a model key", 400);
			}

			if (!(objCatalog.getModelVersion() != null) && objCatalog.getModelVersion().isEmpty()) {
				throw new DataStreamerException("Please provide a model version", 400);
			}

			if (!(objCatalog.getPredictorId() != null) && objCatalog.getPredictorId().isEmpty()) {
				throw new DataStreamerException("Please provide a predictor", 400);
			}

			if (!(codeCloudAuthorization != null) && !(codeCloudAuthorization.isEmpty())) {
				throw new DataStreamerException("Please provide CodeCloudAuthorization header", 400);
			}

			if (!(objCatalog.getPublisherUrl() != null) && objCatalog.getPublisherUrl().isEmpty()) {
				throw new DataStreamerException("Please provide a publisher URL", 400);
			}
			
			
		} catch (Exception e) {
			log.error("Exception in updateCatalog",e);
			aResponseMessage.setCode(500);
			aResponseMessage.setMessage(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(aResponseMessage).build();
		}

		try {
			log.info("RestCatalogServiceImpl::updateCatalog()::intiating update request.");
			String responseCatalogKey = aCatalogService.updateCatalog(user, authorization, codeCloudAuthorization,
					catalogKey, objCatalog);
			log.info("RestCatalogServiceImpl::updateCatalog()::update completed for key: " + responseCatalogKey);
			URI location = new URI(url + objCatalog.getCatalogKey());
			return Response.status(Status.OK).entity("{\"subscriberUrl\":\"" +  objCatalog.getSubscriberUrl()+ "\",\"catalogKey\":\""+  objCatalog.getCatalogKey()+"\"}").contentLocation(location).build();
			//return Response.status(Status.NO_CONTENT).build();
		} catch (Exception e) {
			log.error("Exception in updateCatalog",e);
			aResponseMessage.setCode(500);
			aResponseMessage.setMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(aResponseMessage).build();
		}
	}

	@Override
	public Response getCatalog(String authorization, String catalogKey, String mode) {
		ResponseMessage aResponseMessage = new ResponseMessage();
		
		String user = DataStreamerCatalogUtil.getRemoteUser(request);
		log.info("RestCatalogServiceImpl::getCatalog()::the request has been intiated by user: " + user);
		try {
			if (mode != null && !mode.isEmpty() && mode.equals("concise")){
				log.info("RestCatalogServiceImpl::getPredictorAssociation()::intiating request");
				RelativeModel aRelativeModel = aCatalogService.getPredictorAssociation(user, authorization, catalogKey);
				log.info("RestCatalogServiceImpl::getPRedictorAssociaton::request completed");
				return Response.status(Status.OK).entity(aRelativeModel).build();
			}
			else{
				log.info("RestCatalogServiceImpl::getCatalog()::intiating request");
				String catalog = aCatalogService.getCatalog(user, authorization, catalogKey);
				log.info("RestCatalogServiceImpl::getCatalog()::service returned the entry: " + catalog);
				return Response.status(Status.OK).entity(catalog).build();
			}
		} catch (Exception e) {
			log.error("Exception in getCatalog",e);
			aResponseMessage.setCode(500);
			aResponseMessage.setMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(aResponseMessage).build();
		}
	}

	@Override
	public Response getCatalogs(String authorization, String category, String textSearch) {
		ResponseMessage aResponseMessage = new ResponseMessage();
		
		String user = DataStreamerCatalogUtil.getRemoteUser(request);
		log.info("RestCatalogServiceImpl::getCatalogs()::the request has been intiated by user " + user);
		try {
			log.info("RestCatalogServiceImpl::getCatalogs()::intiating request");
			ArrayList<String> catalogs = aCatalogService.getCatalogs(user, authorization, category, textSearch);
			log.info("RestCatalogServiceImpl::getCatalogs()::no of docments being returned is " + catalogs.size());
			return Response.status(Status.OK).entity(catalogs.toString()).build();
		} catch (Exception e) {
			log.error("Exception in getCatalogs",e);
			aResponseMessage.setCode(500);
			aResponseMessage.setMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(aResponseMessage).build();
		}
	}

	@Override
	public Response deleteCatalog(String authorization, String catalogKey) {
		ResponseMessage aResponseMessage = new ResponseMessage();
		
		String user = DataStreamerCatalogUtil.getRemoteUser(request);
		log.info("RestCatalogServiceImpl::deleteCatalog()::the request has been intiated by user " + user);
		try {
			log.info("RestCatalogServiceImpl::deleteCatalog()::intiating request");
			boolean deleteFlag = aCatalogService.deleteCatalog(user, authorization, catalogKey);
			log.info("RestCatalogServiceImpl::deleteCatalog()::deletion resulted in " + deleteFlag);
			return deleteFlag ? Response.status(Status.NO_CONTENT).build()
					: Response.status(Status.NOT_ACCEPTABLE).build();
		} catch (Exception e) {
			log.error("Exception in deleteCatalog",e);
			aResponseMessage.setCode(500);
			aResponseMessage.setMessage(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(aResponseMessage).build();
		}
	}
}
