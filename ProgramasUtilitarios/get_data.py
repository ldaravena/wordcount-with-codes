# Programa en python para obtener datos de archivos de texto
# Se leen todos los archivos de texto del directorio ingresado en el argumento

import datetime
import sys
import os
import re
import pandas as pd

# Funcion para obtener los datos de los archivos de texto
def get_data(path):
    # Se crea un diccionario para almacenar los siguientes datos:
    # iteracion, total_execution_time, avg_map_time, avg_shuffle_time, avg_reduce_time, cpu_time, physical_memory, virtual_memory, peak_map_physical_memory, peak_map_virtual_memory, peak_reduce_physical_memory, peak_reduce_virtual_memory, total_megabyte_milliseconds_map, total_megabyte_milliseconds_reduce
    data = {
        "iteration": [],
        "total_execution_time": [],
        "avg_map_time": [],
        "avg_shuffle_time": [],
        "avg_reduce_time": [],
        "cpu_time": [],
        "physical_memory": [],
        "virtual_memory": [],
        "peak_map_physical_memory": [],
        "peak_map_virtual_memory": [],
        "peak_reduce_physical_memory": [],
        "peak_reduce_virtual_memory": [],
        "total_megabyte_milliseconds_map": [],
        "total_megabyte_milliseconds_reduce": []
    }
    # Se obtienen los archivos de texto del directorio
    files = os.listdir(path)

    # Variable para almacenar el número de iteración actual
    i = 1
    
    # Se recorren los archivos de texto
    for file in files:
        # Se abre el archivo de texto
        f = open(path + file, 'r')
        # Se lee el archivo de texto
        text = f.read()
        # Se cierra el archivo de texto
        f.close()

        # Se guarda la iteración actual en el diccionario, en la llave "iteracion", agregando a la lista de valores
        data["iteration"].append(i)

        # Se obtienen los datos del archivo de texto
        # Se busca la linea que contiene la frase: "Finished At:"
        line = re.search('Finished At:.*', text).group()

        # Se procesa el tiempo de ejecución
        time = process_time(line)

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "total_execution_time", agregando a la lista de valores
        data["total_execution_time"].append(time)

        # Se busca la linea que contiene la frase: "Average time taken by map tasks:"
        line = re.search('Average time taken by map tasks:.*', text).group()

        # Se procesa el tiempo de ejecución promedio de las tareas de Map:
        time = process_time(line)

        # Se guarda el tiempo promedio de las tareas de Map en el diccionario, en la llave "avg_map_time", agregando a la lista de valores
        data["avg_map_time"].append(time)

        # Se busca la linea que contiene la frase: "Average time taken by shuffle tasks:"
        line = re.search('Average time taken by shuffle tasks:.*', text).group()

        # Se procesa el tiempo de ejecución promedio de las tareas de Shuffle:
        time = process_time(line)

        # Se guarda el tiempo promedio de las tareas de Shuffle en el diccionario, en la llave "avg_shuffle_time", agregando a la lista de valores
        data["avg_shuffle_time"].append(time)

        # Se busca la linea que contiene la frase: "Average time taken by reduce tasks:"
        line = re.search('Average time taken by reduce tasks:.*', text).group()

        # Se procesa el tiempo de ejecución promedio de las tareas de Reduce:
        time = process_time(line)

        # Se guarda el tiempo promedio de las tareas de Reduce en el diccionario, en la llave "avg_reduce_time", agregando a la lista de valores
        data["avg_reduce_time"].append(time) 

        # Se busca la linea que contenga la frase: "|CPU time spent (ms)":
        line = re.search('\|CPU time spent \(ms\).*', text).group()

        # Se obtiene la última palabra de la linea
        cpu_time = line.split()[-1]

        # Se borra el caracter "|" de la palabra
        cpu_time = cpu_time.replace("|", "")

        # Se borran las comas de la palabra
        cpu_time = cpu_time.replace(",", "")

        # Se obtiene el valor numérico de la palabra
        cpu_time = int(re.search('\d+', cpu_time).group())

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "cpu_time", agregando a la lista de valores
        data["cpu_time"].append(cpu_time)

        # Se busca la linea que contenga la frase: "|Physical memory (bytes) snapshot":
        line = re.search('\|Physical memory \(bytes\) snapshot.*', text).group()

        # Se obtiene el último valor de la linea, después del último caracter "|"
        physical_memory = line.split("|")[-1]

        # Se borran las comas de la palabra
        physical_memory = physical_memory.replace(",", "")

        # Se obtiene el valor numérico de la palabra
        physical_memory = int(re.search('\d+', physical_memory).group())

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "physical_memory", agregando a la lista de valores

        data["physical_memory"].append(physical_memory)

        # Se busca la linea que contenga la frase: "|Virtual memory (bytes) snapshot":
        line = re.search('\|Virtual memory \(bytes\) snapshot.*', text).group()

        # Se obtiene la última palabra de la linea
        virtual_memory = line.split("|")[-1]

        # Se borra el caracter "|" de la palabra
        virtual_memory = virtual_memory.replace("|", "")

        # Se borran las comas de la palabra
        virtual_memory = virtual_memory.replace(",", "")

        # Se obtiene el valor numérico de la palabra
        virtual_memory = int(re.search('\d+', virtual_memory).group())

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "virtual_memory", agregando a la lista de valores

        data["virtual_memory"].append(virtual_memory)

        # Se busca la linea que contenga la frase: "|Peak Map Physical memory (bytes)":
        line = re.search('\|Peak Map Physical memory \(bytes\).*', text).group()

        # Se obtiene la última palabra de la linea
        peak_map_physical_memory = line.split("|")[-1]

        # Se borra el caracter "|" de la palabra

        peak_map_physical_memory = peak_map_physical_memory.replace("|", "")

        # Se borran las comas de la palabra
        peak_map_physical_memory = peak_map_physical_memory.replace(",", "")

        # Se obtiene el valor numérico de la palabra

        peak_map_physical_memory = int(re.search('\d+', peak_map_physical_memory).group())

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "peak_map_physical_memory", agregando a la lista de valores

        data["peak_map_physical_memory"].append(peak_map_physical_memory)

        # Se busca la linea que contenga la frase: "|Peak Map Virtual memory (bytes)":
        line = re.search('\|Peak Map Virtual memory \(bytes\).*', text).group()

        # Se obtiene la última palabra de la linea
        peak_map_virtual_memory = line.split("|")[-1]

        # Se borra el caracter "|" de la palabra
        peak_map_virtual_memory = peak_map_virtual_memory.replace("|", "")

        # Se borran las comas de la palabra
        peak_map_virtual_memory = peak_map_virtual_memory.replace(",", "")

        # Se obtiene el valor numérico de la palabra
        peak_map_virtual_memory = int(re.search('\d+', peak_map_virtual_memory).group())

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "peak_map_virtual_memory", agregando a la lista de valores

        data["peak_map_virtual_memory"].append(peak_map_virtual_memory)

        # Se busca la linea que contenga la frase: "|Peak Reduce Physical memory (bytes)":
        line = re.search('\|Peak Reduce Physical memory \(bytes\).*', text).group()

        # Se obtiene la última palabra de la linea
        peak_reduce_physical_memory = line.split("|")[-1]

        # Se borra el caracter "|" de la palabra
        peak_reduce_physical_memory = peak_reduce_physical_memory.replace("|", "")

        # Se borran las comas de la palabra
        peak_reduce_physical_memory = peak_reduce_physical_memory.replace(",", "")

        # Se obtiene el valor numérico de la palabra
        peak_reduce_physical_memory = int(re.search('\d+', peak_reduce_physical_memory).group())

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "peak_reduce_physical_memory", agregando a la lista de valores

        data["peak_reduce_physical_memory"].append(peak_reduce_physical_memory)

        # Se busca la linea que contenga la frase: "|Peak Reduce Virtual memory (bytes)":
        line = re.search('\|Peak Reduce Virtual memory \(bytes\).*', text).group()

        # Se obtiene la última palabra de la linea
        peak_reduce_virtual_memory = line.split("|")[-1]

        # Se borra el caracter "|" de la palabra
        peak_reduce_virtual_memory = peak_reduce_virtual_memory.replace("|", "")

        # Se borran las comas de la palabra
        peak_reduce_virtual_memory = peak_reduce_virtual_memory.replace(",", "")

        # Se obtiene el valor numérico de la palabra
        peak_reduce_virtual_memory = int(re.search('\d+', peak_reduce_virtual_memory).group())

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "peak_reduce_virtual_memory", agregando a la lista de valores

        data["peak_reduce_virtual_memory"].append(peak_reduce_virtual_memory)

        # Se busca la linea que contenga la frase: "|Total megabyte-milliseconds taken by all map tasks":
        line = re.search('\|Total megabyte-milliseconds taken by all map tasks.*', text).group()

        # Se obtiene la última palabra de la linea
        total_megabyte_milliseconds_map = line.split("|")[-1]
        
        # Se borra el caracter "|" de la palabra
        total_megabyte_milliseconds_map = total_megabyte_milliseconds_map.replace("|", "")

        # Se borran las comas de la palabra
        total_megabyte_milliseconds_map = total_megabyte_milliseconds_map.replace(",", "")

        # Se obtiene el valor numérico de la palabra
        total_megabyte_milliseconds_map = int(re.search('\d+', total_megabyte_milliseconds_map).group())

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "total_megabyte_milliseconds_map", agregando a la lista de valores

        data["total_megabyte_milliseconds_map"].append(total_megabyte_milliseconds_map)

        # Se busca la linea que contenga la frase: "|Total megabyte-milliseconds taken by all reduce tasks":
        line = re.search('\|Total megabyte-milliseconds taken by all reduce tasks.*', text).group()

        # Se obtiene la última palabra de la linea
        total_megabyte_milliseconds_reduce = line.split("|")[-1]

        # Se borra el caracter "|" de la palabra
        total_megabyte_milliseconds_reduce = total_megabyte_milliseconds_reduce.replace("|", "")

        # Se borran las comas de la palabra
        total_megabyte_milliseconds_reduce = total_megabyte_milliseconds_reduce.replace(",", "")

        # Se obtiene el valor numérico de la palabra
        total_megabyte_milliseconds_reduce = int(re.search('\d+', total_megabyte_milliseconds_reduce).group())

        # Se guarda el tiempo total en segundos en el diccionario, en la llave "total_megabyte_milliseconds_reduce", agregando a la lista de valores

        data["total_megabyte_milliseconds_reduce"].append(total_megabyte_milliseconds_reduce)

        # Se incrementa la iteración
        i += 1

    # Se retorna el diccionario
    return data      


# Función para procesar los tiempos de la liena de texto
def process_time(line):
    # Variable booleana para indicar si se encontro la palabra "mins"
    found = False
    # Se comprueba que "line" contenga la palabra "mins"
    if "mins" in line:
        found = True
    
    if found:
        # Se obtienen las últimas dos palabras de la linea
        mins = line.split()[-2]
        sec = line.split()[-1]
        # Se obtiene el valor numérico de la palabra "mins"
        mins = int(re.search('\d+', mins).group())
        # Se obtiene el valor numérico de la palabra "sec"
        sec = int(re.search('\d+', sec).group())
        # Se calcula el tiempo total en segundos
        time = mins * 60 + sec
        
        # Retorna el tiempo
        return time

    
    else:
        sec = line.split()[-1]
        # Se obtiene el valor numérico de la palabra "sec"
        sec = int(re.search('\d+', sec).group())
        # Se calcula el tiempo total en segundos
        time = sec
        
        # Retorna el tiempo
        return time



# Funcion principal
def main():
    # Se obtiene el directorio de los archivos de texto
    path = sys.argv[1]
    # Se obtienen los datos de los archivos de texto
    data = get_data(path)

    # Nombre del archivo csv es el timestamp
    timestamp = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")

    # Se crea un archivo csv a partir del diccionario "data", donde las llaves son los nombres de las columnas y las filas son la lista de valores de cada llave
    df = pd.DataFrame(data)
    df.to_csv(timestamp+".csv", index=False)

# Se ejecuta la funcion principal
main()


