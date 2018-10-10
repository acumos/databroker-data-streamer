package org.acumos.streamercatalog.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.acumos.streamercatalog.exception.DataStreamerException;
import org.acumos.streamercatalog.model.CatalogObject;
import org.acumos.streamercatalog.model.RelativeModel;
import org.acumos.streamercatalog.service.CatalogService;
import org.acumos.streamercatalog.controller.RestCatalogServiceImpl;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("rawtypes")
@PropertySource("classpath:application.properties")
public class RestCastalogServiceImplTest {
	
	@Value("${basic.authorization}")
	private String BASIC_AUTH;
	
	@Value("${codecloud.authorization}")
	private String CODECLOUD_AUTH;
	
	@InjectMocks
	RestCatalogServiceImpl restCatalogServiceImpl;
	
	
	private CatalogObject objCatalog ;
	@Mock
	private CatalogService aCatalogService;
	
	@Mock
	private HttpServletRequest request;
	
	@Before
	public void init() {
		objCatalog = new CatalogObject() ;
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
		
	}
	
	@Test
	public void shouldReturnCreatedStatus() throws IOException, DataStreamerException {
		when(request.getRemoteUser()).thenReturn("m09286@cmlp.att.com");
		StringBuffer sb = new StringBuffer("http://localhost:8020/v1/streamers");
		when(request.getRequestURL()).thenReturn(sb);
		when(aCatalogService.saveCatalog("m09286@cmlp.att.com", BASIC_AUTH, objCatalog)).thenReturn("newCatalogKey");
		ResponseEntity response = restCatalogServiceImpl.saveCatalog(BASIC_AUTH, objCatalog);
		
		assertEquals(201,response.getStatusCodeValue());
		
		
	}
	
	@Test
	public void shouldReturnUpdatedStatus() throws IOException, DataStreamerException {
		
		when(request.getRemoteUser()).thenReturn("m09286@cmlp.att.com");
		StringBuffer sb = new StringBuffer("http://localhost:8020/v1/streamers");
		when(request.getRequestURL()).thenReturn(sb);
		when(aCatalogService.updateCatalog("m09286@cmlp.att.com", BASIC_AUTH, "com-att-cmlp_m09286_1534982430385", objCatalog)).thenReturn("catalogKey");
		ResponseEntity response = restCatalogServiceImpl.updateCatalog(BASIC_AUTH, "com-att-cmlp_m09286_1534982430385",objCatalog);
		assertEquals(200,response.getStatusCodeValue());
		
	}
	
	@Test
	public void shouldReturnGetCatalogsStatus() throws IOException, DataStreamerException {
		
		when(request.getRemoteUser()).thenReturn("m09286@cmlp.att.com");
		StringBuffer sb = new StringBuffer("http://localhost:8020/v1/streamers");
		when(request.getRequestURL()).thenReturn(sb);
		ArrayList<String> catalogs = new ArrayList<String>();
		when(aCatalogService.getCatalogs("m09286@cmlp.att.com", BASIC_AUTH, "MsgRouter", "text")).thenReturn(catalogs);
		ResponseEntity response = restCatalogServiceImpl.getCatalogs(BASIC_AUTH, "com-att-cmlp_m09286_1534982430385", "concise");
		assertEquals(200,response.getStatusCodeValue());
		
	}
	
	@Test
	public void shouldReturnGetCatalogStatus() throws IOException, DataStreamerException {
		
		when(request.getRemoteUser()).thenReturn("m09286@cmlp.att.com");
		StringBuffer sb = new StringBuffer("http://localhost:8020/v1/streamers");
		when(request.getRequestURL()).thenReturn(sb);
		RelativeModel aRelativeModel = new RelativeModel();
		when(aCatalogService.getPredictorAssociation("m09286@cmlp.att.com", BASIC_AUTH, "com-att-cmlp_m09286_1534982430385")).thenReturn(aRelativeModel);
		ResponseEntity response = restCatalogServiceImpl.getCatalog(BASIC_AUTH, "com-att-cmlp_m09286_1534982430385", "concise");
		assertEquals(200,response.getStatusCodeValue());
		
	}
	
	@Test
	public void shouldReturnDeleteCatalogStatus() throws IOException, DataStreamerException {
		
		when(request.getRemoteUser()).thenReturn("m09286@cmlp.att.com");
		StringBuffer sb = new StringBuffer("http://localhost:8020/v1/streamers");
		when(request.getRequestURL()).thenReturn(sb);
		when(aCatalogService.deleteCatalog("m09286@cmlp.att.com", BASIC_AUTH, "com-att-cmlp_m09286_1534982430385")).thenReturn(true);
		ResponseEntity response = restCatalogServiceImpl.deleteCatalog(BASIC_AUTH, "com-att-cmlp_m09286_1534982430385");
		assertEquals(204,response.getStatusCodeValue());
		
	}

}
