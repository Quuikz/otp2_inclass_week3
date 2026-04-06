pipeline {
    agent any

    tools {
        maven 'maven'
        git 'Default'
    }

    environment {
       PATH = "/Applications/Docker.app/Contents/Resources/bin:/usr/local/bin:${env.PATH}"
       DOCKERHUB_CREDENTIALS_ID = 'dockerHub'
       DOCKERHUB_REPO = 'rikukuikka/otp2_localization_week3'
       DOCKER_IMAGE_TAG = 'latest'

       BUILD_IMAGE_NAME = 'ui-localization'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url:'https://github.com/Quuikz/otp2_inclass_week3'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn -f pom.xml clean test'
            }
        }

        stage('Code Coverage') {
            steps{
                sh 'mvn -f pom.xml jacoco:report'
            }
        }

        stage('Publish Test Report') {
            steps{
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('Publish Coverage Report') {
                    steps {
                        recordCoverage(
                            tools: [[parser: 'JACOCO', pattern: '**/target/site/jacoco/jacoco.xml']]
                )
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIALS_ID}", passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                        sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                        sh "docker push ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}"
                    }
                }
            }
        }
    }
}

