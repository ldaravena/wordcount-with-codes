#!/bin/bash

# Nombre del archivo original
file=$1

# Número de palabras por línea
n=$2

# Nombre del nuevo archivo
newfile="$file"_"$n"

# Contador de palabras
word_count=0

# Leer el archivo original y escribir el nuevo archivo con "n" palabras por línea
while read line; do
    words=($line)
    for word in "${words[@]}"; do
        word_count=$((word_count+1))
        printf "$word "
        if [ $word_count -eq $n ]; then
            printf "\n"
            word_count=0
        fi
    done
done < $file > $newfile
