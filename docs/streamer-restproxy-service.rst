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
Datastreamer Restproxy Service Developer Guide
=============================

streamer-restproxy-service is for local development where we don't have real message router configured, this proxy url can be configured as publisher/subscriber into catalog
streamer-restproxy-service user need to start local kafka message router on his local machine and point this service to the same kafka message router


**1: Install Message Router (Kafka)**
-----------------------------------------

Install Kafka by following this quickstart guide https://kafka.apache.org/quickstart

Please start zookeeper and kafka services both

by default kafka runs on localhost:9092, keep it like that as the same is configured in application.properties of your service

now go to the root directory of your kafka installation and run below command, this will create a topic called test

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

now you are all set to run your mS

open swagger http://localhost:8080/streamer-restproxy-service/swagger-ui.html

POST API to publish message on topic test
GET API to subscribe message available on topic test

remember to send encoded authorization header of dummy/dummy (this is configured in application.properties, you can change it if you want)
