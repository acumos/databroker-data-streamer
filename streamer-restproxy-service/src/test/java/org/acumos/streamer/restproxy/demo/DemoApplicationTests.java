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

package org.acumos.streamer.restproxy.demo;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.lang.invoke.MethodHandles;

import org.acumos.streamer.restproxy.controller.PublisherSubscriberController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(MockitoJUnitRunner.class)
public class DemoApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private Environment env;

	@InjectMocks
	private PublisherSubscriberController publisherSubscriberController;

	@Before
	public void createClient() throws Exception {
		mockMvc = standaloneSetup(publisherSubscriberController).build();
	}
	
	@Test
	public void sendMessageToKafkaTopic() {
		publisherSubscriberController = mock(PublisherSubscriberController.class);
		ResponseEntity<?> list = publisherSubscriberController.sendMessageToKafkaTopic("authorization", "topic", "msgs");
		Assert.assertNull(list);
	}
	
	@Test
	public void recieveMessageFromKafkaTopic() {
		publisherSubscriberController = mock(PublisherSubscriberController.class);
		ResponseEntity<?> list = publisherSubscriberController.recieveMessageFromKafkaTopic("authorization", "topic", "groupName", "groupId");
		Assert.assertNull(list);
	}

}
