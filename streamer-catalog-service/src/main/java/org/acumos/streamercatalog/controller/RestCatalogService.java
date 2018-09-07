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
	 */
	public Response saveCatalog(String authorization, String codeCloudAuthorization, CatalogObject objCatalog);
	
	/**
	 * Updates catalog object in the database using catalogKey.
	 */
	public Response updateCatalog(String authorization, String codeCloudAuthorization, String catalogKey,
			CatalogObject objCatalog);
	
	/**
	 * Retrieves Catalog object in the database using catalogKey and mode.
	 */
	public Response getCatalog(String authorization, String catalogKey, String mode);
	
	/**
	 * Retrieves catalog objects in the database using either category or a text search.
	 */
	public Response getCatalogs(String authorization, String category, String textSearch);
	
	/**
	 * Deletes catalog object in the database using  catalogKey.
	 */
	public Response deleteCatalog(String authorization, String catalogKey);

}
