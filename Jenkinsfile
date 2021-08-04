pipeline {
    agent any
    stages {
        stage('Give permission to Gradle wrapper') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew assemble'
                stash includes: '**/build/libs/*.jar', name: 'myMoments'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
        stage('Promotion') {
            steps {
                timeout(time: 1, unit: 'DAYS') {
                    input 'Deploy to Heroku?'
                }
            }
        }
        stage('Deploy') {
            environment {
                HEROKU_API_KEY = credentials('HEROKU_API_KEY')
            }
            steps {
                unstash 'myMoments'
                sh './gradlew deployHeroku'
            }
        }
    }
}