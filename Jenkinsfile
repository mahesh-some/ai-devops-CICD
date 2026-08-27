pipeline {
    agent {
        label 'zomato-jenkins-agent'
    }

    environment {
        DOCKER_REPO = 'somemahesh/zomato-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_IMAGE = "${DOCKER_REPO}:${DOCKER_TAG}"
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
                        echo "=========================================="
                        echo "Building Docker image with Kaniko"
                        echo "Image: ${DOCKER_IMAGE}"
                        echo "=========================================="

                        /kaniko/executor \
                          --context "${WORKSPACE}" \
                          --dockerfile "${WORKSPACE}/Dockerfile" \
                          --destination "${DOCKER_IMAGE}" \
                          --cache=false

                        echo "Docker image built and pushed successfully."
                        echo "Published image: ${DOCKER_IMAGE}"
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                container('jnlp') {
                    sh '''
                        echo "=========================================="
                        echo "Deploying Zomato application to Kubernetes"
                        echo "Image: ${DOCKER_IMAGE}"
                        echo "=========================================="

                        echo "Applying Kubernetes Service..."
                        kubectl apply -f k8s/zomato-service.yaml

                        echo "Applying Kubernetes Deployment..."
                        kubectl apply -f k8s/zomato-deployment.yaml

                        echo "Updating Deployment image..."
                        kubectl set image deployment/zomato \
                          zomato=${DOCKER_IMAGE}

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
                        echo "=========================================="
                        echo "KUBERNETES NODES"
                        echo "=========================================="
                        kubectl get nodes -o wide

                        echo "=========================================="
                        echo "ZOMATO DEPLOYMENT"
                        echo "=========================================="
                        kubectl get deployment zomato -o wide

                        echo "=========================================="
                        echo "ZOMATO PODS"
                        echo "=========================================="
                        kubectl get pods -l app=zomato -o wide

                        echo "=========================================="
                        echo "ZOMATO SERVICE"
                        echo "=========================================="
                        kubectl get service zomato -o wide

                        echo "=========================================="
                        echo "DEPLOYED IMAGE"
                        echo "=========================================="
                        kubectl get deployment zomato \
                          -o jsonpath='{.spec.template.spec.containers[0].image}'
                        echo

                        echo "=========================================="
                        echo "ROLLOUT STATUS"
                        echo "=========================================="
                        kubectl rollout status deployment/zomato --timeout=180s

                        echo "=========================================="
                        echo "ZOMATO KUBERNETES DEPLOYMENT VERIFIED"
                        echo "=========================================="
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Zomato Kubernetes CI/CD Pipeline completed successfully.'
            echo "Docker image deployed: ${DOCKER_IMAGE}"
        }

        failure {
            echo 'Zomato Kubernetes CI/CD Pipeline failed.'
        }
    }
}


