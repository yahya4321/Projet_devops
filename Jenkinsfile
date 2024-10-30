pipeline {
    agent any

    tools {
        maven 'M2_HOME'       // Assurez-vous que "M2_HOME" est le nom configuré pour Maven dans Jenkins
        jdk 'JAVA_HOME'       // Assurez-vous que "JAVA_HOME" est le nom configuré pour JDK dans Jenkins
    }

    environment {
        GIT_CREDENTIALS_ID = 'first_credentials' // Remplacez par l'ID de vos identifiants Git si l'authentification est nécessaire
        SONARQUBE_SERVER = 'SonarQube_Server'    // Nom de l'instance SonarQube configurée dans Jenkins
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

        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarQube Scanner' // Assurez-vous que "SonarQube Scanner" est le nom configuré pour SonarQube Scanner dans Jenkins
            }
            steps {
                withSonarQubeEnv('SonarQube_Server') { // Assurez-vous que le nom correspond à l'instance SonarQube configurée dans Jenkins
                    sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=Projet_devops -Dsonar.sources=src -Dsonar.host.url=http://your_sonarqube_server_url -Dsonar.login=your_sonarqube_token"
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml' // Publication des résultats des tests JUnit
        }

        success {
            echo 'Build, Test, and SonarQube Analysis completed successfully!'
        }

        failure {
            echo 'There was an error in the pipeline stages.'
        }
    }
}
