import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.StringTokenizer; 

public class CodeTextFixed{

    // Tamaño del record a leer en la aplicación MapReduce
    public static int RECORD_SIZE = 60;

    public static void main(String[] args) throws Exception { 

        // Archivo de texto de entrada
        String file = args[0];
        
        // Codificación de las palabras
        Integer code = 0;

        // Cuenta la cantidad de bytes que se se escriben
        long bytesCount = 0;

        // Mapa para asignar a cada palabra un entero de 3 bytes
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        // Mapa para decodificar el archivo binario de salida
        HashMap<Integer, String> InvertedMap = new HashMap<Integer, String>();

        try{
            
            // Lee el archivo de texto
            BufferedReader fileBufferRead = new BufferedReader(new FileReader(file));

             // Guarda el texto codificado como bytes en archivo binario
            DataOutputStream dosBytes = new DataOutputStream(new FileOutputStream(file + "_encoded.bin"));

            String line;
            
            // Lee cada linea
            while( (line = fileBufferRead.readLine()) != null ){

                // Separa las palabras
                StringTokenizer itr = new StringTokenizer(line);

                while(itr.hasMoreTokens()){

                    String word = itr.nextToken();
                    
                    // Comprueba si la palabra ya se encuentra codificada
                    boolean isWordPresent = map.containsKey(word);
                    
                    // Si no, crea el código, lo guarda en el mapa y en el archivo binario de
                    if (!isWordPresent){

                        map.put(word, code);
                        
                        int codeInt = code.intValue();

                        byte[] intBytes = new byte[3];
                        intBytes[0] = (byte) (codeInt >> 16);
                        intBytes[1] = (byte) (codeInt >> 8);
                        intBytes[2] = (byte) (codeInt);

                        dosBytes.write(intBytes);
                        code++;
                        bytesCount += 3;
                    }

                    // Si ya está en el mapa recupera el código y lo guarda en el arreglo
                    else{
                        
                        int codeInt = map.get(word).intValue();

                        byte[] intBytes = new byte[3];
                        intBytes[0] = (byte) (codeInt >> 16);
                        intBytes[1] = (byte) (codeInt >> 8);
                        intBytes[2] = (byte) (codeInt);

                        dosBytes.write(intBytes);
                        bytesCount += 3;
                    }
                }
            }
            
            // Comprueba si la cantidad de bytes que se escribieron es múltiplo de RECORD_SIZE
            // Si no lo es, se agregan bytes de relleno con valor ff hasta que sea múltiplo de RECORD_SIZE
            if (bytesCount % RECORD_SIZE != 0){

                long padding = RECORD_SIZE - (bytesCount % RECORD_SIZE);

                for (int i = 0; i < padding; i++){

                    dosBytes.writeByte(255);
                }
            }

            fileBufferRead.close();
            dosBytes.flush();
            dosBytes.close();
            
            // Genera el mapa de decodificación
            map.forEach(

                (key, value) -> InvertedMap.put(value, key)
            );

            // Guardar map en archivo
            File mapFile = new File(file + "_dictionary");
            FileOutputStream fos = new FileOutputStream(mapFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(InvertedMap);
            oos.flush();
            oos.close();
            fos.close();
        }

        catch(Exception e){

            e.printStackTrace();
        }
    }
}
