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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.acumos.streamer.exception.DataStreamerException;


public class PublisherImpl {

	private static final String FILENAME = "filename";

	private static final String BASIC = "Basic ";

	private static final String LOCATION = "Location";

	private static final String X_ATT_DR_PUBLISH_ID = "X-ATT-DR-PUBLISH-ID";

	private static final String _100_CONTINUE = "100-Continue";

	private static final String EXPECT = "Expect";

	private static final String PUT = "PUT";

	private static final String CONTENT_LENGTH = "Content-Length";

	private static final String AUTHORIZATION = "Authorization";

	private static final String FILE_SEPARATOR = "file.separator";

	private static final String USER_DIR = "user.dir";

	private static final String PUBLISH = "publish";

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private String origurl;
	private String auth;
	private String redirfile = ".redir";
	volatile private String url;
	volatile private int redircnt;
	volatile private int faildur;
	volatile private long nexttry;

	private void loadredir() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(redirfile));
			String[] x = br.readLine().split("\t");
			br.close();
			if (x.length == 3 && x[0].equals(origurl)) {
				redircnt = Integer.parseInt(x[2]);
				url = x[1];
			}
		} catch (Exception e) {
			log.error("Exception in PublisherImpl:loadredir()" +e.getMessage());
		}
	}

	private void saveredir() {
		try {
			OutputStream os = new FileOutputStream(redirfile);
			os.write((origurl + "\t" + url + "\t" + redircnt).getBytes());
			os.close();
		} catch (Exception e) {
			log.error("Exception in PublisherImpl:saveredir()" +e.getMessage());
		}
	}

	/**
	 * Bump failure timeout on failure
	 */
	private synchronized void setfail() {
		long now = System.currentTimeMillis();
		if (now >= nexttry) {
			nexttry = now + faildur;
			faildur += faildur;
			if (faildur > 3600000) {
				faildur = 3600000;
			}
		}
	}

	/**
	 * Reset failure timeout on success
	 */
	private synchronized void setsuccess() {
		nexttry = 0;
		faildur = 10000;
		notifyAll();
	}

	/**
	 * Wait for failure timeout to expire
	 */
	private synchronized void faildelay() {
		long now = System.currentTimeMillis();
		while (now < nexttry) {
			try {
				wait(nexttry + 100 - now);
			} catch (Exception e) {
				log.error("Exception in PublisherImpl:faildelay()" +e.getMessage());
			}
			now = System.currentTimeMillis(); // New fix added for retry if its
												// failed.
		}
	}

	private synchronized void clearredir(String saveurl) {
		if (url == saveurl) {
			url = origurl;
			redircnt = 0;
			saveredir();
		}
	}

	private synchronized String adjustredir(String saveurl, String location, String fileid) {
		if (url == saveurl) {
			if (redircnt >= 2) {
				url = origurl;
				redircnt = 0;
				saveredir();
				return (null);
			} else if (location.endsWith("/" + fileid)) {
				String nurl = location.substring(0, location.length() - fileid.length() - 1);
				if (nurl.equals(saveurl) || nurl.equals(origurl)) {
					url = origurl;
					redircnt = 0;
					saveredir();
					return (null);
				}
				url = nurl;
				redircnt++;
				saveredir();
				return (location);
			}
		}
		return (location);
	}

	public void deliver(String cadiAuth, String feedAuth, String xMetadata, MultipartBody attchedFiles) {

		Attachment attachment = null;
		String attachmentfilename = null;
		String fileNameKey = null;
		log.info("going to publish files");
		String publishDirectory = PUBLISH;

		for (int i = 0; i < attchedFiles.getAllAttachments().size(); i++) {
			attachment = attchedFiles.getAllAttachments().get(i);			
			attachmentfilename = attachment.getContentDisposition().getParameter(FILENAME);
			File f = new File(System.getProperty(USER_DIR) + System.getProperty(FILE_SEPARATOR) + publishDirectory
					+ System.getProperty(FILE_SEPARATOR) + attachmentfilename);
			while (attempt(f, attachmentfilename)) {
			}
		}
	}

	private boolean attempt(File f, String fid) {
		byte[] buf = new byte[65536];
		boolean reschedule = false;
		long flen = f.length();
		try {
			faildelay();
			String saveurl = url;
			URL u = new URL(saveurl + "/" + fid);
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setInstanceFollowRedirects(false);
			if (auth != null) {
				uc.setRequestProperty(AUTHORIZATION, auth);
			}
			boolean connectok = false;
			int rc = -1;
			String responsemessage = null;
			String lochdr = null;
			String pubid = null;
			try {
				uc.setFixedLengthStreamingMode(flen);
				uc.setRequestProperty(CONTENT_LENGTH, Long.toString(flen));
				uc.setRequestMethod(PUT);
				uc.setRequestProperty(EXPECT, _100_CONTINUE);
				uc.setDoOutput(true);
				long sofar = 0;
				OutputStream os = null;
				try {
					uc.connect();
					os = uc.getOutputStream();
					connectok = true;
				} catch (ProtocolException pe) {
					// Rcvd error instead of 100-Continue
					try {
						// work around glitch in Java 1.7.0.21 and likely others
						// without this, Java will connect multiple times to the
						// server to run the same request
						uc.setDoOutput(false);
					} catch (Exception e) {
						log.error("Exception in PublisherImpl:attempt()" +e.getMessage());
					}
				}
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
						log.error("Exception in PublisherImpl:attempt():Error publishing file id " + fid);
					}
				}
				rc = uc.getResponseCode();
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
					pubid = uc.getHeaderField(X_ATT_DR_PUBLISH_ID);
				} else if (rc >= 300 && rc < 400) {
					lochdr = uc.getHeaderField(LOCATION);
				}
				connectok = true;
			} catch (Exception e) {
				log.error("Exception in PublisherImpl:attempt():Error publishing file id " + fid);
			}
			try {
				// discard input so can re-use connection
				InputStream is = (rc >= 300) ? uc.getErrorStream() : uc.getInputStream();
				is.close();
			} catch (Exception e) {
				log.error("Exception in PublisherImpl:attempt()" + e.getMessage());
			}
			if (redircnt > 0 && !connectok) {
				// URL had been redirected but replacement URL failed - go back
				// to original URL
				log.error("Error connecting to publish after redirection - reverting to original URL");
				clearredir(saveurl);
				reschedule = true;
				setfail();
				return (reschedule);
			}
			if (rc >= 200 && rc < 300) {
				// 2xx success
				log.info("Published file id " + fid + " to " + saveurl + " publish ID " + pubid);
				reschedule = false;
				setsuccess();
				return (reschedule);
			}
			if (rc >= 300 && rc < 400) {
				// 3xx redirect
				log.info("Publish attempt to " + saveurl + " redirected (" + lochdr + ").");
				String nloc = adjustredir(saveurl, lochdr, fid);
				reschedule = true;
				if (nloc == null) {
					setfail();
				}
				return (reschedule);
			}
			if (rc >= 400 && rc < 500) {
				// 4xx Bad request
				log.error("Publish attempt for " + fid + " to " + saveurl + " permanently rejected code " + rc
						+ " - " + responsemessage);
				setfail();
				return (reschedule);
			}
			// 5xx, can't connect, or unparsable response - Server problem
			log.error("Publish attempt for " + fid + " to " + saveurl + " temporarily rejected code " + rc
					+ " - " + responsemessage);
			setfail();
			reschedule = true;
			return (reschedule);
		} catch (Exception e) {
			log.error("Exception in PublisherImpl:attempt():Unexpected error during publish attempt " + e.getMessage());
			return (reschedule);
		}
	}

	public String doPublish(String cadiAuth, String feedAuth, String xMetadata, MultipartBody file) throws Exception {

		if (feedAuth.split(":").length != 2) {
			throw new DataStreamerException("the format of feedAuth header should be user:password");
		}
		auth = BASIC + Base64.encodeBase64String(feedAuth.getBytes());

		deliver(cadiAuth, auth, xMetadata, file);
		return null;
	}

}
