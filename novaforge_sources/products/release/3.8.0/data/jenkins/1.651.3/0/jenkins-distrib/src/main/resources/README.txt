*** Installation :

To deploy the patch novaforge-jenkins, you need to :
- extract the archive composed by jenkins.war and the jenkins directory.
- copy the webapps in the directory where you want to launch the application (the webapps contains its own servlet container).
- copy the directory jenkins where you want to store the jenkins datas.
- set the environment variable JENKINS_HOME to the path of the "jenkins" directory.
- launch the application with the command line : java -jar jenkins.war -Djavax.net.ssl.keyStore=/path/to/.keystore -Djavax.net.ssl.keyStorePassword=<keystore_pwd> -Djavax.net.ssl.trustStore=/path/to/.keystore -Djavax.net.ssl.trustStorePassword=<keystore_pwd>  (if you want to precise the port used by jenkins, you have to add the parameter--httpPort=$HTTP_PORT, the default port is 8080).
 or java -jar jenkins.war --httpPort=9999 --ajp13Port=8004 --prefix=/jenkins if you use Apache web server in front-end and a virtual host on /jenkins context.

*** Configuration (file config.xml) : 
The user "admin1" can do anything on Jenkins configuration. Change "admin" with the forge superadmin login to allow him to manage the application.
Every authenticated user have permissions to create new job.

To use the SSO authentication mechanism to be authenticated when you access Jenkins via the forge or from outside, you may have to change the CAS configuration url used for authentication:
<casServerUrl>https://localhost:8443/cas/</casServerUrl>.

Note that the CAS URL can be changed via the Jenkins Admin UI: you only need to have the admin rights on Jenkins.

*** Authentification : CAS plugin **
This distribution is using CAS plugin to manage sso : 
 - git : https://github.com/jenkinsci/cas-plugin
 - ci job : https://jenkins.ci.cloudbees.com/job/plugins/job/cas/

*** Features :
When a project choose to add Jenkins application, many things happen :
- All users of the  project have access to Jenkins.
- A default job template is created with the associated roles mapping. Its name is <PROJECT_ID>_<APPLICATION_LABEL>_default.
- To keep this roles mapping on the future created jobs, you just have to create them by copying the default project job template (with the job name <PROJECT_ID>_<APPLICATION_LABEL>_default) and to give them a job name which begins by <PROJECT_ID>_<APPLICATION_LABEL>.

Thanks to this mechanism, any change on roles mapping, any change on the membership (add or delete of a user) of the project will be propagated to all the project jobs.

When a project choose to delete its jenkins application :
- All the project jobs are deleted.

