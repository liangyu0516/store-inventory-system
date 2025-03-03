pipeline {
    agent any

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/liangyu0516/store-inventory-system'
            }
        }

        stage('Run Tests for Microservices') {
            parallel {
                stage('Test Catalog Service') {
                    steps {
                        dir('catalog') {
                            sh 'mvn test'
                        }
                    }
                }

                stage('Test Order Service') {
                    steps {
                        dir('order') {
                            sh 'mvn test'
                        }
                    }
                }

                stage('Test Frontend Service') {
                    steps {
                        dir('frontend') {
                            sh 'mvn test'
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/test/TEST-*.xml'
        }
        failure {
            echo 'One or more tests failed!'
        }
    }
}
