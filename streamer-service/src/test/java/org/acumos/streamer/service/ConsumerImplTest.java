package org.acumos.streamer.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import org.acumos.streamer.exception.DataStreamerException;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerImplTest {
	
	
	private ConsumerImpl consumerImpl = new ConsumerImpl() ;
	
	@Test
	public void shouldUploadFile() throws Exception {
		MultipartBody attachedFiles = mock(MultipartBody.class);
		Attachment attachment=mock(Attachment.class);
		List<Attachment> attachments=new ArrayList<>();
		attachments.add(attachment);
		doNothing().when(attachment).transferTo(any(File.class));
		when(attachedFiles.getAllAttachments()).thenReturn(attachments);
		
		String status = consumerImpl.recieveFile("", "LOGIN:PASSWORD", "", attachedFiles);
		
		assertEquals("upload", status);
	}
	
	@Test(expected=DataStreamerException.class)
	public void shouldThrowExceptionWhenCadiAuthIsNull() throws Exception {
		consumerImpl.recieveFile(null, "LOGIN:PASSWORD", "fileName", null);
	}
	
	@Test(expected=DataStreamerException.class)
	public void shouldThrowExceptionWhenfeedAuthIsNull() throws Exception {
		consumerImpl.recieveFile("", null, "fileName", null);
	}
	
	@Test(expected=DataStreamerException.class)
	public void shouldThrowExceptionWhenfeedAuthIsInvalid() throws Exception {
		consumerImpl.recieveFile("", "Wrong", "fileName", null);
	}
	
	@Test
	public void shouldDeleteFile() throws Exception{
		
		String status = consumerImpl.deleteFile("", "LOGIN:PASSWORD", "");
		assertEquals("delete", status);
	}
}
