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
	 * 				user details from the request
	 * @param authorization
	 * 				basic authorization header value
	 * @param codeCloudAuthorization
	 * 				codeCloudAuthorization header value
	 * @param objCatalog
	 * 				new catalogObject created which has to be saved
	 * @return String catalogKey
	 * @throws IOException
	 * 				InputOutput Exception can be thrown while saving catalogObject
	 * @throws DataStreamerException
	 * 				Custom Exception for the application.
	 */
	public String saveCatalog(String user, String authorization, String codeCloudAuthorization, CatalogObject objCatalog) throws IOException, DataStreamerException;
	
	
	/**
	 * Updates catalog object in the database using catalogKey.
	 * @param user
	 * 				user details from the request
	 * @param authorization
	 * 				basic authorization header value
	 * @param codeCloudAuthorization
	 * 				codeCloudAuthorization header value
	 * @param catalogKey
	 * 				catalogKey from the updated catalogObject
	 * @param objCatalog
	 * 				catalogObject which has updated values
	 * @return String catalogKey
	 * @throws IOException
	 * 				InputOutput Exception can be thrown while updating catalogObject
	 * @throws DataStreamerException
	 * 				Custom Exception for the application.
	 */
	public String updateCatalog(String user, String authorization,  String codeCloudAuthorization, String catalogKey, CatalogObject objCatalog) throws IOException, DataStreamerException;
	
	
	/**
	 * Retrieves Catalog object in the database using catalogKey and mode.
	 * @param user
	 * 				user details from the request
	 * @param authorization
	 * 				basic authorization header value
	 * @param catalogKey
	 * 				catalogKey of the catalogObject which we are trying to retrieve
	 * @return String catalogDetails
	 * 				
	 * @throws IOException
	 * 				InputOutput Exception can be thrown while retrieving catalogObjects
	 * @throws DataStreamerException
	 * 				Custom Exception for the application.
	 */
	public String getCatalog(String user, String authorization, String catalogKey) throws IOException, DataStreamerException;
	
	/**
	 * Retrieves catalog objects in the database using either category or a text search.
	 * @param user
	 * 				user details from the request 
	 * @param authorization 
	 * 				basic authorization header value
	 * @param category 
	 * 				category can be either Dmaap or MsgRouter
	 * @param textSearch
	 * 				some meaningful text to search the catalogObjects
	 * @return ArrayList catalogDetails
	 * @throws DataStreamerException
	 * 				Custom Exception for the application.
	 * @throws IOException
	 * 				InputOutput Exception can be thrown while retrieving catalogObjects
	 */
	public ArrayList<String> getCatalogs(String user, String authorization, String category, String textSearch) throws DataStreamerException, IOException;

	
	/**
	 * Deletes catalog object in the database using  catalogKey.
	 * @param user
	 * 				user details from the request
	 * @param authorization
	 * 				basic authorization header value
	 * @param CatalogKey
	 * 				catalogKey of CatalogObject we are going to delete
	 * @return boolean deleteStatus
	 * 				status whether the catalogObject is deleted or not
	 * @throws DataStreamerException
	 * 				Custom Exception for the application.
	 * @throws IOException
	 * 				InputOutput Exception can be thrown while deleting catalogObject
	 */
	public boolean deleteCatalog(String user, String authorization, String CatalogKey) throws DataStreamerException, IOException;
	
	/**
	 *  Retrieves predictor association which returns RelativeModel.
	 * @param user
	 * 				user details from the request
	 * @param authorization
	 * 				basic authorization header value
	 * @param catalogKey
	 * 				catalogKey of the catalogObject for predictor association. 
	 * @return RelativeModel
	 * 				relativeModel is brief version of catalogObject
	 * @throws DataStreamerException
	 * 				Custom Exception for the application.
	 * @throws IOException
	 * 				InputOutput Exception can be thrown while deleting catalogObject.
	 */
	public RelativeModel getPredictorAssociation(String user, String authorization, String catalogKey) throws DataStreamerException, IOException;
	
}
