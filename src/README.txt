#!/bin/sh 
mv ?*.tar proj1.tar 
tar xvf proj1.tar; rm *.class 
rm *.log javac *.java; java start