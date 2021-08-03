pipeline {
    agent any
    triggers {
        pollSCM '* * * * *'
    }
    stages {
        stage('Build') {
            steps {
                sh 'ls'
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