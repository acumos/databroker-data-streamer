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

package org.acumos.streamercatalog.connection;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.acumos.streamercatalog.common.HelperTool;
import org.acumos.streamercatalog.exception.CmlpDataSrcException;
import org.acumos.streamercatalog.model.CatalogObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;

@Service
public class DbUtilities {

	private static Logger log = LoggerFactory.getLogger(DbUtilities.class);

	private MongoClient mongoClient = null;
	private DB datasrcDB = null;
	private DBCollection datasrcCollection = null;

	private DBCollection getMongoCollection() throws IOException {
		log.info("DbUtilities::getMongoCollection()::trying to get a mongo client instance");

		if (mongoClient == null) {
			log.info(
					"DbUtilities::getMongoCollection()::checking if mongo client is intialised or not, will intialise a copy of it if not intialised.");
			MongoCredential mongoCredential = MongoCredential.createCredential(
					HelperTool.getEnv("mongo_username", HelperTool.getComponentPropertyValue("mongo_username")),
					HelperTool.getEnv("mongo_dbname", HelperTool.getComponentPropertyValue("mongo_dbname")),
					HelperTool.getEnv("mongo_password", HelperTool.getComponentPropertyValue("mongo_password"))
							.toCharArray());

			ServerAddress server = new ServerAddress(
					HelperTool.getEnv("mongo_hostname", HelperTool.getComponentPropertyValue("mongo_hostname")),
					Integer.parseInt(
							HelperTool.getEnv("mongo_port", HelperTool.getComponentPropertyValue("mongo_port"))));
			mongoClient = new MongoClient(server, Arrays.asList(mongoCredential));
			log.info("DbUtilities::getMongoCollection():: a new mongo client has been intialised.");
		}

		log.info("DbUtilities::getMongoCollection()::using mongo client to get db connection.");
		datasrcDB = mongoClient
				.getDB(HelperTool.getEnv("mongo_dbname", HelperTool.getComponentPropertyValue("mongo_dbname")));

		log.info("DbUtilities::getMongoCollection()::using mongo client to get collection.");
		datasrcCollection = datasrcDB.getCollection(HelperTool.getEnv("mongo_collection_name",
				HelperTool.getComponentPropertyValue("mongo_collection_name")));

		log.info("DbUtilities::getMongoCollection()::returning  collection.");
		return datasrcCollection;
	}

	private DBObject createDBObject(CatalogObject objCatalog, String authorization, String codeClouAuthorization,
			String mode) throws IOException {
		log.info("DbUtilities::createDBObject()::intializing db object builder.");
		BasicDBObjectBuilder catalogBuilder = BasicDBObjectBuilder.start();

		log.info("DbUtilities::createDBObject()::intializing _id value.");
		catalogBuilder.append("_id", objCatalog.getCatalogKey());
		log.info("DbUtilities::createDBObject()::intializing catalog collection value.");
		if (objCatalog.getCatalogKey() != null) {
			catalogBuilder.append("catalogKey", objCatalog.getCatalogKey());
		}
		if (objCatalog.getModelKey() != null) {
			catalogBuilder.append("modelKey", objCatalog.getModelKey());
		}
		if (objCatalog.getModelVersion() != null) {
			catalogBuilder.append("modelVersion", objCatalog.getModelVersion());
		}
		if (objCatalog.getPredictorId() != null) {
			catalogBuilder.append("predictorId", objCatalog.getPredictorId());
		}
		if (objCatalog.getPredictorUrl() != null) {
			catalogBuilder.append("predictorUrl", objCatalog.getPredictorUrl());
		}
		if (objCatalog.getPublisherUserName() != null) {
			catalogBuilder.append("publisherUserName", objCatalog.getPublisherUserName());
		}
		if (objCatalog.getPublisherPassword() != null) {
			catalogBuilder.append("publisherPassword", objCatalog.getPublisherPassword());
		}
		if (objCatalog.getPublisherUrl() != null) {
			catalogBuilder.append("publisherUrl", objCatalog.getPublisherUrl());
		}
		if (objCatalog.getSubscriberPassword() != null) {
			catalogBuilder.append("subscriberPassword", objCatalog.getSubscriberPassword());
		}
		if (objCatalog.getSubscriberUsername() != null) {
			catalogBuilder.append("subscriberUsername", objCatalog.getSubscriberUsername());
		}
		if (objCatalog.getCreatedBy() != null && mode.equals("create")) {
			catalogBuilder.append("createdBy", objCatalog.getCreatedBy());
			catalogBuilder.append("createTime", Instant.now().toString());
		}
		if (objCatalog.getModifiedBy() != null && mode.equals("update")) {
			catalogBuilder.append("updatedBy", objCatalog.getModifiedBy());
			catalogBuilder.append("updateTime", Instant.now().toString());
		}
		
		catalogBuilder.append("pollingInterval", objCatalog.getPollingInterval());

		catalogBuilder.append("namespace",
				HelperTool.getEnv("namespace", HelperTool.getComponentPropertyValue("namespace")));
		
		if (objCatalog.getCategory() != null) {
			if (objCatalog.getCategory().equalsIgnoreCase("DMaaP"))
				catalogBuilder.append("category", "DMaaP");
			else if (objCatalog.getCategory().equalsIgnoreCase("MsgRouter"))
				catalogBuilder.append("category", "MsgRouter");
			else
				catalogBuilder.append("category", objCatalog.getCategory());
		} /*else if(objCatalog.getCategory() == null && 
					objCatalog.getMessageRouterDetails() != null && 
					objCatalog.getMessageRouterDetails().getServerName() != null) {
			catalogBuilder.append("category", "MsgRouter");
		} else {
			catalogBuilder.append("category", "DMaaP");
		}*/
		
		if (objCatalog.getDescription() != null) {
			catalogBuilder.append("description", objCatalog.getDescription());
		}
		if (objCatalog.getSubscriberUrl() != null) {
			catalogBuilder.append("subscriberUrl", objCatalog.getSubscriberUrl());
		}
		if (objCatalog.getStreamerName() != null) {
			catalogBuilder.append("streamerName", objCatalog.getStreamerName());
		}
		catalogBuilder.append("status", objCatalog.isStatus());
		catalogBuilder.append("codeCloudAuthorization", codeClouAuthorization);
		catalogBuilder.append("authorization", authorization);
		
		if(objCatalog.getMessageRouterDetails() != null) {
			BasicDBObject messageRouterDetails = new BasicDBObject();
			if (objCatalog.getMessageRouterDetails().getServerName() != null) {
				messageRouterDetails.append("serverName", objCatalog.getMessageRouterDetails().getServerName());
			}
			
			messageRouterDetails.append("serverPort", objCatalog.getMessageRouterDetails().getServerPort());
			
			if (objCatalog.getMessageRouterDetails().getUserName() != null) {
				messageRouterDetails.append("userName", objCatalog.getMessageRouterDetails().getUserName());
			}
			
			if (objCatalog.getMessageRouterDetails().getPassword() != null) {
				messageRouterDetails.append("password", objCatalog.getMessageRouterDetails().getPassword());
			}
			
			if (objCatalog.getMessageRouterDetails().getTopicName() != null) {
				messageRouterDetails.append("topicName", objCatalog.getMessageRouterDetails().getTopicName());
			}
			
			if (objCatalog.getMessageRouterDetails().getSerializer() != null) {
				messageRouterDetails.append("serializer", objCatalog.getMessageRouterDetails().getSerializer());
			}
			
			if (objCatalog.getMessageRouterDetails().getDeSerializer() != null) {
				messageRouterDetails.append("deSerializer", objCatalog.getMessageRouterDetails().getDeSerializer());
			}
			
			catalogBuilder.append("messageRouterDetails", messageRouterDetails);
		}

		return catalogBuilder.get();
	}

	public void insertCatalogDetails(String user, String authorization, String codeClouAuthorization,
			CatalogObject objCatalog) throws CmlpDataSrcException, IOException {
		DBObject result = null;
		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();
		log.info("DbUtilities::insertCatalogDetails()::checking user before insertion");
		if (user != null) {
			query.add("createdBy", user);
		} else {
			log.info("DbUtilities::insertCatalogDetails()::user value is null");
			throw new CmlpDataSrcException("OOPS. User not available");
		}
		
		log.info("DbUtilities::insertCatalogDetails()::checking predictor id");
		if (objCatalog.getStreamerName() != null) {
			query.add("streamerName", objCatalog.getStreamerName());
		} else {
			log.info("DbUtilities::insertCatalogDetails()::streamer name is null");
			throw new CmlpDataSrcException("OOPS. Please provide staremer name.");
		}

		log.info("DbUtilities::insertCatalogDetails()::checking predictor id");
		if (objCatalog.getPredictorId() != null) {
			query.add("predictorId", objCatalog.getPredictorId());
		} else {
			log.info("DbUtilities::insertCatalogDetails()::predictor id is null");
			throw new CmlpDataSrcException("OOPS. Please provide predictor id.");
		}

		log.info("DbUtilities::insertCatalogDetails()::checking publisher url");
		if (objCatalog.getPublisherUrl() != null) {
			query.add("publisherUrl", objCatalog.getPublisherUrl());
		} else {
			log.info("DbUtilities::insertCatalogDetails()::publisher url is null");
			throw new CmlpDataSrcException("OOPS. Please provide publisher url");
		}
		query.add("pollingInterval", objCatalog.getPollingInterval());

		/* if (!query.isEmpty()) { */
		result = getMongoCollection().findOne(query.get());

		/*
		 * } else { throw new CmlpDataSrcException(
		 * "OOPS. Please check catalog key provided and ser persmission for this operation"
		 * ); }
		 */

		if (result == null) {
			WriteResult insertResult = getMongoCollection()
					.insert(createDBObject(objCatalog, authorization, codeClouAuthorization, "create"));
			log.info("DbUtilities::insertCatalogDetails()::id of the inserted mongo object: "
					+ insertResult.getUpsertedId());
		} else {
			log.info(
					"DbUtilities::insertCatalogDetails()::Please check publisher URL and predictor id, since this association is already present");
			throw new CmlpDataSrcException(
					"OOPS. Please check publisher URL and predictor id, since this association is already present");
		}

	}

	public ArrayList<String> getCatalogDetails(String user, String category, String textSearch, String authorization)
			throws CmlpDataSrcException, IOException {
		ArrayList<String> retrieved = new ArrayList<String>();
		DBCursor cursor = null;
		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();
		log.info("DbUtilities::getCatalogDetails()::checking inputs");

		if (textSearch != null) {
			return getCatalogDetailsByTextSearch(user, textSearch);
		} else {
			if (user != null) {
				log.info(
						"DbUtilities::getCatalogDetails()::checking user, get operation is being performed by " + user);
				query.add("createdBy", user);
			} else if (user == null) {
				throw new CmlpDataSrcException("user can't be null");
			}

			if (category != null) {
				log.info("DbUtilities::getCatalogDetails(), checking category, get operation is being performed for "
						+ category + " category.");
				query.add("category", category);
			}

		}
		//query.add("status", true);

		log.info("DbUtilities::getCatalogDetails(), running query");
		cursor = getMongoCollection().find(query.get());

		log.info("DbUtilities::getCatalogDetails(), processing resultset");

		DBObject tempStorage;

		while (cursor.hasNext()) {
			tempStorage = cursor.next();
			tempStorage.removeField("codeCloudAuthorization");
			retrieved.add(tempStorage.toString());
		}
		return retrieved;
	}

	private ArrayList<String> getCatalogDetailsByTextSearch(String user, String textSearch)
			throws CmlpDataSrcException, IOException {

		ArrayList<String> results = new ArrayList<String>();
		DBCursor cursor = null;

		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();

		log.info("DbUtilities::getCatalogDetailsByTextSearch()::checking inputs");

		if (user == null) {
			throw new CmlpDataSrcException("user can't be null");
		}

		log.info("DbUtilities::getCatalogDetailsByTextSearch()::checking user, get operation is being performed by "
				+ user);
		query.add("createdBy", user);
		//query.add("status", true);

		if (textSearch != null && !textSearch.isEmpty()) {
			log.info("DbUtilities::getCatalogDetailsByTextSearch()::running mongodb query");

			cursor = getMongoCollection().find(query.get());

			log.info("Creating Regular Expression Pattern of the textSearch input" + textSearch);
			Pattern pattern = Pattern.compile(textSearch, Pattern.CASE_INSENSITIVE);
			Matcher m;
			String str_doc;

			log.info("DbUtilities::getCatalogDetailsByTextSearch()::processing resultset");
			while (cursor.hasNext()) {
				DBObject tempResult = cursor.next();
				tempResult.removeField("codeCloudAuthorization");
				str_doc = tempResult.toString();

				m = pattern.matcher(str_doc);
				log.info("DbUtilities::getCatalogDetailsByTextSearch()::matching regex textSearch pattern");
				if (m.find()) {
					log.info(
							"DbUtilities::getCatalogDetailsByTextSearch()::document matching regex textSearch pattern added to results List");
					results.add(str_doc);
				}
			}
		} else {
			log.info("DbUtilities::getCatalogDetailsByTextSearch()::textSearch value is null or is empty");
			throw new CmlpDataSrcException(
					"OOPS. TextSearch field is required. Please provide a value for textSearch.");
		}

		return results;
	}

	public boolean deleteCatalog(String user, String catalogKey) throws IOException, CmlpDataSrcException {
		boolean delete = false;
		DBCursor cursor = null;
		WriteResult result = null;

		log.info("DbUtilities::deletecatalog(), intializing query object");
		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();

		log.info("DbUtilities::deletecatalog(), checking user");
		if (user != null && !user.isEmpty()) {
			query.add("createdBy", user);
		} else {
			log.info("DbUtilities::deletecatalog(), user value is null");
			throw new CmlpDataSrcException("OOPS. User not available. Please provide a user.");
		}

		log.info("DbUtilities::deletecatalog(), checking catalogKey");
		if (catalogKey != null && !catalogKey.isEmpty()) {
			query.add("_id", catalogKey);
		} else {
			log.info("DbUtilities::deletecatalog(), datsourceKey value is null");
			throw new CmlpDataSrcException("OOPS. Please provide catalog key");
		}

		log.info("DbUtilities::deletecatalog(), populating cursor with collection that is to be deleted");
		cursor = getMongoCollection().find(query.get());

		log.info("DbUtilities::deletecatalog(), checking cursor for value");
		if (cursor.hasNext()) {
			log.info("DbUtilities::deletecatalog(), issuing command to delete object with id: " + catalogKey);
			result = getMongoCollection().remove(query.get());
			log.info("result for deletion: " + result.toString());
			delete = !result.isUpdateOfExisting();
		} else {
			log.info("DbUtilities::deletecatalog(), deletion failed for object with id: " + catalogKey
					+ " .Please check catalog key provided and user persmission for this operation");
			throw new CmlpDataSrcException(
					"OOPS. Please check catalog key provided and user persmission for this operation");
		}
		return delete;
	}


	public boolean softDeleteCatalog(String user, String catalogKey) throws CmlpDataSrcException, IOException {
		boolean delete = false;
		DBCursor cursor = null;
		WriteResult result = null;
		DBObject DBObj = null;

		log.info("DbUtilities::softDeletecatalog(), intializing query object");
		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();

		log.info("DbUtilities::softDeletecatalog(), checking user");
		if (user != null && !user.isEmpty()) {
			query.add("createdBy", user);
		} else {
			log.info("DbUtilities::softDeletecatalog(), user value is null");
			throw new CmlpDataSrcException("OOPS. User not available. Please provide a user.");
		}

		log.info("DbUtilities::softDeletecatalog(), checking catalogKey");
		if (catalogKey != null && !catalogKey.isEmpty()) {
			query.add("_id", catalogKey);
		} else {
			log.info("DbUtilities::softDeletecatalog()::datsourceKey value is null");
			throw new CmlpDataSrcException("OOPS. Please provide catalog key");
		}

		log.info("DbUtilities::softDeletecatalog()::populating cursor with collection that is to be deleted");
		cursor = getMongoCollection().find(query.get());

		log.info("DbUtilities::softDeletecatalog(), checking cursor for value");
		if (cursor.hasNext()) {
			log.info("DbUtilities::softDeletecatalog(), issuing command to delete object with id: " + catalogKey);
			DBObj = cursor.next();
			DBObj.put("status", new Boolean(false));
			result = getMongoCollection().update(query.get(), new BasicDBObject().append("$set", DBObj));
			log.info("result for deletion: " + result.toString());
			delete = result.isUpdateOfExisting();
		} else {
			log.info("DbUtilities::softDeletecatalog(), deletion failed for object with id: " + catalogKey
					+ " .Please check catalog key provided and user persmission for this operation");
			throw new CmlpDataSrcException(
					"OOPS. Please check catalog key provided and user persmission for this operation");
		}
		return delete;
	}

	public boolean updateCatalog(String user, String catalogKey, String authorization, String codeClouAuthorization,
			CatalogObject objCatalog) throws CmlpDataSrcException, IOException {
		boolean update = false;
		WriteResult result = null;
		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();

		log.info("DbUtilities::updatecatalog()::checking user");
		if (user != null) {
			query.add("createdBy", user);
		} else {
			log.info("DbUtilities::updatecatalog()::user value is null");
			throw new CmlpDataSrcException("OOPS. User not available");
		}

		log.info("DbUtilities::updatecatalog()::checking catalogkey");
		if (catalogKey != null) {
			query.add("_id", catalogKey);
		} else {
			log.info("DbUtilities::updatecatalog()::catalogKey value is null");
			throw new CmlpDataSrcException("OOPS. Please provide catalog key");
		}

		if (!query.isEmpty()) {
			log.info("DbUtilities::updatecatalog(), issuing command to update object with id: " + catalogKey);
			result = getMongoCollection().update(query.get(), new BasicDBObject().append("$set",
					createDBObject(objCatalog, authorization, codeClouAuthorization, "update")));

			update = result.isUpdateOfExisting();
		} else {
			log.info("DbUtilities::updatecatalog()::updation failed for object with id: " + catalogKey
					+ " .Please check catalog key provided and user persmission for this operation");
			throw new CmlpDataSrcException(
					"OOPS. Please check catalog key provided and ser persmission for this operation");
		}
		return update;
	}

	public DBObject getCatalogDetailsByKey(String user, String catalogKey, String mode)
			throws IOException, CmlpDataSrcException {

		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();
		log.info("DbUtilities::getCatalogDetailsByKey()::checking inputs");

		if (user != null) {
			log.info("DbUtilities::getCatalogDetailsByKey()::checking user, get operation is being performed by user: "
					+ user);
			query.add("createdBy", user);
		} else {
			log.info("DbUtilities::getCatalogDetailsByKey()::user has not been provided");
			throw new CmlpDataSrcException("Please provide proper AAF authentication");
		}

		if (catalogKey != null) {
			log.info(
					"DbUtilities::getCatalogDetailsByKey()::checking category, get operation is being performed for id: "
							+ catalogKey);
			query.add("_id", catalogKey);
		} else {
			log.info("DbUtilities::getCatalogDetailsByKey()::catalog key has not been provided");
			throw new CmlpDataSrcException("Please provide a catalog key");
		}

		//query.add("status", true);

		log.info("DbUtilities::getCatalogDetailsByKey()::running query");
		DBObject result = getMongoCollection().findOne(query.get());

		if (result == null) {
			log.info(
					"DbUtilities::getCatalogDetailsByKey()::No data is available for provided catalog key. Either the key doesn't exist or user doesn't has permission for the record for user: "
							+ user + " and record: " + catalogKey);
			throw new CmlpDataSrcException(
					"No data is available for provided catalog key. Either the key doesn't exist or user doesn't has permission for the record.");
		}

		if (mode.equals("detail")) {
			result.removeField("codeCloudAuthorization");
			result.removeField("authorization");
		}

		return result;
	}

}
