package org.foo

class Utilities implements Serializable{
    def steps
    def setSteps(steps){
        this.steps = steps
    }
    // 下载代码
    def checkOutFromGit(branch, gitCredentialsId, repoUrl) {
        steps.git branch: "${branch}", credentialsId: "${gitCredentialsId}", url: "${repoUrl}"
    }
    // 编译&单元测试
    def packageAndJunit() {
        steps.sh 'mvn org.jacoco:jacoco-maven-plugin:prepare-agent package dependency:copy-dependencies -U -Pproduction -Dmaven.test.failure.ignore'
    }
    // 生成镜像
    def buildImage(registry, dockerCredential, imageName) {
        steps.docker.withRegistry("https://${registry}", "${dockerCredential}") {
            steps.docker.build("${imageName}", '.').push()
        }
    }
}
