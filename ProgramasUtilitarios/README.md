# Programas Utilitarios

Este directorio contiene programas y scripts que fueron utilizados para facilitar tareas en el desarrollo de la Memoria de Título

## limpiar_texto.cpp
Programa en C++ para eliminar caracteres que no sean letras ni números de archivos de texto (conservando espacios, saltos de línea y tabulaciones), además de convertir todas las letras en minúsculas. Fue utilizado para *limpiar* el dataset **english** obtenido del corpus Pizza & Chili de la Universidad de Chile, que originalmente contenía caracteres Unicode. Este dataset fue el más utilizado durante el desarrollo de la memoria de título. Este programa se ejecuta fuera del cluster.

## cleantext_wordsperline.cpp
Al igual que *limpiar_texto.cpp*, este programa escrito en C++ limpia los archivos de texto de entrada y además permite establecer cuántas palabras contienen las líneas de texto del archivo de salida. Fue utilizado con el dataset obtenido de la Wikipedia en inglés. Este programa se ejecuta fuera del cluster.

## n_words_per_line.sh
Este script escrito en Bash recibe dos argumentos: el nombre de un archivo de texto y un número entero que representa cuántas palabras tendrá cada línea de texto. Con lo cual se genera un archivo de texto cambiando el número de palabras por línea. Fue utilizado con el dataset *English limpio* para generar archivos de texto con diferentes números de palabras por línea para hacer pruebas con la aplicación WordCount. Este script se ejecuta fuera del cluster.

## iniciar.sh, terminar.sh, reiniciar.sh:
Estos tres scripts simples escritos en Bash permiten iniciar, terminar y reiniciar los servicios de Hadoop: **HDFS**, **YARN** y **HistoryServer**. Se ejecutan desde el nodo Master.

## tests.sh:
Este script escrito en Bash se ejecuta en el nodo Master y permite realizar múltiples iteraciones de la ejecución de una aplicación de MapReduce con el dataset que se encuentre en el HDFS. También se obtiene la suma de verificación *md5sum* de los archivos de salida para verificar que la ejecución fue correcta. En esta versión del script se ejecuta la aplicación WordCount 20 veces. Es recomendable reiniciar los servicios de Hadoop antes de ejecutar este script (usando el script *reiniciar.sh*) para que los **ID** de las ejecuciones de los jobs tengan el mismo prefijos y sean secuenciales, lo cual es importante para el script *retrieve_counters.sh*.

## retrieve_counters.sh:

Este script escrito en Bash se ejecuta en el nodo Master y se utiliza para obtener la información de los *counters* del historial de las ejecuciones de una aplicación de MapReduce usando el script *test.sh*. Este script recibe como parámetro el prefijo del **ID** de la ejecución del job en MapReduce y entrega un archivo con la información de los *counters* en formato *human*, que es un archivo de texto. (ejemplo de ID: **job_1677670410970**)

## get-data.py:

Este programa escrito en Python permite obtener la información de los archivos generados en el script *retrieve_counters.sh* que corresponden a la información de la ejecución del job en MapReduce. Recibe como parámetro un directorio con los archivos obtenidos desde el historial en formato *human* y entrega un archivo csv con los datos más relevantes. Este archivo puede importarse en una plantilla de cálculo para obtener los promedios de los valores y generar gráficos. Es importante que en el directorio solo existan archivos obtenidos desde el historial en formato *human*, si no, el programa terminará con errores.

## yarn-utils.py:

Este programa escrito en Python permite obtener los valores recomendados para la configuración de los parámetros de los recursos de YARN. Es una versión modificada ligeramente para que funcione con Python3. La versión original proviene de https://github.com/mahadevkonar/ambari-yarn-utils. Los parármetros que recibe este programa son los siguientes:

-c : Número de cores por nodo

-m : Memoria RAM (en GB) por nodo

-d : Cantidad de discos de almacenamiento por nodo

-k : True si se utiliza la base de datos HBase, False en caso contrario

En el caso del cluster utilizado para el desarrollo de la memoria de título, los parámetros fueron los siguientes:

```
-c 4 -m 4 -d 1 -k False
```
Los resultados que entregó este programa fueron los siguientes:

```
 Using cores=4 memory=4GB disks=1 hbase=False
 Profile: cores=4 memory=3072MB reserved=1GB usableMem=3GB disks=1
 Num Container=3
 Container Ram=1024.0MB
 Used Ram=3GB
 Unused Ram=1GB
 
 yarn.scheduler.minimum-allocation-mb=1024.0
 yarn.scheduler.maximum-allocation-mb=3072.0
 yarn.nodemanager.resource.memory-mb=3072.0
 yarn.app.mapreduce.am.resource.mb=512
 yarn.app.mapreduce.am.command-opts=-Xmx409m
 
 mapreduce.map.memory.mb=512
 mapreduce.map.java.opts=-Xmx409m
 mapreduce.reduce.memory.mb=1024
 mapreduce.reduce.java.opts=-Xmx819m
 mapreduce.task.io.sort.mb=204
 ```
Estas configuraciones se aplicaron a los archivos de configuración.
