FROM dockercentral.it.att.com:5100/com.att.ajsc.public/anapsix/alpine-java:8_jdk
VOLUME /tmp
ADD cmlpstreamercatalog.jar app.jar
VOLUME /etc
ADD /etc/ /etc/
ADD config.properties /config.properties
ADD cadi.properties /cadi.properties
ADD keyfile /keyfile
ADD startService.sh /startService.sh
RUN chmod 700 /startService.sh
ENTRYPOINT ./startService.sh 
