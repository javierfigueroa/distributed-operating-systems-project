#!/bin/sh
kill `ps -ef | grep 7777 | egrep -v grep | awk '{print $2}'`
rmiregistry 7777&
mv ?*.tar proj2.tar
tar xvf proj2.tar; rm *.class
rm *.log
javac *.java; java start
