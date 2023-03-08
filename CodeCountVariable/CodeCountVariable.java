import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.JobContext;

public class CodeCountVariable{

    // Clase Mapper
    public static class CodedVByteMapper extends Mapper<NullWritable, IntWritable, IntWritable, IntWritable> {

        private final static IntWritable one = new IntWritable(1);

        public void map(NullWritable key, IntWritable value, Context context) throws IOException, InterruptedException {

            // Escribir el par (clave, valor)
            context.write(value, one);
        }
    }

    // Clase Reducer
    public static class CodedVByteReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

            int sum = 0;

            for (IntWritable val : values) {
                sum += val.get();
            }

            result.set(sum);
            context.write(key, result);
        }
    }

    // Main
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        // Configuración del tamaño máximo de un registro
        conf.set("max.vbyte.record.size", "5");

        Job job = Job.getInstance(conf, "CodeCountVariable");

        job.setJarByClass(CodeCountVariable.class);

        job.setMapperClass(CodedVByteMapper.class);

        job.setCombinerClass(CodedVByteReducer.class);

        job.setReducerClass(CodedVByteReducer.class);

        job.setOutputKeyClass(IntWritable.class);

        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(VByteInputFormat.class);

        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));

        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
    
    
