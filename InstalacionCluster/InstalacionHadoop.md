## **Instalación de Hadoop**

Las siguientes instrucciones están basadas en la documentación generado por el Dr. Zheng Li (*muchas gracias*), que a su vez se basó en la información obtenida de:
- https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html
- https://dev.to/donaldsebleung/setting-up-a-single-node-hadoop-cluster-p0a
- https://medium.com/analytics-vidhya/setting-up-hadoop-3-2-1-d5c58338cba1
- https://dev.to/donaldsebleung/setting-up-a-single-node-hadoop-cluster-p0a
- https://stackoverflow.com/questions/14582387/hadoop-datanodes-available-0-0-total-0-dead

Esta instalación se realizó en un clúster de 4 nodos Raspberry Pi 4 con el sistema operativo Ubuntu Server 22.04.1 LTS (64-bit).

### **1) Instalación de Java en todos los Nodos**

Las siguientes instrucciones deben aplicarse en todos los nodos del clúster:

1. Para instalar Java en todos los nodos, se debe ejecutar el siguiente comando:

        sudo apt install openjdk-8-jdk

2. Editar la dirección del HOME:

        nano $HOME/.profile

3. Agregar las siguiente líneas al final del archivo:

        export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-arm64"
        export HADOOP_HOME="/opt/hadoop-3.3.1"

4. Guardar y salir del archivo.
5. Aplicar los cambios con:

        source $HOME/.profile
6. Descargar la versión 3.3.1 de Hadoop desde https://hadoop.apache.org/release/3.3.1.html (versión más reciente de Hadoop para arquitectura ARM) con el comando:

        wget https://archive.apache.org/dist/hadoop/common/hadoop-3.3.1/hadoop-3.3.1-aarch64.tar.gz

 7. Descomprimir el archivo descargado con

        tar xvf hadoop-3.3.1-aarch64.tar.gz

8. Mover el directorio descomprimido a la ubicación del HADOOP_HOME con el comando:

        sudo mv hadoop-3.3.1 "$HADOOP_HOME"

9. Eliminar el archivo descargado con el comando:

        rm hadoop-3.3.1-aarch64.tar.gz

10. Se agregan los binarios del Hadoop a la dirección HOME:
    
        echo "export PATH=\"\$PATH:\$HADOOP_HOME/bin\"" >> $HOME/.profile
        echo "export PATH=\"\$PATH:\$HADOOP_HOME/sbin\"" >> $HOME/.profile

11. Se aplican los cambios con:

        source $HOME/.profile

12. Se ingresa JAVA_HOME en el archivo de configuración de el ambiente de Hadoop:

        echo "export JAVA_HOME=\"/usr/lib/jvm/java-8-openjdk-arm64\"" >> "$HADOOP_HOME/etc/hadoop/hadoop-env.sh"

### **2) SOLO PARA EL NODO MASTER**

1. Se obtiene un texto de prueba para probar el funcionamiento del Hadoop:

        wget https://raw.githubusercontent.com/ErikSchierboom/sentencegenerator/master/samples/the-king-james-bible.txt

2. Se crea el directorio de entrada para las pruebas:

        mkdir input

3. Se mueve el archivo de texto a la carpeta de entrada:

        mv the-king-james-bible.txt input

4. Se ejecuta la aplicación MapReduce **WordCount** con el directorio de entrada y el directorio de salida:

        hadoop jar "$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.1.jar" wordcount input output

5. Se confirma la salida del output con:

        ls -lh output

6. Se verifican los resultados de las últimas 10 líneas del archivo de salida con:

        tail output/part-r-00000

7. Debe obtenerse el resultado:


        youth:  7
        youth;  8
        youth?  2
        youthful        1
        youths  1
        youths, 1
        zeal    13
        zeal,   3
        zealous 8
        zealously       2

8. Ahora se procede a instalar el clúster pseudo-distribuido de Hadoop. Para esto se modifica el archivo **core-site.xml** con el comando:

        nano "$HADOOP_HOME/etc/hadoop/core-site.xml"

9. Se agrega la configuración:
    
            <configuration>
                <property>
                    <name>fs.defaultFS</name>
                    <value>hdfs://localhost:9000</value>
                </property>
            </configuration>

10. Se modifica el archivo **hdfs-site.xml** con el comando:

        nano "$HADOOP_HOME/etc/hadoop/hdfs-site.xml"

11. Se agrega la configuración, para establecer el número de réplicas de los bloques de los archivos en 1:
    
            <configuration>
                <property>
                    <name>dfs.replication</name>
                    <value>1</value>
                </property>
            </configuration>

12. Se generan las SSH keys con el comando: (se debe pulsar enter en cada paso)

        ssh-keygen

13. Configurar las keys autorizadas con el comando:

        cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys

14. Cambiar los permisos de la carpeta **.ssh** con el comando:

        chmod 600 $HOME/.ssh/authorized_keys

15. Confirmar que se puede acceder a localhost sin contraseña con el comando:

        ssh localhost

16. Si todo esta correcto salir del ssh con el comando:

        exit

17. Cambiar el dueño de $HADOOP_HOME al usuario actual con:

        sudo chown -R "$(whoami):" "$HADOOP_HOME"

18. Editar /etc/hosts/ borrando la línea que empieza con 127.0.1.1 y agregar la ip local del nodo junto con su nombre:

        sudo nano /etc/hosts

19. En este ejemplo se agrega la siguiente línea:

        192.168.1.99 master

20. Para mantener la configuración de los hosts sin perderla al reiniciar la máquina se debe editar el archivo cloud.cfg con el comando:

        sudo nano /etc/cloud/cloud.cfg

21. Al cual se le debe comentar o eliminar la línea:

        - update_etc_hosts

22. Formatear el sistema de archivos HDFS con el comando:

        hdfs namenode -format

23. Iniciar HDFS con el comando:

        start-dfs.sh

24. Desde un computador conectado a la red local ingresar a la versión web de HDFS y verificar que funcione. En este ejemplo dirección web es http://192.168.1.99:9870

25. Crear el directorio en HDFS para los trabajos de MapReduce:

        hadoop fs -mkdir -p "/user/$(whoami)"

26. Copiar el texto descargado anteriormente al sistema de archivos distribuido de Hadoop (HDFS) con:
    
        hadoop fs -mkdir input
        hadoop fs -put input/the-king-james-bible.txt input

27. Ejecutar la aplicación WordCount con el comando:

        hadoop jar "$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.1.jar" wordcount input output

28. Verificar que se haya creado el directorio de salida con:

        hadoop fs -ls output

29. Verificar el contenido de las últimas 10 líneas del archivo de salida con:

        hadoop fs -tail output/part-r-00000

30. Debe obtenerse el resultado:

        youth:  7
        youth;  8
        youth?  2
        youthful        1
        youths  1
        youths, 1
        zeal    13
        zeal,   3
        zealous 8
        zealously       2

31. Para detener el clúster pseudo-distribuido de Hadoop se ejecuta el comando:

        stop-dfs.sh

### **3) PARA CADA UNO DE LOS NODOS WORKER**

1. Generar las SSH keys con el comando: (se debe pulsar enter en cada paso)

        ssh-keygen

2. Configurar las keys autorizadas con el comando:

        cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys

3. Cambiar los permisos de la carpeta **.ssh** con el comando:

        chmod 600 $HOME/.ssh/authorized_keys

4. Confirmar que se puede acceder a localhost sin contraseña con el comando:

        ssh localhost

5. Si todo esta correcto salir del ssh con el comando:

        exit

6. Cambiar el dueño de $HADOOP_HOME al usuario actual con:

        sudo chown -R "$(whoami):" "$HADOOP_HOME"

7. Editar /etc/hosts/ borrando la línea que empieza con 127.0.1.1 y se agregan las IPs de todos los nodos del clúster junto con sus nombres:

        sudo nano /etc/hosts

8. En este ejemplo se agregan las siguientes líneas:

        192.168.1.99 master
        192.168.1.98 worker1
        192.168.1.97 worker2
        192.168.1.95 worker3

9. Para mantener la configuración de los hosts sin perderla al reiniciar la máquina se debe editar el archivo cloud.cfg con el comando:

        sudo nano /etc/cloud/cloud.cfg

10. Al cual se le debe comentar o eliminar la línea:

        - update_etc_hosts

11. Compartir las SSH keys con los otros nodos del clúster (omitir la línea correspondiente al nodo worker actual):

        ssh-copy-id master
        ssh-copy-id worker1
        ssh-copy-id worker2
        ssh-copy-id worker3

### **4) PARA EL NODO MASTER**

1. Editar el archivo hosts para agregar los nombres de los nodos worker:

        sudo nano /etc/hosts

2. En este ejemplo se agregan las siguientes líneas:

        192.168.1.98 worker1
        192.168.1.97 worker2
        192.168.1.95 worker3

3. Compartir las SSH keys con los nodos Worker:

        ssh-copy-id worker1
        ssh-copy-id worker2
        ssh-copy-id worker3

4. Editar el archivo **core-site.xml** con el comando:

        nano "$HADOOP_HOME/etc/hadoop/core-site.xml"

5. Se cambia la configuración, para establecer el nombre del nodo master:
    
            <configuration>
                <property>
                    <name>fs.defaultFS</name>
                    <value>hdfs://master:9000</value>
                </property>
            </configuration>

6. Editar el archivo **hdfs-site.xml** con el comando:

        nano "$HADOOP_HOME/etc/hadoop/hdfs-site.xml"

7. Se edita la configuración de esta manera:

        <configuration>
            <property>
                <name>dfs.namenode.name.dir</name>
                <value>/opt/hadoop-3.3.1/data/nameNode</value>
            </property>
            <property>
                <name>dfs.datanode.data.dir</name>
                <value>/opt/hadoop-3.3.1/data/dataNode</value>
            </property>
            <property>
                <name>dfs.replication</name>
                <value>1</value>
            </property>
        </configuration>

8. Se agregan los nodos workers al archivo **workers** con el comando:

        nano "$HADOOP_HOME/etc/hadoop/workers"

9. Borrar la línea que dice **localhost** y agregar los nombres de los nodos workers:

        worker1
        worker2
        worker3

10. Copiar los archivos desde el nodo master a los nodos workers usando **scp**:

        scp /opt/hadoop-3.3.1/etc/hadoop/* worker1:/opt/hadoop-3.3.1/etc/hadoop/
        scp /opt/hadoop-3.3.1/etc/hadoop/* worker2:/opt/hadoop-3.3.1/etc/hadoop/
        scp /opt/hadoop-3.3.1/etc/hadoop/* worker3:/opt/hadoop-3.3.1/etc/hadoop/

11. Formatear el sistema de archivos HDFS con el comando:

        hdfs namenode -format

12. Hacerlo correr con el comando:

        start-dfs.sh

13. Exportar los paths:

        export HADOOP_COMMON_HOME=$HADOOP_HOME
        export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
        export HADOOP_HDFS_HOME=$HADOOP_HOME
        export HADOOP_MAPRED_HOME=$HADOOP_HOME
        export HADOOP_YARN_HOME=$HADOOP_HOME


### **5) SOLO NODOS WORKER**

1. Editar la configuración de YARN en el archivo **yarn-site.xml** con el comando:

        nano "$HADOOP_HOME/etc/hadoop/yarn-site.xml"

2. Agregar a la sección **configuration** lo siguiente:

        <property>
            <name>yarn.resourcemanager.hostname</name>
            <value>master</value>
        </property>

### **6) PARA EL NODO MASTER**

1. Iniciar YARN con el comando:

        start-yarn.sh

2. Verificar que esté funcionando con:

        yarn node -list

3. La herramienta de administración web de YARN se puede acceder desde el navegador con la URL: http://192.168.1.99:8088

4. Probar el funcionamiento del clúster con la aplicación que calcula decimales de Pi:

        yarn jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.1.jar pi 16 1000

5. La ejecución debe devolver algo así:

        Job Finished in 5.917 seconds
        Estimated value of Pi is 3.14250000000000000000

6. Se ha finalizado la instalación de Hadoop.

---
Las configuraciones aquí descritas son básicas y se recomienda modificar los archivos de configuración con los valores presentados en este repositorio.

Autor: Leonardo Aravena Cuevas

Marzo de 2023






