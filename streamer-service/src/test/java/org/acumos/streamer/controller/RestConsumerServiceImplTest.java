package org.acumos.streamer.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.acumos.streamer.service.ConsumerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.acumos.streamer.exception.DataStreamerException;


@RunWith(MockitoJUnitRunner.class)
public class RestConsumerServiceImplTest {
	
	@InjectMocks
	private RestConsumerServiceImpl restConsumerService;
	
	@Mock
	private ConsumerService aConsumerService;
	
	@Mock
	private HttpServletRequest request;

	@Test
	public void shouldGetSuccessfulOperateData() throws Exception {
	   String value="success";
		when(aConsumerService.operateData(anyString(),anyString(),anyString(),anyString(),anyString(),any(InputStream.class))).thenReturn(value);
		when(request.getRemoteUser()).thenReturn("Test");
		
		Response response = restConsumerService.operateData("authorization", "feedAuthorization", "catalogKey", "fileName", null);
		
		assertNotNull(response);
		
		verify(aConsumerService).operateData(anyString(),anyString(),anyString(),anyString(),anyString(),any(InputStream.class));
		verify(request,times(2)).getRemoteUser();
		
				
	}

	
	@Test
	public void shouldGetNoContentDataOperateData() throws Exception {
	   String value="failure";
		when(aConsumerService.operateData(anyString(),anyString(),anyString(),anyString(),anyString(),any(InputStream.class))).thenReturn(value);
		when(request.getRemoteUser()).thenReturn("Test");
		
		Response response = restConsumerService.operateData("authorization", "feedAuthorization", "catalogKey", "fileName", null);
		
		assertNotNull(response);
		assertEquals(204, response.getStatus());
		verify(aConsumerService).operateData(anyString(),anyString(),anyString(),anyString(),anyString(),any(InputStream.class));
		verify(request,times(2)).getRemoteUser();
		
				
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetExceptionOperateData() throws Exception {
		
		when(aConsumerService.operateData(anyString(),anyString(),anyString(),anyString(),anyString(),any(InputStream.class))).thenThrow(DataStreamerException.class);
		when(request.getRemoteUser()).thenReturn("Test");
		
		Response response = restConsumerService.operateData("authorization", "feedAuthorization", "catalogKey", "fileName", null);
		
		assertNotNull(response);
		assertEquals(500, response.getStatus());
		verify(aConsumerService).operateData(anyString(),anyString(),anyString(),anyString(),anyString(),any(InputStream.class));
		verify(request,times(2)).getRemoteUser();
		
				
	}
}
