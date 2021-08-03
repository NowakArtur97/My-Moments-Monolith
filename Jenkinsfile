pipeline {
    agent any
    tools {
        gradle "GRADLE"
        jdk 'openjdk-11'
    }
    environment {
        JAVA11_HOME = "${tool 'openjdk-11'}"
    }
    triggers {
        pollSCM '* * * * *'
    }
    stages {
        stage('Checks') {
            steps {
                sh 'ls'
                sh 'java --version'
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