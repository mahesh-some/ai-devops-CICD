pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Verify WAR') {
            steps {
                sh 'ls -lh target/devops-webapp.war'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                sh '''
                    scp -o StrictHostKeyChecking=no \
                    -i /home/jenkins/.ssh/id_ed25519 \
                    target/devops-webapp.war \
                    deployer@3.110.204.155:/var/lib/tomcat10/webapps/
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    ssh -o StrictHostKeyChecking=no \
                    -i /home/jenkins/.ssh/id_ed25519 \
                    deployer@3.110.204.155 \
                    "sleep 5 && curl -f http://localhost:8080/devops-webapp/"
                '''
            }
        }
    }

    post {
        success {
            echo 'CI/CD deployment successful!'
        }

        failure {
            echo 'CI/CD pipeline failed.'
        }
    }
}
