#/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <json-Key> <src-document>"
    echo "  --> Reads input document from " 
    exit 1
fi

LINE=`grep $1 $2` || exit -1
SED_COMMAND="s/\"$1\": \"\(.*\)\"/\1/g"
VALUE=`echo $LINE | sed "$SED_COMMAND"` || exit -1

if [ "X" = "X${VALUE}" ] ; then
   echo "Missing Value for $2 in $1";
   exit 1
fi

echo ${VALUE}

