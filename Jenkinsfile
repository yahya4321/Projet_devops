pipeline {
    agent any
    stages {
        stage(' Git') {
            steps {
                // Récupère le code depuis le dépôt Git
                git branch: 'Adam_branch', url: 'https://github.com/yahya4321/Projet_devops.git'
            }
        }
        stage('maven build') {
            steps {
                // Récupère le code depuis le dépôt Git
                sh "mvn clean install"
            }
        }

    }
}
