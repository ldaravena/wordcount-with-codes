#!/bin/bash
# Bash scritp to run multiple tests

# Declaration of the program to test
PROGRAM="yarn jar WordCount.jar WordCount /user/pi/wordcount/input /user/pi/wordcount/output"

# Declaration to remove output directory
REMOVE="hadoop fs -rm -r /user/pi/wordcount/output"

# Declaration to retrieve output file
RETRIEVE="hadoop fs -get /user/pi/wordcount/output/part-r-00000"

# 20 iterations
for i in {1..20}
do
    # Run the test
    echo "Test $i"
    $REMOVE
    wait
    sleep 10
    $PROGRAM
    wait
    sleep 10
    $RETRIEVE
    wait
    # Get the md5sum of the output file
    md5sum part-r-00000 >> md5sum_output.txt
    wait
    # Remove the output file
    rm part-r-00000
    wait 
    sleep 10

    # Print new line
    echo ""
    
done
