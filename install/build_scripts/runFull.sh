if ["$1" = ""] 
then
	echo 'Provide Build tag'
else
 ./getSources.sh $1
 ./buildApp.sh $1
 ./assembleEar.sh $1
 ./assemblePackage.sh $1
fi
