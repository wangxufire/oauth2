# base image
FROM java:latest

LABEL PROJECT="oauth2-server" \
      VERSION="0.3.0" \
      AUTHOR="liyue@hd123.com" \
      COPYRIGHT="Shanghai HEADING Information Engineering Co., Ltd."

# developer info
MAINTAINER  "liyue" <liyue@hd123.com>

# current workspace
WORKDIR /opt

# config root password
RUN /bin/echo 'root:root' | chpasswd

# common setting
ENV LANG en_US.UTF-8
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN /bin/echo "Asia/Shanghai" > /etc/timezone

# copy host resources to image
RUN /bin/mkdir /opt/config
COPY application.yaml /opt/config
COPY oauth2-server.jar /opt/app.jar

# config env
ENV log /var/log/oauth2-server

# mount file or directory
VOLUME /var/log

# open image port
EXPOSE 8180 8443

# exec command
ENTRYPOINT ["/usr/bin/java"]
CMD ["-ea","-server","-Xmx1024M","-Xms1024M","-Dfile.encoding=UTF-8","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/app.jar","--spring.config.location=/opt/config/application.yaml"]