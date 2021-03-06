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

package org.acumos.streamer.config;

import org.acumos.streamer.DataStreamerApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	/**
	 * @return new Docket
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo());
	}
	
	
	private ApiInfo apiInfo() {
		final String version = DataStreamerApplication.class.getPackage().getImplementationVersion();
		return new ApiInfoBuilder() //
				.title("Acumos DataStreamer Service REST API") //
				.description("Provides operational features like prediction and scoring" //
						+ " with the stream of data recieved. "//
						+ " All service endpoints require basic HTTP authentication.")
				.termsOfServiceUrl("Terms of service") //
				.contact(new Contact("Acumos Dev Team", //
						"http://acumos.readthedocs.io/en/latest/submodules/databroker/data-streamer/docs/", //
						"noreply@acumos.org")) //
				.license("Apache 2.0 License").licenseUrl("http://www.apache.org/licenses/LICENSE-2.0") // 
				.version(version == null ? "version not available" : version) //
				.build();
	}
}
