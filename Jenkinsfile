pipeline {
    agent any

    tools {
        maven 'M2_HOME'       // Assurez-vous que "M2_HOME" est le nom configuré pour Maven dans Jenkins
        jdk 'JAVA_HOME'       // Assurez-vous que "JAVA_HOME" est le nom configuré pour JDK dans Jenkins
    }

    environment {
        GIT_URL = 'https://github.com/yahya4321/Projet_devops.git'
        GIT_BRANCH = 'Firas_Univer'
        CREDENTIALS_ID = 'GitHub_Credentials'
        SONAR_TOKEN = credentials('sonar_token')
        DOCKER_CREDENTIALS_ID = 'Docker_Credentials'
        DOCKER_IMAGE_NAME = 'projetdevops/alpine'
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Cloner le dépôt depuis la branche spécifiée en utilisant les identifiants
                git branch: "${env.GIT_BRANCH}",
                    url: "${env.GIT_URL}",
                    credentialsId: "${env.CREDENTIALS_ID}"
            }
        }

        stage('Get Version') {
            steps {
                script {
                    // Extraire la version du fichier pom.xml et la stocker dans une variable d'environnement
                    env.APP_VERSION = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                    echo "Application version: ${env.APP_VERSION}"
                }
            }
        }

        stage('Build') {
            steps {
                // Compilation du projet Maven
                sh 'mvn clean compile'
            }
        }
         stage('Docker Build and Push') {
             steps {
                 script {
                     // Construire l'image Docker en utilisant le fichier Dockerfile
                     def jarFile = sh(script: "ls target/*.jar | head -n 1", returnStdout: true).trim()
                     def imageName = "${env.DOCKER_IMAGE_NAME}:${env.APP_VERSION}"

                     // Construire l'image Docker en passant le fichier JAR en tant qu'argument
                     sh "docker build --build-arg JAR_FILE=${jarFile} -t ${imageName} ."

                     // Connexion au registre Docker
                     withCredentials([usernamePassword(credentialsId: "${env.DOCKER_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                         sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                     }

                     // Pousser l'image Docker vers le registre
                     sh "docker push ${imageName}"
                 }
             }
         }

         stage('Mockito Tests') {
                    steps {
                        // Exécution des tests unitaires Mockito avec Maven
                        sh 'mvn test '
                    }
                }

        stage('Test') {
            steps {
                // Exécution des tests unitaires avec Maven
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                // Empaquetage de l'application Maven
                sh 'mvn package'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Analyse SonarQube
                sh 'mvn sonar:sonar'
            }
        }

        stage('Clean') {
            steps {
                // Nettoyage du projet Maven
                sh 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                // Compilation du projet Maven
                sh 'mvn compile'
            }
        }
    }

    post {
        always {
            // Archive les artefacts de build (par exemple, fichiers JAR ou WAR) pour les récupérer dans Jenkins
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
