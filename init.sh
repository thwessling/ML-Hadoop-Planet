#!/bin/bash


echo Copying training data to hadoop dfs...

hadoop dfs -mkdir planet_train
hadoop dfs -copyFromLocal planet_train/instances_all.txt planet_train/

echo Done.
