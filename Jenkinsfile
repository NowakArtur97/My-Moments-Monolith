pipeline {
    agent any
    tools {
        gradle "GRADLE"
        jdk 'openjdk-11'
    }
    triggers {
        pollSCM '* * * * *'
    }
    stages {
        stage('Check') {
            steps {
                sh 'ls'
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