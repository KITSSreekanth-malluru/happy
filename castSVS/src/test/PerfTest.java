package test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerfTest
{
    public static void main(String[] args) throws Exception
    {
       
        List<Callable<String>> calls = new ArrayList<Callable<String>>();
        for (int i = 0; i < 40000; i++)
        {
            calls.add(new Callable<String> () {

                public String call() throws Exception
                {
                    read();
                    return "OK";
                }
                
            });
        }
        ExecutorService es = Executors.newFixedThreadPool(20);
        es.invokeAll(calls);
        es.shutdown();
        System.out.println("end" + (total / 40000));
        
    }
    
    static Random r = new Random();
    static int i = 0;
    static long total = 0;
    private static void read() {
        try
        {   
            String prodId = "" + (10000000 + r.nextInt(50000) );
            long start = System.currentTimeMillis();
            //URL url = new URL("http://localhost:8080/springREST/stock?prodId=" + prodId + "&postalCode=01000");
            URL url = new URL("http://localhost:8080/springREST/stock?prodId=" + prodId + "&storeId=" + (1 + r.nextInt(100)));
          
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("accept", "apllication/json");
            InputStream is =  conn.getInputStream();
            byte[] bytes = new byte[1024];
            int read = -1;
            //StringBuffer sb = new StringBuffer();
            while ((read = is.read(bytes)) > -1) {
                //sb.append(new String(bytes, 0, read));
            }
            is.close();
            conn.disconnect();
          
           // System.out.println(sb);
            total += (System.currentTimeMillis() - start);
            System.out.println("" + (i++) + ":" + (System.currentTimeMillis() - start));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
