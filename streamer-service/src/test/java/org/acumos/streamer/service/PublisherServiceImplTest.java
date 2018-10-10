package org.acumos.streamer.service;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:application.properties")
public class PublisherServiceImplTest {
	
	PublisherServiceImpl publisherServiceImpl = new PublisherServiceImpl();
	
	 @Value("${basic.authorization}")
	 private String authorization;
	
	@Test
	public void shouldReturnPublishId() {
		
		
		String publishId = publisherServiceImpl.publish("com-att-cmlp_m09286_1534982430385", authorization, 
						"", "http://olsd005.wnsnet.attws.com:3904/events/com.att.cmlp.27174-MsgRouterTest-v2", null);
		
		
		assertNull(publishId);
		
	}
}
