<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE process SYSTEM "dynamosystemresource:/atg/dtds/pdl/pdl_1.0.dtd">
<process author="admin" creation-time="1254247066824" enabled="true" last-modified-by="admin" modification-time="1254247092417">
  <segment migrate-subjects="true">
    <segment-name>main</segment-name>
    <!--================================-->
    <!--== startWorkflow  -->
    <!--================================-->
    <event id="1">
      <event-name>atg.workflow.StartWorkflow</event-name>
      <filter operator="eq">
        <event-property>
          <property-name>processName</property-name>
        </event-property>
        <constant>/Commerce/castorama/OneStepDeploymentWorkflow.wdl</constant>
      </filter>
      <filter operator="eq">
        <event-property>
          <property-name>segmentName</property-name>
        </event-property>
        <constant>main</constant>
      </filter>
      <attributes>
        <attribute name="atg.workflow.elementType">
          <constant>startWorkflow</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubUser:execute;Admin$role$administrators-group:execute;Profile$role$epubSuperAdmin:execute;Profile$role$epubManager:execute;Admin$role$managers-group:execute;Profile$role$epubAdmin:execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>A basic workflow which focuses on creating and editing commerce assets. Deploys to production target.</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Manage Commerce Assets</constant>
        </attribute>
      </attributes>
    </event>
    <!--================================-->
    <!--== Create project without a workflow and process' project name  -->
    <!--================================-->
    <action id="2">
      <action-name>createProjectForProcess</action-name>
    </action>
    <!--================================-->
    <!--== author  -->
    <!--================================-->
    <label id="3">
      <attributes>
        <attribute name="atg.workflow.assignable">
          <constant type="java.lang.Boolean">true</constant>
        </attribute>
        <attribute name="atg.workflow.elementType">
          <constant>task</constant>
        </attribute>
        <attribute name="atg.workflow.acl">
          <constant>Profile$role$epubManager:write,execute;Profile$role$epubUser:write,execute;Admin$role$administrators-group:write,execute;Admin$role$managers-group:write,execute;Profile$role$epubSuperAdmin:write,execute;Profile$role$epubAdmin:write,execute</constant>
        </attribute>
        <attribute name="atg.workflow.description">
          <constant>Create and modify assets for eventual deployment</constant>
        </attribute>
        <attribute name="atg.workflow.name">
          <constant>author</constant>
        </attribute>
        <attribute name="atg.workflow.displayName">
          <constant>Author</constant>
        </attribute>
      </attributes>
    </label>
    <fork exclusive="true" id="4">
      <branch id="4.1">
        <!--================================-->
        <!--== review  -->
        <!--================================-->
        <event id="4.1.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Commerce/castorama/OneStepDeploymentWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>4.1.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Assets are ready for approval</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>review</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Ready for Review</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Change Project's Current project's Editable to false  -->
        <!--================================-->
        <action id="4.1.2">
          <action-name construct="modify-action">modify</action-name>
          <action-param name="modified">
            <subject-property>
              <property-name>project</property-name>
              <property-name>editable</property-name>
            </subject-property>
          </action-param>
          <action-param name="operator">
            <constant>assign</constant>
          </action-param>
          <action-param name="modifier">
            <constant type="java.lang.Boolean">false</constant>
          </action-param>
        </action>
        <!--================================-->
        <!--== Check assets are up to date  -->
        <!--================================-->
        <action id="4.1.3">
          <action-name>assetsUpToDate</action-name>
        </action>
        <!--================================-->
        <!--== Approve and deploy project to target Castorama-Site(Local Dev environment)  -->
        <!--================================-->
        <action id="4.1.4">
          <action-name>approveAndDeployProject</action-name>
          <action-param name="target">
            <constant>TARGET_NAME</constant>
          </action-param>
        </action>
        <!--================================-->
        <!--== waitForDeploymentToComplete  -->
        <!--================================-->
        <label id="4.1.5">
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>task</constant>
            </attribute>
            <attribute name="atg.workflow.assignable">
              <constant type="java.lang.Boolean">false</constant>
            </attribute>
            <attribute name="atg.workflow.acl">
              <constant>Profile$role$epubAdmin:write,execute;Profile$role$epubSuperAdmin:write,execute;Admin$role$administrators-group:write,execute</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Wait for deployment to complete on Production target</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>waitForDeploymentToComplete</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Wait for Production Deployment Completion</constant>
            </attribute>
          </attributes>
        </label>
        <fork exclusive="true" id="4.1.6">
          <branch id="4.1.6.1">
            <!--================================-->
            <!--== Wait for deployment to complete on target Castorama-Site(Local Dev environment)  -->
            <!--================================-->
            <event id="4.1.6.1.1">
              <event-name>atg.deployment.DeploymentStatus</event-name>
              <filter operator="eq">
                <event-property>
                  <property-name>targetId</property-name>
                </event-property>
                <constant>TARGET_NAME</constant>
              </filter>
            </event>
            <fork id="4.1.6.1.2">
              <branch id="4.1.6.1.2.1">
                <!--================================-->
                <!--== Deployment completed event status is success on target Castorama-Site(Local Dev environment)  -->
                <!--================================-->
                <condition id="4.1.6.1.2.1.1">
                  <filter operator="deploymentCompleted">
                    <constant>1</constant>
                    <constant>TARGET_NAME</constant>
                  </filter>
                </condition>
                <!--================================-->
                <!--== Validate project is deployed on target Castorama-Site(Local Dev environment)  -->
                <!--================================-->
                <action id="4.1.6.1.2.1.2">
                  <action-name>validateProjectDeployed</action-name>
                  <action-param name="target">
                    <constant>TARGET_NAME</constant>
                  </action-param>
                </action>
                <!--================================-->
                <!--== Check in project's workspace  -->
                <!--================================-->
                <action id="4.1.6.1.2.1.3">
                  <action-name>checkInProject</action-name>
                </action>
                <!--================================-->
                <!--== Complete project  -->
                <!--================================-->
                <action id="4.1.6.1.2.1.4">
                  <action-name>completeProject</action-name>
                </action>
                <!--================================-->
                <!--== Complete process  -->
                <!--================================-->
                <action id="4.1.6.1.2.1.5">
                  <action-name>completeProcess</action-name>
                </action>
              </branch>
              <branch id="4.1.6.1.2.2">
                <!--================================-->
                <!--== Deployment completed event status is failure on target Castorama-Site(Local Dev environment)  -->
                <!--================================-->
                <condition id="4.1.6.1.2.2.1">
                  <filter operator="deploymentCompleted">
                    <constant>0</constant>
                    <constant>TARGET_NAME</constant>
                  </filter>
                </condition>
                <!--================================-->
                <!--== Release asset locks  -->
                <!--================================-->
                <action id="4.1.6.1.2.2.2">
                  <action-name>releaseAssetLocks</action-name>
                </action>
                <jump id="4.1.6.1.2.2.3" target="3"/>
              </branch>
            </fork>
          </branch>
        </fork>
      </branch>
      <branch id="4.2">
        <!--================================-->
        <!--== delete  -->
        <!--================================-->
        <event id="4.2.1">
          <event-name>atg.workflow.TaskOutcome</event-name>
          <filter operator="eq">
            <event-property>
              <property-name>processName</property-name>
            </event-property>
            <constant>/Commerce/castorama/OneStepDeploymentWorkflow.wdl</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>segmentName</property-name>
            </event-property>
            <constant>main</constant>
          </filter>
          <filter operator="eq">
            <event-property>
              <property-name>outcomeElementId</property-name>
            </event-property>
            <constant>4.2.1</constant>
          </filter>
          <attributes>
            <attribute name="atg.workflow.elementType">
              <constant>outcome</constant>
            </attribute>
            <attribute name="atg.workflow.description">
              <constant>Delete the project</constant>
            </attribute>
            <attribute name="atg.workflow.name">
              <constant>delete</constant>
            </attribute>
            <attribute name="atg.workflow.displayName">
              <constant>Delete Project</constant>
            </attribute>
          </attributes>
        </event>
        <!--================================-->
        <!--== Delete project  -->
        <!--================================-->
        <action id="4.2.2">
          <action-name>deleteProject</action-name>
        </action>
      </branch>
    </fork>
  </segment>
</process>
