pipeline {
    agent any
    tools {
        gradle "GRADLE"
        jdk 'openjdk-11'
    }
    environment {
        JAVA_HOME = "${tool 'openjdk-11'}"
        PATH="${environment 'JAVA_HOME'}/bin"
    }

    triggers {
        pollSCM '* * * * *'
    }
    stages {
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