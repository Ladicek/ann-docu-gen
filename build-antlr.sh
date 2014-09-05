#!/bin/bash

ROOT=$(dirname $(readlink -f $0))

cd $ROOT/annotation-processor
java -jar $ROOT/lib/antlr-4.4-complete.jar src/cz/ladicek/annDocuGen/annotationProcessor/javaParser/Java.g4

