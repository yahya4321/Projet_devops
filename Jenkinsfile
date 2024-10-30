pipeline {
    agent any

    tools {
        maven 'M2_HOME'       // Assurez-vous que "M2_HOME" est le nom configuré pour Maven dans Jenkins
        jdk 'JAVA_HOME'       // Assurez-vous que "JAVA_HOME" est le nom configuré pour JDK dans Jenkins
    }

    environment {
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
                sh 'mvn clean install' // Effectue la construction de votre projet
            }
        }
    }
}
