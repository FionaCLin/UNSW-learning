#!/bin/bash
#for i in $(ls ./examples/0/*.py)
#for i in $(ls ./examples/1/*.py)
#for i in $(ls ./examples/2/*.py)
for i in $(ls ./examples/3/*.py)
  #for i in $(ls ./examples/4/*.py)
  #for i in $(ls ./examples/5/*.py)
do
  echo $i
  echo python3
  python3 "$i"


  echo perl
  ./pypl.pl "$i"
  ./pypl.pl "$i"|perl

  #  cat "./examples/2/answer$i.pl"
done
./pypl.pl "$i"
#  ./pypl.pl "$i"

