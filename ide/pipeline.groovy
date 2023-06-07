pipeline {
    agent any
    triggers { pollSCM('* * * * *') }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/pioquel/jgsu-spring-petclinic.git', branch: 'main'
            }
        }
        stage('Build') {
            steps {
                sh './mvnw clean package'
                // sh 'false'
            }

            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }

                changed {
                    emailext subject: "Job \'${JOB_NAME}\' (buil ${BUILD_NUMBER}) ${currentBuild.result}",
                        body: "Please go to ${BUILD_URL} and verify the build",
                        compressLog: true,
                        attachLog: true, 
                        to: "test@jenkings",
                        recipientProviders: [upstreamDevelopers(), requestor()]
                }
            }
        }
    }
}
