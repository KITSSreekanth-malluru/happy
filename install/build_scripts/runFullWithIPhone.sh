if ["$1" = ""] 
then
	echo 'Provide Build tag'
else
 ./getSources.sh $1
 ./getIPhoneSources.sh $1
 ./buildAppWithIPhone.sh $1
 ./assembleEarWithIPhone.sh $1
 ./assemblePackage.sh $1
fi
