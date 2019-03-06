class command implements Serializable {
// 下载代码
    def checkOutFromGit(branch,credentialsId,repo) {
        git branch: "${branch}", credentialsId: "${credentialsId}", url: "${repo}"
    }
// 编译&单元测试
    def packageAndJunit(){
        sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package dependency:copy-dependencies -U -Pproduction -Dmaven.test.failure.ignore'
    }
// 代码审查
    def codeReview(sonarQubeScannerHome){
        withSonarQubeEnv('SonarQube') {
            sh "${sonarQubeScannerHome}/bin/sonar-scanner"
        }
    }
// 生成镜像
    def buildImage(repo,credential,imageName){
        docker.withRegistry("https://${repo}","${credential}") {
            docker.build("${imageName}",'.').push()
        }
    }
// 依赖安全检查
    def dependencyCheck(scanPath){
        dependencyCheckAnalyzer datadir: '',
            hintsFile: '',
            includeCsvReports: false,
            includeHtmlReports: false,
            includeJsonReports: false,
            isAutoupdateDisabled: false,
            outdir: '',
            scanpath: "${scanPath}",
            skipOnScmChange: false,
            skipOnUpstreamChange: false,
            suppressionFile: '',
            zipExtensions: ''
        // 有高级别组件漏洞时，fail掉pipeline
        dependencyCheckPublisher canComputeNew: false,
            defaultEncoding: '',
            failedTotalHigh: '0',
            healthy: '',
            pattern: '',
            unHealthy: '',
            unstableTotalHigh: '1',
            unstableTotalLow: '10',
            unstableTotalNormal: '5',
//            failedTotalHigh: '20',
            failedTotalLow: '100',
            failedTotalNormal: '50'
    }
// 部署到k8s集群
    def applyK8s(yaml,tag){
        sh "cat ${yaml} | sed 's/{tag}/${tag}/' | kubectl --kubeconfig /jenkins/.kube/config apply -f -"
    }
    def junit(url){
        junit "${url}"
    }
    def apiTest(path,postmanCJ,postmanEJ,reporterEXml,reporterEHtml,reporterTHtml,reportName,reportTitles){
        try{
            sh "export PATH=$path:/jenkins/tools/node-v8.11.2-linux-x64/bin/&&" +
                "newman run ${postmanCJ} " +
                "-e ${postmanEJ} " +
                "--reporters html,cli,junit " +
                "--reporter-junit-export ${reporterEXml} "+
                "--reporter-html-export ${reporterEHtml} " +
                "--reporter-html-template ${reporterTHtml}"
        } catch (e) {
            echo 'API测试出现错误' + e.toString()
        }
        publishHTML([allowMissing: false,
                     alwaysLinkToLastBuild: true,
                     keepAll: false,
                     reportDir: './',
                     reportFiles: 'reporter.html',
                     reportName: "${reportName}",
                     reportTitles: "${reportTitles}"])
    }
    def static  echo(){
        echo 'class echo ...'
    }
}