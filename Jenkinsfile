pipeline {
    agent any

    environment {
        BACKEND_IMAGE  = 'edunode/backend'
        FRONTEND_IMAGE = 'edunode/frontend'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build backend image') {
            steps {
                sh 'docker build -t $BACKEND_IMAGE:$BUILD_NUMBER -t $BACKEND_IMAGE:latest ./backend'
            }
        }

        stage('Build frontend image') {
            steps {
                sh 'docker build -t $FRONTEND_IMAGE:$BUILD_NUMBER -t $FRONTEND_IMAGE:latest ./frontend'
            }
        }

        stage('Verify images') {
            steps {
                sh 'docker images | grep edunode/'
            }
        }
    }

    post {
        success {
            echo 'Pipeline termine avec succes.'
        }
        failure {
            echo 'Le pipeline a echoue : lis la Console Output pour trouver la cause.'
        }
    }
}