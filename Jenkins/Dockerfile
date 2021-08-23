FROM jenkins/jenkins:2.289.1-jdk11

USER root
RUN curl -sSL https://get.docker.com/ | sh
RUN usermod -a -G docker jenkins
USER jenkins

ENV JENKINS_USER admin
ENV JENKINS_PASS admin

# install Jenkins plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Jenkins runs all grovy files from init.groovy.d dir
COPY config.xml /usr/share/jenkins/ref/jobs/My-Moments-Monolith/config.xml
COPY default-user.groovy /usr/share/jenkins/ref/init.groovy.d/

# allows to skip Jenkins setup wizard
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
