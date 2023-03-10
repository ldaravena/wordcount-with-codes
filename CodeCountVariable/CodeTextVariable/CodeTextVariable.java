import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer; 
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CodeTextVariable {

    public static HashMap<String, Integer> getFreqMap(String file){

        // Mapa para guardar la frecuencia de cada palabra
        HashMap<String, Integer> freqMap = new HashMap<String, Integer>();

        try{
            
            BufferedReader fileBufferRead = new BufferedReader(new FileReader(file));

            String line;
            
            // Lee cada linea
            while( (line = fileBufferRead.readLine()) != null ){

                // Separa las palabras
                StringTokenizer itr = new StringTokenizer(line);

                while(itr.hasMoreTokens()){

                    String word = itr.nextToken();
                    
                    // Comprueba si la palabra ya se encuentra en el mapa
                    boolean isWordPresent = freqMap.containsKey(word);
                    
                    // Si no, le asigna una frecuencia de 1
                    if (!isWordPresent){

                        freqMap.put(word, 1);
                    }

                    // Si está, incrementa la frecuencia de la palabra
                    else{

                        freqMap.put(word, freqMap.get(word) + 1);
                    }
                }
            }
            
            // Cierra el archivo
            fileBufferRead.close();
        }

        catch(Exception e){

            e.printStackTrace();
        }

        return freqMap;
    }

    public static HashMap<String, Integer> getCodeMap(HashMap<String, Integer> freqMap){
        
        HashMap<String, Integer> codeMap = new HashMap<String, Integer>();

        // Ordena el mapa de frecuencias por valor, de mayor a menor
        freqMap = freqMap.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(
                Map.Entry::getKey, 
                Map.Entry::getValue, 
                (e1, e2) -> e1, 
                LinkedHashMap::new
            ));

        // Crea un nuevo mapa para asignar a cada palabra el orden en que aparece en el mapa de frecuencias
        int i = 0;

        for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {

            codeMap.put(entry.getKey(), i);
            i++;
        }
        
        return codeMap;
    }

    public static void saveDecodeMap(HashMap<String, Integer> codeMap, String file){
        
        HashMap<Integer, String> InvertedMap = new HashMap<Integer, String>();

        // Genera el mapa de decodificación
        codeMap.forEach(
                (key, value) -> InvertedMap.put(value, key)
            );

        // Guardar el mapa a archivo
        try{

            File mapFile = new File(file + "_dictionaryVByte");
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

    public static void encodeText(HashMap<String, Integer> codeMap, String file){

        try{ 
        
            BufferedReader fileBufferRead = new BufferedReader(new FileReader(file));

            DataOutputStream dataOutput = new DataOutputStream(new FileOutputStream(file + "_encodedVByte.bin"));

            String line;

            while((line = fileBufferRead.readLine()) != null){

                StringTokenizer itr = new StringTokenizer(line);

                while(itr.hasMoreTokens()){

                    String word = itr.nextToken();

                    dataOutput.write(encodeByteArray(codeMap.get(word)));
                }
            }

            fileBufferRead.close();
            dataOutput.flush();   
            dataOutput.close();

        } catch (IOException e){

            e.printStackTrace();
        }
    }

    // Codifica un entero usando VByte
    public static byte[] encodeByteArray(int value) throws IOException {
        
        if (value < 0) {
            throw new IllegalArgumentException("Value must be positive");
        }

        // Tamaño máximo de bytes a utilizar
        byte[] bytes = new byte[5];

        int i = 0;

        // Mientras el valor siga siendo mayor a 127
        while(value > 127) {

            // Se guardan los 7 bites menos signficativos
            bytes[i++] = (byte)(value & 0x7F);

            //Se realiza el desplazamiento de 7 bits a la derecha
            value>>>=7;
        }

        // Se guarda el último valor del último byte y el bit más significativo se establece en 1
        bytes[i++] = (byte)(value | 0x80);

        // Se crea un nuevo arreglo de bytes que sea del tamaño de bytes utilizados
        byte[] result = new byte[i];

        // Se copia el contenido del arreglo "bytes" al nuevo arreglo "result"
        System.arraycopy(bytes, 0, result, 0, i);

        return result;
    }

    public static void main(String[] args) throws Exception {

        String file = args[0];

        // Obtiene el mapa de frecuencias
        HashMap<String, Integer> freqMap = getFreqMap(file);

        // Obtiene el mapa de codificación
        HashMap<String, Integer> codeMap = getCodeMap(freqMap);

        // Guarda el mapa de decodificación
        saveDecodeMap(codeMap, file);

        // Codifica el texto
        encodeText(codeMap, file);   
    }
}

