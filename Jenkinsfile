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
        SONAR_TOKEN = credentials('sonar_token')
        DOCKER_CREDENTIALS_ID = 'Docker_Credentials'
        DOCKER_IMAGE_NAME = 'firaskdidi/projetdevops'
        REMOTE_HOST = '192.168.50.4'   // VM IP address
        REMOTE_USER = 'vagrant'
        REMOTE_PATH = '/home/vagrant/dockercompose'
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
                withCredentials([string(credentialsId: 'sonar_token', variable: 'SONAR_TOKEN')]) {
                    sh "mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN"
                }
            }
        }

        stage('Clean') {
            steps {
                sh 'mvn clean'
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
                        -p 9090:9090 \
                        -v \$(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
                        prom/prometheus
                    """
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                        sh 'mvn deploy -s /var/lib/jenkins/.m2/settings.xml'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${env.APP_VERSION} --build-arg JAR_FILE=tp-foyer-${env.APP_VERSION}.jar ."
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'Docker_Credentials',
                                                     usernameVariable: 'DOCKERHUB_USERNAME',
                                                     passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        // Log in to Docker Hub without using string interpolation
                        sh '''
                            echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                        '''

                        // Push the image to Docker Hub
                        sh "docker push ${DOCKER_IMAGE_NAME}:${env.APP_VERSION}"

                        // Logout from Docker Hub
                        sh "docker logout"
                    }
                }
            }
        }

        stage('Docker Compose') {
            steps {
                sh "docker compose up -d docker-compose.yml"
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
