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
        steps.sh 'mvn package -Dmaven.test.skip=true'
    }
    // 生成镜像
    def buildImage(symbols, registry, dockerCredential, imageName) {
        symbols.docker.withRegistry("https://${registry}", "${dockerCredential}") {
            symbols.docker.build("${imageName}", '.').push()
        }
    }
}
