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

import org.acumos.streamercatalog.exception.CmlpDataSrcException;
import org.acumos.streamercatalog.model.CatalogObject;
import org.acumos.streamercatalog.model.RelativeModel;

/**
 * @author sg453d
 *
 */
public interface CatalogService {
	
	/**
	 * Saves catalog object to the database.
	 */
	public String saveCatalog(String user, String authorization, String codeCloudAuthorization, CatalogObject objCatalog) throws IOException, CmlpDataSrcException;
	
	
	/**
	 * Updates catalog object in the database using catalogKey.
	 */
	public String updateCatalog(String user, String authorization,  String codeCloudAuthorization, String catalogKey, CatalogObject objCatalog) throws IOException, CmlpDataSrcException;
	
	
	/**
	 * Retrieves Catalog object in the database using catalogKey and mode.
	 */
	public String getCatalog(String user, String authorization, String catalogKey) throws IOException, CmlpDataSrcException;
	
	
	/**
	 * Retrieves catalog objects in the database using either category or a text search.
	 */
	public ArrayList<String> getCatalogs(String user, String authorization, String category, String textSearch) throws CmlpDataSrcException, IOException;

	
	/**
	 * Deletes catalog object in the database using  catalogKey.
	 */
	public boolean deleteCatalog(String user, String authorization, String CatalogKey) throws CmlpDataSrcException, IOException;
	
	/**
	 * Retrieves predictor association which returns RelativeModel.
	 */	
	public RelativeModel getPredictorAssociation(String user, String authorization, String catalogKey) throws CmlpDataSrcException, IOException;
	
}
