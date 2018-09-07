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
import org.acumos.streamer.common.HelperTool;
import org.acumos.streamer.exception.CmlpDataSrcException;

/**
 * @author am375y
 *
 */
@Service
public class ConsumerServiceImpl implements ConsumerService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	PublisherService aPublisherService;

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
			String catalogKey, InputStream attachedFiles) throws CmlpDataSrcException {

		log.info("ConsumerServiceImpl::operateData()::data recieved from data router.");

		String filename = null;
		try {
			filename = URLEncoder.encode(fileid, "UTF-8").replaceAll("^\\.", "%2E").replaceAll("\\*", "%2A");
			log.info("ConsumerServiceImpl::operateData()::the intermdeiate file name generated is " + filename);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.info("ConsumerServiceImpl::operateData()::Encountered error while generating file name :"
					+ e.getMessage());
		}

		String finalname = null;
		try {
			log.info("ConsumerServiceImpl::operateData()::user dir:" + System.getProperty("user.dir"));
			log.info("ConsumerServiceImpl::operateData()::" + HelperTool.getEnv("filepath_string", HelperTool.getComponentPropertyValue("filepath_string")));
		    log.info("ConsumerServiceImpl::operateData()::" + HelperTool.getEnv("file_suffix", HelperTool.getComponentPropertyValue("file_suffix")));
			finalname = System.getProperty("user.dir") + System.getProperty("file.separator")
					+ HelperTool.getEnv("filepath_string", HelperTool.getComponentPropertyValue("filepath_string"))
					+ System.getProperty("file.separator") + filename
					+ HelperTool.getEnv("file_suffix", HelperTool.getComponentPropertyValue("file_suffix"));
			log.info("ConsumerServiceImpl::operateData()::generating the absolute path of file " + finalname);
		} catch (IOException e) {
			log.info("ConsumerServiceImpl::operateData()::Encountered error while generating absolute path of file :"
					+ e.getMessage());
		}

		log.info("ConsumerServiceImpl::operateData()::fetching details about the catlog key: " + catalogKey
				+ "  from catalog manager.");
		JSONObject predictorDetails = null;

		try {
			log.info("ConsumerServiceImpl::operateData()::fetching details of predictor");
			predictorDetails = HelperTool.getPredictorDetails(authorization,catalogKey);		
		
		} catch (IOException | CmlpDataSrcException e) {
			log.info("ConsumerServiceImpl::operateData()::Encountered error while fetchng details of predictor :"
					+ e.getMessage());
		}

		String predictorUrl = predictorDetails.getString("predictorUrl");
		log.info("ConsumerServiceImpl::operateData()::catalog anager returned following predictor url :" + predictorUrl
				+ " to post data recieved for catalog key: " + catalogKey);
		
		if (predictorDetails.getBoolean("status")){
			return "success";
		}

		URL aUrl = null;
		try {
			log.info("ConsumerServiceImpl::operateData()::intiating URL connection to predictor");
			aUrl = new URL(predictorUrl);
		} catch (MalformedURLException e) {
			log.info("ConsumerServiceImpl::operateData()::Encountered error while prearing URL for predictor");
			e.printStackTrace();
		}
		HttpURLConnection aHttpURLConnection = null;
		try {
			log.info("ConsumerServiceImpl::operateData()::opening HTTP connection");
			aHttpURLConnection = (HttpURLConnection) aUrl.openConnection();
		} catch (IOException e) {
			log.info("ConsumerServiceImpl::operateData()::HTTP connection opening failed");
			e.printStackTrace();
		}
		
		aHttpURLConnection.setInstanceFollowRedirects(false);
		aHttpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		try {
			log.info("ConsumerServiceImpl::operateData()::setting up method type for HTTP call");
			aHttpURLConnection.setRequestMethod("POST");
		} catch (ProtocolException e1) {
			log.info("ConsumerServiceImpl::operateData()::failed to intiate method type for HTTP call");
			e1.printStackTrace();
		}
		aHttpURLConnection.setRequestProperty("Authorization", predictorDetails.getString("authorization"));//"Basic bTA5Mjg2QGNtbHAuYXR0LmNvbTpQRXllIW5UaGVTa3k=");
		aHttpURLConnection.setRequestProperty("CodeCloud-Authorization",
				predictorDetails.getString("codeCloudAuthorization"));// "Basic
																		// bTA5Mjg2OkF3ZXNvbWVjM3Awcm9ja3Mh");

		JSONObject postObject = new JSONObject();
		postObject.put("model_key", predictorDetails.getString("modelKey")); // "com_att_omni_m09286_ST_CMLP_smpmml01");//predictorDetails.getString("modelKey"));//"com_att_omni_m09286_ST_CMLP_smpmml01");
		postObject.put("model_version", predictorDetails.getString("modelVersion"));// "91c6871352a632ebe3a8ceb237da2ae926f212b2");//predictorDetails.getString("modelVersion"));//"91c6871352a632ebe3a8ceb237da2ae926f212b2");

		log.info("ConsumerServiceImpl::operateData()::sending the POST HTTP request");
		aHttpURLConnection.setDoOutput(true);
		DataOutputStream wr = null;
		try {
			wr = new DataOutputStream(aHttpURLConnection.getOutputStream());
		} catch (IOException e) {
			log.info("ConsumerServiceImpl::operateData()::Encountered error while intaiating o/p stream for HTTP connection.");
			e.printStackTrace();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(attachedFiles));
		StringBuilder fileContents = new StringBuilder();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				fileContents.append(line);
				fileContents.append("\n");
			}
		} catch (IOException e) {
			log.info("ConsumerServiceImpl::operateData()::Encountered error while reading the incoming data");
			e.printStackTrace();
		}
		postObject.put("payload", fileContents.toString());
		try {
			wr.writeBytes(postObject.toString());
			wr.flush();
			wr.close();
		} catch (IOException e) {
			log.info("ConsumerServiceImpl::operateData()::encountered error while writing incoming data to HTTP connection for scoring");
			e.printStackTrace();
		}

		int responseCode = 0;
		try {
			attachedFiles.close();
			br.close();
			responseCode = aHttpURLConnection.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("ConsumerServiceImpl::operateData()::the response from predictor with URL as: " + predictorUrl
				+ " returned a rsponse as " + responseCode);
		if (responseCode != 200) {
			String errorMessage = null;
			try {
				errorMessage = aHttpURLConnection.getResponseMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("ConsumerServiceImpl::operateData()::There was error during scoring. he preditor retuned a code: "
					+ responseCode + ". The assocaed reuned message is " + errorMessage);
			throw new CmlpDataSrcException("There was error during scoring. he preditor retuned a code: " + responseCode
					+ ". The assocaed reuned message is " + errorMessage);
		}

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(aHttpURLConnection.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String inputLine;
		StringBuffer responseFromScoring = new StringBuffer();

		try {
			while ((inputLine = in.readLine()) != null) {
				responseFromScoring.append(inputLine);
				responseFromScoring.append("\n");
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// https://feeds-uat-drtr.web.att.com/publish/4862
		log.info("ConsumerServiceImpl::operateData()::response message size is" + responseFromScoring.length());
		OutputStream os = null;
		try {
			os = new FileOutputStream(finalname);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			os.write(responseFromScoring.toString().getBytes());
			os.flush();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String feedAuth = predictorDetails.getString("usrname") + ":" + predictorDetails.getString("password");
		String publishId = aPublisherService.publish(catalogKey, feedAuth, finalname,
				predictorDetails.getString("publisherUrl"), null);
		log.info("ConsumerServiceImpl::operateData()::The scoring response has been published as " + publishId);
		(new File(finalname)).delete();
		return publishId;
	}

	@Override
	public String recieveData(String user, String authorization, String feedAuthorization,
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
