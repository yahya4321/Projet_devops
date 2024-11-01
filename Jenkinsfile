pipeline {
    agent any

    tools {
        maven 'M2_HOME'       // Assurez-vous que "M2_HOME" est le nom configuré pour Maven dans Jenkins
        jdk 'JAVA_HOME'       // Assurez-vous que "JAVA_HOME" est le nom configuré pour JDK dans Jenkins
    }

    environment {
        GIT_CREDENTIALS_ID = 'first_credentials' // Remplacez par l'ID de vos identifiants Git si l'authentification est nécessaire
        SONARQUBE_SERVER = 'SonarQube_Server'    // Nom de l'instance SonarQube configurée dans Jenkins
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'   // Docker Hub credentials ID
        DOCKER_IMAGE = 'yahya4321/tp-foyer'   // Docker image name on Docker Hub
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'Yahya_Branch_Bloc',
                    url: 'https://github.com/yahya4321/Projet_devops.git',
                    credentialsId: 'first_credentials'
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
                           // Build Docker image using the Dockerfile
                           sh "docker build -t ${DOCKER_IMAGE}:${env.BUILD_NUMBER} ."
                       }
                   }
               }

               stage('Push Docker Image to Docker Hub') {
                   steps {
                       script {
                           // Log in to Docker Hub and push the image
                           withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID,
                                                            usernameVariable: 'DOCKERHUB_USERNAME',
                                                            passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                               sh "echo ${DOCKERHUB_PASSWORD} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin"
                               sh "docker push ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                               sh "docker logout"
                           }
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
