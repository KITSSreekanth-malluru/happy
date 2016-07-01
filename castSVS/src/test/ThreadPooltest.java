package test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPooltest
{
    public static void main(String[] args)
    {
        ExecutorService es = Executors.newFixedThreadPool(3);
        System.out.println(es.getClass());
        for (int i = 0; i < 20; i++)
        {
            System.out.println("submit" + i);
            try
            {
                es.submit(new Callable()
                {
                
                    public Object call() throws Exception
                    {
                        Thread.sleep(1000);
                        return "";
                    }
                
                });
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           
            System.out.println("exit" + i);
            
        }
        
    }
}
