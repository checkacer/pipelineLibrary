#!groovy 
def call(String a){
   if(a == "a"){
     echo "starting fetch code......"
   }else{
     echo "-----------------bbb"
   }
   node {
     stage("in groovy 1") {
       echo "-----------111"
     }
     stage("in groovy 2") {
       echo "-----------2222"
     }
     stage("in groovy 3") {
       echo "-----------3333"
     }
   }
}
