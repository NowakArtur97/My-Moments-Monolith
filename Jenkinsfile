pipeline {
    agent any
    triggers {
        pollSCM '* * * * *'
    }
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
                sh 'ls'
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
            steps {
                sh 'ls'
                unstash 'myMoments'
                sh 'git push https://heroku:${HEROKU_API_KEY}@git.heroku.com/${HEROKU_APP_NAME}.git master'
            }
        }
    }
}