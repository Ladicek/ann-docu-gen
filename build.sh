#!/bin/bash

ROOT=$(dirname $(readlink -f $0))

echo "clean"
find $ROOT -type d -name target | xargs rm -rf

echo "compile 'framework'"
cd $ROOT/framework
mkdir target
javac -d target src/cz/ladicek/annDocuGen/api/*.java

echo "compile 'annotation-processor'"
cd $ROOT/annotation-processor
mkdir target
javac -d target -cp $ROOT/framework/target/ src/cz/ladicek/annDocuGen/annotationProcessor/*.java
cp -a resources/* target

echo "compile 'example'"
cd $ROOT/example
mkdir target
javac -d target -cp $ROOT/framework/target/:$ROOT/annotation-processor/target/ src/cz/ladicek/annDocuGen/example/*.java
