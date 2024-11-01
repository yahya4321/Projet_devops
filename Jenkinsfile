pipeline {
    agent any

    tools {
        maven 'M2_HOME'       // Assurez-vous que "M2_HOME" est le nom configuré pour Maven dans Jenkins
        jdk 'JAVA_HOME'       // Assurez-vous que "JAVA_HOME" est le nom configuré pour JDK dans Jenkins
    }

     environment {
          GIT_CREDENTIALS_ID = 'first_credentials'
          SONARQUBE_SERVER = 'SonarQube_Server'
          DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
          DOCKER_IMAGE = 'yahya4321/tp-foyer'
          REMOTE_HOST = '192.168.50.4'   // VM IP address
          REMOTE_USER = 'vagrant'         // VM SSH user
          REMOTE_PATH = '/home/vagrant/your-app-directory'  // Directory to store docker-compose.yml on the VM
      }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'Yahya_Branch_Bloc',
                    url: 'https://github.com/yahya4321/Projet_devops.git',
                    credentialsId: 'first_credentials'
            }
        }
              stage('Get Version') {
                    steps {
                        script {
                            // Extract the version from the pom.xml and store it in an environment variable
                            env.APP_VERSION = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                            echo "App version is: ${env.APP_VERSION}"
                        }
                    }
                }


        stage('Build') {
            steps {
                sh 'mvn clean install' // Effectue la construction de votre projet
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test' // Lancement des tests, y compris ceux utilisant Mockito
            }
        }
           stage('Code Coverage') {
                   steps {
                       jacoco execPattern: 'target/jacoco.exec', // Locate JaCoCo report
                              classPattern: 'target/classes',
                              sourcePattern: 'src/main/java',
                              exclusionPattern: '**/Test*.class' // Optional, exclude test classes
                   }
               }
               stage('Deploy to Nexus') {
                          steps {
                              script {
                                  // Deploy the artifact to Nexus
                                  withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                                      sh 'mvn deploy -s $HOME/.m2/settings.xml'
                                  }
                              }
                          }
                      }

       stage('SonarQube Analysis') {
           environment {
               scannerHome = tool 'SonarQube Scanner' // Must match the tool name configured in Jenkins
           }
           steps {
               withSonarQubeEnv('SonarQube_Server') { // Must match the configured SonarQube name in Jenkins
                   sh """
                       ${scannerHome}/bin/sonar-scanner \
                       -Dsonar.projectKey=Projet_devops \
                       -Dsonar.sources=src \
                       -Dsonar.java.binaries=target/classes \
                       -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                       -Dsonar.host.url=http://192.168.50.4:9000 \
                       -Dsonar.login=sqp_3dacd87cd7e8ab4825e87c7a77e06027cb999d37
                   """
               }
           }

       }
          stage('Build Docker Image') {
                    steps {
                        script {
                            // Build Docker image using the extracted version
                            sh "docker build -t ${DOCKER_IMAGE}:${env.APP_VERSION} --build-arg JAR_FILE=tp-foyer-${env.APP_VERSION}.jar ."
                        }
                    }
                }

               stage('Push Docker Image to Docker Hub') {
                        steps {
                            script {
                                withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID,
                                                                 usernameVariable: 'DOCKERHUB_USERNAME',
                                                                 passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                                    sh "echo ${DOCKERHUB_PASSWORD} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin"
                                    sh "docker push ${DOCKER_IMAGE}:${env.APP_VERSION}"
                                    sh "docker logout"
                                }
                            }
                        }
                    }
                    stage('Setup Remote Directory and Upload Docker Compose') {
                                steps {
                                    script {
                                        // Create the directory and copy docker-compose.yml to the VM
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
                // Copy docker-compose.yml file to VM
                sh """
                    scp -o StrictHostKeyChecking=no docker-compose.yml vagrant@192.168.50.4:/home/vagrant/your-app-directory/
                """

                // Connect to VM and run Docker Compose
                sh """
                    ssh -o StrictHostKeyChecking=no vagrant@192.168.50.4 <<EOF
                    cd /home/vagrant/your-app-directory
                    /usr/bin/docker compose down
                    /usr/bin/docker compose up -d
    EOF
                """
            }
        }
    }


       }

 post {
         always {
             junit '**/target/surefire-reports/*.xml' // Publication des résultats des tests JUnit
             jacoco() // Publish JaCoCo coverage report

         }

         success {
             echo 'Build, Test, and SonarQube Analysis completed successfully!'
             emailext (
                 subject: "Jenkins Pipeline Success: ${currentBuild.fullDisplayName}",
                 body: """<p>The Jenkins pipeline for <b>${env.JOB_NAME}</b> completed successfully.</p>
                          <p>Build URL: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>""",
                 to: 'yahyaaahamdi8756@gmail.com' // Replace with the admin's email
             )
         }

         failure {
             echo 'There was an error in the pipeline stages.'
             emailext (
                 subject: "Jenkins Pipeline Failure: ${currentBuild.fullDisplayName}",
                 body: """<p>The Jenkins pipeline for <b>${env.JOB_NAME}</b> failed.</p>
                          <p>Check the logs for more details: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>""",
                 to: 'yahyaaahamdi8756@gmail.com' // Replace with the admin's email
             )
         }
     }
}
