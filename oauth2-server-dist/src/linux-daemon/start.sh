#########################################################################
# File Name: start.sh
# Author: liyue
# Mail: liyue@hd123.com
# Created Time: Sat Nov 28 17:10:20 2015
#########################################################################
#!/bin/sh

APP_BIN="/opt/oauth2-server.jar"

# -Xmx 1024M -Xms 1024M
JAVA_OPTS="-ea \
           -server \
           -Xmx1024M -Xms1024M \
           -Dfile.encoding=UTF-8 \
           -Djava.security.egd=file:/dev/./urandom"

APP_OPTS="--spring.config.location=/opt/config/application.yaml"

# start service
exec /usr/bin/java ${JAVA_OPTS} -jar ${APP_BIN} ${APP_OPTS}