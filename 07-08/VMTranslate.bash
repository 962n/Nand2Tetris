#!/bin/bash

if [ $# -ne 1 ]; then
  echo "指定された引数が無効です(引数は1つ)"
  exit 1
fi

cd ./VMTranslator/build/classes/kotlin/main/
kotlin VMTranslatorKt $1

exit 0
