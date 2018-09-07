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

import javax.ws.rs.core.Response;
import org.acumos.streamercatalog.model.CatalogObject;


public interface RestCatalogService {
	/**
	 * Saves catalog object to the database.
	 * @param authorization 
	 * 				basic authorization header value
	 * @param objCatalog 
	 * 				new Catalog object which is created
	 * @return status of saved Catalog Object.
	 */
	public Response saveCatalog(String authorization, CatalogObject objCatalog);
	
	/**
	 * Updates catalog object in the database using catalogKey.
	 * @param authorization 
	 * 				basic authorization header value
	 * @param catalogKey 
	 * 				updated CatalogObject's key
	 * @param objCatalog 
	 * 				updated Catalog Object
	 * @return status of the updated Catalog Object.
	 */
	public Response updateCatalog(String authorization, String catalogKey,CatalogObject objCatalog);
	
	/**
	 * Retrieves Catalog object in the database using catalogKey and mode.
	 * @param authorization 
	 * 				basic authorization header value
	 * @param catalogKey 
	 * 				catalogKey of catalogObject which are trying to get details.
	 * @param mode 
	 * 				mode of how much data in the catalog object is required can be precise
	 * @return status of the get CatalogObject call.
	 */
	public Response getCatalog(String authorization, String catalogKey, String mode);
	
	/**
	 * Retrieves catalog objects in the database using either category or a text search.
	 * @param authorization 
	 * 				basic authorization header value
	 * @param category
	 * 				category of which catalogObjects we want to retrieve can be MsgRouter, DataRouter
	 * @param textSearch
	 * 				simple text search can be done by giving appropriate text.
	 * @return status of the getCatalogs by category or textsearch
	 */
	public Response getCatalogs(String authorization, String category, String textSearch);
	
	/**
	 * Deletes catalog object in the database using  catalogKey.
	 * @param authorization 
	 * 				basic authorization header value
	 * @param catalogKey
	 * 				catalogKey of the catalog object we are going to delete.
	 * @return status f the deleteCatalog call.
	 */
	public Response deleteCatalog(String authorization, String catalogKey);

}
