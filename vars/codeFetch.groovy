#!groovy 
def call(String a){
   if(a == "a"){
     echo "starting fetch code......"
   }else{
     echo "-----------------bbb"
   }
   node {
     def mvnHome
     def jdkHome
     stage('Preparation') { 
        git credentialsId: '13735461-01df-48bf-85cc-373338e73227', url: 'https://github.com/checkacer/runindockerdemo'      
        mvnHome = tool 'maven'
        jdkHome = tool 'jdk'
     }
     stage('Package') {
        sh "'${mvnHome}/bin/mvn' package"
     }
      
     stage('Results') {
        archive 'target/*.jar'
	junit 'target/surefire-reports/TEST-*.xml'
     }
  }
}
