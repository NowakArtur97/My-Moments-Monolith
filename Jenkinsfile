pipeline {
    agent any
    tools {
        gradle "GRADLE"
        jdk 'openjdk-11'
    }
    env.JAVA_HOME="${tool 'openjdk-11'}"
    env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"

    triggers {
        pollSCM '* * * * *'
    }
    stages {
        stage('Checks') {
            steps {
                sh 'ls'
                sh 'java -version'
                sh 'gradle --version'
            }
        }
        stage('Build') {
            steps {
                sh 'gradle assemble'
            }
        }
        stage('Test') {
            steps {
                sh 'gradle test'
            }
        }
    }
}