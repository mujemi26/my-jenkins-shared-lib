    def call(Map config = [:]) {
       def projectKey = config.projectKey
       def projectName = config.projectName
       def sonarHostUrl = config.sonarHostUrl

        withSonarQubeEnv('mySonarQube') {
          sh """
            ./gradlew sonar \
                -Dsonar.projectKey=${projectKey} \
                 -Dsonar.projectName="${projectName}" \
                 -Dsonar.host.url=${sonarHostUrl}
           """
        }
     }
