echo 'Build app: ' $1

if ["$1" = ""] 
then
	echo 'Provide Build tag'
else
 echo 'Build application is started'

	cd $1
	echo build.number=$1 > version.properties
	ant all

 echo 'Build application is done'

fi

