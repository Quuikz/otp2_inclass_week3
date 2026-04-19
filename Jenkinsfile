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

       DB_URL = 'jdbc:mariadb://localhost:3306/'
       DB_NAME = 'fuel_calculator_localization'
       DB_USER = 'fuel_calculator_user'
       DB_PASSWORD = 'password'

       BUILD_IMAGE_NAME = 'ui-localization'
       SKIP_DOCKER = 'true'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url:'https://github.com/Quuikz/otp2_inclass_week3'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn -f pom.xml clean verify'
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

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeLocalServer') {
                    sh """
                    ${tool 'SonarQube 8.0.1'}/bin/sonar-scanner \\
                    -Dsonar.projectKey=devops-demo \\
                    -Dsonar.sources=src \\
                    -Dsonar.projectName=DevOps-Demo \\
                    -Dsonar.host.url=$SONAR_HOST_URL \\
                    -Dsonar.token=$SONAR_AUTH_TOKEN \\
                    -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }

        stage('Build Docker Image') {
            when {
                expression { env.SKIP_DOCKER != 'true' }
            }
            steps {
                script {
                    docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            when {
                expression { env.SKIP_DOCKER != 'true' }
            }
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

