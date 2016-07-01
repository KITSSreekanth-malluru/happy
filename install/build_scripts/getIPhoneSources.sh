echo 'Get IPhone souces from: ' $1

echo 'Get souces is started'

if [ -e Casto_Mobile ]
then
  echo Delete Casto_Mobile
  rm -r Casto_Mobile
fi
echo 'Get IPhone'



echo svn export "https://svnmsq.epam.com/kits-mnt/Casto_Mobile" --username $3

svn export "https://svnmsq.epam.com/kits-mnt/Casto_Mobile" --username $3

echo unzip "Casto_Mobile/Back - end sources/Release_7_exp/production_r7_exp.zip" -d $1

unzip "Casto_Mobile/Back - end sources/Release_7_exp/production_r7_exp.zip" -d $1

echo 'Get IPhone souces is done'

