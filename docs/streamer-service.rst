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
Datastreamer Service Developer Guide
=============================

streamer-service is basically a background process which runs every 60 minutes and process configured message router via streamer-catalog-service
streamer-service pulls the scoring data from message router, send it to configured predictor to score and publish the score result back to configured message router

**1: Running service**
-----------------------------------------

in order to run streamer service, you will need to have catalog service up and running

change catalog_url_prefix=<catalog_url> in application.properties as streamer service talks to catalog service to pull metadata