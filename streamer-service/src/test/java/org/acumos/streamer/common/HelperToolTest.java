package org.acumos.streamer.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.acumos.streamer.exception.DataStreamerException;


@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:application.properties")
public class HelperToolTest {
	
	 @Value("${basic.authorization}")
	 private String BASIC_AUTH;

	@InjectMocks
	private DataStreamerUtil helperTool;
	
	@Mock
	HttpServletRequest request;
	
	@Test
	public void shouldGetComponentPropertyValue() throws IOException {
		
		
		String filepath_string = DataStreamerUtil.getComponentPropertyValue("filepath_string");
		assertEquals("publish", filepath_string);		
		
	}
	
	@Test
	public void shouldGetComponentPropertyValueWithDefaultValue() throws IOException {
		
		String filepath_string = DataStreamerUtil.getComponentPropertyValue("filepath_string","file");
		assertEquals("publish", filepath_string);		
	}
	
	@Test
	public void shouldGetComponentPropertyValueWithActualNull() throws IOException {
		
		String filepath_string = DataStreamerUtil.getComponentPropertyValue("","filepath_string");
		assertEquals("filepath_string", filepath_string);		
	}

	@Test
	public void shouldReturnUser() {
				
		when (request.getRemoteUser()).thenReturn("test");
		String user =DataStreamerUtil.getRemoteUser(request);
		assertEquals("test", user);	
		
	}
	
	@Test
	public void shouldReturnPrincipal() {
		
		Principal principal = new MockPrincipal();
		when(request.getUserPrincipal()).thenReturn(principal);
		
		
		String userPrincipal =DataStreamerUtil.getRemoteUser(request);
		
		assertEquals("user",userPrincipal);
		
	}
	
	@Test
	public void shouldReturnNullUser() {
		
		
		when(request.getHeader(anyString())).thenReturn(BASIC_AUTH);
				
		String remoteUser = DataStreamerUtil.getRemoteUser(request);
		
		assertNull(remoteUser);
		
	}
	
	@Test
	public void shouldNotReturnAuthUser() {
		
		
		when(request.getHeader(anyString())).thenReturn("bTA5Mjg2QGNtbHAuYXR0LmNvbTpjbWxwcjBja2JhbmQh");
				
		String remoteUser = DataStreamerUtil.getRemoteUser(request);
		
		assertNotEquals("m09286@cmlp.att.com", remoteUser);
		
	}
	
	@Test
	public void shouldReturnKey() {
		String envKey =DataStreamerUtil.getEnv("kubernetes.namespace","test");
		
		assertEquals("test",envKey);
				
	}
	
	@Test
	public void shouldReturnMsgFromSubscriber() throws Exception {
		
		String[] messages = DataStreamerUtil.getMsgsFromSubscriber(BASIC_AUTH, "http://olsd005.wnsnet.attws.com:3904/events/com.att.cmlp.27174-MsgRouterTest-v2/CG1/C1");
		
		assertNotNull(messages);
	}
	
	@Test
	public void shouldReturnCatalogsArray() throws Exception {
		
		JSONArray  catalogs = DataStreamerUtil.getCatalogsByCategory(BASIC_AUTH, "MsgRouter");
		
		assertNotNull(catalogs);
	}
	
	@Test(expected=DataStreamerException.class)
	public void shouldReturnPredictorDetails() throws Exception {
		
		JSONObject  catalogDetails = DataStreamerUtil.getCatalogDetails(BASIC_AUTH, "com-att-cmlp_m09286_1534982430385");
		
	}

	static class MockPrincipal implements Principal{

		@Override
		public String getName() {
			
			return "user";
		}
		
	}
}
