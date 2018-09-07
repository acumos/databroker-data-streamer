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

package org.acumos.streamercatalog.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acumos.streamercatalog.exception.CmlpDataSrcException;
import org.acumos.streamercatalog.model.CatalogObject;
import com.att.eelf.configuration.EELFLogger;

/**
 * <p>
 * Helper class to maintain application level resources
 * </p>
 * 
 */
public class HelperTool {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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


	public static boolean isPath(String text) {

		if (text == null) {
			return false;
		}
		File inFile = new File(text);

		return !(!inFile.isDirectory() && !inFile.isFile());
	}


	public static boolean isFileExists(String text) throws IOException {

		if (text == null) {
			return false;
		}
		File inFile = null;
		if (text.toLowerCase().startsWith("file://")) {
			inFile = new File((new URL(text)).getFile());
		} else {
			inFile = new File(text);
		}

		return (inFile.exists());
	}

	public static boolean isFileUrl(String urlString) {
		try {
			new URL(urlString);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static String readHttpURLtoString(URL urlPath) throws IOException {
		String outcome = null;
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlPath.openStream()))) {
			StringBuilder stringBuilder = new StringBuilder();

			String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null) {
				stringBuilder.append(inputLine);
				stringBuilder.append(System.lineSeparator());
			}
			outcome = stringBuilder.toString().trim();
		}
		return outcome;
	}

	public static String extractUsername(String userName) {
		return (userName.indexOf("@") > 0) ? userName.substring(0, userName.indexOf("@")) : userName;

	}

	public static boolean isFileinHttp(URL urlPath) {
		try {
			readHttpURLtoString(urlPath);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isHTTPServerOnline(String ipAddress, int port) {
		boolean b = true;
		try {
			InetSocketAddress sa = new InetSocketAddress(ipAddress, port);
			Socket ss = new Socket();
			ss.connect(sa, 1);
			ss.close();
		} catch (Exception e) {
			b = false;
		}
		return b;
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

	private static void setEnv(String envKey, String value) {
		if (value != null) {
			System.setProperty(envKey, value);
		}
	}


	private static void removeDirectory(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null && files.length > 0) {
				for (File aFile : files) {
					removeDirectory(aFile);
				}
			}
			directory.delete();
		} else {
			directory.delete();
		}
	}

	private static void cleanDirectory(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null && files.length > 0) {
				for (File aFile : files) {
					removeDirectory(aFile);
				}
			}
		}
		directory.delete();
	}
	
	/*private static String readAttachedFileContents(Attachment attachment) {
		String contents = null;
		
		FileInputStream fis = null;

		try {
			
			String attachmentfilename = attachment.getContentDisposition().getFilename();
			String tmpFileName = System.getProperty("user.dir") + System.getProperty("file.separator") +  attachmentfilename + ".tmp";
			
			File tmpFile = new File(tmpFileName);
			
			attachment.transferTo(tmpFile);
				
			fis = new FileInputStream(tmpFile);

			StringBuilder sb = new StringBuilder();

			int content;
			while ((content = fis.read()) != -1) {
				// convert to char and display it
				sb.append((char) content);
			}
			
			contents = sb.toString();
			
			Files.deleteIfExists(Paths.get(tmpFileName));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return contents;
	}*/
	
	public static JSONObject getModelDetails (String authorization, String modelKey) throws IOException, CmlpDataSrcException{
		
		logger.info("HelperTool::getModelDetails()::prepping request to model manager to get predictorId using modelKey: " + modelKey);
		HttpClient client = HttpClients.createDefault();
		
		String mmsUrl = HelperTool.getEnv("mms_url",
				HelperTool.getComponentPropertyValue("mms_url")) + modelKey.replace("-", "_");
		logger.info("HelperTool::getModelDetails()::the model manager url is " + mmsUrl );

		HttpGet request = new HttpGet(mmsUrl);
		request.addHeader("Authorization", authorization);

		HttpResponse response = client.execute(request);
		logger.info("HelperTool::getModelDetails()::response from model manager service is " + response.getStatusLine().getStatusCode()
				+ " for model key: " + modelKey);
		
		StringBuilder modelDetails = new StringBuilder();
		
		if (response.getStatusLine().getStatusCode() == 200) {
			logger.info("HelerTool::getModelDetails()::Received OK response from model manager URL for predictor: " + modelKey);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				modelDetails.append(line);
			}
			rd.close();
		} else if (response.getStatusLine().getStatusCode() == 401) {
			logger.info("HelerTool::getModelDetails()::Received Bad response 401 from model manager. Throwing exception...");
			throw new CmlpDataSrcException(
					"OOPS. The model manager didn't authorise for details of the model with key as :" + modelKey + ". Please ask admin to provide access to get credentials.");
		} else {
			logger.info("HelerTool::getModelDetails()::Received Bad response" + response.getStatusLine().getStatusCode());
			logger.info("HelerTool::getModelDetails()::Received Bad response  (other than 401) from model manager. Throwing exception...");
			throw new CmlpDataSrcException("OOPS. Something went wrong with Model");
		}

		try {
			request.releaseConnection();
		} catch (Exception e) {
			logger.info("HelerTool::getModelDetails()::ignoring the exception during release connection of http request.");
		}
		return new JSONObject(modelDetails.toString());
	}
	
	public static JSONObject getPredictorDetails (String authorization, String predictorId) throws IOException, CmlpDataSrcException{
		
		logger.info("HelperTool::getPredictorDetails()::prepping request to predictor manager to get info on predictor: " + predictorId);
		HttpClient client = HttpClients.createDefault();
		
		String pmsUrl = HelperTool.getEnv("pms_url",
				HelperTool.getComponentPropertyValue("pms_url")) + predictorId.replace("-", "_");
		logger.info("HelperTool::getPredictorDetails()::the predictor manager url is " + pmsUrl );

		HttpGet request = new HttpGet(pmsUrl);
		request.addHeader("Authorization", authorization);

		HttpResponse response = client.execute(request);
		logger.info("HelperTool::getPredictorDetails()::response from predictor manager service is " + response.getStatusLine().getStatusCode()
				+ " for predictor key: " + predictorId);
		
		StringBuilder predictorDetails = new StringBuilder();
		
		if (response.getStatusLine().getStatusCode() == 200) {
			logger.info("HelerTool::getPredictorDetails()::Received OK response from predictor manager URL for predictor: " + predictorId);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				predictorDetails.append(line);
			}
			rd.close();
		} else if (response.getStatusLine().getStatusCode() == 401) {
			logger.info("HelerTool::getPredictorDetails()::Received Bad response 401 from predictor manager. Throwing exception...");
			throw new CmlpDataSrcException(
					"OOPS. The predcitor manager didn't authorise for details of the preictor with key as :" + predictorId + ". Please ask admin to provide access to get credentials.");
		} else {
			logger.info("HelerTool::getPredictorDetails()::Received Bad response" + response.getStatusLine().getStatusCode());
			logger.info("HelerTool::getPredictorDetails()::Received Bad response  (other than 401) from predictor manager. Throwing exception...");
			throw new CmlpDataSrcException("OOPS. Something went wrong with Predictor");
		}

		try {
			request.releaseConnection();
		} catch (Exception e) {
			logger.info("HelerTool::getPredictorDetails()::ignoring the exception during release connection of http request.");
		}
		return new JSONObject(predictorDetails.toString());
	}
	
	//TO DO:if this method required or not
	public static JSONObject getPredictorUrl (String authorization, String predictorId) throws IOException, CmlpDataSrcException{
		//String pmsUrl = HelperTool.getEnv("pms_url", HelperTool.getComponentPropertyValue("pms_url"));
		
		HttpClient client = HttpClients.createDefault();

		HttpGet request = new HttpGet(HelperTool.getEnv("pms_url",
				HelperTool.getComponentPropertyValue("pms_url")) + predictorId);
		request.addHeader("Authorization", authorization);

		HttpResponse response = client.execute(request);

		// to store all decrypted credentials
		Map<String, String> detailsMap = new HashMap<>();
		StringBuilder predictorDetails = new StringBuilder();

		// reading response
		logger.info("response from predictor manager service " + response.getStatusLine().getStatusCode()
				+ " for predictor key: " + predictorId);
		if (response.getStatusLine().getStatusCode() == 200) {
			logger.info("getPredictorUrl(), Received OK response from ConfigMgmt.");
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			// reading response and populating map
			String line = "";
			while ((line = rd.readLine()) != null) {
				predictorDetails.append(line);
			}
			rd.close();
		} else if (response.getStatusLine().getStatusCode() == 401) {
			logger.info("getPredictorUrl(), Received Bad response from ConfigMgmt. Throwing exception...");
			throw new CmlpDataSrcException(
					"OOPS. The database sharing is still not completed. Please ask admin to provide access to get credentials.");
		} else {
			logger.info("getPredictorUrl(), Received Bad response from ConfigMgmt. Throwing exception...");
			throw new CmlpDataSrcException("OOPS. Something went wrong with decryption");
		}

		try {
			request.releaseConnection();
		} catch (Exception e) {
			logger.info("ignoring the exception during release connection of http request.");
		}
		
		return new JSONObject(predictorDetails.toString());
	}
	
	public static String getkey(String user) throws IOException {
		logger.info("HelperTool::getKey()::intiating generating key");
		String namespace = HelperTool.getEnv("namespace", HelperTool.getComponentPropertyValue("namespace"));
		String name = namespace.replace(".", "_") + "_"
				+ ((user.indexOf("@") > 0) ? user.substring(0, user.indexOf("@")).replaceAll("-", "_")
						: user.replaceAll("-", "_"))
				+ "_"
				+ Instant.now().toEpochMilli();
		logger.info("HelperTool::getKey()::generated key is " + name);
		return name;
	}
	
	public static void validateRequest(String user, String codeCloudAuthorization, CatalogObject objCatalog) throws CmlpDataSrcException, Exception {
		
		if (!(user != null)) {
			throw new CmlpDataSrcException("Please provide a valid credentials", 400);
		}

		if (!(objCatalog.getModelKey() != null) || objCatalog.getModelKey().isEmpty()) {
			throw new CmlpDataSrcException("Please provide a model key", 400);
		}

		if (!(objCatalog.getModelVersion() != null) || objCatalog.getModelVersion().isEmpty()) {
			throw new CmlpDataSrcException("Please provide a model version", 400);
		}

		if (!(objCatalog.getPredictorId() != null) || objCatalog.getPredictorId().isEmpty()) {
			throw new CmlpDataSrcException("Please provide a predictor", 400);
		}

		if (!(codeCloudAuthorization != null) || codeCloudAuthorization.isEmpty()) {
			throw new CmlpDataSrcException("Please provide CodeCloudAuthorization header", 400);
		}

		if (!(objCatalog.getPublisherUrl() != null) || objCatalog.getPublisherUrl().isEmpty()) {
			throw new CmlpDataSrcException("Please provide a publisher URL", 400);
		}
		
		//MsgRouter validation
		if((objCatalog.getCategory() != null) || objCatalog.getCategory().isEmpty()) {
						
			if (objCatalog.getCategory().equalsIgnoreCase("MsgRouter") && (!(objCatalog.getSubscriberUrl() != null) || objCatalog.getSubscriberUrl().isEmpty())) {
				throw new CmlpDataSrcException("Please provide a subscriber URL", 400);
			}
			
			if(objCatalog.getCategory().equalsIgnoreCase("MsgRouter") && (objCatalog.getPollingInterval()%5!=0 )) {
				throw new CmlpDataSrcException("Invalid PollingInteravl. Please use 5 or multiples of 5 minutes as value", 400);
			}
		}
		
		//MsgRouter validation
//		 if(objCatalog.getMessageRouterDetails() != null) {
//			if(objCatalog.getMessageRouterDetails().getServerName() == null) {
//				throw new CmlpDataSrcException("Please provide serverName for Message Router.", 400);
//			}
//			
//			if(objCatalog.getMessageRouterDetails().getServerPort() == 0) {
//				throw new CmlpDataSrcException("Please provide serverPort for Message Router.", 400);
//			}
//			
//			if(objCatalog.getMessageRouterDetails().getTopicName() == null) {
//				throw new CmlpDataSrcException("Please provide topicName for Message Router.", 400);
//			}
//		}
	}
	
	
	public static String getEncrypt(String inStr) {
		
		try {
			
		} catch(Exception e) {
			inStr = null;
		}
		
		return inStr;
	}
	
	
	public static String getDecrypt(String inStr) {
		try {
			
		} catch(Exception e) {
			inStr = null;
		}
		
		return inStr;
	}
}
