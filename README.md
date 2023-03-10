# Wordcount with Codes
Documentación y programas generados durante el desarrollo de la memoria de título: ***"Estudio empírico del uso de datos codificados para la aplicación WordCount en el ambiente de procesamiento distribuido Hadoop"***, para la obtención del título: **Ingenierio Civil Informático** de la Universidad de Concepción, Chile.

## Descripción de los directorios:

- **CodeCountFixed**: Contiene la aplicación de MapReduce CodeCountFixed además del software **CodeTextFixed** desarrollado para generar los códigos de 3 bytes, el software **DecodeResultFixed** que decodifica el resultado entregado por CodeCountFixed. En el directorio **OtrasVersiones** hay dos versiones de CodeCountFixed: **CodeCountFixed2** que lee un registro a la vez y **CodeCountFixedBytes** que no decodifica los 3 bytes de cada registro.
- **CodeCountFixed4B**: Contiene la aplicación de MapReduce CodeCountFixed4B que utiliza códigos de 4 bytes. **CodeTextFixed4B** es el software desarrollado en Java para crear estos códigos de 4 bytes y **DecodeResultFixed4B** permite decodificar el resultado entregado por CodeCountFixed4B.
- **CodeCountVariable**: Contiene el directorio **CodeCountVariable** donde se encuentra dicha aplicación y la clase *VByteInputFormat* que permite la lectura de códigos de largo variable codificados con VByte. En el directorio **CodeTextVariable** se encuentra el software desarrollado para crear estos códigos VByte. En el directorio **DecodeResultVariable** se encuentra el software que decodifica los resultados entregados por CodeCountVariable. En el directorio **OtrasVersiones** se encuentran 4 versiones de CodeCountVariable. La **V1** realiza la codificación en la clase Mapper. La **V2** no realiza decodificación. La **V3** realiza codificación en el *RecordReader* una vez leídos todos los bytes del registro. La **V4** es la misma que la CodeCountVariable. Esta realiza la decodificación en el *RecordReader* al leer cada byte.
- **Configuraciones**: Contiene las configuraciones del cluster. Para mayor detalle leer el archivo **configuraciones.md**
- **Documentos**: Contiene la Propuesta de Memoria de Título y la **Memoría de Título**.
- **InstalacionCluster**: Contiene las instrucciones para instalar el sistema operativo en las tarjetas MicroSD de las Raspberry Pi 4 utilizadas en esta memoria y las instrucciones de la instalación de Hadoop.
- **ProgramasUtilitarios**: Contiene software y scripts desarrollados para facilitar ciertas tareas. La descripción de cada software se encuentra en el archivo **programas_utilitarios.md**.
- **WordCount**: Contiene la aplicación de MapReduce WordCount.


Autor: Leonardo Aravena Cuevas

Marzo de 2023
