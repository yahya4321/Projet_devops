pipeline {
    agent any

    tools {
        maven 'M2_HOME'       // Ensure "M2_HOME" is configured in Jenkins
        jdk 'JAVA_HOME'       // Ensure "JAVA_HOME" is configured in Jenkins
    }

    environment {
        GIT_URL = 'https://github.com/yahya4321/Projet_devops.git'
        GIT_BRANCH = 'Firas_Univer'
        CREDENTIALS_ID = 'GitHub_Credentials'
        DOCKER_CREDENTIALS_ID = 'Docker_Credentials'
        SONAR_TOKEN = credentials('sonar_token')
        DOCKER_IMAGE_NAME = 'firaskdidi/projetdevops'
        REMOTE_HOST = '192.168.50.4'   // VM IP address
        REMOTE_USER = 'vagrant'
        REMOTE_PATH = '/home/vagrant/your-app-directory'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: "${env.GIT_BRANCH}",
                    url: "${env.GIT_URL}",
                    credentialsId: "${env.CREDENTIALS_ID}"
            }
        }

        stage('Get Version') {
            steps {
                script {
                    env.APP_VERSION = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                    echo "Application version: ${env.APP_VERSION}"
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'  // This will compile and package the JAR
            }
        }

        stage('Verify JAR File') {
            steps {
                script {
                    def jarFile = sh(script: 'ls -1 target/*.jar', returnStdout: true).trim()
                    if (!jarFile) {
                        error("No JAR file found!")
                    } else {
                        echo "JAR file found: ${jarFile}"
                    }
                }
            }
        }

        stage('Mockito Tests') {
            steps {
                sh 'mvn test'
            }
        }
        stage('SonarQube Analysis') {
                    steps {
                        sh 'mvn sonar:sonar'
                    }
                }
        stage('Deploy to Nexus') {
                            steps {
                                script {
                                    withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                                        sh 'mvn deploy -s /usr/share/maven/conf/settings.xml'
                                    }
                                }
                            }
                        }

        stage('Build Docker Image (Spring Part)') {
            steps {
                script {
                    def dockerImage = docker.build("${DOCKER_IMAGE_NAME}:${env.APP_VERSION}") // Tagging the image with the app version
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID,
                                                     usernameVariable: 'DOCKERHUB_USERNAME',
                                                     passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        // Log in to Docker Hub
                        sh '''
                            echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                        '''

                        // Push the image to Docker Hub with the correct tag
                        sh "docker push ${DOCKER_IMAGE_NAME}:${env.APP_VERSION}"

                        // Logout from Docker Hub
                        sh "docker logout"
                    }
                }
            }
        }

        stage('Debug Workspace') {
            steps {
                sh 'ls -l ${WORKSPACE}'
            }
        }

        stage('Docker compose (BackEnd MySql)') {
            steps {
                script {
                    sh 'docker compose -f ${WORKSPACE}/Docker-compose.yml up -d'
                }
            }
        }
        stage('Build and Run Grafana') {
                    steps {
                        script {
                            // Stop and remove the existing Grafana container if it exists
                            sh 'docker rm -f grafana || true'

                            // Run Grafana container
                            sh """
                            docker run -d --name grafana \
                                -p 3000:3000 \
                                grafana/grafana
                            """
                        }
                    }
                }

                stage('Build and Run Prometheus') {
                    steps {
                        script {
                            // Stop and remove the existing Prometheus container if it exists
                            sh 'docker rm -f prometheus-p || true'

                            // Run Prometheus container
                            sh """
                            docker run -d --name prometheus-p \
                                -p 9091:9090 \
                                -v \$(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
                                prom/prometheus
                            """
                        }
                    }
                }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }
        success {
            echo 'Pipeline executed successfully!'
            // Send success email
            mail to: 'kdidifiras30@gmail.com',
                subject: "Jenkins Pipeline Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Good news! The pipeline ${env.JOB_NAME} has completed successfully. Check the details here: ${env.BUILD_URL}"
        }
        failure {
            echo 'Pipeline failed.'
            // Send failure email
            mail to: 'kdidifiras30@gmail.com',
                subject: "Jenkins Pipeline Failure: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Unfortunately, the pipeline ${env.JOB_NAME} has failed. Please check the details here: ${env.BUILD_URL}"
        }
    }
}
