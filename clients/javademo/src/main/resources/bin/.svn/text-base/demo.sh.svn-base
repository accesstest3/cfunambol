#!/bin/sh

#
# Copyright (C) 2007 Funambol, Inc.  All rights reserved.
#
#  Before running this script:
#  - set the JAVA_HOME
#

# Setting CMD_HOME
# resolving links - $0 could be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`

CMD_HOME=`cd "$PRGDIR/.." ; pwd`

#
# BUNDLED_JAVA_HOME is set to the JRE home distributed in the bundle. If this
# JDK is not found JAVA_HOME will be used.
#
BUNDLED_JAVA_HOME=$CMD_HOME/../../tools/jre-1.6.0/jre

if [ -d "$BUNDLED_JAVA_HOME" ]; then
   JAVA_HOME=$BUNDLED_JAVA_HOME
fi

if [ -z "$JAVA_HOME" ]; then
  echo "Please, set JAVA_HOME before running this script."
  exit 1
fi

if [ ! -f "$JAVA_HOME/bin/java" ]
then
    echo "Please set JAVA_HOME to the path of a valid jre."
    exit;
fi

# Setting classpath
cd $CMD_HOME/lib
for jarfile in *.jar; do export CLASSPATH=$CLASSPATH:lib/$jarfile; done
cd ..


LANG=en_US.UTF-8
JAVA_OPTS="-Dfile.encoding=UTF-8 -Dwd=."

$JAVA_HOME/bin/java $JAVA_OPTS com.funambol.syncclient.javagui.Client $* > /dev/null 2>&1 </dev/zero &
