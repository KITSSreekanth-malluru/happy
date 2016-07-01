package test;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jboss.cache.Region;
import org.jboss.cache.config.EvictionConfig;
import org.jboss.cache.config.EvictionRegionConfig;
import org.jboss.cache.eviction.LRUAlgorithmConfig;
import org.jboss.cache.jmx.JmxRegistrationManager;

import com.castorama.stock.mchannel.cache.CacheOwner;

public class TestCache
{
    public static void main(String[] args) throws MalformedObjectNameException, NullPointerException
    {
        CacheFactory factory = new DefaultCacheFactory();
        Cache cache = factory.createCache(TestCache.class.getResourceAsStream("cache-configuration.xml"));        
      
        
        Fqn fqn = Fqn.fromString("/org/jboss/data1");
       
        
        cache.create();
        cache.start();

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName on = new ObjectName("jboss.cache:service=Cache");
        JmxRegistrationManager jmxManager = new JmxRegistrationManager(server, cache, on);
        jmxManager.registerAllMBeans();
        
        //Region region = cache.getRegion(fqn, true);
        //region.setEvictionRegionConfig(erc);
        Node n = null;// = cache.getRoot().addChild(fqn);
        
        
        //EvictionConfig ec = cache.getConfiguration().getEvictionConfig();
        //if (ec == null) {
        //    ec = new EvictionConfig();
        //    ec.setWakeupInterval(5000);
         //   cache.getConfiguration().setEvictionConfig(ec);
            
       // }
        //cache.getRegion(fqn, true);
        
        LRUAlgorithmConfig lruAlgorithm = new LRUAlgorithmConfig(-1, -1, 3);       
        EvictionRegionConfig erc = new EvictionRegionConfig(fqn, lruAlgorithm);          
        cache.getRegion(fqn, true).setEvictionRegionConfig(erc);
        
        n.addChild(Fqn.fromString("data11"));
        n.addChild(Fqn.fromString("data12"));
        n.addChild(Fqn.fromString("data13"));
        n.addChild(Fqn.fromString("data14"));
        n.addChild(Fqn.fromString("data15"));
        n.addChild(Fqn.fromString("data16"));
        
        System.out.println(n.getChildren().size());
        //System.out.println(fqn.get(0));
        //System.out.println(cache.getNode(fqn));
        
        //System.out.println("fff");
        //System.out.println(cache.getNode("/test/test"));
        //cache.put("/test/test", new HashMap());
        //System.out.println(cache.getNode("/test/test"));
       
        //Fqn.fromString("/test/test")
        //cache.addCacheListener(arg0)
        while (true) {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(n.getChildren().size());
        }
        
    }
}
