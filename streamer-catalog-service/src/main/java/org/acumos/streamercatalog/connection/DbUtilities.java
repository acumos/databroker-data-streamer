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

import org.acumos.streamercatalog.common.DataStreamerCatalogUtil;
import org.acumos.streamercatalog.exception.DataStreamerException;
import org.acumos.streamercatalog.model.CatalogObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

@Component
public class DbUtilities {

	private static final String DETAIL = "detail";

	private static final String _ID = "_id";

	private static final String MESSAGE_ROUTER_DETAILS = "messageRouterDetails";

	private static final String DE_SERIALIZER = "deSerializer";

	private static final String SERIALIZER = "serializer";

	private static final String TOPIC_NAME = "topicName";

	private static final String PASSWORD = "password";

	private static final String USER_NAME = "userName";

	private static final String SERVER_PORT = "serverPort";

	private static final String SERVER_NAME = "serverName";

	private static final String AUTHORIZATION2 = "authorization";

	private static final String STATUS = "status";

	private static final String STREAMER_NAME = "streamerName";

	private static final String SUBSCRIBER_URL = "subscriberUrl";

	private static final String DESCRIPTION = "description";

	private static final String MSG_ROUTER = "MsgRouter";

	private static final String CATEGORY = "category";

	private static final String DMAAP = "DMaaP";

	private static final String NAMESPACE = "namespace";

	private static final String POLLING_INTERVAL = "pollingInterval";

	private static final String UPDATE_TIME = "updateTime";

	private static final String UPDATED_BY = "updatedBy";

	private static final String UPDATE = "update";

	private static final String CREATE_TIME = "createTime";

	private static final String CREATED_BY = "createdBy";

	private static final String CREATE = "create";

	private static final String SUBSCRIBER_USERNAME = "subscriberUsername";

	private static final String SUBSCRIBER_PASSWORD = "subscriberPassword";

	private static final String PUBLISHER_URL = "publisherUrl";

	private static final String PUBLISHER_PASSWORD = "publisherPassword";

	private static final String PUBLISHER_USER_NAME = "publisherUserName";

	private static final String PREDICTOR_URL = "predictorUrl";

	private static final String MODEL_VERSION = "modelVersion";

	private static final String MODEL_KEY = "modelKey";

	private static final String CATALOG_KEY = "catalogKey";

	private static final String MONGO_PORT = "mongo_port";

	private static final String MONGO_HOSTNAME = "mongo_hostname";

	private static final String MONGO_PASSWORD = "mongo_password";

	private static final String MONGO_DBNAME = "mongo_dbname";

	private static final String MONGO_USERNAME = "mongo_username";

	private static Logger log = LoggerFactory.getLogger(DbUtilities.class);

	private MongoClient mongoClient = null;
	private DB datasrcDB = null;
	private DBCollection datasrcCollection = null;
	
	@Autowired
	Environment env;
	
	@Autowired
	DataStreamerCatalogUtil dataStreamerCatalogUtil;
	
	
	public DbUtilities() {
	}

	private DBCollection getMongoCollection() throws IOException {
		log.info("DbUtilities::getMongoCollection()::trying to get a mongo client instance");

		if (mongoClient == null) {
			log.info(
					"DbUtilities::getMongoCollection()::checking if mongo client is intialised or not, will intialise a copy of it if not intialised.");
			MongoCredential mongoCredential = MongoCredential.createCredential(
					dataStreamerCatalogUtil.getEnv(MONGO_USERNAME, dataStreamerCatalogUtil.getComponentPropertyValue(MONGO_USERNAME)),
					dataStreamerCatalogUtil.getEnv(MONGO_DBNAME, dataStreamerCatalogUtil.getComponentPropertyValue(MONGO_DBNAME)),
					dataStreamerCatalogUtil.getEnv(MONGO_PASSWORD, dataStreamerCatalogUtil.getComponentPropertyValue(MONGO_PASSWORD))
							.toCharArray());

			ServerAddress server = new ServerAddress(
					dataStreamerCatalogUtil.getEnv(MONGO_HOSTNAME, dataStreamerCatalogUtil.getComponentPropertyValue(MONGO_HOSTNAME)),
					Integer.parseInt(
							dataStreamerCatalogUtil.getEnv(MONGO_PORT, dataStreamerCatalogUtil.getComponentPropertyValue(MONGO_PORT))));
			mongoClient = new MongoClient(server, Arrays.asList(mongoCredential));
			log.info("DbUtilities::getMongoCollection():: a new mongo client has been intialised.");
		}

		log.info("DbUtilities::getMongoCollection()::using mongo client to get db connection.");
		datasrcDB = mongoClient
				.getDB(dataStreamerCatalogUtil.getEnv(MONGO_DBNAME, dataStreamerCatalogUtil.getComponentPropertyValue(MONGO_DBNAME)));

		log.info("DbUtilities::getMongoCollection()::using mongo client to get collection.");
		datasrcCollection = datasrcDB.getCollection(dataStreamerCatalogUtil.getEnv("mongo_collection_name",
				dataStreamerCatalogUtil.getComponentPropertyValue("mongo_collection_name")));

		log.info("DbUtilities::getMongoCollection()::returning  collection.");
		return datasrcCollection;
	}

	private DBObject createDBObject(CatalogObject objCatalog, String authorization,String mode) throws IOException {
		log.info("DbUtilities::createDBObject()::intializing db object builder.");
		BasicDBObjectBuilder catalogBuilder = BasicDBObjectBuilder.start();

		log.info("DbUtilities::createDBObject()::intializing _id value.");
		catalogBuilder.append(_ID, objCatalog.getCatalogKey());
		log.info("DbUtilities::createDBObject()::intializing catalog collection value.");
		if (objCatalog.getCatalogKey() != null) {
			catalogBuilder.append(CATALOG_KEY, objCatalog.getCatalogKey());
		}
		if (objCatalog.getModelKey() != null) {
			catalogBuilder.append(MODEL_KEY, objCatalog.getModelKey());
		}
		if (objCatalog.getModelVersion() != null) {
			catalogBuilder.append(MODEL_VERSION, objCatalog.getModelVersion());
		}
		if (objCatalog.getPredictorUrl() != null) {
			catalogBuilder.append(PREDICTOR_URL, objCatalog.getPredictorId());
		}
		if (objCatalog.getPublisherUserName() != null) {
			catalogBuilder.append(PUBLISHER_USER_NAME, objCatalog.getPublisherUserName());
		}
		if (objCatalog.getPublisherPassword() != null) {
			catalogBuilder.append(PUBLISHER_PASSWORD, objCatalog.getPublisherPassword());
		}
		if (objCatalog.getPublisherUrl() != null) {
			catalogBuilder.append(PUBLISHER_URL, objCatalog.getPublisherUrl());
		}
		if (objCatalog.getSubscriberPassword() != null) {
			catalogBuilder.append(SUBSCRIBER_PASSWORD, objCatalog.getSubscriberPassword());
		}
		if (objCatalog.getSubscriberUsername() != null) {
			catalogBuilder.append(SUBSCRIBER_USERNAME, objCatalog.getSubscriberUsername());
		}
		if (objCatalog.getCreatedBy() != null && mode.equals(CREATE)) {
			catalogBuilder.append(CREATED_BY, objCatalog.getCreatedBy());
			catalogBuilder.append(CREATE_TIME, Instant.now().toString());
		}
		if (objCatalog.getModifiedBy() != null && mode.equals(UPDATE)) {
			catalogBuilder.append(UPDATED_BY, objCatalog.getModifiedBy());
			catalogBuilder.append(UPDATE_TIME, Instant.now().toString());
		}
		
		catalogBuilder.append(POLLING_INTERVAL, objCatalog.getPollingInterval());

		catalogBuilder.append(NAMESPACE,
				dataStreamerCatalogUtil.getEnv(NAMESPACE, dataStreamerCatalogUtil.getComponentPropertyValue(NAMESPACE)));
		
		if (objCatalog.getCategory() != null) {
			if (objCatalog.getCategory().equalsIgnoreCase(DMAAP))
				catalogBuilder.append(CATEGORY, DMAAP);
			else if (objCatalog.getCategory().equalsIgnoreCase(MSG_ROUTER))
				catalogBuilder.append(CATEGORY, MSG_ROUTER);
			else
				catalogBuilder.append(CATEGORY, objCatalog.getCategory());
		} /*else if(objCatalog.getCategory() == null && 
					objCatalog.getMessageRouterDetails() != null && 
					objCatalog.getMessageRouterDetails().getServerName() != null) {
			catalogBuilder.append("category", "MsgRouter");
		} else {
			catalogBuilder.append("category", "DMaaP");
		}*/
		
		if (objCatalog.getDescription() != null) {
			catalogBuilder.append(DESCRIPTION, objCatalog.getDescription());
		}
		if (objCatalog.getSubscriberUrl() != null) {
			catalogBuilder.append(SUBSCRIBER_URL, objCatalog.getSubscriberUrl());
		}
		if (objCatalog.getStreamerName() != null) {
			catalogBuilder.append(STREAMER_NAME, objCatalog.getStreamerName());
		}
		catalogBuilder.append(STATUS, objCatalog.isStatus());
		catalogBuilder.append(AUTHORIZATION2, authorization);
		
		if(objCatalog.getMessageRouterDetails() != null) {
			BasicDBObject messageRouterDetails = new BasicDBObject();
			if (objCatalog.getMessageRouterDetails().getServerName() != null) {
				messageRouterDetails.append(SERVER_NAME, objCatalog.getMessageRouterDetails().getServerName());
			}
			
			messageRouterDetails.append(SERVER_PORT, objCatalog.getMessageRouterDetails().getServerPort());
			
			if (objCatalog.getMessageRouterDetails().getUserName() != null) {
				messageRouterDetails.append(USER_NAME, objCatalog.getMessageRouterDetails().getUserName());
			}
			
			if (objCatalog.getMessageRouterDetails().getPassword() != null) {
				messageRouterDetails.append(PASSWORD, objCatalog.getMessageRouterDetails().getPassword());
			}
			
			if (objCatalog.getMessageRouterDetails().getTopicName() != null) {
				messageRouterDetails.append(TOPIC_NAME, objCatalog.getMessageRouterDetails().getTopicName());
			}
			
			if (objCatalog.getMessageRouterDetails().getSerializer() != null) {
				messageRouterDetails.append(SERIALIZER, objCatalog.getMessageRouterDetails().getSerializer());
			}
			
			if (objCatalog.getMessageRouterDetails().getDeSerializer() != null) {
				messageRouterDetails.append(DE_SERIALIZER, objCatalog.getMessageRouterDetails().getDeSerializer());
			}
			
			catalogBuilder.append(MESSAGE_ROUTER_DETAILS, messageRouterDetails);
		}

		return catalogBuilder.get();
	}

	public void insertCatalogDetails(String user, String authorization,CatalogObject objCatalog) throws DataStreamerException, IOException {
		DBObject result = null;
		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();
		log.info("DbUtilities::insertCatalogDetails()::checking user before insertion");
		if (user != null) {
			query.add(CREATED_BY, user);
		} else {
			log.info("DbUtilities::insertCatalogDetails()::user value is null");
			throw new DataStreamerException("OOPS. User not available");
		}
		
		log.info("DbUtilities::insertCatalogDetails()::checking predictor id");
		if (objCatalog.getStreamerName() != null) {
			query.add(STREAMER_NAME, objCatalog.getStreamerName());
		} else {
			log.info("DbUtilities::insertCatalogDetails()::streamer name is null");
			throw new DataStreamerException("OOPS. Please provide staremer name.");
		}

		log.info("DbUtilities::insertCatalogDetails()::checking predictor id");
		if (objCatalog.getPredictorUrl() != null) {
			query.add(PREDICTOR_URL, objCatalog.getPredictorUrl());
		} else {
			log.info("DbUtilities::insertCatalogDetails()::predictor id is null");
			throw new DataStreamerException("OOPS. Please provide predictor id.");
		}

		log.info("DbUtilities::insertCatalogDetails()::checking publisher url");
		if (objCatalog.getPublisherUrl() != null) {
			query.add(PUBLISHER_URL, objCatalog.getPublisherUrl());
		} else {
			log.info("DbUtilities::insertCatalogDetails()::publisher url is null");
			throw new DataStreamerException("OOPS. Please provide publisher url");
		}
		query.add(POLLING_INTERVAL, objCatalog.getPollingInterval());

		/* if (!query.isEmpty()) { */
		result = getMongoCollection().findOne(query.get());

		/*
		 * } else { throw new CmlpDataSrcException(
		 * "OOPS. Please check catalog key provided and ser persmission for this operation"
		 * ); }
		 */

		if (result == null) {
			WriteResult insertResult = getMongoCollection()
					.insert(createDBObject(objCatalog, authorization,CREATE));
			log.info("DbUtilities::insertCatalogDetails()::id of the inserted mongo object: "
					+ insertResult.getUpsertedId());
		} else {
			log.info(
					"DbUtilities::insertCatalogDetails()::Please check publisher URL and predictor id, since this association is already present");
			throw new DataStreamerException(
					"OOPS. Please check publisher URL and predictor id, since this association is already present");
		}

	}

	public ArrayList<String> getCatalogDetails(String user, String category, String textSearch, String authorization)
			throws DataStreamerException, IOException {
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
				query.add(CREATED_BY, user);
			} else if (user == null) {
				throw new DataStreamerException("user can't be null");
			}

			if (category != null) {
				log.info("DbUtilities::getCatalogDetails(), checking category, get operation is being performed for "
						+ category + " category.");
				query.add(CATEGORY, category);
			}

		}
		//query.add("status", true);

		log.info("DbUtilities::getCatalogDetails(), running query");
		cursor = getMongoCollection().find(query.get());

		log.info("DbUtilities::getCatalogDetails(), processing resultset");

		DBObject tempStorage;

		while (cursor.hasNext()) {
			tempStorage = cursor.next();
			retrieved.add(tempStorage.toString());
		}
		return retrieved;
	}

	private ArrayList<String> getCatalogDetailsByTextSearch(String user, String textSearch)
			throws DataStreamerException, IOException {

		ArrayList<String> results = new ArrayList<String>();
		DBCursor cursor = null;

		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();

		log.info("DbUtilities::getCatalogDetailsByTextSearch()::checking inputs");

		if (user == null) {
			throw new DataStreamerException("user can't be null");
		}

		log.info("DbUtilities::getCatalogDetailsByTextSearch()::checking user, get operation is being performed by "
				+ user);
		query.add(CREATED_BY, user);
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
			throw new DataStreamerException(
					"OOPS. TextSearch field is required. Please provide a value for textSearch.");
		}

		return results;
	}

	public boolean deleteCatalog(String user, String catalogKey) throws IOException, DataStreamerException {
		boolean delete = false;
		DBCursor cursor = null;
		WriteResult result = null;

		log.info("DbUtilities::deletecatalog(), intializing query object");
		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();

		log.info("DbUtilities::deletecatalog(), checking user");
		if (user != null && !user.isEmpty()) {
			query.add(CREATED_BY, user);
		} else {
			log.info("DbUtilities::deletecatalog(), user value is null");
			throw new DataStreamerException("OOPS. User not available. Please provide a user.");
		}

		log.info("DbUtilities::deletecatalog(), checking catalogKey");
		if (catalogKey != null && !catalogKey.isEmpty()) {
			query.add(_ID, catalogKey);
		} else {
			log.info("DbUtilities::deletecatalog(), datsourceKey value is null");
			throw new DataStreamerException("OOPS. Please provide catalog key");
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
			throw new DataStreamerException(
					"OOPS. Please check catalog key provided and user persmission for this operation");
		}
		return delete;
	}


	public boolean softDeleteCatalog(String user, String catalogKey) throws DataStreamerException, IOException {
		boolean delete = false;
		DBCursor cursor = null;
		WriteResult result = null;
		DBObject DBObj = null;

		log.info("DbUtilities::softDeletecatalog(), intializing query object");
		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();

		log.info("DbUtilities::softDeletecatalog(), checking user");
		if (user != null && !user.isEmpty()) {
			query.add(CREATED_BY, user);
		} else {
			log.info("DbUtilities::softDeletecatalog(), user value is null");
			throw new DataStreamerException("OOPS. User not available. Please provide a user.");
		}

		log.info("DbUtilities::softDeletecatalog(), checking catalogKey");
		if (catalogKey != null && !catalogKey.isEmpty()) {
			query.add(_ID, catalogKey);
		} else {
			log.info("DbUtilities::softDeletecatalog()::datsourceKey value is null");
			throw new DataStreamerException("OOPS. Please provide catalog key");
		}

		log.info("DbUtilities::softDeletecatalog()::populating cursor with collection that is to be deleted");
		cursor = getMongoCollection().find(query.get());

		log.info("DbUtilities::softDeletecatalog(), checking cursor for value");
		if (cursor.hasNext()) {
			log.info("DbUtilities::softDeletecatalog(), issuing command to delete object with id: " + catalogKey);
			DBObj = cursor.next();
			DBObj.put(STATUS, new Boolean(false));
			result = getMongoCollection().update(query.get(), new BasicDBObject().append("$set", DBObj));
			log.info("result for deletion: " + result.toString());
			delete = result.isUpdateOfExisting();
		} else {
			log.info("DbUtilities::softDeletecatalog(), deletion failed for object with id: " + catalogKey
					+ " .Please check catalog key provided and user persmission for this operation");
			throw new DataStreamerException(
					"OOPS. Please check catalog key provided and user persmission for this operation");
		}
		return delete;
	}

	public boolean updateCatalog(String user, String catalogKey, String authorization,CatalogObject objCatalog) throws DataStreamerException, IOException {
		boolean update = false;
		WriteResult result = null;
		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();

		log.info("DbUtilities::updatecatalog()::checking user");
		if (user != null) {
			query.add(CREATED_BY, user);
		} else {
			log.info("DbUtilities::updatecatalog()::user value is null");
			throw new DataStreamerException("OOPS. User not available");
		}

		log.info("DbUtilities::updatecatalog()::checking catalogkey");
		if (catalogKey != null) {
			query.add(_ID, catalogKey);
		} else {
			log.info("DbUtilities::updatecatalog()::catalogKey value is null");
			throw new DataStreamerException("OOPS. Please provide catalog key");
		}

		if (!query.isEmpty()) {
			log.info("DbUtilities::updatecatalog(), issuing command to update object with id: " + catalogKey);
			result = getMongoCollection().update(query.get(), new BasicDBObject().append("$set",
					createDBObject(objCatalog, authorization, UPDATE)));

			update = result.isUpdateOfExisting();
		} else {
			log.info("DbUtilities::updatecatalog()::updation failed for object with id: " + catalogKey
					+ " .Please check catalog key provided and user persmission for this operation");
			throw new DataStreamerException(
					"OOPS. Please check catalog key provided and ser persmission for this operation");
		}
		return update;
	}

	public DBObject getCatalogDetailsByKey(String user, String catalogKey, String mode)
			throws IOException, DataStreamerException {

		BasicDBObjectBuilder query = BasicDBObjectBuilder.start();
		log.info("DbUtilities::getCatalogDetailsByKey()::checking inputs");

		if (user != null) {
			log.info("DbUtilities::getCatalogDetailsByKey()::checking user, get operation is being performed by user: "
					+ user);
			query.add(CREATED_BY, user);
		} else {
			log.info("DbUtilities::getCatalogDetailsByKey()::user has not been provided");
			throw new DataStreamerException("Please provide proper AAF authentication");
		}

		if (catalogKey != null) {
			log.info(
					"DbUtilities::getCatalogDetailsByKey()::checking category, get operation is being performed for id: "
							+ catalogKey);
			query.add(_ID, catalogKey);
		} else {
			log.info("DbUtilities::getCatalogDetailsByKey()::catalog key has not been provided");
			throw new DataStreamerException("Please provide a catalog key");
		}

		//query.add("status", true);

		log.info("DbUtilities::getCatalogDetailsByKey()::running query");
		DBObject result = getMongoCollection().findOne(query.get());

		if (result == null) {
			log.info(
					"DbUtilities::getCatalogDetailsByKey()::No data is available for provided catalog key. Either the key doesn't exist or user doesn't has permission for the record for user: "
							+ user + " and record: " + catalogKey);
			throw new DataStreamerException(
					"No data is available for provided catalog key. Either the key doesn't exist or user doesn't has permission for the record.");
		}

		if (mode.equals(DETAIL)) {
			result.removeField(AUTHORIZATION2);
		}

		return result;
	}

}
