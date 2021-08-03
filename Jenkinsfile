pipeline {
    agent any
    tools {
        gradle "gradle-6.8"
    }

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