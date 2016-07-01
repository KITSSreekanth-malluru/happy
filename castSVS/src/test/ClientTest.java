package test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.cache.Fqn;

import com.castorama.stock.mchannel.rest.model.Stock;
import com.castorama.stock.mchannel.rest.model.StocksBag;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

public class ClientTest
{static Random r = new Random();
    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException
    {
    	for(int i = 10000000; i< 10020000; i++) {
            read(i);
    	}
        
    	
/*        Stock s1 = new Stock();
        s1.setProdId("24534");
        Stock s2 = new Stock();
        StocksBag sb = new StocksBag(s1, s2);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(System.out, sb);
*/    }
    
    private static void read(int i) {
        try
        {   
            String prodId = "" + (10000000 + r.nextInt(50000) );
            long start = System.currentTimeMillis();
            //URL url = new URL("http://localhost:8080/springREST/stock?prodId=" + prodId + "&postalCode=01000");
            //URL url = new URL("http://epbyminw0088:8180/springRest/stock?prodId="+i+"&storeId=2&channel=1");
            URL url = new URL("http://epbyminw0088:8180/springRest/stock");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setRequestProperty("Content-Type", "apllication/xml");
            conn.setRequestProperty("Accept", "application/json");
            
            InputStream is = conn.getInputStream();
            byte[] bytes = new byte[1024];
            int read = -1;
            StringBuffer sb = new StringBuffer();
            while ((read = is.read(bytes)) > -1) {
                sb.append(new String(bytes, 0, read));
            }
            is.close();
            conn.disconnect();
          System.out.println(sb);
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
