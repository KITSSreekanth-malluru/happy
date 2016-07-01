echo 'Assemble EAR: ' $1


if ["$1" = ""] 
then
	echo 'Provide Build tag'
else
 echo 'Assemble ear started'

 rm -r castorama.ear

 $DYNAMO_HOME/bin/runAssembler -prependJars DP155373_lib.jar,PR_COMMERCE-168355_90p1_v1_lib.jar,DAF-168282v91.jar,PR126142_9.0p1_lib.jar,pr155078v90p1.jar,pr158096pr159452v90p1.jar,pr158142v90p1.jar,PR165171_9.0p1_lib_v2.jar,pr157166pr162315v90p1.jar,pr157152_pr160015_pr158170_pr160012_v2_lib.jar,pr142211v90p1.jar,PR_DAF-168065_90p1_v1_lib.jar -standalone -distributable -liveconfig castorama.ear -layer staging -m DafEar.Admin commerce.castorama.env.auxserver commerce.castorama.env.ca commerce.castorama.env.commerce commerce.castorama.env.callCenter commerce.castorama.env.ws commerce.castorama.env.integration commerce.castorama.env.slm PubPortlet DCS-UI DCS-UI.Search SearchAdmin.AdminUI
# $DYNAMO_HOME/bin/runAssembler -prependJars PR126142_9.0p1_lib.jar,pr155078v90p1.jar,pr158096pr159452v90p1.jar,pr158142v90p1.jar,PR165171_9.0p1_lib_v2.jar,pr157166pr162315v90p1.jar,pr157152_pr160015_pr158170_pr160012_v2_lib.jar -standalone -distributable -liveconfig castorama.ear -layer staging -m DafEar.Admin commerce.castorama.env.auxserver commerce.castorama.env.ca commerce.castorama.env.commerce commerce.castorama.env.callCenter commerce.castorama.env.ws commerce.castorama.env.integration commerce.castorama.env.slm PubPortlet DCS-UI DCS-UI.Search SearchAdmin.AdminUI DCS-CSR


 cp -r $DYNAMO_HOME/../commerce/castorama/castWS/mappingFiles /opt/CommerceApp/castorama.ear/atg_bootstrap.war/WEB-INF/ATG-INF/commerce/castorama/castWS

 cp -r /opt/CommerceApp/$1/install/docroot/documents/castorama_SearchAdminUI_jsps/castorama /opt/CommerceApp/castorama.ear/AdminUI.war
 
 echo 'Assemble ear is done'

fi

