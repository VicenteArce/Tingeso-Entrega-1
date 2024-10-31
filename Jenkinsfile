pipeline{
    agent any
    tools{
        maven "maven"

    }
    stages{
        stage("Build JAR File"){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/VicenteArce/Tingeso-Entrega-1']])
                dir("Backend"){
                    bat "mvn clean install"
                }
            }
        }
        stage("Test"){
            steps{
                dir("Backend"){
                    bat "mvn test"
                }
            }
        }        
        stage("Build and Push Docker Image"){
            steps{
                dir("Backend"){
                    script{
                         withDockerRegistry(credentialsId: 'docker-credentials', url: '') {
                            bat "echo ${env.DOCKER_PASSWORD} | docker login -u ${env.DOCKER_USERNAME} --password-stdin"
                            bat "docker build -t vicentearce/prestabanco-backend ."
                            bat "docker push vicentearce/prestabanco-backend"
                        }
                    }                    
                }
            }
        }
    }
}
