package org.acumos.streamercatalog.service;

import static org.mockito.Mockito.when;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.acumos.streamercatalog.connection.DbUtilities;
import org.acumos.streamercatalog.exception.DataStreamerException;
import org.acumos.streamercatalog.model.CatalogObject;
import org.acumos.streamercatalog.service.CatalogServiceImpl;

@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:application.properties")
public class CatalogServiceImplTest {
	
	@InjectMocks
	CatalogServiceImpl catalogServieImpl;
	
	@Value("${basic.authorization}")
	private String BASIC_AUTH;
	
	private CatalogObject objCatalog;
	
	@Mock
	private DbUtilities connection;
	
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
	public void saveCatalogShouldReturnException() throws IOException {
		
		
		try {
			connection.insertCatalogDetails("m09286@cmlp.att.com", BASIC_AUTH, objCatalog);
			String catalogkey =catalogServieImpl.saveCatalog("m09286@cmlp.att.com", BASIC_AUTH, objCatalog);
		}catch (DataStreamerException expected) {
			
		}
	}
	
	@Test
	public void updateCatalogShouldReturnException() throws IOException {
		
		
		try {
			connection.insertCatalogDetails("m09286@cmlp.att.com", BASIC_AUTH, objCatalog);
			String catalogkey =catalogServieImpl.updateCatalog("m09286@cmlp.att.com", BASIC_AUTH,"com-att-cmlp_m09286_1534982430385",objCatalog);
		}catch (DataStreamerException expected) {
			
		}
	}
	
	
	
	@Test
	public void shouldDeleteCatalog() throws IOException, DataStreamerException {		


		when(connection.softDeleteCatalog("m09286@cmlp.att.com", "com-att-cmlp_m09286_1534982430385")).thenReturn(true);
		when(catalogServieImpl.deleteCatalog("m09286@cmlp.att.com", BASIC_AUTH, "com-att-cmlp_m09286_1534982430385")).thenReturn(true);	
	}
}
