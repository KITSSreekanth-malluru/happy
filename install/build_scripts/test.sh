uname="--username Mikalai_Khatsko"
revision="-r HEAD"
branch=""
is_pause=false
while [ "x$1" != "x" ]
do
  case $1 in
      -help) echo -help
	  ;;
      -r) 
          while [ "x$2" != "x" ] 
            do
            case $2 in
                -*) break
                    ;;
                *) revision="-r $2" 
                    ;;
            esac
            shift
          done
          echo "revision=$revision"
          ;;      
      -u)
          while [ "x$2" != "x" ]
            do
            case $2 in
                -*) break
                    ;;
                *) uname="--username $2" 
                    ;;
            esac
            shift
          done
          echo "uname=$uname"
          ;;
      -b)
          while [ "x$2" != "x" ]
            do
            case $2 in
                -*) break
                    ;;
                *) branch="https://svnmsq.epam.com/kits-mnt/branches/$2"
                   br_name=$2
                    ;;
            esac
            shift
          done
          echo "branch=$branch"
          echo "br_name=$br_name"
          ;;
     -p)
          is_pause="true"
          echo "is_pause=$is_pause"
          ;;

  esac
  shift
done

if [ "x$branch" == "x" ]
then
   echo 'Provide Build tag'
   exit 0
fi

if [ "x$is_pause" == "xtrue" ]
then 
  echo
  read -p "Press enter to get script getSources.sh from svn"
fi

echo get script getSources.sh from svn
svn export "$branch/install/build_scripts/getSources.sh" $uname

echo convert to unix
dos2unix getSources.sh

echo set permissions to run script
chmod 754 getSources.sh




if [ "x$is_pause" == "xtrue" ]
then 
  echo
  read -p "Press enter to run getSources from $branch"
fi

echo run getSources from $branch started
./getSources.sh $revision $branch $uname >> log/getSources_$br_name.log
echo run getSources from $branch ended


if [ "x$is_pause" == "xtrue" ]
then 
  echo
  read -p "Press enter to export build_scripts directory"
fi
echo export build_scripts directory started
svn export "$branch/install/build_scripts" $uname
echo copy scripts into script folder
for file in ./build_scripts/*
do
  if [ -f $file ] && [ -w $file ]
  then
    dos2unix $file
    chmod 754 $file
  fi
done
echo export build_scripts directory ended


#if [ "x$is_pause" == "xtrue" ]
#then 
#  echo
#  read -p "Press enter to run getIphoneSources script"
#fi
#echo run getIphoneSources script started
#echo build_scripts/getIPhoneSources.sh $br_name $uname 
#./build_scripts/getIPhoneSources.sh $br_name $uname > log/getIphoneSources_$br_name.log
#echo run getIphoneSources script ended


if [ "x$is_pause" == "xtrue" ]
then 
  echo
  read -p "Press enter to run buildAppWithIPhone script"
fi
echo run buildAppWithIPhone script started
cp userenv.properties $br_name
./build_scripts/buildAppWithIPhone.sh $br_name $uname > log/buildAppWithIPhone_$br_name.log
echo run buildAppWithIPhone script ended


if [ "x$is_pause" == "xtrue" ]
then 
  echo
  read -p "Press enter to run assembleEarWithIPhone script"
fi
echo run assembleEarWithIPhone script started
echo create hotfixes directory
mkdir hotfixes
echo copy hotfixes into hotfixes folder
for file in ./$br_name/install/build_scripts/hotfixes/*
do
  if [ -f $file ]
  then
    cp $file ./hotfixes/
  fi
done

./build_scripts/assembleEarWithIPhone.sh $br_name 'build_scripts/hotfixes/' > log/assembleEarWithIPhone_$br_name.log
echo run assembleEarWithIPhone script ended


if [ "x$is_pause" == "xtrue" ]
then 
  echo
  read -p "Press enter to run  assemblePackage script"
fi
echo run  assemblePackage script started
./build_scripts/assemblePackage.sh `pwd $br_name`/$br_name $br_name > log/assemblePackage_$br_name.log

echo run  assemblePackage script ended
