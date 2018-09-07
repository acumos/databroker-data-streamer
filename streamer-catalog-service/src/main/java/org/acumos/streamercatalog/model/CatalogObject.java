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

/**
 * @author sg453d
 *
 */
public class CatalogObject {
	
	private String catalogKey;
	private String publisherUrl;
	private String publisherUserName;
	private String publisherPassword;
	private String subscriberUsername;
	private String subscriberPassword;
	private String predictorId;
	private String modelKey;
	private String modelVersion;
	private String predictorUrl;
	private boolean status;
	private String createTime;
	private String updateTime;
	private String createdBy;
	private String modifiedBy;
	private String namespace;
	private String description;
	private String category; //DMaaP, MsgRouter
	private String subscriberUrl;
	private String streamerName;
	private int pollingInterval;
	//Message Router Details
	private CmlpMessageRouterDetails messageRouterDetails;
	/**
	 * @return the catalogKey
	 */
	public String getCatalogKey() {
		return catalogKey;
	}
	/**
	 * @param catalogKey the catalogKey to set
	 */
	public void setCatalogKey(String catalogKey) {
		this.catalogKey = catalogKey;
	}
	/**
	 * @return the publisherUrl
	 */
	public String getPublisherUrl() {
		return publisherUrl;
	}
	/**
	 * @param publisherUrl the publisherUrl to set
	 */
	public void setPublisherUrl(String publisherUrl) {
		this.publisherUrl = publisherUrl;
	}
	/**
	 * @return the publisherUserName
	 */
	public String getPublisherUserName() {
		return publisherUserName;
	}
	/**
	 * @param publisherUserName the publisherUserName to set
	 */
	public void setPublisherUserName(String publisherUserName) {
		this.publisherUserName = publisherUserName;
	}
	/**
	 * @return the publisherPassword
	 */
	public String getPublisherPassword() {
		return publisherPassword;
	}
	/**
	 * @param publisherPassword the publisherPassword to set
	 */
	public void setPublisherPassword(String publisherPassword) {
		this.publisherPassword = publisherPassword;
	}
	/**
	 * @return the subscriberUsername
	 */
	public String getSubscriberUsername() {
		return subscriberUsername;
	}
	/**
	 * @param subscriberUsername the subscriberUsername to set
	 */
	public void setSubscriberUsername(String subscriberUsername) {
		this.subscriberUsername = subscriberUsername;
	}
	/**
	 * @return the subscriberPassword
	 */
	public String getSubscriberPassword() {
		return subscriberPassword;
	}
	/**
	 * @param subscriberPassword the subscriberPassword to set
	 */
	public void setSubscriberPassword(String subscriberPassword) {
		this.subscriberPassword = subscriberPassword;
	}
	/**
	 * @return the predictorId
	 */
	public String getPredictorId() {
		return predictorId;
	}
	/**
	 * @param predictorId the predictorId to set
	 */
	public void setPredictorId(String predictorId) {
		this.predictorId = predictorId;
	}
	/**
	 * @return the modelKey
	 */
	public String getModelKey() {
		return modelKey;
	}
	/**
	 * @param modelKey the modelKey to set
	 */
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}
	/**
	 * @return the modelVersion
	 */
	public String getModelVersion() {
		return modelVersion;
	}
	/**
	 * @param modelVersion the modelVersion to set
	 */
	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}
	/**
	 * @return the predictorUrl
	 */
	public String getPredictorUrl() {
		return predictorUrl;
	}
	/**
	 * @param predictorUrl the predictorUrl to set
	 */
	public void setPredictorUrl(String predictorUrl) {
		this.predictorUrl = predictorUrl;
	}
	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}
	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	/**
	 * @return the updateTime
	 */
	public String getUpdateTime() {
		return updateTime;
	}
	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}
	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}
	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the subscriberUrl
	 */
	public String getSubscriberUrl() {
		return subscriberUrl;
	}
	/**
	 * @param subscriberUrl the subscriberUrl to set
	 */
	public void setSubscriberUrl(String subscriberUrl) {
		this.subscriberUrl = subscriberUrl;
	}
	/**
	 * @return the streamerName
	 */
	public String getStreamerName() {
		return streamerName;
	}
	/**
	 * @param streamerName the streamerName to set
	 */
	public void setStreamerName(String streamerName) {
		this.streamerName = streamerName;
	}
	/**
	 * @return the pollingInterval
	 */
	public int getPollingInterval() {
		return pollingInterval;
	}
	/**
	 * @param pollingInterval the pollingInterval to set
	 */
	public void setPollingInterval(int pollingInterval) {
		this.pollingInterval = pollingInterval;
	}
	/**
	 * @return the messageRouterDetails
	 */
	public CmlpMessageRouterDetails getMessageRouterDetails() {
		return messageRouterDetails;
	}
	/**
	 * @param messageRouterDetails the messageRouterDetails to set
	 */
	public void setMessageRouterDetails(CmlpMessageRouterDetails messageRouterDetails) {
		this.messageRouterDetails = messageRouterDetails;
	}
	
	
	
}
