echo 'Assemble package: ' $1

echo 'Assemble package is started'
if [ -e BuildPackage ]
then
  rm BuildPackage/*
fi
mkdir BuildPackage
curr_dir=`pwd`
echo 1
cd /opt/atg
tar -cf $curr_dir/BuildPackage/commerce.$2.tar commerce
echo 2
cd $curr_dir/castorama.ear/atg_bootstrap.war/WEB-INF/ATG-INF/home/servers
tar -cf $curr_dir/BuildPackage/servers.$2.tar cast-jboss-*
echo 3
rm -r $curr_dir/castorama.ear/atg_bootstrap.war/WEB-INF/ATG-INF/home/servers/cast-jboss-*
echo 4
cd $curr_dir/
tar -cf $curr_dir/BuildPackage/castorama.ear.$2.tar castorama.ear
	

cp $curr_dir/License/* $curr_dir/castorama.ear/atg_bootstrap.war/WEB-INF/ATG-INF/home/localconfig
tar -cf $curr_dir/BuildPackage/castorama.ear.qa.$2.tar castorama.ear
    
echo 5
cd $1
tar -cf $curr_dir/BuildPackage/installScript.$2.tar commonbuildtasks install cast/build/tempconfig userenv.properties build.xml 

echo 'Assemble package is done'


