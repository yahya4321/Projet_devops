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

                stage('Build and Run Prometheus') {
                    steps {
                        script {
                            // Stop and remove the existing Prometheus container if it exists
                            sh 'docker rm -f prometheus || true'

                            // Run Prometheus container
                            sh """
                            docker run -d --name prometheus \
                                -p 9090:9090 \
                                -v \$(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
                                prom/prometheus
                            """
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
                      // Log in to Docker Hub sans utiliser d'interpolation de chaînes
                      sh '''
                          echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                      '''

                      // Poussez l'image dans Docker Hub
                      sh "docker push ${DOCKER_IMAGE_NAME}:${env.APP_VERSION}"

                      // Déconnexion de Docker Hub
                      sh "docker logout"
                  }
              }
          }
      }
     stage('Setup Remote Directory and Upload Docker Compose') {
                steps {
                    script {
                        sh """
                        ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} 'mkdir -p ${REMOTE_PATH}'
                        scp -o StrictHostKeyChecking=no docker-compose.yml ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PATH}/docker-compose.yml
                        """
                    }
                }
            }
         stage('Deploy to VM') {
                    steps {
                        script {
                            // Run Docker Compose on the VM
                            sh """
                            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} << 'EOF'
                            export APP_VERSION=${env.APP_VERSION}
                            cd ${REMOTE_PATH}
                            /usr/bin/docker compose down
                            APP_VERSION=${env.APP_VERSION} /usr/bin/docker compose up -d
        EOF
                            """
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

        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
