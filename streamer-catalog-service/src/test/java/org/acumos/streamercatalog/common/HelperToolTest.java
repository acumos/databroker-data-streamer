package org.acumos.streamercatalog.common;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.acumos.streamercatalog.common.DataStreamerCatalogUtil;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import org.acumos.streamercatalog.exception.DataStreamerException;
import org.acumos.streamercatalog.model.CatalogObject;

@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:application.properties")
public class HelperToolTest {
	
	@Value("${basic.authorization}")
	private String BASIC_AUTH;

	@InjectMocks
	private DataStreamerCatalogUtil helperTool;
	
	@Mock
	HttpServletRequest request;
	
	@Mock
	private CatalogObject objCatalog;
	
	
	@Test
	public void shouldGetComponentPropertyValue() throws IOException {
		
		
		String filepath_string = DataStreamerCatalogUtil.getComponentPropertyValue("mongo_port");
		assertEquals("32156", filepath_string);		
		
	}
	
	@Test
	public void shouldGetComponentPropertyValueWithDefaultValue() throws IOException {
		
		String filepath_string = DataStreamerCatalogUtil.getComponentPropertyValue("filepath_string","file");
		assertEquals("file", filepath_string);		
	}
	
	@Test
	public void shouldGetComponentPropertyValueWithActualNull() throws IOException {
		
		String filepath_string = DataStreamerCatalogUtil.getComponentPropertyValue("","filepath_string");
		assertEquals("filepath_string", filepath_string);		
	}

	@Test
	public void shouldReturnUser() {
				
		when (request.getRemoteUser()).thenReturn("test");
		String user =DataStreamerCatalogUtil.getRemoteUser(request);
		assertEquals("test", user);	
		
	}
	
	@Test
	public void shouldReturnPrincipal() {
		
		Principal principal = new MockPrincipal();
		when(request.getUserPrincipal()).thenReturn(principal);
		
		
		String userPrincipal =DataStreamerCatalogUtil.getRemoteUser(request);
		
		assertEquals("user",userPrincipal);
		
	}
	
	@Test
	public void shouldReturnNullUser() {
		
		
		when(request.getHeader(anyString())).thenReturn(BASIC_AUTH);
				
		String remoteUser = DataStreamerCatalogUtil.getRemoteUser(request);
		
		assertNull(remoteUser);
		
	}
	
	@Test
	public void shouldNotReturnAuthUser() {
		
		
		when(request.getHeader(anyString())).thenReturn("bTA5Mjg2QGNtbHAuYXR0LmNvbTpjbWxwcjBja2JhbmQh");
				
		String remoteUser = DataStreamerCatalogUtil.getRemoteUser(request);
		
		assertNotEquals("m09286@cmlp.att.com", remoteUser);
		
	}
	
	@Test
	public void shouldReturnKey() {
		String envKey =DataStreamerCatalogUtil.getEnv("kubernetes.namespace","test");
		
		assertEquals("test",envKey);
				
	}
	
	@Test
	public void shouldReturnKeyWithUser() throws IOException {
		String key = DataStreamerCatalogUtil.getkey("am375y");
		
		assertNotEquals("pocsub",key);
	}
	
	@Test
	public void shouldGetModelDetails() throws IOException, DataStreamerException {
		
		JSONObject modelDetails = DataStreamerCatalogUtil.getModelDetails(BASIC_AUTH, "com_att_cs_m09286_ST_CMLP_pmmltroydemo001");
		
		assertNotNull(modelDetails);
	}
	
	@Test
	public void shouldGetPredictorUrl () throws IOException, DataStreamerException {
		
		JSONObject predictorUrl = DataStreamerCatalogUtil.getPredictorUrl(BASIC_AUTH, "com_att_omni_m09286_20180731210641");
		
		assertNotNull(predictorUrl);
		
	}
	
	@Test
	public void shouldGetPredictorDetails () throws IOException, DataStreamerException {
		
		JSONObject predictorDetails = DataStreamerCatalogUtil.getPredictorDetails(BASIC_AUTH, "com-att-omni_m09286_ST_CMLPPLGRD_TestMsgRouter1");
		
		assertNotNull(predictorDetails);
		
	}
	
	public CatalogObject init() {
		
		objCatalog.setCatalogKey("");
		objCatalog.setModelKey("com-att-omni_m09286_ST_CMLPPLGRD_TestMsgRouter1");
		objCatalog.setModelVersion("16c3f5f198e1048a672a63855800216c35971324");
		objCatalog.setPredictorId("com_att_omni_m09286_20180731210641");
		objCatalog.setPredictorUrl("http://cmlp-portal.prod.sci.att.com/com-att-omni/com_att_omni_m09286_20180731210641/v2/syncPredictions");
		objCatalog.setPublisherUserName("m09286@cmlp.att.com");
		objCatalog.setPublisherPassword("cmlpr0ckband");
		objCatalog.setPublisherUrl("http://olsd005.wnsnet.attws.com:3904/events/com.att.cmlp.27174-MsgRouterTest-v2");
		objCatalog.setSubscriberPassword("cmlpr0ckband");
		objCatalog.setSubscriberUsername("m09286@cmlp.att.com");
		objCatalog.setCategory("MsgRouter");
		objCatalog.setDescription("testing msgrouter");
		objCatalog.setSubscriberUrl("http://olsd005.wnsnet.attws.com:3904/events/com.att.cmlp.27174-MsgRouterTest-v2/CG1/C1");
		objCatalog.setPollingInterval(5);
		objCatalog.setStreamerName("TestMsgRouter");
		
	return objCatalog;
		
	}
	
	@Test
	public void shouldValidateModelKeyRequest () throws DataStreamerException, Exception {
		
		CatalogObject catalogObj = init();
		
		catalogObj.setModelKey("");
		
		try {
		DataStreamerCatalogUtil.validateRequest("m09286@cmlp.att.com",catalogObj);
		}
		
		catch (DataStreamerException expected) {
			
		}
		
	}
	
	@Test
	public void shouldValidateuserRequest() throws DataStreamerException, Exception {
		
		CatalogObject catalogObj = init();
	
	try {
	DataStreamerCatalogUtil.validateRequest("",catalogObj);
	}
	
	catch (DataStreamerException expected) {
		
	}
	
}
	
	@Test
	public void shouldValidateModelVersionRequest() throws DataStreamerException, Exception {
		
		CatalogObject catalogObj = init();
		catalogObj.setModelVersion("");
	
	try {
	DataStreamerCatalogUtil.validateRequest("m09286@cmlp.att.com",catalogObj);
	}
	
	catch (DataStreamerException expected) {
		
	}
	
}
	
	@Test
	public void shouldValidatePredictorIdRequest() throws DataStreamerException, Exception {
		
		CatalogObject catalogObj = init();
		catalogObj.setPredictorId("");
	
	try {
	DataStreamerCatalogUtil.validateRequest("m09286@cmlp.att.com",catalogObj);
	}
	
	catch (DataStreamerException expected) {
		
	}
	
}
	
	@Test
	public void shouldValidatePublisherUrlRequest() throws DataStreamerException, Exception {
		
	CatalogObject catalogObj = init();

		catalogObj.setPublisherUrl("");
	
	try {
	DataStreamerCatalogUtil.validateRequest("m09286@cmlp.att.com",catalogObj);
	}
	
	catch (DataStreamerException expected) {
		
	}
	
}
	
	@Test
	public void shouldValidateCodeCLoudValidationRequest() throws DataStreamerException, Exception {
		
		CatalogObject catalogObj = init();
	
	try {
	DataStreamerCatalogUtil.validateRequest("m09286@cmlp.att.com",catalogObj);
	}
	
	catch (DataStreamerException expected) {
		
	}
	
}
	
	@Test
	public void shouldValidateSubscriberUrlRequest() throws DataStreamerException, Exception {
		
	CatalogObject catalogObj = init();	
	objCatalog.setSubscriberUrl("");
	
	try {
	DataStreamerCatalogUtil.validateRequest("m09286@cmlp.att.com",catalogObj);
	}
	
	catch (DataStreamerException expected) {
		
	}
	
}
	
	@Test
	public void shouldValidatePollingIntervalRequest() throws DataStreamerException, Exception {
		
		CatalogObject catalogObj = init();		
		catalogObj.setPollingInterval(7);
	
	try {
	DataStreamerCatalogUtil.validateRequest("m09286@cmlp.att.com",catalogObj);
	}
	
	catch (DataStreamerException expected) {
		
	}
	
}
	
	static class MockPrincipal implements Principal{

		@Override
		public String getName() {
			
			return "user";
		}
		
	}

}
