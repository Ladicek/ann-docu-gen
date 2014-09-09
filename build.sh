#!/bin/bash

ROOT=$(dirname $(readlink -f $0))

$ROOT/clean.sh

echo "compile 'framework'"
cd $ROOT/framework
mkdir target
javac -d target src/cz/ladicek/annDocuGen/api/*.java

echo "compile 'annotation-processor'"
cd $ROOT/annotation-processor
mkdir target
javac -d target -cp $JAVA_HOME/lib/tools.jar:$ROOT/framework/target/ src/cz/ladicek/annDocuGen/annotationProcessor/*.java
cp -a resources/* target

echo "compile 'example'"
cd $ROOT/example
mkdir target
javac -d target -cp $JAVA_HOME/lib/tools.jar:$ROOT/framework/target/:$ROOT/annotation-processor/target/ src/cz/ladicek/annDocuGen/example/*.java
