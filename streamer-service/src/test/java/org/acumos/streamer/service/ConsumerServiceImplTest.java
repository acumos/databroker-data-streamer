package org.acumos.streamer.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import org.acumos.streamer.exception.DataStreamerException;


@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:application.properties")
public class ConsumerServiceImplTest {
	
	 @Value("${basic.authorization}")
	 private String BASIC_AUTH;

	@InjectMocks
	private ConsumerServiceImpl consumerServiceImpl;
	
	@Mock
	private PublisherService aPublisherService;
	
	@Test
	public void shouldRecieveDataNull() {
		
		String recievedData = consumerServiceImpl.receiveData("", BASIC_AUTH, "", null);
		assertNull(recievedData);
	}
	
	@Test
	public void shouldDeleteDataNull() {
		
		String deletedData = consumerServiceImpl.deleteData("", BASIC_AUTH, "", null);
		assertNull(deletedData);
	}
	
	@Test
	public void shouldOperateDataNotNull() throws DataStreamerException {
		
		InputStream attachment=mock(InputStream.class);
		List<InputStream> attachments=new ArrayList<>();
		attachments.add(attachment);
		
		String operatedData = consumerServiceImpl.operateData("", BASIC_AUTH, "","publish","com-att-omni_m09286_ST_CMLPPLGRD_TestMsgRouter1", attachment);
		assertEquals("success",operatedData);
	}


}
