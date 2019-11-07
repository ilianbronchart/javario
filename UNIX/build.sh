#!/bin/bash

clear
cd ..
javac src/main/*.java -d bin
jar cfm Javario.jar Manifest.txt -C bin src/main
jar tf Javario.jar
