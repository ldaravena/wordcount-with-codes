## Configuraciones

Hadoop es altamente configurable y puede resultar un poco abrumador obtener la configuración deseada en el clúster. En el directorio **NodoMaster** se encuentran los archivos de configuración que fueron utilizados durante el desarrollo de este proyecto. Estos archivos deben ser copiados en el directorio de instalación de Hadoop, en el nodo Master. Si se siguieron las instrucciones de instalación que están en el directorio **InstalacionCluster**, la ruta donde se deben copiar los archivos es:

    /opt/hadoop-3.3.1/etc/hadoop/

En el directorio **NodosWorker** están los mismo archivos de configuración, pero estos deben ser copiados a todos los nodos Worker, en la misma ruta que en el nodo Master.