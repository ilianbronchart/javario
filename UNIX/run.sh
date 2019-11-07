#!/bin/bash

clear
cd ..
javac src/main/*.java -d bin
java -cp bin src.main.Main
