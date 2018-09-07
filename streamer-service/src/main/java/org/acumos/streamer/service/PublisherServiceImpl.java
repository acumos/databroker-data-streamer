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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class PublisherServiceImpl implements PublisherService {
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public String publish(String catalogKey, String feedAuthorization, String filePath, String publisherUrl, Map<String, String> metaDataMap) {
		byte[] buf = new byte[65536];
		File f = new File(filePath);

		String fid = filePath.substring(filePath.lastIndexOf(System.getProperty("file.separator")) + 1);
		System.out.println("file id is " + fid);
		long flen = f.length();
		try {
			String saveurl = publisherUrl;
			URL u = new URL(saveurl + "/" + fid);
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setInstanceFollowRedirects(true);
				uc.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String(feedAuthorization.getBytes()));
				log.info("PublisherServiceImpl::publish()::generating headers");
			int rc = -1;
			String responsemessage = null;
			String lochdr = null;
			String pubid = null;
			try {
				log.info("PublisherServiceImpl::publish()::generating ");
				uc.setFixedLengthStreamingMode(flen);
				uc.setRequestProperty("Content-Length", Long.toString(flen));
				uc.setRequestProperty("Content-Type", "application/octet-stream");
				uc.setRequestProperty("X-ATT-DR-META",
						"{\"file_type\":\"csv\",\"feed_type\":\"delta-data-set\",\"compression\":\"N\",\"delimiter\":\",\",\"record_count\":\"1000000000\",\"publish_date\":\"2018-05-23  23:59:59\",\"file_size\":\"376\",\"splits\":\"1\",\"feed_id\":\"1078\",\"version\":\"1.0\"}");
				uc.setRequestMethod("PUT");
				uc.setRequestProperty("Expect", "100-Continue");
				uc.setDoOutput(true);
				long sofar = 0;
				OutputStream os = null;
				try {
					uc.connect();
					log.info("PublisherServiceImpl::publish()::connection done");
					os = uc.getOutputStream();
					//connectok = true;
				} catch (ProtocolException pe) {
					try {
						uc.setDoOutput(false);
						log.info("PublisherServiceImpl::publish()::got protocol exception, intiating re-dircetion");
					} catch (Exception e) {
					}
				}
				if (os != null) {
					try {
						log.info("PublisherServiceImpl::publish()::writing o/p for connection");
						InputStream is = new FileInputStream(f);
						while (sofar < flen) {
							int len = buf.length;
							if (flen - sofar < len) {
								len = (int) (flen - sofar);
							}
							len = is.read(buf, 0, len);
							os.write(buf, 0, len);
							sofar += len;
						}
						is.close();
						os.close();
					} catch (Exception e) {
						System.err.println("Error publishing file id " + fid);
						e.printStackTrace();
					}
				}
				rc = uc.getResponseCode();
				log.info("PublisherServiceImpl::publish()::response from intial connection is:  " + rc);
				responsemessage = uc.getResponseMessage();
				if (responsemessage == null) {
					// work around for glitch in Java 1.7.0.21 and likely others
					// When Expect: 100 is set and a non-100 response is
					// received, the response message is not set but the
					// response code is
					String h0 = uc.getHeaderField(0);
					if (h0 != null) {
						int i = h0.indexOf(' ');
						int j = h0.indexOf(' ', i + 1);
						if (i != -1 && j != -1) {
							responsemessage = h0.substring(j + 1);
						}
					}
				}
				if (rc >= 200 && rc < 300) {
					pubid = uc.getHeaderField("X-ATT-DR-PUBLISH-ID");
					log.info("PublisherServiceImpl::publish()::the published id is " + pubid);
					return pubid;
				} else if (rc >= 300 && rc < 400) {
					
					lochdr = uc.getHeaderField("Location");
					log.info("PublisherServiceImpl::publish()::intiating re-dircetion" + lochdr);
					String cookies = uc.getHeaderField("Set-Cookie");
					
					log.info("PublisherServiceImpl::publish()::setting headers for re-dircetion");
					uc = (HttpURLConnection) new URL(lochdr).openConnection();
					uc.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String(feedAuthorization.getBytes()));
					uc.setRequestProperty("Content-Length", Long.toString(flen));
					uc.setRequestProperty("Content-Type", "application/octet-stream");
					uc.setRequestProperty("X-ATT-DR-META",
							"{\"file_type\":\"csv\",\"feed_type\":\"delta-data-set\",\"compression\":\"N\",\"delimiter\":\",\",\"record_count\":\"1000000000\",\"publish_date\":\"2018-05-23  23:59:59\",\"file_size\":\"376\",\"splits\":\"1\",\"feed_id\":\"1078\",\"version\":\"1.0\"}");
					uc.setRequestMethod("PUT");
					uc.setRequestProperty("Expect", "100-Continue");
					uc.setDoOutput(true);
											
					uc.connect();
					os = uc.getOutputStream();
					
					if (os != null) {
						try {
							InputStream is = new FileInputStream(f);
							while (sofar < flen) {
								int len = buf.length;
								if (flen - sofar < len) {
									len = (int) (flen - sofar);
								}
								len = is.read(buf, 0, len);
								os.write(buf, 0, len);
								sofar += len;
							}
							is.close();
							os.close();
						} catch (Exception e) {
							System.err.println("Error publishing file id " + fid);
							e.printStackTrace();
						}
					}
					
					rc = uc.getResponseCode();
					log.info("PublisherServiceImpl::publish()::responseCode from re-dircetion : " + rc);
					responsemessage = uc.getResponseMessage();
					pubid = uc.getHeaderField("X-ATT-DR-PUBLISH-ID");
					if (rc == 200){
						return pubid;
					}
				}
			} catch (Exception e) {
				System.err.println("Unable to publish file id " + fid);
				e.printStackTrace();
			}
			try {
				// discard input so can re-use connection
				InputStream is = (rc >= 300) ? uc.getErrorStream() : uc.getInputStream();
				int i;
				while ((i = is.read(buf)) > 0) {
					System.out.write(buf, 0, i);
				}
				is.close();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			log.info("Unexpected error during publish attempt");
			e.printStackTrace();
		}
		return null;
	}

}
