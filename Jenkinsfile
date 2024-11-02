pipeline {
    agent any

    tools {
        maven 'M2_HOME'       // Ensure "M2_HOME" is configured in Jenkins
        jdk 'JAVA_HOME'       // Ensure "JAVA_HOME" is configured in Jenkins
    }

    environment {
        GIT_URL = 'https://github.com/your-repo/project.git'  // Replace with your Git repo
        GIT_BRANCH = 'main'                                   // Replace with your branch
        CREDENTIALS_ID = 'GitHub_Credentials'                 // Git credentials ID
        DOCKER_CREDENTIALS_ID = 'Docker_Credentials'          // Docker credentials ID
        DOCKER_IMAGE_NAME = 'firaskdidi/projetdevops/alpine'  // Docker Hub image name
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: "${env.GIT_BRANCH}",
                    url: "${env.GIT_URL}",
                    credentialsId: "${env.CREDENTIALS_ID}"
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package'  // Adjust as per your build tool
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image with a version tag
                    env.APP_VERSION = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${env.APP_VERSION} ."
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    // Use credentials for Docker Hub login
                    withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}",
                                                     usernameVariable: 'DOCKERHUB_USERNAME',
                                                     passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        // Log in to Docker Hub
                        sh "echo ${DOCKERHUB_PASSWORD} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin"

                        // Push the image to Docker Hub
                        sh "docker push ${DOCKER_IMAGE_NAME}:${env.APP_VERSION}"

                        // Log out from Docker Hub
                        sh "docker logout"
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed.'
            cleanWs()  // Clean up workspace
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
