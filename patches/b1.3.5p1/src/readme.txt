Build 1.3.5 PATCH 1

Content: 

1. Fix for the one step deployment flow on staging layer
2. Document schanner fix



#1. Fix for the one step deployment flow on staging layer
Steps:

(1) Copy the archived patch to CA server and untar the file, for example, to /home/b1.3.5p1

(2) On CA server, find EAR file which is used for CA jboss instance. Copy /home/b1.3.5p1/cast/Versioned/configlayers/stagingandprod/b1.3.5p1_config.jar 
    to {castorama.ear}/atg_bootstrap.war/WEB-INF/ATG-INF/commerce/castorama/cast/Versioned/configlayers/stagingandprod.
   
(3) Edit {castorama.ear}/atg_bootstrap.war/WEB-INF/ATG-INF/commerce/castorama/cast/Versioned/META-INF/MANIFEST.MF file:
    Append " configlayers/stagingandprod/b1.3.5p1_config.jar" to ATG-StagingConfig-Path. Patched line should look like
    ATG-StagingConfig-Path: configlayers/stagingandprod/config.jar configlayers/stagingandprod/b1.3.5p1_config.jar
   
(4) If you have already configured OneStepDeploymentWorkflow.wdl, remove OneStepDeploymentWorkflow.wdl 
    from {ATG-DATA}/servers/{ca_server_name}/localconfig/atg/registry/data/epubworkflows/Commerce/castorama

(5) Restart CA

(6) Run ACC and update CommerceOneStepDeploymentWorkflow.wdl


#2. Document schanner fix
Steps:

(1) Copy the archived patch to FO/Staging servers and untar the file, for example, to /home/b1.3.5p1
(2) On every FO/Staging server, find EAR file which is used for jboss instances. 
    Backup {castorama.ear}/atglib/_commerce.castorama.castSearch.Production_slib_sclasses.jar. 
    Copy /home/b1.3.5p1/castSearch/lib/_commerce.castorama.castSearch.Production_slib_sclasses.jar 
    to {castorama.ear}/atglib.
(3) Restart affected servers.


