 def call(Map config = [:]) {
    def imageName = config.imageName
     def version = config.version
    def deploymentName = config.deploymentName
    def containerName = config.containerName
   
    withCredentials([string(credentialsId: 'kind-kubeconfig', variable: 'KUBECONFIG')]) {
       sh """
          echo "\${KUBECONFIG}".replaceAll('"', '\\\\"') > kubeconfig.tmp
             kubectl apply --kubeconfig kubeconfig.tmp -f - <<EOF
              apiVersion: apps/v1
             kind: Deployment
              metadata:
                name: ${deploymentName}
             spec:
               selector:
                  matchLabels:
                     app: ${containerName}
                replicas: 1
              template:
                 metadata:
                   labels:
                     app: ${containerName}
               spec:
                 containers:
                    - name: ${containerName}
                      image: "${imageName}:${version}"
                    ports:
                      - containerPort: 8080
           EOF
          rm kubeconfig.tmp
         """
        }
  }
