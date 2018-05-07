set RES_BASE=E:\Work\Workspace\J2EEcrm\yunn\quickstart\CustomerCentre\html
set LIB_HOME=%RES_BASE%\WEB-INF\lib

set CP=%LIB_HOME%\wade-container.jar
java -server -cp %CP% com.wade.container.util.security.Password 1234