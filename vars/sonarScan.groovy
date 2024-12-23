     def call(Map config = [:]) {
          def projectKey = config.projectKey
          def projectName = config.projectName
          def sonarHostUrl = config.sonarHostUrl

         withSonarQubeEnv('mySonarQube') {
             withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                sh """
                     ./gradlew sonar --stacktrace
                 """
                sh """
                   ./gradlew -Dsonar.login=\${SONAR_TOKEN} \
                    -Dsonar.projectKey=${projectKey} \
                    -Dsonar.projectName="${projectName}" \
                    -Dsonar.host.url=${sonarHostUrl}
                """
               }
            }
       }
