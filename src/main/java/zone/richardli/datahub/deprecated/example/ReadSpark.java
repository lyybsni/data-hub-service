package zone.richardli.datahub.deprecated.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

/**
 * This is an example of Sparking reading HBase.
 */
@Slf4j
@Deprecated
public class ReadSpark implements Serializable {

    public void execute(JavaSparkContext javaSparkContext) {
        SparkSession spark = SparkSession.builder().master("yarn").master("local").appName("hello-wrold")
                // .config("spark.some.config.option", "some-value")
                .getOrCreate();

        Configuration conf = HBaseConfiguration.create();

        Scan scan = new Scan();
        // 指定列，可以不写相当于select * from XX
        byte[] family_bytes = "info".getBytes();

        byte[] user_id_bytes = "user_id".getBytes();

        byte[] birthday_bytes = "birthday".getBytes();
        byte[] gender_bytes = "gender".getBytes();
        byte[] user_type_bytes = "user_type".getBytes();
        scan.addFamily(family_bytes);

        scan.addColumn(family_bytes, user_id_bytes);

        scan.addColumn(family_bytes, birthday_bytes);
        scan.addColumn(family_bytes, gender_bytes);
        scan.addColumn(family_bytes, user_type_bytes);
        // 设置读取的最大的版本数
        scan.readVersions(3);

        try {
            // 将scan编码
            ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
            String scanToString = Base64.getEncoder().encodeToString(proto.toByteArray());
            // 表名
            String tableName = "test_info";
            conf.set(TableInputFormat.INPUT_TABLE, tableName);
            conf.set(TableInputFormat.SCAN, scanToString);
            // ZooKeeper集群
            conf.set("hbase.zookeeper.quorum", "127.0.0.1");
            conf.set("hbase.zookeeper.property.clientPort", "2181");
            conf.set("hbase.master", "127.0.0.1");

            // 将HBase数据转成RDD
            JavaPairRDD<ImmutableBytesWritable, Result> HBaseRdd = javaSparkContext.newAPIHadoopRDD(conf, TableInputFormat.class,
                    ImmutableBytesWritable.class, Result.class);

            // 再将以上结果转成Row类型RDD
            JavaRDD<Row> HBaseRow = HBaseRdd.map(new Function<>() {

                private static final long serialVersionUID = 1L;

                @Override
                public Row call(Tuple2<ImmutableBytesWritable, Result> tuple2) {
                    Result result = tuple2._2;
                    String rowKey = Bytes.toString(result.getRow());
                    log.info("INFO - {}", rowKey);
                    String birthday = Bytes.toString(result.getValue(family_bytes, birthday_bytes));
                    String gender = Bytes.toString(result.getValue(family_bytes, gender_bytes));
                    String user_type = Bytes.toString(result.getValue(family_bytes, user_type_bytes));
                    return RowFactory.create(rowKey, birthday, gender, user_type);
                    // return RowFactory.create(birthday, gender, user_type);
                }
            });

            show(spark, HBaseRow);

            // 获取birthday多版本数据
            JavaRDD<Row> multiVersionHBaseRow = HBaseRdd
                    .mapPartitions(new FlatMapFunction<>() {


                        private static final long serialVersionUID = 1L;

                        @Override
                        public Iterator<Row> call(Iterator<Tuple2<ImmutableBytesWritable, Result>> t) {
                            // TODO Auto-generated method stub
                            List<Row> rows = new ArrayList<>();
                            while (t.hasNext()) {
                                Result result = t.next()._2();
                                String rowKey = Bytes.toString(result.getRow());
                                // 获取当前rowKey的family_bytes列族对应的所有Cell
                                List<Cell> cells = result.getColumnCells(family_bytes, birthday_bytes);
                                for (Cell cell : cells) {
                                    String birthday = Bytes.toString(CellUtil.cloneValue(cell));
                                    rows.add(RowFactory.create(rowKey, birthday, cell.getTimestamp()));
                                }
                            }
                            return rows.iterator();
                        }
                    });
            List<StructField> multiVersionStructFields = Arrays.asList(
                    DataTypes.createStructField("rowKey", DataTypes.StringType, true),
                    DataTypes.createStructField("birthday", DataTypes.StringType, true),
                    DataTypes.createStructField("timestamp", DataTypes.LongType, true));
            // 构建schema
            StructType multiVersionSchema = DataTypes.createStructType(multiVersionStructFields);
            // 生成DataFrame
            Dataset<Row> multiVersionHBaseDF = spark.createDataFrame(multiVersionHBaseRow, multiVersionSchema);
            multiVersionHBaseDF.show();

        } catch (Exception e) {
            log.error("Spark Task Exception:", e);
        }
    }

    private void show(SparkSession spark, JavaRDD<Row> HBaseRow) {
        // 顺序必须与构建RowRDD的顺序一致
        List<StructField> structFields = Arrays.asList(
                DataTypes.createStructField("user_id", DataTypes.StringType, true),
                DataTypes.createStructField("birthday", DataTypes.StringType, true),
                DataTypes.createStructField("gender", DataTypes.StringType, true),
                DataTypes.createStructField("user_type", DataTypes.StringType, true));
        // 构建schema
        StructType schema = DataTypes.createStructType(structFields);
        // 生成DataFrame
        Dataset<Row> HBaseDF = spark.createDataFrame(HBaseRow, schema);
        HBaseDF.show();
    }

}

