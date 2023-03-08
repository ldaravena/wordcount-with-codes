#!/bin/bash

start-dfs.sh
wait
sleep 5

start-yarn.sh
wait
sleep 5 

/opt/hadoop-3.3.1/bin/mapred --daemon start historyserver
wait
sleep 5

echo -e "\nHADOOP INICIADO\n"
