//---  Stock Visualization Installation Notes  ---//

The steps below describe how to enable Stock Visualization functionality in you local version of Castorama.fr site.

1. DB schema.
1.1. Create new oracle schema.

CREATE USER           CAST_STOCK
IDENTIFIED BY         CAST_STOCK
DEFAULT   TABLESPACE  CAST_USERS
TEMPORARY TABLESPACE  TEMP;

GRANT DBA TO CAST_STOCK;

1.2. Create stock tables in CAST_STOCK schema using the @sql/cast_stock.sql
1.3. This installation does not provide stock data, please use import.

2. JBoss server (cast-jboss-stock). 

2.1. Create new JBoss server by making a copy of ‘default’ folder and renaming it to “cast-jboss-stock”.
2.2. Edit “%JBOSS-HOME%/server/cast-jboss-stock/conf/jboss-service.xml” file. Specify port/host mapping to prevent any conflicts with other Jboss instances.
2.2. Delete “jboss-cache-jdk50.jar” from “%JBOSS-HOME%/server/cast-jboss-stock/lib” folder.
2.3. Copy all jars from “install/lib” to “%JBOSS-HOME%/server/cast-jboss-stock/lib” folder.
2.4. Copy “install/atg-ds.xml” into “%JBOSS-HOME%/server/cast-jboss-stock/deploy” folder and adjust it to your environment.
2.5. Add ‘stock_ds’ data source definition to “%JBOSS-HOME%/server/cast-jboss-integration/deploy/atg-ds.xml”.
2.6. Edit “%JBOSS-HOME%/server/cast-jboss-stock/conf/props/jmx-console-users.properties” file. Ensure that admin/admin credentials are specified for JMX access.

3. Configuration.

3.1. Adjust /com/castorama/integration/stock/RemoteStockCacheAccessor to your environment:
   -specify the server and RMI port where cast-jboss-stock instance is running;
   -edit JMXConsoleUserName/JMXConsolePassword if non default credentials are specified in jmx-console-users.properties file (step 2.6.).
3.2. Adjust /com/castorama/stockvisualization/StockVisualizationManager to your environment:
   -point URLTemplate property to cast-jboss-stock web service.

4. Build.

4.1. Build application using “ant all” target from castSVS module root folder.
4.2. Execute “ant deploy-ear” to copy ear file to cast-jboss-stock instance.

5. Run.

5.1. Make changes in “bin/env.bat” to set system variables properly.
5.2. Use “bin/run-jboss.bat” or “bin/debug-jboss.bat” as samples to start server.

6. Using.

6.1. Pages for testing Web Service:
   http://HOST:PORT/stock/svtest.jsp (By Postal Code)
   http://HOST:PORT/stock/indext.jsp (By Store Id)

6.2. Integration supervision page:
   http://HOST:PORT/stock/stockAvailabilitySupervision.jsp
