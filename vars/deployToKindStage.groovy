def call(Map config = [:]) {
    withCredentials([string(credentialsId: 'kind-kubeconfig', variable: 'KUBECONFIG')]) {
          def deploymentManifest = """
                   apiVersion: apps/v1
                    kind: Deployment
                    metadata:
                       name: "${env.DEPLOYMENT_NAME}"
                    spec:
                       selector:
                           matchLabels:
                             app: "${env.CONTAINER_NAME}"
                        replicas: 1
                    template:
                      metadata:
                          labels:
                             app: "${env.CONTAINER_NAME}"
                       spec:
                           containers:
                              - name: "${env.CONTAINER_NAME}"
                                image: "${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_VERSION}"
                                ports:
                                   - containerPort: 8080
                    """
              stage('Deploy to Kind') {
                  steps {
                     sh """
                        echo "${deploymentManifest}" > deployment.yaml
                        kubectl apply --kubeconfig <(echo "\${KUBECONFIG}".replaceAll('(?m)^ *certificate-authority-data:.*$', '')) --insecure-skip-tls-verify -f deployment.yaml
                        rm deployment.yaml
                      """
                     }
                  }
              }
          }
