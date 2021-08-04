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
                sh './gradlew assemble -Dspring.profiles.active=prod'
                stash includes: '**/build/libs/*.jar', name: 'myMoments'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test -Dspring.profiles.active=ci'
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
                unstash 'myMoments'
                sh 'git push https://heroku:${HEROKU_API_KEY}@git.heroku.com/${HEROKU_APP_NAME}.git HEAD:master'
            }
        }
    }
}