# Bash script to retrieve the counters file from mapreduce job

# Recieve the job id as a parameter
JOBID=$1

# 20 iterations
for i in {1..20}
do

    # If the iteration number is less than 10, add a "0" before the number
    if [ $i -lt 10 ]
    then
        JOBID2=$JOBID"_000"$i
    else
        JOBID2=$JOBID"_00"$i
    fi


    # Retrieve the counters file
    mapred job -history $JOBID2 -outfile out-$i -format human
    wait
    sleep 2

    
done
