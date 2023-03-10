import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

// Clase para leer el archivo binario de entrada codificado con VByte
public class VByteInputFormat extends FileInputFormat<NullWritable, IntWritable> {

    @Override
    public RecordReader<NullWritable, IntWritable> createRecordReader(InputSplit split, TaskAttemptContext context)
        throws IOException, InterruptedException {
        return new VByteRecordReader();
    }

    @Override
    protected boolean isSplitable(JobContext context, Path file) {
        return true;
    }

    // Método para generar los splits
    @Override
    public List<InputSplit> getSplits(JobContext context) throws IOException {

        Configuration conf = context.getConfiguration();
        List<FileStatus> files = listStatus(context);
        List<InputSplit> splits = new ArrayList<InputSplit>();

        // Por cada archivo
        for (FileStatus file : files) {
            Path path = file.getPath();
            FileSystem fs = path.getFileSystem(conf);
            
            // Arreglo con los bloques del archivo
            BlockLocation[] blockLocations = fs.getFileBlockLocations(file, 0, file.getLen());

            // Lista para almacenar las posiciones de los registros que limitan los splits
            List<Long> recordBoundaries = new ArrayList<Long>();

            FSDataInputStream in = fs.open(path);

            // Agrega el límite del primer split (inicio del primer bloque)
            recordBoundaries.add(blockLocations[0].getOffset());

            // Por cada bloque
            for (int i = 0; i < blockLocations.length - 1; i++) {

                // start: posición del primer byte del bloque siguiente
                long start = blockLocations[i+1].getOffset();
                
                // Se posiciona en el primer byte del bloque siguiente
                in.seek(start);
                
                while (true) {
                    
                    // Lee un byte
                    int b = in.read();

                    // Si el bit más significativo es 1, se ha encontrado el límite del registro
                    if ((b & 0x80) != 0) {

                        // Agrega la posición del límite del registro
                        recordBoundaries.add(in.getPos());
                        break;
                    }
                }
            }

            // Cierra el archivo
            in.close();

            // Agrega el límite del último split (fin del último bloque) si es que este no está en la lista
            long lastBoundarie = blockLocations[blockLocations.length-1].getOffset() + blockLocations[blockLocations.length-1].getLength();
            if (!recordBoundaries.contains(lastBoundarie)) recordBoundaries.add(lastBoundarie);

            // Genera los splits a partir de las posiciones de los registros que limitan los splits
            for (int i = 0; i < recordBoundaries.size()-1; i++) {
                
                long start = recordBoundaries.get(i);
                long end = recordBoundaries.get(i+1);

                // Agrega el split desde "start" con el tamaño "end - start"
                splits.add(new FileSplit(path, start, end - start, null));
            }
        }
        
        return splits;
    }      
}

// Clase para leer los registros
class VByteRecordReader extends RecordReader<NullWritable, IntWritable> {

    private FSDataInputStream in;
    private long start;
    private long end;
    private long pos;

    // Tamaño máximo de un registro
    private int MAX_RECORD_SIZE;

    //No se utiliza la clave
    private NullWritable key = NullWritable.get();
    private IntWritable value = new IntWritable();

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {

        FileSplit fileSplit = (FileSplit) split;
        Configuration conf = context.getConfiguration();
        Path path = fileSplit.getPath();
        FileSystem fs = path.getFileSystem(conf);

        // Obtiene el tamaño máximo de un registro desde el main
        MAX_RECORD_SIZE = Integer.parseInt(conf.get("max.vbyte.record.size"));

        in = fs.open(path);
        start = fileSplit.getStart();
        end = start + fileSplit.getLength();
        pos = start;
        in.seek(start);
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {

        // Mientras no se llegue al final del split
        if (pos < end) {

            int length = 0;
            byte[] bytes = new byte[MAX_RECORD_SIZE];

            int number = 0;
            int i = 0;

            // Busca el siguiente registro
            while (true) {

                // Lee un byte
                int b = in.read();

                // Lo guarda como tipo byte en el arreglo bytes
                bytes[length] = (byte)b;
                length++;

                // Decodifica el byte a entero
                number |= (bytes[i] & 0x7F) << (7 * i);
                i++;

                // Si el bit más significativo es 1 se completó el registro
                if ((b & 0x80) != 0) {

                    // Establece el valor de value
                    value.set(number);

                    break;
                }

                // Si el registro es más largo de lo permitido se indica el error
                if (length == MAX_RECORD_SIZE) {
                    throw new IOException("ERROR: Registro excede longitud máxima");
                }
            }

            // Actualiza la posición
            pos = in.getPos();

            return true;
        }

        return false;
    }

    @Override
    public NullWritable getCurrentKey() throws IOException, InterruptedException {
        return key;
    }

    @Override
    public IntWritable getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return (float)(pos - start) / (end - start);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}

