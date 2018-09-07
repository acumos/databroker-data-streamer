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

package org.acumos.streamer;

import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.acumos.streamer.service.MessageRouterTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SchedulerJob {

	@Autowired
	private TaskExecutor taskExecutor;
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@PostConstruct
	@Scheduled(fixedDelay=600000)
	public void runFixedSchdule() {
		log.info("Schdule job invoked");
		taskExecutor.execute(new MessageRouterTask());
		log.info("Schdule job end");
	}
}
