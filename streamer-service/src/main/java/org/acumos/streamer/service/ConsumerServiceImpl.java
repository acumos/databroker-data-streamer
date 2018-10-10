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

package org.acumos.streamer.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.acumos.streamer.common.DataStreamerUtil;
import org.acumos.streamer.exception.DataStreamerException;

@Service
public class ConsumerServiceImpl implements ConsumerService {

	private static final String FEED_AUTH_DIFF = ":";

	private static final String PASSWORD = "password";

	private static final String USRNAME = "usrname";

	private static final String PAYLOAD = "payload";

	private static final String NEWLINE = "\n";

	private static final String MODEL_VERSION2 = "modelVersion";

	private static final String MODEL_VERSION = "model_version";

	private static final String MODEL_KEY2 = "modelKey";

	private static final String MODEL_KEY = "model_key";

	private static final String AUTHORIZATION3 = "authorization";

	private static final String AUTHORIZATION2 = "Authorization";

	private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";

	private static final String CONTENT_TYPE = "Content-Type";

	private static final String SUCCESS = "success";

	private static final String STATUS = "status";

	private static final String FILE_SUFFIX = "file_suffix";

	private static final String FILEPATH_STRING = "filepath_string";

	private static final String USER_DIR = "user.dir";

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	PublisherService aPublisherService;
	
	@Autowired
	DataStreamerUtil dataStreamerUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.att.cmlp.cmlpstreamer.service.ConsumerService#operateData(java.lang.
	 * String, java.lang.String, java.lang.String,
	 * org.apache.cxf.jaxrs.ext.multipart.MultipartBody)
	 */
	@Override
	public String operateData(String user, String authorization, String feedAuthorization, String fileid,
			String catalogKey, InputStream attachedFiles) throws DataStreamerException {

		log.info("ConsumerServiceImpl::operateData()::data recieved from data router.");

		String filename = null;
		try {
			filename = URLEncoder.encode(fileid, "UTF-8").replaceAll("^\\.", "%2E").replaceAll("\\*", "%2A");
			log.info("ConsumerServiceImpl::operateData()::the intermdeiate file name generated is " + filename);
		} catch (UnsupportedEncodingException e) {
			log.error("ConsumerServiceImpl::operateData()::Encountered error while generating file name :"
					+ e.getMessage());
		}

		String finalname = null;
		try {
			log.info("ConsumerServiceImpl::operateData()::user dir:" + System.getProperty(USER_DIR));
			log.info("ConsumerServiceImpl::operateData()::" + dataStreamerUtil.getEnv(FILEPATH_STRING, dataStreamerUtil.getComponentPropertyValue(FILEPATH_STRING)));
		    log.info("ConsumerServiceImpl::operateData()::" + dataStreamerUtil.getEnv(FILE_SUFFIX, dataStreamerUtil.getComponentPropertyValue(FILE_SUFFIX)));
			finalname = System.getProperty(USER_DIR) + System.getProperty("file.separator")
					+ dataStreamerUtil.getEnv(FILEPATH_STRING, dataStreamerUtil.getComponentPropertyValue(FILEPATH_STRING))
					+ System.getProperty("file.separator") + filename
					+ dataStreamerUtil.getEnv(FILE_SUFFIX, dataStreamerUtil.getComponentPropertyValue(FILE_SUFFIX));
			log.info("ConsumerServiceImpl::operateData()::generating the absolute path of file " + finalname);
		} catch (IOException e) {
			log.error("ConsumerServiceImpl::operateData()::Encountered error while generating absolute path of file :"
					+ e.getMessage());
		}

		log.info("ConsumerServiceImpl::operateData()::fetching details about the catlog key: " + catalogKey
				+ "  from catalog manager.");
		JSONObject catalogDetails = null;

		try {
			log.info("ConsumerServiceImpl::operateData()::fetching details of predictor");
			catalogDetails = dataStreamerUtil.getCatalogDetails(authorization,catalogKey);		
		
		} catch (IOException | DataStreamerException e) {
			log.error("ConsumerServiceImpl::operateData()::Encountered error while fetchng details of predictor :"
					+ e.getMessage());
		}

		String predictorUrl = catalogDetails.getString("predictorUrl");
		log.info("ConsumerServiceImpl::operateData()::catalog anager returned following predictor url :" + predictorUrl
				+ " to post data recieved for catalog key: " + catalogKey);
		
		if (catalogDetails.getBoolean(STATUS)){
			return SUCCESS;
		}

		URL aUrl = null;
		try {
			log.info("ConsumerServiceImpl::operateData()::intiating URL connection to predictor");
			aUrl = new URL(predictorUrl);
		} catch (MalformedURLException e) {
			log.error("ConsumerServiceImpl::operateData()::Encountered error while preparing URL for predictor");
		}
		HttpURLConnection aHttpURLConnection = null;
		try {
			log.info("ConsumerServiceImpl::operateData()::opening HTTP connection");
			aHttpURLConnection = (HttpURLConnection) aUrl.openConnection();
		} catch (IOException e) {
			log.error("ConsumerServiceImpl::operateData()::HTTP connection opening failed");
		}
		
		aHttpURLConnection.setInstanceFollowRedirects(false);
		aHttpURLConnection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8);
		try {
			log.info("ConsumerServiceImpl::operateData()::setting up method type for HTTP call");
			aHttpURLConnection.setRequestMethod("POST");
		} catch (ProtocolException e1) {
			log.error("ConsumerServiceImpl::operateData()::failed to intiate method type for HTTP call");
		}
		aHttpURLConnection.setRequestProperty(AUTHORIZATION2, catalogDetails.getString(AUTHORIZATION3));

		JSONObject postObject = new JSONObject();
		postObject.put(MODEL_KEY, catalogDetails.getString(MODEL_KEY2));
		postObject.put(MODEL_VERSION, catalogDetails.getString(MODEL_VERSION2));

		log.info("ConsumerServiceImpl::operateData()::sending the POST HTTP request");
		aHttpURLConnection.setDoOutput(true);
		DataOutputStream wr = null;
		try {
			wr = new DataOutputStream(aHttpURLConnection.getOutputStream());
		} catch (IOException e) {
			log.error("ConsumerServiceImpl::operateData()::Encountered error while intaiating o/p stream for HTTP connection.");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(attachedFiles));
		StringBuilder fileContents = new StringBuilder();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				fileContents.append(line);
				fileContents.append(NEWLINE);
			}
		} catch (IOException e) {
			log.error("ConsumerServiceImpl::operateData()::Encountered error while reading the incoming data");
		}
		postObject.put(PAYLOAD, fileContents.toString());
		try {
			wr.writeBytes(postObject.toString());
			wr.flush();
			wr.close();
		} catch (IOException e) {
			log.error("ConsumerServiceImpl::operateData()::encountered error while writing incoming data to HTTP connection for scoring");
		}

		int responseCode = 0;
		try {
			attachedFiles.close();
			br.close();
			responseCode = aHttpURLConnection.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("ConsumerServiceImpl::operateData()::encountered error while getting the response code from the URLConnection");
		}

		log.info("ConsumerServiceImpl::operateData()::the response from predictor with URL as: " + predictorUrl
				+ " returned a rsponse as " + responseCode);
		if (responseCode != 200) {
			String errorMessage = null;
			try {
				errorMessage = aHttpURLConnection.getResponseMessage();
			} catch (IOException e) {
				log.error("ConsumerServiceImpl::operateData()::encountered error while getting responseMessage from URLConnection");
			}
			log.info("ConsumerServiceImpl::operateData()::There was error during scoring. he preditor retuned a code: "
					+ responseCode + ". The assocaed reuned message is " + errorMessage);
			throw new DataStreamerException("There was error during scoring. he preditor retuned a code: " + responseCode
					+ ". The assocaed reuned message is " + errorMessage);
		}

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(aHttpURLConnection.getInputStream()));
		} catch (IOException e) {
			log.error("ConsumerServiceImpl::operateData()::encountered error while getting inputStream from URLConnection");
		}
		String inputLine;
		StringBuffer responseFromScoring = new StringBuffer();

		try {
			while ((inputLine = in.readLine()) != null) {
				responseFromScoring.append(inputLine);
				responseFromScoring.append(NEWLINE);
			}
			in.close();
		} catch (IOException e) {
			log.error("ConsumerServiceImpl::operateData()::encountered error while reading inputStream from URLConnection");
		}

		log.info("ConsumerServiceImpl::operateData()::response message size is" + responseFromScoring.length());
		OutputStream os = null;
		try {
			os = new FileOutputStream(finalname);
		} catch (FileNotFoundException e) {
			
		}
		try {
			os.write(responseFromScoring.toString().getBytes());
			os.flush();
			os.close();
		} catch (IOException e) {
			log.error("ConsumerServiceImpl::operateData()::encountered error while writting to a outputStream");
		}
		String feedAuth = catalogDetails.getString(USRNAME) + FEED_AUTH_DIFF + catalogDetails.getString(PASSWORD);
		String publishId = aPublisherService.publish(catalogKey, feedAuth, finalname,
				catalogDetails.getString("publisherUrl"), null);
		log.info("ConsumerServiceImpl::operateData()::The scoring response has been published as " + publishId);
		(new File(finalname)).delete();
		return publishId;
	}

	@Override
	public String receiveData(String user, String authorization, String feedAuthorization,
			MultipartBody attachedFiles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteData(String user, String authorization, String feedAuthorization, MultipartBody attachedFiles) {
		// TODO Auto-generated method stub
		return null;
	}

}
