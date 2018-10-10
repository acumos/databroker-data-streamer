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

package org.acumos.streamercatalog.test.controller;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.lang.invoke.MethodHandles;

import org.acumos.streamercatalog.controller.RestCatalogController;
import org.acumos.streamercatalog.model.CatalogObject;
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
public class RestCatalogControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private Environment env;

	@InjectMocks
	private RestCatalogController restCatalogController;

	@Before
	public void createClient() throws Exception {
		mockMvc = standaloneSetup(restCatalogController).build();
	}
	
	@Test
	public void deleteCatalog() {
		restCatalogController = mock(RestCatalogController.class);
		ResponseEntity<?> list = restCatalogController.deleteCatalog("authorization", "catalogKey");
		Assert.assertNull(list);
	}
	
	@Test
	public void getCatalog() {
		restCatalogController = mock(RestCatalogController.class);
		ResponseEntity<?> list = restCatalogController.getCatalog("authorization", "catalogKey", "mode");
		Assert.assertNull(list);
	}
	
	@Test
	public void getCatalogs() {
		restCatalogController = mock(RestCatalogController.class);
		ResponseEntity<?> list = restCatalogController.getCatalogs("authorization", "catalogKey", "mode");
		Assert.assertNull(list);
	}
	
	@Test
	public void saveCatalog() {
		restCatalogController = mock(RestCatalogController.class);
		ResponseEntity<?> list = restCatalogController.saveCatalog("authorization", new CatalogObject());
		Assert.assertNull(list);
	}
	
	@Test
	public void updateCatalog() {
		restCatalogController = mock(RestCatalogController.class);
		ResponseEntity<?> list = restCatalogController.updateCatalog("authorization", "catalogKey", new CatalogObject());
		Assert.assertNull(list);
	}
	
}
