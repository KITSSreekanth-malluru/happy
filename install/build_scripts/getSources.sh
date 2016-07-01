while [ "x$1" != "x" ]
do
 my_params="$my_params $1"
 shift
done 

echo 'Get souces is started'

echo svn export $my_params

svn export $my_params --force

echo 'Get souces is done'
