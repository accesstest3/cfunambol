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
# If JAVA_HOME points to a jdk, it is taken to launch the client, otherwise the
# JAVA_HOME is set to the JRE distributed in the bundle. If this JDK is not
# found, it is used the java command in the commands path.
#
JAVA_CMD=bin/java

if [ -z "$JAVA_HOME" ]; then
  JAVA_HOME="$CMD_HOME"/../../tools/jre-1.6.0/jre
fi

if [ ! -f "$JAVA_HOME/$JAVA_CMD" ]
then
    JAVA_CMD="java"
fi

if [ -f "$JAVA_HOME/$JAVA_CMD" ]
then
    JAVA_CMD="$JAVA_HOME"/$JAVA_CMD
fi

# Setting classpath
cd "$CMD_HOME/lib"
for jarfile in *.jar; do export CLASSPATH=$CLASSPATH:lib/$jarfile; done
cd ..

echo "Using $JAVA_CMD"

$JAVA_CMD com.funambol.syncclient.cl.Main
