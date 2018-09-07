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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acumos.streamer.common.HelperTool;
import org.acumos.streamer.exception.CmlpDataSrcException;

@Service
public class MessageRouterTask implements  Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private final String  authorization = "Basic bTA5Mjg2QGNtbHAuYXR0LmNvbTpjbWxwcjBja2JhbmQ=";
	private String catalogKey;
	
	public void run() {
		
     	log.info("MessageRouterTask::run: Begin");
	        
		JSONParser parser = new JSONParser();
	     
	    String results = null;
	       
		JSONArray catalogArray;
		
			try {
				catalogArray = HelperTool.getCatalogsByCategory(authorization, "MsgRouter");
				
		        for (Object str : catalogArray.toArray()) {
		        	try {
			        	org.json.simple.JSONObject streamerCatalogObj = (org.json.simple.JSONObject) parser.parse(str.toString());
		
				        log.info("Loading catalog "+ streamerCatalogObj.get("catalogKey"));
		
				        catalogKey =(String)streamerCatalogObj.get("catalogKey");
			
						//Step 2: Launch the Listener in new thread to retrieve data from the subscriber with pollingInterval.
						String subscriberUrl = (String) streamerCatalogObj.get("subscriberUrl");
						String publisherUrl = (String) streamerCatalogObj.get("publisherUrl");
						String[] responseDataFromSubscriberUrl = null;
						int pollingInterval = 0;
						try 
						{
							if(streamerCatalogObj.containsKey("pollingInterval")) {
							 pollingInterval = Integer.valueOf(streamerCatalogObj.get("pollingInterval").toString());
							}
							responseDataFromSubscriberUrl = getMessageSubscriber(authorization,subscriberUrl,pollingInterval);
						}
						catch(Exception e) {
							log.error("MessageRouterTask::Run: Failed to launch listener to retreive data from subscriberUrl: " + subscriberUrl);
							throw new CmlpDataSrcException("Thread failed to execute data retreival from subscriber: " + subscriberUrl);
						}
			
						//Step 3: sends the data to predictor for scoring.
						org.json.JSONObject predictDetails = getPredictDetails(catalogKey);
						if(responseDataFromSubscriberUrl!=null) {
							results = executePredictorScoring(authorization, streamerCatalogObj, responseDataFromSubscriberUrl,predictDetails);
						}
						publishResults(results,authorization, publisherUrl);
			        	}catch(ParseException  | CmlpDataSrcException |IOException  e) {
			        		log.error("MessageRouterTask::executePredictorScoring(),publishResults()::failed in either execute predictor or publishing results",e.getMessage());	
			        	}catch(Exception  e) {
			        		log.error("MessageRouterTask::executePredictorScoring(),publishResults()::failed in either execute predictor or publishing results",e.getMessage());	
			        	}
		        }
		        
			}catch(Exception ex) {
				log.error("MessageRouterTask::run()::exception in MessageRouter Task. The thread continues:",ex.getMessage());
			}
	}

	protected String[] getMessageSubscriber(String Authorization, String subscriberUrl, Integer pollingInterval) 
			throws IOException, CmlpDataSrcException, InterruptedException, ExecutionException, TimeoutException, ParseException {
        	log.info("MessageRouterTask::getMessageSubscriber: Begin");
       
			 List<String> responseData = new ArrayList<String>();
			 int counter=0;
			 while(true) {
				    log.info("fetching data from subscriber: " + subscriberUrl);
				    String[] responseDataLocal = HelperTool.getMsgsFromSubscriber(Authorization, subscriberUrl);
					//if no response and counter is 0 then wait for polling interval
				    if(responseDataLocal.length==0 && counter==0) {
				    	TimeUnit.MINUTES.sleep(pollingInterval);
						counter++;
						continue;
					}
				    
					for (String msg: responseDataLocal ) {
						responseData.add(msg);
						log.info("the msg  from for each loop: " + msg);
					}
					
					break;
					
			 }
			
			if(responseData.isEmpty()) {
				log.info("There is no Data to retrieve from the subscriber: " + subscriberUrl);
			}
				
			String[] array = responseData.toArray(new String[responseData.size()]);	
			log.info("The data from subscriber url is:" +array);
			return array;
		
	}
	protected String executePredictorScoring(String authorization,  
			org.json.simple.JSONObject streamerCatalogObj, String[] responseData,org.json.JSONObject predictDetails) throws CmlpDataSrcException, IOException {
		log.info("MessageRouterTask::executePredictorScoring():: Begin");
		String predictorUrl = streamerCatalogObj.get("predictorUrl").toString();
		//String predictorUrl = "http://localhost:9091/com-att-cmlp/com_att_cmlp_m09286_20180817162844/v2/syncPredictions";
		URL aUrl = null;
		try {
			log.info("Intiating URL connection to predictor");
			aUrl = new URL(predictorUrl);
		} catch (MalformedURLException e) {
			log.error("MessageRouterTask::executePredictorScoring()::Encountered error while prearing URL for predictor",e);
			throw new CmlpDataSrcException("Encountered error while prearing URL for predictor");
		}
		HttpURLConnection aHttpURLConnection = null;
		try {
			log.info("MessageRouterTask::operateData()::opening HTTP connection");
			aHttpURLConnection = (HttpURLConnection) aUrl.openConnection();
		} catch (IOException e) {
			log.info("MessageRouterTask::executePredictorScoring()::Predictor HTTP connection opening failed",e);
			throw new CmlpDataSrcException("Predictor HTTP connection opening failed");
		}
		
		aHttpURLConnection.setInstanceFollowRedirects(false);
		aHttpURLConnection.setRequestProperty("Content-Type", "text/plain");
		try {
			log.info("MessageRouterTask::executePredictorScoring()::setting up method type for HTTP call");
			aHttpURLConnection.setRequestMethod("POST");
		} catch (ProtocolException e1) {
			log.info("MessageRouterTask::executePredictorScoring()::failed to intiate method type for HTTP call",e1);
			throw new CmlpDataSrcException("Failed to intiate method type for HTTP call to predictor");
		}
		aHttpURLConnection.setRequestProperty("Authorization", authorization);
		
		String codeCloudAuthorization = predictDetails.getString("codeCloudAuthorization");
	    aHttpURLConnection.setRequestProperty("CodeCloud-Authorization", codeCloudAuthorization);
	    aHttpURLConnection.setRequestProperty("ATT-ModelKey", (String) streamerCatalogObj.get("modelKey"));
	    aHttpURLConnection.setRequestProperty("ATT-ModelVersion", (String) streamerCatalogObj.get("modelVersion"));

		log.info("MessageRouterTask::executePredictorScoring()::sending predictor POST HTTP request");
		aHttpURLConnection.setDoOutput(true);
		OutputStream wr = null;
		try {
			wr =aHttpURLConnection.getOutputStream();
		} catch (IOException e) {
			log.info("MessageRouterTask::executePredictorScoring()::"
					+ "Encountered error while intaiating o/p stream for HTTP connection.",e);
			throw new CmlpDataSrcException("Encountered error while intaiating o/p stream for HTTP connection");
		}

		StringBuffer strBuffer = new StringBuffer();
		for(String response  :responseData ) {
			
			strBuffer.append(response);
			strBuffer.append("\n");
			
		}
		try {
			wr.write(strBuffer.toString().getBytes());
			wr.flush();
			wr.close();
		} catch (IOException e) {
			log.info("MessageRouterTask::executePredictorScoring()::"
					+ "encountered error while writing incoming data to HTTP connection for scoring",e);
			throw new CmlpDataSrcException("Encountered error while writing incoming data to HTTP connection for scoring");
		}

		int responseCode = aHttpURLConnection.getResponseCode();

		log.info("MessageRouterTask::executePredictorScoring()::the response from predictor with URL as: " + predictorUrl
				+ " returned a rsponse as " + responseCode);
		if ( responseCode > 299 ) {
			String errorMessage = "";
			try {
				errorMessage = aHttpURLConnection.getResponseMessage();
			} catch (IOException e) {
				log.error("MessageRouterTask::executePredictorScoring()::Error getting the error response message", e);
			}
			log.error("MessageRouterTask::executePredictorScoring()::There was error during scoring. he preditor retuned a code: "
					+ responseCode + ". The assocaed reuned message is " + errorMessage);
			throw new CmlpDataSrcException("There was error during scoring. the preditor retuned a code: " + responseCode
					+ ". The assocaed reuned message is " + errorMessage);
		}

		BufferedReader in = null;
		String inputLine;
		StringBuffer responseFromScoring = new StringBuffer();
		try {
			in = new BufferedReader(new InputStreamReader(aHttpURLConnection.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				responseFromScoring.append(inputLine);
				responseFromScoring.append("\n");
				System.out.println(responseFromScoring);
			}
			in.close();
		} catch (IOException e) {
			log.error("MessageRouterTask::executePredictorScoring()::Error reading the response from predicor scoring", e);
			throw new CmlpDataSrcException("Error reading the response from predicor scoring");
		}

		// https://feeds-uat-drtr.web.att.com/publish/4862
		log.info("MessageRouterTask::executePredictorScoring()::response message size is" + responseFromScoring.length());
		
		return responseFromScoring.toString();
		
	}
	
	protected void publishResults(String results,String authorization, String publisherUrl) throws CmlpDataSrcException, IOException {
				
		URL aUrl = null;
		try {
			log.info("Intiating URL connection to Publisher");
			aUrl = new URL(publisherUrl);
		} catch (MalformedURLException e) {
			log.error("MessageRouterTask::publishResults()::Encountered error while prearing URL for Publisher",e);
		}
		HttpURLConnection aHttpURLConnection = null;
		try {
			log.info("MessageRouterTask::publishResults()::opening HTTP connection");
			aHttpURLConnection = (HttpURLConnection) aUrl.openConnection();
		} catch (IOException e) {
			log.info("MessageRouterTask::publishResults()::Publisher HTTP connection opening failed",e);
		}
		
		aHttpURLConnection.setInstanceFollowRedirects(false);
		aHttpURLConnection.setRequestProperty("Content-Type", "text/plain");
		try {
			log.info("MessageRouterTask::publishResults()::setting up method type for HTTP call");
			aHttpURLConnection.setRequestMethod("POST");
		} catch (ProtocolException e1) {
			log.info("MessageRouterTask::publishResults()::failed to intiate method type for HTTP call",e1);
		}
		aHttpURLConnection.setRequestProperty("Authorization", authorization);
		
		log.info("MessageRouterTask::publishResults()::sending predictor POST HTTP request");
		aHttpURLConnection.setDoOutput(true);
				
		OutputStream outputStream = aHttpURLConnection.getOutputStream();
		outputStream.write(results.getBytes());
		outputStream.flush();
		outputStream.close();
		
		int responseCode = 300;
		try {
			responseCode = aHttpURLConnection.getResponseCode();
		} catch (IOException e1) {
			log.error("MessageRouterTask::publishResults()::Error getting the response code", e1.getMessage());
		}

		log.info("MessageRouterTask::publishResults()::the response from publisher with URL as: " + publisherUrl
				+ " returned a rsponse as " + responseCode);
		if ( responseCode > 299 ) {
			String errorMessage = "";
			try {
				errorMessage = aHttpURLConnection.getResponseMessage();
			} catch (IOException e) {
				log.error("MessageRouterTask::publishResults()::Error getting the error response message", e.getMessage());
			}
			log.error("MessageRouterTask::publishResults()::There was error during scoring. the publisher retuned a code: "
					+ responseCode + ". The associated returned message is " + errorMessage);
		}
		
		log.info("In MessageRouterTask:publishResults() the prediction results are:"+results);
		
	}
		
	
  protected org.json.JSONObject getPredictDetails(String catalogKey) {
	  org.json.JSONObject predictorDetails = null;
	  try {
			log.info("MessageRouterTask::getPredictDetails()::fetching details of predictor");
			predictorDetails = HelperTool.getPredictorDetails(authorization,catalogKey);
			
			
		
		} catch (IOException | CmlpDataSrcException e) {
			log.info("MessageRouterTask::getPredictDetails()::Encountered error while fetchng details of predictor :"
					+ e.getMessage());
		}
	  
	  return predictorDetails;

  }	

}
