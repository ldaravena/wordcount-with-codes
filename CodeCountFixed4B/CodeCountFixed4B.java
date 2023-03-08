import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FixedLengthInputFormat;

public class CodeCountFixed4B{
  // Clase Mapper<Input Key, Input Value, Output Key, Output Value>
 
  public static class CodedMapper
       extends Mapper<Object, BytesWritable, IntWritable, IntWritable>{
    
    private final static IntWritable one = new IntWritable(1);

    public void map(Object key, BytesWritable value, Context context
                    ) throws IOException, InterruptedException {
    
      // Ciclo para leer los 80 bytes de "value" y separarlos cada 4 bytes
      for (int i = 0; i < 80; i += 4) {
          
          // Concatena los 4 bytes de "value"
          ByteBuffer byteBuffer = ByteBuffer.wrap(value.getBytes(), i, 4);
          
          // Los 4 bytes los castea como entero y crear el IntWritable "word"
          IntWritable word = new IntWritable(byteBuffer.getInt());
          
          // Le asigna el valor 1 cada código entero
          context.write(word, one);
      }
    }
  }

  // Clase Reducer<Input Key, Input Value, Output Key, Output Value>
  public static class IntSumReducer
       extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

    private IntWritable result = new IntWritable();

    public void reduce(IntWritable key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {

        sum += val.get();
      }
      
      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();

    // Configuración del formato de entrada
    conf.setInt(FixedLengthInputFormat.FIXED_RECORD_LENGTH, 80);
    
    Job job = Job.getInstance(conf, "CodeCountFixed4B");
    
    job.setJarByClass(CodeCountFixed4B.class);
    
    job.setMapperClass(CodedMapper.class);
    
    job.setCombinerClass(IntSumReducer.class);
    
    job.setReducerClass(IntSumReducer.class);

    // Formato de entrada
    job.setInputFormatClass(FixedLengthInputFormat.class);
    
    job.setOutputKeyClass(IntWritable.class);
    
    job.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
