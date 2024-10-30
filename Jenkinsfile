pipeline {
    agent any

    tools {
        maven 'M2_HOME' // Assurez-vous que Maven est installé et configuré dans Jenkins
        jdk 'JAVA_HOME'  // Assurez-vous que JDK est installé et configuré dans Jenkins
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
                    credentialsId: 'first_credentials'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }


    }


}
