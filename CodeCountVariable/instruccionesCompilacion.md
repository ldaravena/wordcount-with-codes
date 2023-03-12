## Instrucciones de Compilación

La compilación de este programa se debe realizar en el nodo Master del cluster.

1. Crear un directorio para las clases compiladas

        mkdir container

2. Exportar la classpath de Hadoop

        export HADOOP_CLASSPATH=$(hadoop classpath)

3. Compilar el programa

        javac -classpath $HADOOP_CLASSPATH -d container/ *.java

4. Empaquetar el programa en el ejecutable jar

        jar -cvf CodeCountVariable.jar -C container/ .

Para ejecutar el programa con YARN, se debe ejecutar el siguiente comando:

    yarn jar <archivo.jar> <Nombre clase principal> <argumentos adicionales>

Ejemplo de ejecución:

    yarn jar CodeCountVariable.jar CodeCountVariable /user/pi/codecountvariable/input /user/pi/codecountvariable/output

