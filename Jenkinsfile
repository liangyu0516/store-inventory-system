pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/liangyu0516/store-inventory-system'
            }
        }

        stage('Test') {
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
}
