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
.. ===============LICENSE_END===========================================================

=============================
Datastreamer Service Overview
=============================

The Acumos Datastreamer Service provides ways to store the datastreamer metadata plus to 
handle both datarouter and messagerouter. Also it provides a rest proxy service which
talks to kafka client running on that environment and provides http end point for sending
and recieving messages.

There are three services running in this Streamer component. One is the Streamer Catalog
component which handles and stores the catalog objects which contain the url in which we
retrieve messages from and publish data to. Second one is the Streamer component itself which
handles message routing which gets data from the url which is provided in the catalog and sends 
to prediction and gets the results back and publich the results to the URL which was provided 
in the catalog.Third one is the rest proxy component which enables you create the URLS from 
which you can send and recieve data when you have Kafka running locally. If you have a topic
defined in kafka locally or wherever you running this rest proxy service it enables to send and
recieve data.
 
The server component is a Spring-Boot application that provides REST services to callers.
The client component is a Java library that provides business objects (models) and
methods to simplify the use of the REST service.

The source is available from the Linux Foundation Gerrit server:

    https://gerrit.acumos.org/r/gitweb?p=databroker/data-streamer.git;a=summary

The CI/CD jobs are in the Linux Foundation Jenkins server:

    https://jenkins.acumos.org/view/databroker-data-streamer/

Issues are tracked in the Linux Foundation Jira server:

    https://jira.acumos.org/secure/Dashboard.jspa

Mongo DB Install document can be found at docs/database_install.rst

Further information is available from the Linux Foundation Wiki:

    https://wiki.acumos.org/

