pipeline {

    agent {
    label 'jenkins-agent'
}

    environment {
        TOMCAT_HOST = '13.233.145.247'
        TOMCAT_USER = 'deployer'
        TOMCAT_WEBAPPS = '/var/lib/tomcat10/webapps'
        SSH_KEY = '/home/jenkins/.ssh/id_ed25519'
        APP_NAME = 'devops-webapp'
    }

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
                sh 'ls -lh target/${APP_NAME}.war'
            }
        }

        stage('Test SSH Connection') {
            steps {
                sh '''
                    ssh -o BatchMode=yes \
                    -o StrictHostKeyChecking=no \
                    -i ${SSH_KEY} \
                    ${TOMCAT_USER}@${TOMCAT_HOST} \
                    "whoami && hostname"
                '''
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                sh '''
                    scp -o BatchMode=yes \
                    -o StrictHostKeyChecking=no \
                    -i ${SSH_KEY} \
                    target/${APP_NAME}.war \
                    ${TOMCAT_USER}@${TOMCAT_HOST}:${TOMCAT_WEBAPPS}/
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    ssh -o BatchMode=yes \
                    -o StrictHostKeyChecking=no \
                    -i ${SSH_KEY} \
                    ${TOMCAT_USER}@${TOMCAT_HOST} \
                    "sleep 5 && \
                    test -f ${TOMCAT_WEBAPPS}/${APP_NAME}.war && \
                    curl -f http://localhost:8080/${APP_NAME}/"
                '''
            }
        }
    }

    post {

        success {
            echo '========================================='
            echo 'CI/CD deployment successful!'
            echo 'Application deployed to Tomcat.'
            echo '========================================='
        }

        failure {
            echo '========================================='
            echo 'CI/CD pipeline failed!'
            echo 'Check the Jenkins console output.'
            echo '========================================='
        }
    }
}
