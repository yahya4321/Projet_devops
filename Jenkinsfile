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
