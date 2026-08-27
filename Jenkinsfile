pipeline {
    agent {
        label 'zomato-jenkins-agent'
    }

    stages {

        stage('Verify Agent') {
            steps {
                sh 'echo Running on Kubernetes Jenkins Agent'
                sh 'hostname'
                sh 'java -version'
                sh 'mvn -version'
                sh 'ssh -V'
                sh 'scp -V || true'
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Maven Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Verify WAR') {
            steps {
                sh 'ls -lh target/'
                sh 'test -f target/devops-webapp.war'
            }
        }

    }

    post {
        success {
            echo 'Zomato Kubernetes CI Pipeline completed successfully.'
        }

        failure {
            echo 'Zomato Kubernetes CI Pipeline failed.'
        }
    }
}
