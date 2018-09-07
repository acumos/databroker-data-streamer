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

package org.acumos.streamer.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acumos.streamer.exception.CmlpDataSrcException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * Helper class to maintain application level resources
 * </p>
 * 
 * @author mn461x
 * @since Feb 23, 2017
 * @version $Id$
 */


public class HelperTool {
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static String getComponentPropertyValue(String key) throws IOException {
		Properties prop = new Properties();
		InputStream input;
		if (new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "config.properties")
				.exists()) {
			input = new FileInputStream(
					System.getProperty("user.dir") + System.getProperty("file.separator") + "config.properties");
		} else {
			input = HelperTool.class.getResourceAsStream("/config.properties");
		}
		prop.load(input);
		input.close();

		return prop.getProperty(key);
	}
	
	public static String getComponentPropertyValue(String key, String defaultValue) throws IOException {
		Properties prop = new Properties();
		InputStream input;
		String value = null;
		if (new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "config.properties")
				.exists()) {
			input = new FileInputStream(
					System.getProperty("user.dir") + System.getProperty("file.separator") + "config.properties");
		} else {
			input = HelperTool.class.getResourceAsStream("/config.properties");
		}
		prop.load(input);
		input.close();

		value = prop.getProperty(key);
		
		if(value != null)
			return value;
		else
			return defaultValue;
	}

	public static String getRemoteUser(HttpServletRequest request) {
		if (request.getRemoteUser() != null) {
			return request.getRemoteUser();
		}
		if (request.getUserPrincipal() != null) {
			return request.getUserPrincipal().getName();
		}
		String authorization = request.getHeader("Authorization");
		if (authorization != null && authorization.startsWith("Basic")) {
			// Authorization: Basic base64credentials
			String base64Credentials = authorization.substring("Basic".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
			// credentials = username:password
			final String[] values = credentials.split(":", 2);
			return values[0];
		}
		return null;
	}

	public static String getEnv(String envKey, String defaultValue) {
		String value = System.getenv(envKey);
		if (value == null) {
			value = System.getProperty(envKey);
		}
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	public static void setEnv(String envKey, String value) {
		if (value != null) {
			System.setProperty(envKey, value);
		}
	}

	
	public static JSONObject getPredictorDetails (String authorization, String catalogKey) throws IOException, CmlpDataSrcException{
		
		String catalogUrl = HelperTool.getEnv("catalog_url_prefix",
				HelperTool.getComponentPropertyValue("catalog_url_prefix")) + catalogKey + "?mode=concise";
		log.info("HelerTool::getPredictorDetails()::url to be called for getting information about catalog is " + catalogUrl);
		
		HttpClient client = HttpClients.createDefault();

		HttpGet request = new HttpGet(catalogUrl);
		request.addHeader("Authorization", authorization);

		HttpResponse response = client.execute(request);

		StringBuilder responseContents = new StringBuilder();

		log.info("HelerTool::getPredictorDetails()::response from streamer catalog service is " + response.getStatusLine().getStatusCode()
				+ " for predictor key: " + catalogKey);
		if (response.getStatusLine().getStatusCode() == 200) {
			log.info("HelerTool::getPredictorDetails()::Received OK response from ConfigMgmt.");
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			String line = "";
			while ((line = rd.readLine()) != null) {
				responseContents.append(line);
			}
			rd.close();
		} else if (response.getStatusLine().getStatusCode() == 401) {
			log.info("HelerTool::getPredictorDetails()::Received Bad response from ConfigMgmt. Throwing exception...");
			throw new CmlpDataSrcException(
					"OOPS. The database sharing is still not completed. Please ask admin to provide access to get credentials.");
		} else {
			log.info("HelerTool::getPredictorDetails()::Received Bad response from ConfigMgmt. Throwing exception...");
			throw new CmlpDataSrcException("OOPS. Something went wrong with decryption");
		}

		try {
			request.releaseConnection();
		} catch (Exception e) {
			log.info("ignoring the exception during release connection of http request.");
		}
		return new JSONObject(responseContents.toString());
	}
	
	public static JSONArray getCatalogsByCategory (String authorization, String category) 
			throws IOException, CmlpDataSrcException, ParseException{
		
		String configPrefix = HelperTool.getEnv("catalog_url_prefix", HelperTool.getComponentPropertyValue("catalog_url_prefix"));
		String catalogPrefix = configPrefix.endsWith("/")?configPrefix.substring(0,configPrefix.lastIndexOf("/")):configPrefix;
		
		String catalogUrl = catalogPrefix  + "?category=" + category;
		log.info("HelerTool::getCatalogByCategory()::url to be called for searching catalog by category " + catalogUrl);
		
		HttpClient client = HttpClients.createDefault();

		HttpGet request = new HttpGet(catalogUrl);
		request.addHeader("Authorization", authorization);

		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		String responseContents = EntityUtils.toString(response.getEntity());

		log.info("HelerTool::getCatalogByCategory()::response from streamer catalog service is " + statusCode);
		if (statusCode > 299) {
			log.info("Error occurred searching catalog: " + responseContents);
			throw new CmlpDataSrcException("Error occurred searching catalog: " + responseContents);
		}

		try {
			request.releaseConnection();
		} catch (Exception e) {
			log.info("ignoring the exception during release connection of http request.");
		}
		
        JSONParser parser = new JSONParser();
        JSONArray catalogArray = (JSONArray) parser.parse(responseContents);
		return catalogArray;
	}
	
	public static String[] getMsgsFromSubscriber (String authorization, String subscriberUrl) 
			throws IOException, CmlpDataSrcException, ParseException{
		
		HttpClient client = HttpClients.createDefault();
		ObjectMapper objMapper = new ObjectMapper();

		HttpGet request = new HttpGet(subscriberUrl);
		request.addHeader("Authorization", authorization);

		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		String responseContents = EntityUtils.toString(response.getEntity());
		
		System.out.println(responseContents);
		
		if(responseContents == null || responseContents.equals("") ) {
			return new String[] {};
		}
		
		String[] responseContentsFromObjMapper = objMapper.readValue(responseContents, String[].class); 
		System.out.println(responseContentsFromObjMapper);
		
	
		log.info("HelerTool::getMsgsFromSubscriber()::response from MsgRouter subscriberUrl service is " + statusCode);
		if (statusCode > 299) {
			log.info("Error occurred during getting messages from subscriberUrl: " + responseContents);
			throw new CmlpDataSrcException("Error occurred during getting messages from subscriberUrl: " + responseContents);
		}

		try {
			request.releaseConnection();
		} catch (Exception e) {
			log.info("ignoring the exception during release connection of http request.",e.getMessage());
		}
      
		return responseContentsFromObjMapper;
	}
	
}
