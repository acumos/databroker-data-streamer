package org.acumos.streamer.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.acumos.streamer.exception.DataStreamerException;

@PropertySource("classpath:application.properties")
@RunWith(MockitoJUnitRunner.class)
public class MessageRouterTaskTest {
	
	MessageRouterTask msgRouterTask = new MessageRouterTask();	
	
	@Value("${basic.authorization}")
	private String  authorization;
	 		

	@Test
	public void shouldgetMessageSubscriber() throws IOException, DataStreamerException, InterruptedException, ExecutionException, TimeoutException, ParseException {
		
		String[] Array = msgRouterTask.getMessageSubscriber(authorization, "http://olsd005.wnsnet.attws.com:3904/events/com.att.cmlp.27174-MsgRouterTest-v2/CG1/C1", 0);
		
		assertNotNull(Array);
	}

	@Test
	public void shouldReturnPredictorDetails() {
	
		JSONObject predictorDetails = msgRouterTask.getCatalogDetails("com-att-cmlp_m09286_1534982430385");
		assertNull(predictorDetails);
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldThrowExceptionExecutePredictorScoring() throws Exception {
	
		msgRouterTask.executePredictorScoring(authorization, null, null, null);
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=DataStreamerException.class)
	public void shouldReturnExecutePredictorScoring() throws Exception {
		org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
		jsonObject.putIfAbsent("predictorUrl", "http://cmlp-portal.prod.sci.att.com/com-att-omni/com_att_omni_m09286_20180731210641/v2/syncPredictions");
		
		org.json.JSONObject predictDetails = new org.json.JSONObject();
		predictDetails.put("codeCloudAuthorization", "Basic bTA5Mjg2QGNtbHAuYXR0LmNvbTpBTmV3RGF5VG9kYXkh");
		
		String predictorScoring = msgRouterTask.executePredictorScoring(authorization, jsonObject, new String[] {}, predictDetails);
		
		System.out.println(predictorScoring);
	}

}
