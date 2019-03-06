#!groovy 
import org.foo.Command
def call(String giturl){
    if(giturl == "a"){
        echo "starting fetch code......"
    }else{
        echo "-----------------bbb"
    }
    tools {
        maven 'maven'
	jdk 'jdk'
    }
    def comm = new org.foo.Command(steps)
    comm.packageAndJunit()
}
