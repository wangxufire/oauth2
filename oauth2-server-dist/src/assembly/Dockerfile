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
ADD config /opt/config
COPY oauth2-server.jar /opt/oauth2-server.jar
COPY systemd/start.sh /opt/start.sh

# runtime setting
RUN /bin/chmod +x /opt/start.sh

#config env
ENV log /var/log/oauth2-server

# mount file or directory
VOLUME /var/log

# open image port
EXPOSE 8180 8443

# exec command
ENTRYPOINT ["/bin/bash"]
CMD ["/opt/start.sh"]