def call(body) {
    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        // Variable inputs that modify the behavior of the job
        parameters {
            choice(name: 'architecture', choices: ['win-x64', 'win-x86'], description: 'Pick architecture:')
        }

        // Definition of env variables that can be used throughout the pipeline job
        environment {
			      OUTPUT_PATH = pipelineParams.projectPath?.toString()
            OUTPUT_FOLDER = "Build-${currentBuild.number}"
            //BAT_COMMAND = "dotnet publish ${pipelineParams.projectPath == null ? " " : OUTPUT_PATH} -c Release --runtime ${params.architecture} -o ${env.OUTPUT_FOLDER} "
        }

        options {
            timestamps()
            buildDiscarder(logRotator(numToKeepStr: '10'))
        }

        agent any

        stages {
            stage('Build') {
                steps {
                    script {
                        bat "dotnet --version"
                    }
                }
            }
        }

        //Any action we want to perform after all the steps have succeeded or failed
        post {
            success {
                echo "Success!"
                //archiveArtifacts artifacts: "${env.OUTPUT_FOLDER}/**/*", onlyIfSuccessful: true
                //bat "@RD /S /Q ${env.OUTPUT_FOLDER}" // remove directory
            }
            failure {
                echo "Failure!"
            }
        }
    }
}
