#!/bin/bash

if [ -z $1 ] 
then 
  echo "ERROR. Expected parameters:  ./run.sh [ibis|chess|weather] [TRI|PWN|RND] testname"
  exit 1
fi

nohup java com.lukzar.Main $1 $2 $3 I > ${1}_${2}_${3}.out &
