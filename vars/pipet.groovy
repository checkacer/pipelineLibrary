import org.foo.Utils
def call(){
    pipeline {
        agent any
        environment {
            registry = "registry.cn-hangzhou.aliyuncs.com"
            gitCredentialsId = "a6600fad-d566-4408-b024-2d5e8ea29311"
            dockerCredential = "aliyun"
            branch = "master"
            repoUrl = "https://github.com/checkacer/runindockerdemo"
        }
        tools {
            maven 'maven'
            jdk 'jdk'
        }

        stages {
            def util = new Utils(steps)
            stage('test') {
                steps {
                    echo "Hello World"
                }
            }
        }

    }
}