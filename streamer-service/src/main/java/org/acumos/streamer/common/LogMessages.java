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

package org.acumos.streamer.common;

import com.att.eelf.i18n.EELFResolvableErrorEnum;
import com.att.eelf.i18n.EELFResourceManager;

public enum LogMessages implements EELFResolvableErrorEnum {

	RESTSERVICE_HELLO, RESTSERVICE_HELLO_NAME, SPRINSERVICE_HELLO, SPRINSERVICE_HELLO_NAME, SPRINSERVICE_HELLO_MESSAGE, SPRINSERVICE_HELLO_MESSAGE_NAME;

	private static final String LOGMESSAGES = "logmessages";

	static {

		EELFResourceManager.loadMessageBundle(LOGMESSAGES);

	}

}