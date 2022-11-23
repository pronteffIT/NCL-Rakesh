pipeline {
    agent any
    stages {   
        stage('Build') {		
            steps {
		script{
                    env.APP_LIST = sh(script: "git diff --name-only HEAD HEAD~1 |  sed -E 's/^([A-Za-z0-9_]*)\\/(.*)/\\1/g'| grep '_APP'| sort | uniq | tr '\n' ':' ",returnStdout: true).trim()			
		    def appList = "${env.APP_LIST}"	
		    def fileName = "${env.WORKSPACE}"+"/BarfileOverrides/AppDeployment.properties"
	            echo "$fileName"
		    def props = readProperties  file:"$fileName"
		    if( appList == null || appList.indexOf("APP")<0 ){
		       echo "No App changed"	    
		       currentBuild.result = 'SUCCESS'
		       return
		    }
		    def firstApp = appList.substring(0,appList.indexOf(':'))
		    echo firstApp	
		    echo props[firstApp]
		    if( props[firstApp] == null){
		    	env.INT_SERVER_LIST='IS_APPS01'	
		    }else{
			env.INT_SERVER_LIST=props[firstApp]   
		    }
                }		  
                withAnt(installation: 'ANT') {
                    wrap([$class: 'Xvfb']) {	    
			    sh "ant -DAPP_LIST=${env.APP_LIST} -DBUILD_ENV=DEV -DINT_SERVER_LIST=${env.INT_SERVER_LIST} -DIIB_NODE_LIST=HDQDIBNODE01LV -DDELIMITER=\":\" -e -f /var/pbc/buildScripts/build_bootstrap_group.xml"
                    }
                }
            }
        }
    }
}
