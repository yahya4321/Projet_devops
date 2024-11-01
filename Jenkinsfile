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

        stage('Test') {
            steps {
                // Exécution des tests unitaires avec Maven, y compris les tests Mockito
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                // Empaquetage de l'application Maven
                sh 'mvn package'
            }
        }
        stage('Sonarqube') {
                    steps {
                        // Empaquetage de l'application Maven
                        sh 'mvn Sonarqube'
                    }
                }
        stage('Clean') {
                    steps {
                        // Empaquetage de l'application Maven
                        sh 'mvn Clean'
                    }
                }
       stage('Compile') {
                   steps {
                       // Empaquetage de l'application Maven
                       sh 'mvn Compile'
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
