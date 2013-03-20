#!/bin/sh
kill `ps -ef | grep 7777 | egrep -v grep | awk '{print $2}'`
rmiregistry 7777&
mv ?*.tar proj3.tar
tar xvf proj3.tar; rm *.class
rm *.log
javac *.java; java start