#!/usr/bin/env groovy

def call(Map config) {
    // Extract parameters from config
    def imageName = config.DOCKER_IMAGE_NAME
    def imageVersion = config.DOCKER_IMAGE_VERSION
    def deploymentName = config.DEPLOYMENT_NAME
    def containerName = config.CONTAINER_NAME
    
    // Use withCredentials instead of withKubeConfig
    withCredentials([file(credentialsId: 'kind-kubeconfig', variable: 'KUBECONFIG')]) {
        // Deploy using kubectl
        sh """
            # Verify kubectl can connect
            kubectl --kubeconfig=\$KUBECONFIG cluster-info
            
            # Create or update the deployment
            kubectl --kubeconfig=\$KUBECONFIG create deployment ${deploymentName} \
                --image=${imageName}:${imageVersion} \
                --dry-run=client -o yaml | kubectl --kubeconfig=\$KUBECONFIG apply -f -

            # Update the container name if it exists
            kubectl --kubeconfig=\$KUBECONFIG set env deployment/${deploymentName} \
                CONTAINER_NAME=${containerName}

            # Expose the deployment as a service
            kubectl --kubeconfig=\$KUBECONFIG expose deployment ${deploymentName} \
                --port=8080 \
                --target-port=8080 \
                --type=NodePort \
                --dry-run=client -o yaml | kubectl --kubeconfig=\$KUBECONFIG apply -f -

            # Wait for deployment to be ready
            kubectl --kubeconfig=\$KUBECONFIG rollout status deployment/${deploymentName}
            
            # Get deployment status
            echo "Deployment Status:"
            kubectl --kubeconfig=\$KUBECONFIG get deployment ${deploymentName}
            
            # Get service details
            echo "Service Status:"
            kubectl --kubeconfig=\$KUBECONFIG get service ${deploymentName}
        """
    }
}
