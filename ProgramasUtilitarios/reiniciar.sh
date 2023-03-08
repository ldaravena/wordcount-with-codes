#!/bin/bash

stop-yarn.sh
wait
sleep 5

stop-dfs.sh
wait
sleep 10

start-dfs.sh
wait
sleep 5

start-yarn.sh
wait
sleep 5 

echo -e "\nHADOOP REINICIADO\n"