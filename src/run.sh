#!/bin/bash

if [ -z $1 ] 
then 
  echo "ERROR. Testname unset. Expected parameter"
  exit 1
fi

nohup java com.lukzar.Main $1 > ${1}.out &
