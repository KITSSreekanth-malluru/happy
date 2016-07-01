echo 'Assemble ear started'
if [ -e castorama.ear ]
then 
  echo remove castorama.ear directory
  rm -r castorama.ear
fi

pj=
for file in `find $2 -name *.jar*`
do
  if [ -f $file ]
  then
    full_file=`pwd $file`/$file
    pj=$pj$full_file,
  fi
done

echo prependJars=$pj
echo $DYNAMO_HOME/bin/runAssembler -prependJars $pj -standalone  -liveconfig castorama.ear -layer staging -m DafEar.Admin commerce.castorama.env.auxserver commerce.castorama.env.ca commerce.castorama.env.commerce commerce.castorama.env.callCenter commerce.castorama.env.ws commerce.castorama.env.integration commerce.castorama.env.slm commerce.castorama.env.iphone commerce.castorama.env.iphoneVersioned PubPortlet DCS-UI DCS-UI.Search SearchAdmin.AdminUI
  

  $DYNAMO_HOME/bin/runAssembler -standalone -distributable   -liveconfig castorama.ear -layer staging -m DafEar.Admin commerce.castorama.env.auxserver commerce.castorama.env.ca commerce.castorama.env.commerce commerce.castorama.env.callCenter commerce.castorama.env.ws commerce.castorama.env.integration commerce.castorama.env.slm commerce.castorama.env.iphone commerce.castorama.env.iphoneVersioned PubPortlet DCS-UI DCS-UI.Search SearchAdmin.AdminUI

  cp -r $DYNAMO_HOME/../commerce/castorama/castWS/mappingFiles ./castorama.ear/atg_bootstrap.war/WEB-INF/ATG-INF/commerce/castorama/castWS

  cp -r ./$1/install/docroot/documents/castorama_SearchAdminUI_jsps/castorama ./castorama.ear/AdminUI.war
 
echo 'Assemble ear is done'


