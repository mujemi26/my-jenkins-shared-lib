def call(Map config = [:]) {
   withCredentials([string(credentialsId: 'kind-kubeconfig', variable: 'KUBECONFIG')]) {
     def deploymentManifest = """
         apiVersion: apps/v1
         kind: Deployment
         metadata:
             name: ${config.DEPLOYMENT_NAME}
             labels:
                app: ${config.CONTAINER_NAME}
         spec:
           selector:
              matchLabels:
               app: ${config.CONTAINER_NAME}
            replicas: 1
          template:
               metadata:
                  labels:
                    app: ${config.CONTAINER_NAME}
                spec:
                   containers:
                     - name: ${config.CONTAINER_NAME}
                      image: "${config.DOCKER_IMAGE_NAME}:${config.DOCKER_IMAGE_VERSION}"
                      ports:
                         - containerPort: 8080
              """
      stage('Deploy to Kind Cluster') {
        steps {
           sh """
             echo "$KUBECONFIG".replaceAll('(?m)^ *certificate-authority-data:.*$', '') > kubeconfig.tmp
             kubectl apply --kubeconfig kubeconfig.tmp --insecure-skip-tls-verify -f - <<EOF
              ${deploymentManifest}
              EOF
           rm kubeconfig.tmp
         """
       }
    }
  }
}
