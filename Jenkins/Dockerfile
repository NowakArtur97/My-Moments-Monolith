#FROM jenkins:1.642.2
#ADD config.xml /usr/share/jenkins/ref/jobs/My-Moments-Monolith/config.xml
#EXPOSE 8083
FROM jenkins/jenkins:2.289.1-jdk11

USER root
RUN curl -sSL https://get.docker.com/ | sh
RUN usermod -a -G docker jenkins
USER jenkins

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

COPY config.xml /usr/share/jenkins/ref/jobs/My-Moments-Monolith/config.xml

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false