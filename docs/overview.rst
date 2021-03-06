.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

=============================
Datastreamer Service Overview
=============================

The Acumos Datastreamer Service provides ways to store the datastreamer metadata plus to handle both datarouter and message router
Datastreamer is divided into two mS i.e. streamer-catalog-service and streamer-service
streamer-catalog-service is responsible to store the metadata about message router (eg Kafka) which is publisher/subscriber url and its credentials
streamer-service is basically a background process which runs every 60 minutes and process configured message router via streamer-catalog-service
streamer-service pulls the scoring data from message router, send it to configured predictor to score and publish the score result back to configured message router
streamer-restproxy-service is for local development where we don't have real message router configured, this proxy url can be configured as publisher/subscriber into catalog
streamer-restproxy-service user need to start local kafka message router on his local machine and point this service to the same kafka message router
The server component is a Spring-Boot application that provides REST service to callers.

The source is available from the Linux Foundation Gerrit server:

    https://gerrit.acumos.org/r/gitweb?p=databroker/data-streamer.git;a=summary

The CI/CD jobs are in the Linux Foundation Jenkins server:

    https://jenkins.acumos.org/view/databroker-data-streamer/

Issues are tracked in the Linux Foundation Jira server:

    https://jira.acumos.org/secure/Dashboard.jspa

Mongo DB Install document can be found at docs/database_install.rst

Further information is available from the Linux Foundation Wiki:

    https://wiki.acumos.org/

