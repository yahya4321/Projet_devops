pipeline {
    agent any

    tools {
        maven 'Maven' // Assurez-vous que Maven est installé et configuré dans Jenkins
        jdk 'JDK11'  // Assurez-vous que JDK est installé et configuré dans Jenkins
    }

    environment {
        SONARQUBE_SERVER = 'SonarQube' // Remplacez par le nom du serveur SonarQube configuré dans Jenkins
        GIT_CREDENTIALS_ID = 'first_credentials' // Remplacez par l'ID de vos identifiants Git si l'authentification est nécessaire
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'Yahya_Branch_Bloc',
                    url: 'https://github.com/yahya4321/Projet_devops.git',
                    credentialsId: first_credentials
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(SONARQUBE_SERVER) {
                    sh 'mvn sonar:sonar'
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml' // Facultatif : publier les résultats des tests
        }
        success {
            echo 'Analyse SonarQube réussie.'
        }
        failure {
            echo 'Échec de l\'analyse SonarQube.'
        }
    }
}
