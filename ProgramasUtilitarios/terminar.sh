#!/bin/bash

stop-yarn.sh
wait
sleep 5

stop-dfs.sh
wait

echo -e "\nHADOOP TERMINADO\n"