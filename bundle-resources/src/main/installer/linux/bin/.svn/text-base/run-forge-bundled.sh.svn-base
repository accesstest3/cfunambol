#!/bin/sh

name=$1
tarfile=$2

if test "x$name" = "x" ; then
    echo " "
    echo "usage: $0 <name> <tarfile>"
    echo " "
    exit;
fi

cd `dirname $0`/../../../..

export FORGE_HOME=`pwd`/output
export J2EE_HOME=${FORGE_HOME}/Funambol/tools/tomcat
export APPSRV=tomcat60

if [ ! -d ${FORGE_HOME} ]
then 
    echo "No output directory found: nothing to do."
    exit;
fi

cd ${FORGE_HOME}

if [ ! -d Funambol ]
then
    echo "No Funambol directory found: nothing to do."
    exit;
fi

if [ -f "Funambol/Funambol Web Site.url" ]
then
    rm "Funambol/Funambol Web Site.url"
fi

if [ -f "Funambol/webdemo-client.url" ]
then
    rm "Funambol/webdemo-client.url"
fi

if [ -f "Funambol/admin/funambol.url" ]
then
    rm "Funambol/admin/funambol.url"
fi

#
# Now we set execution permission to the relevant files.
#
chmod +x Funambol/bin/*
chmod +x Funambol/tools/jre-1.6.0/jre/bin/*
chmod +x Funambol/tools/tomcat/bin/*
chmod +x Funambol/admin/bin/*
chmod +x Funambol/plug-ins/javademo/bin/*.sh
chmod +x Funambol/plug-ins/cl/bin/*.sh
chmod +x Funambol/ds-server/ant/bin/*

#
# Tarring
#
echo "Archiving..."
tar cvzf ${tarfile} Funambol

#
# Checksumming
#
echo "Checksumming..."
SUM=`sum ${tarfile}`

cp ${FORGE_HOME}/installer/linux/head $name.bin
echo " " >>$name.bin
echo "#" >>$name.bin
echo "# TO UPDATE EVERYTIME" >>$name.bin
echo "#" >>$name.bin
echo "packageName=Funambol" >>$name.bin
echo "diskSpaceRequired=120000" >>$name.bin
echo "pkgSum=\"$SUM\"" >>$name.bin
echo "destDir=/opt" >>$name.bin
echo "startingLine=786" >>$name.bin
echo "###" >>$name.bin
echo " " >>$name.bin
cat ${FORGE_HOME}/installer/linux/tail >>$name.bin
# The following line cannot be added if the tail file has already and
# EOL as the last line. Some editors add one, some strip,...
#echo "" >>$name.bin
cat ${tarfile} >> $name.bin
