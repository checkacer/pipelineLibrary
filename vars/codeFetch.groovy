#!groovy 
def call(String a){
   if(a == "a"){
     echo "starting fetch code......"
   }else{
     echo "-----------------bbb"
   }
   pipeline {
     agent any
     environment {
       abc = "abc"
     }
     tools {
       jdk 'jdk'
     }
     stages {
       stage('in library') {
         steps {
	   echo "++++++++ in ++++++"
	 }       
       }
     }
   }
}
