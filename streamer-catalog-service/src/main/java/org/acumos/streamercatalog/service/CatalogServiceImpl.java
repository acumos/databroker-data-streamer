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

import org.acumos.streamercatalog.common.DataStreamerCatalogUtil;
import org.acumos.streamercatalog.connection.DbUtilities;
import org.acumos.streamercatalog.exception.DataStreamerException;
import org.acumos.streamercatalog.model.CatalogObject;
import org.acumos.streamercatalog.model.RelativeModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.mongodb.DBObject;

@Service
public class CatalogServiceImpl implements CatalogService {

	private static final String ASSOCIATE = "associate";

	private static final String STATUS = "status";

	private static final String AUTHORIZATION2 = "authorization";

	private static final String MODEL_VERSION = "modelVersion";

	private static final String MODEL_KEY = "modelKey";

	private static final String PREDICTOR_URL = "predictorUrl";

	private static final String PREDICTOR_ID = "predictorId";

	private static final String PUBLISHER_USER_NAME = "publisherUserName";

	private static final String PUBLISHER_URL = "publisherUrl";

	private static final String PUBLISHER_PASSWORD = "publisherPassword";

	private static final String CATALOG_KEY = "catalogKey";

	private static final String DETAIL = "detail";

	private static final String STREAMER_BASE_URL = "streamer_base_url";

	private static final String DMAAP = "dmaap";

	private static final String PREDICTOR_SCORING_URL_SUFFIX = "predictor_scoring_url_suffix";

	private static final String INGRESS_URL = "ingressUrl";

	private static Logger log = LoggerFactory.getLogger(CatalogServiceImpl.class);

	@Autowired
	private DbUtilities connection;
	
	@Autowired
	Environment env;
	
	@Autowired
	DataStreamerCatalogUtil dataStreamerCatalogUtil;
	

	@Override
	public String saveCatalog(String user, String authorization, CatalogObject objCatalog)
			throws IOException, DataStreamerException {

		log.info(
				"CatalogServiceImpl::saveCatalog()::fetching predictor details from predictor manager for predictor id: "
						+ objCatalog.getPredictorId());
		
		JSONObject predictorDetails = dataStreamerCatalogUtil.getPredictorDetails(authorization, objCatalog.getPredictorId());
		String catalogKey = null;

		log.info("CatalogServiceImpl::saveCatalog()::predictor details fetched. The total no of keys in json is "
				+ predictorDetails.length());
		log.info("CatalogServiceImpl::saveCatalog()::predictor details: " + predictorDetails.toString());
		if (predictorDetails.length() < 1) {
			log.info(
					"CatalogServiceImpl::saveCatalog()::there is no element in predictor details, json is less than one. Throwing exception.......");
			throw new DataStreamerException(
					"The predictor id is invalid, as predictor manager didn't provide any information for the given predictor id");
		}

		if (predictorDetails.length() > 1) {
			if (objCatalog.getStreamerName().contains("am375y")){
				catalogKey = "pocsub";
			} else{
				catalogKey = dataStreamerCatalogUtil.getkey(user);
			}
//			catalogKey = "pocsub";// HelperTool.getkey(objCatalog.getNamespace(),
//			//catalogKey =  HelperTool.getkey(objCatalog.getNamespace(), user);		// user);
			
			objCatalog.setCatalogKey(catalogKey);
			objCatalog.setCreatedBy(user);
			objCatalog.setStatus(true);
			
			log.info("CatalogServiceImpl::saveCatalog()::checking if ingress for predcitor is present or not");
			if (predictorDetails.has(INGRESS_URL) && !predictorDetails.getString(INGRESS_URL).isEmpty()
					&& predictorDetails.getString(INGRESS_URL) != null) {
				log.info("CatalogServiceImpl::saveCatalog()::ingress URL for predictor "
						+ objCatalog.getPredictorId().replace("-", "_") + " is "
						+ predictorDetails.getString(INGRESS_URL));
				String predictorUrl = predictorDetails.getString(INGRESS_URL);
				log.info("CatalogServiceImpl::saveCatalog():replacing https to http :predictorUrl "+predictorUrl );
				objCatalog.setPredictorUrl(
						predictorUrl + dataStreamerCatalogUtil.getEnv(PREDICTOR_SCORING_URL_SUFFIX,
								dataStreamerCatalogUtil.getComponentPropertyValue(PREDICTOR_SCORING_URL_SUFFIX)));
			} else {
				log.info(
						"CatalogServiceImpl::saveCatalog()::predictor manager didn't send ingress details for predictor "
								+ objCatalog.getPredictorId() + ". Throwing exception.........");
				throw new DataStreamerException("The predictor manager didn't send back a URL for scoring");
			}
			
			log.info("CatalogServiceImpl::saveCatalog()::initiating insertion into mongo db.");
			
			if(objCatalog.getCategory().equalsIgnoreCase(DMAAP)) {
				String subscriberUrl = dataStreamerCatalogUtil.getEnv(STREAMER_BASE_URL,
						dataStreamerCatalogUtil.getComponentPropertyValue(STREAMER_BASE_URL)) + catalogKey;
				
				objCatalog.setSubscriberUrl(subscriberUrl);
			}
			
			connection.insertCatalogDetails(user, authorization, objCatalog);
		}
		return catalogKey;
	}

	@Override
	public String updateCatalog(String user, String authorization, String catalogKey, CatalogObject objCatalog)
			throws IOException, DataStreamerException {

		JSONObject predictorDetails = dataStreamerCatalogUtil.getPredictorDetails(authorization, objCatalog.getPredictorId());
		if (predictorDetails.length() < 1) {
			throw new DataStreamerException(
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
			if (predictorDetails.has(INGRESS_URL) && !predictorDetails.getString(INGRESS_URL).isEmpty()
					&& predictorDetails.getString(INGRESS_URL) != null) {
				String predictorUrl = predictorDetails.getString(INGRESS_URL);
				log.info("CatalogServiceImpl::saveCatalog():replacing https to http :predictorUrl "+predictorUrl );
				
				objCatalog.setPredictorUrl(
						predictorUrl+ dataStreamerCatalogUtil.getEnv(PREDICTOR_SCORING_URL_SUFFIX,
								dataStreamerCatalogUtil.getComponentPropertyValue(PREDICTOR_SCORING_URL_SUFFIX)));
			} else {
				throw new DataStreamerException(
						"The predictor manager didn't send back a URL for ingress for predictor");
			}
			if(objCatalog.getCategory().equalsIgnoreCase(DMAAP)) {
			String subscriberUrl = dataStreamerCatalogUtil.getEnv(STREAMER_BASE_URL,
					dataStreamerCatalogUtil.getComponentPropertyValue(STREAMER_BASE_URL)) + catalogKey;
			objCatalog.setSubscriberUrl(subscriberUrl);
			}
			connection.updateCatalog(user, catalogKey, authorization, objCatalog);
		} else {
			throw new DataStreamerException("The predictor details seems to be incorrect. Please contact admin.");
		}
		return catalogKey;
	}

	@Override
	public String getCatalog(String user, String authorization, String catalogKey)
			throws IOException, DataStreamerException {
		log.info("CatalogServiceImpl::getcatalog()::initiating request");
		return connection.getCatalogDetailsByKey(user, catalogKey, DETAIL).toString();
	}

	@Override
	public ArrayList<String> getCatalogs(String user, String authorization, String category, String textSearch)
			throws DataStreamerException, IOException {
		log.info("CatalogServiceImpl::getCatalogs()::initiating request");
		ArrayList<String> results = connection.getCatalogDetails(user, category, textSearch, authorization);
		return results;
	}

	@Override
	public boolean deleteCatalog(String user, String authorization, String catalogKey)
			throws IOException, DataStreamerException {
		log.info("CatalogServiceImpl::deleteCatalog()::initiating request");
		return connection.softDeleteCatalog(user, catalogKey);
	}

	@Override
	public RelativeModel getPredictorAssociation(String user, String authorization, String catalogKey)
			throws IOException, DataStreamerException {
		log.info("CatalogServiceImpl::getPredictorAssciation()::initiating request");
		DBObject relationResponse = connection.getCatalogDetailsByKey(user, catalogKey, ASSOCIATE);
		log.info("CatalogServiceImpl::getPredictorAssociation()::the reponse has been fetched which being "
				+ relationResponse.toString());

		RelativeModel aRelativeModel = new RelativeModel();
		aRelativeModel.setCatalogKey(relationResponse.get(CATALOG_KEY).toString());
		aRelativeModel.setPassword(relationResponse.get(PUBLISHER_PASSWORD).toString());
		aRelativeModel.setPublisherUrl(relationResponse.get(PUBLISHER_URL).toString());
		aRelativeModel.setUsrname(relationResponse.get(PUBLISHER_USER_NAME).toString());
		aRelativeModel.setPredictorId(relationResponse.get(PREDICTOR_ID).toString());
		aRelativeModel.setPredictorUrl(relationResponse.get(PREDICTOR_URL).toString());
		aRelativeModel.setModelKey(relationResponse.get(MODEL_KEY).toString());
		aRelativeModel.setModelVersion(relationResponse.get(MODEL_VERSION).toString());
		aRelativeModel.setAuthorization(relationResponse.get(AUTHORIZATION2).toString());
		aRelativeModel.setStatus(Boolean.valueOf(relationResponse.get(STATUS).toString()));

		log.info("CatalogServiceImpl::getPredictorAssociation()::sending result back");
		return aRelativeModel;
	}

}
