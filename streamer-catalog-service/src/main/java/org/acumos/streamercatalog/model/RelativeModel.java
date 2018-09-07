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

package org.acumos.streamercatalog.model;

public class RelativeModel {
	
	private String catalogKey;
	private String predictorId;
	private String predictorUrl;
	private String publisherUrl;
	private String usrname;
	private String password;
	private String modelKey;
	private String modelVersion;
	private String codeCloudAuthorization;
	private String authorization;
	private boolean status;
	
	public String getCatalogKey() {
		return catalogKey;
	}
	public void setCatalogKey(String catalogKey) {
		this.catalogKey = catalogKey;
	}
	public String getPredictorId() {
		return predictorId;
	}
	public void setPredictorId(String predictorId) {
		this.predictorId = predictorId;
	}
	public String getPredictorUrl() {
		return predictorUrl;
	}
	public void setPredictorUrl(String predictorUrl) {
		this.predictorUrl = predictorUrl;
	}
	public String getPublisherUrl() {
		return publisherUrl;
	}
	public void setPublisherUrl(String publisherUrl) {
		this.publisherUrl = publisherUrl;
	}
	public String getUsrname() {
		return usrname;
	}
	public void setUsrname(String usrname) {
		this.usrname = usrname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getModelKey() {
		return modelKey;
	}
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}
	public String getModelVersion() {
		return modelVersion;
	}
	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}
	public String getCodeCloudAuthorization() {
		return codeCloudAuthorization;
	}
	public void setCodeCloudAuthorization(String codeCloudAuthorization) {
		this.codeCloudAuthorization = codeCloudAuthorization;
	}
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
}
