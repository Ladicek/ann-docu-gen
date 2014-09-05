#!/bin/bash

ROOT=$(dirname $(readlink -f $0))

echo "clean"
find $ROOT -type d -name target | xargs rm -rf
rm -f $ROOT/annotation-processor/src/cz/ladicek/annDocuGen/annotationProcessor/javaParser/*.java
rm -f $ROOT/annotation-processor/src/cz/ladicek/annDocuGen/annotationProcessor/javaParser/*.tokens
