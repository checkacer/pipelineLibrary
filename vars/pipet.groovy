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
        stages {
            def comm = new org.foo.Utils(steps)
            stage('CheckOut') {
                steps {
                    comm.checkOutFromGit("${map.branch}","${gitCredentialsId}","${map.repoUrl}")
                }
            }
            stage('编译&单元测试') {
                steps {
                    comm.packageAndJunit()
                }
            }
            stage('生成镜像') {
                steps {
                    script {
                        comm.buildImage("${registry}","${dockerCredentialsId}","${imageName}")
                    }
                }
            }
        }
    }
}