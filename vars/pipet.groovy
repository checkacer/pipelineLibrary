#!groovy
import org.foo.Utils
def call(Map map){
    pipeline {
        agent any
        environment {
            tag = "v0.3.${BUILD_NUMBER}"
            registry = "registry.cn-hangzhou.aliyuncs.com"
            imageName = "dcits/i-test:${tag}"
            gitCredentialsId = "a6600fad-d566-4408-b024-2d5e8ea29311"
            dockerCredential = "aliyun"
        }
        tools {
            maven 'maven'
            jdk 'jdk'
        }
        def utils = new Utils(steps)
        stage('build') {
            utils.checkOutFromGit("${map.branch}","${gitCredentialsId}","${map.repoUrl}")
        }

    }
}