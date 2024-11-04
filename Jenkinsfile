pipeline {
    agent any
    stages {
        stage('Git') {
            steps {
                // Récupère le code depuis le dépôt Git
                git branch: 'Adam_branch', url: 'https://github.com/yahya4321/Projet_devops.git'
            }
        }
        stage('Maven Build') {
            steps {
                // Compile le projet avec Maven
                sh "mvn clean install"
            }
        }
        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarQube Scanner'
            }
            steps {
                withSonarQubeEnv('SonarQube_Server') {
                    sh """
                        ${scannerHome}/bin/sonar-scanner \
                        -Dsonar.projectKey=Projet_devops \
                        -Dsonar.sources=src \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.host.url=http://192.168.50.4:9000 \
                        -Dsonar.login=sqa_b103f48152642382edd93eff08781c96f2dbcb17
                    """
                }
            }
        }
        stage('Mockito') {
                    steps {
                        sh 'mvn test'
                    }
        }
        stage('Nexus') {
            steps {
                script {
                    // Remplacez les valeurs par vos configurations spécifiques
                    def nexusUrl = "http://192.168.50.4:8081"
                    def artifactId = "tp-foyer"
                    def version = "5.0.0"
                    def groupId = "tn.esprit"

                    // Déployer l'artifact dans Nexus
                    sh """
                        mvn deploy:deploy-file -DgroupId=${groupId} \
                        -DartifactId=${artifactId} \
                        -Dversion=${version} \
                        -Dpackaging=jar \
                        -Dfile=target/${artifactId}-${version}.jar \
                        -DrepositoryId=deploymentRepo \
                        -Durl=${nexusUrl}/repository/maven-releases/

                    """
                }
            }
        }
        stage('Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${env.APP_VERSION} --build-arg JAR_FILE=tp-foyer-${env.APP_VERSION}.jar ."
                }
            }
        }

    }
}
