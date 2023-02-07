wget https://repo1.maven.org/maven2/io/delta/delta-core_2.12/1.0.0/delta-core_2.12-1.0.0.jar &&\
mv delta-core_2.12-1.0.0.jar opt/spark/jars/ &&\
echo "superset string : hive://hive@spark:10000/" &&\
./opt/spark/sbin/start-thriftserver.sh \
  --conf spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension \
  --conf spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog \
  --packages 'io.delta:delta-core_2.12:1.0.0'