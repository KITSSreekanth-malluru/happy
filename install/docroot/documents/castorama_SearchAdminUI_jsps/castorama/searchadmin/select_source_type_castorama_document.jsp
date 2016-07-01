<%@ include file="/castorama/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="handler" var="handler"/>
  <d:getvalueof param="type" var="type"/>

  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>                                                                                         
      <tr>
        <td class="label">
          <span id="<c:out value='settings.${type.sourceTypeInternalName}PathAlert'/>"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
          <fmt:message key="select_source_type.castoramadocument.path"/>
        </td>
        <td>
          <d:input type="text" iclass="textField" name="settings.${type.sourceTypeInternalName}Path"
                   bean="${handler}.settings.${type.sourceTypeInternalName}Path"/>
        </td>
      </tr>
      <tr>
        <td class="label">
        </td>
        <td>
          <table cellspacing="0" cellpadding="0" width="100%">
            <tbody>
              <tr>
                <td width="20%" align="right"><span
                    id="<c:out value='settings.${type.sourceTypeInternalName}HostMachineAlert'/>"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
                  <fmt:message key="select_source_type.castoramadocument.host.machine"/></td>
                <td>
                    <d:input type="text" name="settings.${type.sourceTypeInternalName}HostMachine" iclass="textField"
                           bean="${handler}.settings.${type.sourceTypeInternalName}HostMachine"/>
                </td>
              </tr>
              <tr>
                <td width="20%" align="right">
                  <span id="<c:out value='settings.${type.sourceTypeInternalName}PortAlert'/>"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
                  <fmt:message key="select_source_type.castoramadocument.port"/>
                </td>
                <td>
                  <d:input type="text" name="settings.${type.sourceTypeInternalName}Port" iclass="textField"
                           bean="${handler}.settings.${type.sourceTypeInternalName}Port"/>
                </td>
              </tr>
            </tbody>
          </table>
        </td>
      </tr>
    </tbody>
  </table>  
</d:page>
