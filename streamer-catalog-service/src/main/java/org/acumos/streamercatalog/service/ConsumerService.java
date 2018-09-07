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

package org.acumos.streamercatalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
/*Used for the PACT Contract verification only*/
public class ConsumerService {

	private String url;
	private RestTemplate restTemplate;

	@Autowired
	public ConsumerService(@Value("${producer}") String url) {
		this.url = url;
		this.restTemplate = new RestTemplate();
	}

	public Object getWelcomeMsg() {
		return restTemplate.exchange(url+ "/streamer/catalog/service/hello?name=User",HttpMethod.GET, null, String.class).getBody();
	}

}