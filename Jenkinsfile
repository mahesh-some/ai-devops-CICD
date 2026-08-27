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

        stage('Deploy to Kubernetes') {
            steps {
                container('jnlp') {
                    sh '''
                        echo "Deploying Zomato application to Kubernetes..."

                        kubectl apply -f k8s/zomato-deployment.yaml
                        kubectl apply -f k8s/zomato-service.yaml

                        echo "Waiting for deployment rollout..."
                        kubectl rollout status deployment/zomato --timeout=180s

                        echo "Kubernetes deployment completed successfully."
                    '''
                }
            }
        }

        stage('Verify Kubernetes Deployment') {
            steps {
                container('jnlp') {
                    sh '''
                        echo "=== Kubernetes Nodes ==="
                        kubectl get nodes -o wide

                        echo "=== Zomato Deployment ==="
                        kubectl get deployment zomato

                        echo "=== Zomato Pods ==="
                        kubectl get pods -l app=zomato -o wide

                        echo "=== Zomato Service ==="
                        kubectl get service zomato

                        echo "=== Zomato Image ==="
                        kubectl get deployment zomato \
                          -o jsonpath='{.spec.template.spec.containers[0].image}'
                        echo
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Zomato Kubernetes CI/CD Pipeline completed successfully.'
        }

        failure {
            echo 'Zomato Kubernetes CI/CD Pipeline failed.'
        }
    }
}
