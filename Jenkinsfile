pipeline {
    agent any
    tools {
        gradle "gradle-6.8"
    }

    triggers {
        pollSCM '* * * * *'
    }
    stages {
        stage('Give permission to gradlew') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew assemble'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }
}