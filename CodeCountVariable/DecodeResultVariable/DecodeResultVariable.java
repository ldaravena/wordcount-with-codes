import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;

public class DecodeResultVariable {

    public static void main(String[] args) throws Exception { 
        
        // Archivo de entrada
        String file = args[0];

        // Mapa de entrada
        String mapFile = args[1];

        // Archivo de salida
        String outputFile = file + "_decoded";

        // Mapa para guardar el texto de entrada
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

        // Lectura del map
        File readMap = new File(mapFile);
        FileInputStream fis = new FileInputStream(readMap);
        ObjectInputStream ois = new ObjectInputStream(fis);

        @SuppressWarnings("unchecked")
        HashMap<Integer, String> mapInFile = (HashMap<Integer, String>)ois.readObject();
        ois.close();
        fis.close();

        // Lee cada línea del archivo de entrada
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();

        // Por cada linea:
        while (line != null) {

            // Separa las palabras
            StringTokenizer itr = new StringTokenizer(line);

            while(itr.hasMoreTokens()){

                //La primera palabra es un entero que representa la palabra codificada
                Integer i = Integer.parseInt(itr.nextToken());

                //La segunda palabra es un entero que representa la cantidad de veces que aparece la palabra codificada
                Integer j = Integer.parseInt(itr.nextToken());

                // Agrega la palabra codificada y la cantidad de veces que aparece en el mapa
                map.put(i, j);

            }

            line = br.readLine();
        }
        
        br.close();

        // Por cada clave del map lo decodifica usando mapInFile y lo guarda en un archivo de texto
        FileOutputStream fileOut = new FileOutputStream(outputFile);
        DataOutputStream dos = new DataOutputStream(fileOut);

        for (Integer key : map.keySet()) {
            
            // Si la palabra no está en el mapa de entrada, la ignora
            if(mapInFile.get(key) != null){

                // Escribe en el archivo de salida la palabra decodificada y la cantidad de veces que aparece
                String aux = mapInFile.get(key) + "\t" + map.get(key) + "\n";
                dos.writeBytes(aux);
            }
        }

        fileOut.close();
        dos.close();
    }
}
