pipeline {
    agent {
        label 'zomato-jenkins-agent'
    }

    environment {
        DOCKER_IMAGE = 'somemahesh/zomato-app:1.0'
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

        stage('Build & Push Docker Image') {
            steps {
                container('kaniko') {
                    sh '''
                        echo "Building Docker image with Kaniko..."
                        echo "Image: ${DOCKER_IMAGE}"

                        /kaniko/executor \
                          --context "${WORKSPACE}" \
                          --dockerfile "${WORKSPACE}/Dockerfile" \
                          --destination "${DOCKER_IMAGE}" \
                          --cache=false

                        echo "Docker image built and pushed successfully."
                    '''
                }
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
