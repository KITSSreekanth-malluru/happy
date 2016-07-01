echo 'Build app: ' $1

if [ "+$1" = "+" ] 
then
	echo 'Provide Build tag'
else
 echo 'Build application is started'
#       n=$(date +"%Y_%m_%d_%H:%M:%S")

#	mv $1/commonbuildtasks/build.properties $1/commonbuildtasks/build.properties_$n.bak

#	cp -f $1/commonbuildtasks/buildWithIPhone.properties $1/commonbuildtasks/build.properties

	cd $1
	ant all

 echo 'Build application is done'

fi

