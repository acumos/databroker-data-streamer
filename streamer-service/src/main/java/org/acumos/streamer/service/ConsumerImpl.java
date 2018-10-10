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

package org.acumos.streamer.service;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

import javax.ws.rs.core.Response.Status;

import org.acumos.streamer.exception.DataStreamerException;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;;

@Service
public class ConsumerImpl {
	
	private static final String UPLOAD = "upload";

	private static final String FILE_SEPARATOR = "file.separator";

	private static final String USER_DIR = "user.dir";

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private String OutputDirectory = "received";

	private String auth = "LOGIN" + ":" + "PASSWORD";

	public String recieveFile(String cadiAuth, String feedAuth, String fileName, MultipartBody attchedFiles) throws DataStreamerException {
		return common(cadiAuth, feedAuth, fileName, attchedFiles, false);
	}
	
	protected String deleteFile(String cadiAuth, String feedAuth, String fileName) throws DataStreamerException{
		return common(cadiAuth, feedAuth, fileName, null, true);
	}

	protected String common(String cadiAuth, String feedAuth, String fileName, MultipartBody attchedFiles, boolean isdelete) throws DataStreamerException{
		if (cadiAuth == null || feedAuth == null) {
			log.info("Rejecting request with no Authorization header.");
			throw new DataStreamerException("Unauthorised. Please send proper auth.", Status.FORBIDDEN.getStatusCode());
		}
		if (!auth.equals(feedAuth)) {
			log.info("Rejecting request with incorrect Authorization header.");
			throw new DataStreamerException("Unauthorised. Please send proper auth.", Status.UNAUTHORIZED.getStatusCode());
		}
		String finalname = System.getProperty(USER_DIR) + System.getProperty(FILE_SEPARATOR) + OutputDirectory + System.getProperty(FILE_SEPARATOR) + fileName;
		String tmpname = System.getProperty(USER_DIR) + System.getProperty(FILE_SEPARATOR) + OutputDirectory + System.getProperty(FILE_SEPARATOR) + "." + fileName;;
		try {
			if (isdelete) {
				(new File(finalname)).delete();
				log.info("Received delete for file id " + fileName + " as " + finalname);
				return "delete";
			} else {
					Attachment attachment = attchedFiles.getAllAttachments().get(0);
					attachment.transferTo(new File(tmpname));
				(new File(tmpname)).renameTo(new File(finalname));
				log.info("Received file id " + tmpname + "  as " + finalname);
				return UPLOAD;
			}
		} catch (IOException ioe) {
			(new File(tmpname)).delete();
			log.info("Failure to save file " + finalname , ioe);
			throw new DataStreamerException("saving the recieved file " + fileName + " failed.");
		}
	}
}
