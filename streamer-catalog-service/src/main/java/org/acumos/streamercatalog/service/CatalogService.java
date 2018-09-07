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

import org.acumos.streamercatalog.exception.DataStreamerException;
import org.acumos.streamercatalog.model.CatalogObject;
import org.acumos.streamercatalog.model.RelativeModel;

public interface CatalogService {
	
	/**
	 * Saves catalog object to the database.
	 * @param user
	 * @param authorization
	 * @param codeCloudAuthorization
	 * @param objCatalog
	 * @return String catalogKey
	 * @throws IOException
	 * @throws DataStreamerException
	 */
	public String saveCatalog(String user, String authorization, String codeCloudAuthorization, CatalogObject objCatalog) throws IOException, DataStreamerException;
	
	
	/**
	 * Updates catalog object in the database using catalogKey.
	 * @param user
	 * @param authorization
	 * @param codeCloudAuthorization
	 * @param catalogKey
	 * @param objCatalog
	 * @return String catalogKey
	 * @throws IOException
	 * @throws DataStreamerException
	 */
	public String updateCatalog(String user, String authorization,  String codeCloudAuthorization, String catalogKey, CatalogObject objCatalog) throws IOException, DataStreamerException;
	
	
	/**
	 * Retrieves Catalog object in the database using catalogKey and mode.
	 * @param user
	 * @param authorization
	 * @param catalogKey
	 * @return String catalogDetails
	 * @throws IOException
	 * @throws DataStreamerException
	 */
	public String getCatalog(String user, String authorization, String catalogKey) throws IOException, DataStreamerException;
	
	/**
	 * Retrieves catalog objects in the database using either category or a text search.
	 * @param user 
	 * @param authorization 
	 * @param category 
	 * @param textSearch
	 * @return ArrayList<String> catalogDetails
	 * @throws DataStreamerException
	 * @throws IOException
	 */
	public ArrayList<String> getCatalogs(String user, String authorization, String category, String textSearch) throws DataStreamerException, IOException;

	
	/**
	 * Deletes catalog object in the database using  catalogKey.
	 * @param user
	 * @param authorization
	 * @param CatalogKey
	 * @return boolean deleteStatus
	 * @throws DataStreamerException
	 * @throws IOException
	 */
	public boolean deleteCatalog(String user, String authorization, String CatalogKey) throws DataStreamerException, IOException;
	
	/**
	 *  Retrieves predictor association which returns RelativeModel.
	 * @param user
	 * @param authorization
	 * @param catalogKey
	 * @return RelativeModel aRelativeModel
	 * @throws DataStreamerException
	 * @throws IOException
	 */
	public RelativeModel getPredictorAssociation(String user, String authorization, String catalogKey) throws DataStreamerException, IOException;
	
}
