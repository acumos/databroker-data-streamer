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

package org.acumos.streamercatalog.service;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.acumos.streamercatalog.common.HelperTool;
import org.acumos.streamercatalog.connection.DbUtilities;
import org.acumos.streamercatalog.controller.RestCatalogServiceImpl;

import org.acumos.streamercatalog.exception.CmlpDataSrcException;
import org.acumos.streamercatalog.model.CatalogObject;
import org.acumos.streamercatalog.model.RelativeModel;
import com.mongodb.DBObject;

@Service
public class CatalogServiceImpl implements CatalogService {

	private static Logger log = LoggerFactory.getLogger(CatalogServiceImpl.class);

	@Autowired
	private DbUtilities connection;
	

	@Override
	public String saveCatalog(String user, String authorization, String codeCloudAuthorization, CatalogObject objCatalog)
			throws IOException, CmlpDataSrcException {

		log.info(
				"CatalogServiceImpl::saveCatalog()::fetching predictor details from predictor manager for predictor id: "
						+ objCatalog.getPredictorId());
		//To-Do get predictorId from modeldetails using modelKey as UI will be updated soon and it will no longer provide predictorId.
		//JSONObject modelDetails = HelperTool.getModelDetails(authorization,objCatalog.getModelKey());
		
		JSONObject predictorDetails = HelperTool.getPredictorDetails(authorization, objCatalog.getPredictorId());
		String catalogKey = null;

		log.info("CatalogServiceImpl::saveCatalog()::predictor details fetched. The total no of keys in json is "
				+ predictorDetails.length());
		log.info("CatalogServiceImpl::saveCatalog()::predictor details: " + predictorDetails.toString());
		if (predictorDetails.length() < 1) {
			log.info(
					"CatalogServiceImpl::saveCatalog()::there is no element in predictor details, json is less than one. Throwing exception.......");
			throw new CmlpDataSrcException(
					"The predictor id is invalid, as predictor manager didn't provide any information for the given predictor id");
		}

		if (predictorDetails.length() > 1) {
			if (objCatalog.getStreamerName().contains("am375y")){
				catalogKey = "pocsub";
			} else{
				catalogKey = HelperTool.getkey(user);
			}
//			catalogKey = "pocsub";// HelperTool.getkey(objCatalog.getNamespace(),
//			//catalogKey =  HelperTool.getkey(objCatalog.getNamespace(), user);		// user);
			
			objCatalog.setCatalogKey(catalogKey);
			objCatalog.setCreatedBy(user);
			objCatalog.setStatus(true);
			
			log.info("CatalogServiceImpl::saveCatalog()::checking if ingress for predcitor is present or not");
			if (predictorDetails.has("ingressUrl") && !predictorDetails.getString("ingressUrl").isEmpty()
					&& predictorDetails.getString("ingressUrl") != null) {
				log.info("CatalogServiceImpl::saveCatalog()::ingress URL for predictor "
						+ objCatalog.getPredictorId().replace("-", "_") + " is "
						+ predictorDetails.getString("ingressUrl"));
				String predictorUrl = predictorDetails.getString("ingressUrl");
				predictorUrl = predictorUrl.replaceAll("https://","http://");
				log.info("CatalogServiceImpl::saveCatalog():replacing https to http :predictorUrl "+predictorUrl );
				objCatalog.setPredictorUrl(
						predictorUrl + HelperTool.getEnv("predictor_scoring_url_suffix",
								HelperTool.getComponentPropertyValue("predictor_scoring_url_suffix")));
			} else {
				log.info(
						"CatalogServiceImpl::saveCatalog()::predictor manager didn't send ingress details for predictor "
								+ objCatalog.getPredictorId() + ". Throwing exception.........");
				throw new CmlpDataSrcException("The predictor manager didn't send back a URL for scoring");
			}
			
			log.info("CatalogServiceImpl::saveCatalog()::initiating insertion into mongo db.");
			
			if(objCatalog.getCategory().equalsIgnoreCase("dmapp")) {
				String subscriberUrl = HelperTool.getEnv("streamer_base_url",
						HelperTool.getComponentPropertyValue("streamer_base_url")) + catalogKey;
				
				objCatalog.setSubscriberUrl(subscriberUrl);
			}
			
			connection.insertCatalogDetails(user, authorization, codeCloudAuthorization, objCatalog);
		}
		return catalogKey;
	}

	@Override
	public String updateCatalog(String user, String authorization, String codeCloudAuthorization, String catalogKey, CatalogObject objCatalog)
			throws IOException, CmlpDataSrcException {

		JSONObject predictorDetails = HelperTool.getPredictorDetails(authorization, objCatalog.getPredictorId());
		if (predictorDetails.length() < 1) {
			throw new CmlpDataSrcException(
					"The predictor id is invalid, as predictor manager didn't provide any information for the given predictor id");
		}

		if (predictorDetails.length() > 1) {// &&
											// catalogKey.equals(objCatalog.getCatalogKey()))
											// {
			objCatalog.setCatalogKey(catalogKey);// HelperTool.getkey(objCatalog.getNamespace(),
												// user));
			//objCatalog.setCatalogKey(catalogKey);
			objCatalog.setModifiedBy(user);
			objCatalog.setStatus(true);
			if (predictorDetails.has("ingressUrl") && !predictorDetails.getString("ingressUrl").isEmpty()
					&& predictorDetails.getString("ingressUrl") != null) {
				String predictorUrl = predictorDetails.getString("ingressUrl");
				
				predictorUrl = predictorUrl.replaceAll("https://","http://");
				log.info("CatalogServiceImpl::saveCatalog():replacing https to http :predictorUrl "+predictorUrl );
				
				objCatalog.setPredictorUrl(
						predictorUrl+ HelperTool.getEnv("predictor_scoring_url_suffix",
								HelperTool.getComponentPropertyValue("predictor_scoring_url_suffix")));
			} else {
				throw new CmlpDataSrcException(
						"The predictor manager didn't send back a URL for ingress for predictor");
			}
			if(objCatalog.getCategory().equalsIgnoreCase("dmapp")) {
			String subscriberUrl = HelperTool.getEnv("streamer_base_url",
					HelperTool.getComponentPropertyValue("streamer_base_url")) + catalogKey;
			objCatalog.setSubscriberUrl(subscriberUrl);
			}
			connection.updateCatalog(user, catalogKey, authorization, codeCloudAuthorization, objCatalog);
		} else {
			throw new CmlpDataSrcException("The predictor details seems to be incorrect. Please contact admin.");
		}
		return catalogKey;
	}

	@Override
	public String getCatalog(String user, String authorization, String catalogKey)
			throws IOException, CmlpDataSrcException {
		log.info("CatalogServiceImpl::getcatalog()::initiating request");
		return connection.getCatalogDetailsByKey(user, catalogKey, "detail").toString();
	}

	@Override
	public ArrayList<String> getCatalogs(String user, String authorization, String category, String textSearch)
			throws CmlpDataSrcException, IOException {
		log.info("CatalogServiceImpl::getCatalogs()::initiating request");
		ArrayList<String> results = connection.getCatalogDetails(user, category, textSearch, authorization);
		return results;
	}

	@Override
	public boolean deleteCatalog(String user, String authorization, String catalogKey)
			throws IOException, CmlpDataSrcException {
		log.info("CatalogServiceImpl::deleteCatalog()::initiating request");
		return connection.softDeleteCatalog(user, catalogKey);
	}

	@Override
	public RelativeModel getPredictorAssociation(String user, String authorization, String catalogKey)
			throws IOException, CmlpDataSrcException {
		log.info("CatalogServiceImpl::getPredictorAssciation()::initiating request");
		DBObject relationResponse = connection.getCatalogDetailsByKey(user, catalogKey, "associate");
		log.info("CatalogServiceImpl::getPredictorAssociation()::the reponse has been fetched which being "
				+ relationResponse.toString());

		RelativeModel aRelativeModel = new RelativeModel();
		aRelativeModel.setCatalogKey(relationResponse.get("catalogKey").toString());
		aRelativeModel.setPassword(relationResponse.get("publisherPassword").toString());
		aRelativeModel.setPublisherUrl(relationResponse.get("publisherUrl").toString());
		aRelativeModel.setUsrname(relationResponse.get("publisherUserName").toString());
		aRelativeModel.setPredictorId(relationResponse.get("predictorId").toString());
		aRelativeModel.setPredictorUrl(relationResponse.get("predictorUrl").toString());
		aRelativeModel.setModelKey(relationResponse.get("modelKey").toString());
		aRelativeModel.setModelVersion(relationResponse.get("modelVersion").toString());
		aRelativeModel.setCodeCloudAuthorization(relationResponse.get("codeCloudAuthorization").toString());
		aRelativeModel.setAuthorization(relationResponse.get("authorization").toString());
		aRelativeModel.setStatus(Boolean.valueOf(relationResponse.get("status").toString()));

		log.info("CatalogServiceImpl::getPredictorAssociation()::sending result back");
		return aRelativeModel;
	}

}
