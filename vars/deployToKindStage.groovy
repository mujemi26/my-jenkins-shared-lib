#!/usr/bin/env groovy

def call(Map config) {
    // Extract parameters from config
    def imageName = config.DOCKER_IMAGE_NAME
    def imageVersion = config.DOCKER_IMAGE_VERSION
    def deploymentName = config.DEPLOYMENT_NAME
    def containerName = config.CONTAINER_NAME
    
    withKubeConfig([credentialsId: 'kind-kubeconfig']) {
        // Deploy using kubectl with insecure-skip-tls-verify
        sh """
            # Create or update the deployment
            kubectl create deployment ${deploymentName} \
                --image=${imageName}:${imageVersion} \
                --dry-run=client -o yaml | kubectl --insecure-skip-tls-verify apply -f -

            # Wait a bit for the deployment to be created
            sleep 5

            # Set the container name
            kubectl --insecure-skip-tls-verify set env deployment/${deploymentName} \
                CONTAINER_NAME=${containerName}

            # Expose the deployment as a service
            kubectl expose deployment ${deploymentName} \
                --port=8080 \
                --target-port=8080 \
                --type=NodePort \
                --dry-run=client -o yaml | kubectl --insecure-skip-tls-verify apply -f -

            # Wait for deployment to be ready
            kubectl --insecure-skip-tls-verify rollout status deployment/${deploymentName} --timeout=60s || true

            # Get deployment and service status
            echo "Checking deployment status..."
            kubectl --insecure-skip-tls-verify get deployment ${deploymentName}
            
            echo "\nChecking pods status..."
            kubectl --insecure-skip-tls-verify get pods -l app=${deploymentName}
            
            echo "\nChecking service status..."
            kubectl --insecure-skip-tls-verify get svc ${deploymentName}
        """
    }
}
