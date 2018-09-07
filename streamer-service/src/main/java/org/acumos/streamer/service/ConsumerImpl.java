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

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.acumos.streamer.exception.CmlpDataSrcException;

@Service
public class ConsumerImpl {
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private String Login = "LOGIN";
	private String Password = "PASSWORD";
	private String OutputDirectory = "received";

	private String auth = Login + ":" + Password;

	public String recieveFile(String cadiAuth, String feedAuth, String fileName, MultipartBody attchedFiles) throws CmlpDataSrcException {
		return common(cadiAuth, feedAuth, fileName, attchedFiles, false);
	}
	
	protected String deleteFile(String cadiAuth, String feedAuth, String fileName) throws CmlpDataSrcException{
		return common(cadiAuth, feedAuth, fileName, null, true);
	}

	protected String common(String cadiAuth, String feedAuth, String fileName, MultipartBody attchedFiles, boolean isdelete) throws CmlpDataSrcException{
		if (cadiAuth == null || feedAuth == null) {
			log.info("Rejecting request with no Authorization header.");
			throw new CmlpDataSrcException("Unauthorised. Please send proper auth.", Status.FORBIDDEN.getStatusCode());
		}
		if (!auth.equals(feedAuth)) {
			log.info("Rejecting request with incorrect Authorization header.");
			throw new CmlpDataSrcException("Unauthorised. Please send proper auth.", Status.UNAUTHORIZED.getStatusCode());
		}
		String finalname = System.getProperty("user.dir") + System.getProperty("file.separator") + OutputDirectory + System.getProperty("file.separator") + fileName;
		String tmpname = System.getProperty("user.dir") + System.getProperty("file.separator") + OutputDirectory + System.getProperty("file.separator") + "." + fileName;;
		//try {
			if (isdelete) {
				(new File(finalname)).delete();
				log.info("Received delete for file id " + fileName + " as " + finalname);
				return "delete";
			} else {
					Attachment attachment = attchedFiles.getAllAttachments().get(0);
					//TO-DO resolve this error in the following line adding workaround temporarily
					//attachment.transferTo(new File(tmpname));
				(new File(tmpname)).renameTo(new File(finalname));
				log.info("Received file id " + tmpname + "  as " + finalname);
				return "upload";
			}
		/*} catch (IOException ioe) {
			(new File(tmpname)).delete();
			log.info("Failure to save file " + finalname , ioe);
			throw new CmlpDataSrcException("saving the recieved file " + fileName + " failed.");
		}*/
	}
}
