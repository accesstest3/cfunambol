#!/bin/sh

#
#  Copyright (C) 2007 Funambol, Inc.  All rights reserved.
#
#  Before running this script:
#  - set the JAVA_HOME
#

ulimit -n 1000000
  
CURRENT_DIR=$PWD

# Setting MULTI_CLIENT_DRIVER_HOME
# resolving links - $0 may is a softlink
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

MULTI_CLIENT_DRIVER_HOME=`cd "$PRGDIR/.." ; pwd`

BUNDLED_JAVA_HOME=

if [ ! -z "$BUNDLED_JAVA_HOME" ]; then
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
cd $MULTI_CLIENT_DRIVER_HOME/lib
for jarfile in *.jar; do export CLASSPATH=$CLASSPATH:lib/$jarfile; done
cd ..
JAVA_OPTS="$JAVA_OPTS -Xmx1000M -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=12346 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"

  # Uncomment the following line to enable remote debug
#  JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=7777,server=y,suspend=n"

CLASSPATH=$CLASSPATH:$MULTI_CLIENT_DRIVER_HOME/config
echo MULTI_CLIENT_DRIVER_HOME: $MULTI_CLIENT_DRIVER_HOME
echo CLASSPATH: "$CLASSPATH"
echo JAVA_OPTS: $JAVA_OPTS

if [ "$2" = "-debug" ] ; then
"$JAVA_HOME/bin/java" $JAVA_OPTS com.funambol.ctp.client.driver.MultiClient
else
"$JAVA_HOME/bin/java" $JAVA_OPTS com.funambol.ctp.client.driver.MultiClient
fi

cd $CURRENT_DIR