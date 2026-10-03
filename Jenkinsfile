pipeline {
    agent any

    environment {
        BACKEND_IMAGE  = 'edunode/backend'
        FRONTEND_IMAGE = 'edunode/frontend'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test backend') {
    steps {
        sh '''
            docker run --rm \
              --volumes-from jenkins \
              -v maven-repo:/root/.m2 \
              -w "$WORKSPACE/backend" \
              maven:3.9.6-eclipse-temurin-21 \
              mvn -B clean test
        '''
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
        }
    }
}

              stage('SonarQube analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        docker run --rm \
                          --volumes-from jenkins \
                          --network sonarqube_default \
                          -v maven-repo:/root/.m2 \
                          -e SONAR_TOKEN \
                          -e MAVEN_OPTS="-Xmx512m" \
                          -w "$WORKSPACE/backend" \
                          maven:3.9.6-eclipse-temurin-21 \
                          mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar \
                            -Dsonar.host.url=http://sonarqube:9000 \
                            -Dsonar.qualitygate.wait=true \
                            -Dsonar.qualitygate.timeout=300
                    '''
                }
            }
        }

        stage('Build backend image') {
            steps {
                sh 'docker build -t $BACKEND_IMAGE:$BUILD_NUMBER ./backend'
            }
        }

        stage('Build frontend image') {
            steps {
                sh 'docker build -t $FRONTEND_IMAGE:$BUILD_NUMBER ./frontend'
            }
        }

                stage('Trivy scan') {
            steps {
                sh '''
                    for IMG in $BACKEND_IMAGE:$BUILD_NUMBER $FRONTEND_IMAGE:$BUILD_NUMBER; do
                      echo "===== Scan de $IMG ====="
                      docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v trivy-cache:/root/.cache/ \
                        aquasec/trivy:latest image \
                          --severity HIGH,CRITICAL \
                          --ignore-unfixed \
                          --exit-code 0 \
                          "$IMG"
                    done
                '''
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_TOKEN')]) {
                    sh '''
                        echo "$DH_TOKEN" | docker login -u "$DH_USER" --password-stdin

                        docker tag $BACKEND_IMAGE:$BUILD_NUMBER  $DH_USER/edunode-backend:$BUILD_NUMBER
                        docker tag $BACKEND_IMAGE:$BUILD_NUMBER  $DH_USER/edunode-backend:latest
                        docker tag $FRONTEND_IMAGE:$BUILD_NUMBER $DH_USER/edunode-frontend:$BUILD_NUMBER
                        docker tag $FRONTEND_IMAGE:$BUILD_NUMBER $DH_USER/edunode-frontend:latest

                        docker push $DH_USER/edunode-backend:$BUILD_NUMBER
                        docker push $DH_USER/edunode-backend:latest
                        docker push $DH_USER/edunode-frontend:$BUILD_NUMBER
                        docker push $DH_USER/edunode-frontend:latest
                    '''
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
        }
        success {
            echo 'Pipeline termine avec succes.'
        }
        failure {
            echo 'Le pipeline a echoue : lis la Console Output pour trouver la cause.'
        }
    }
}