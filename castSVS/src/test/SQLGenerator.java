package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SQLGenerator
{   
    private static Random random = new Random();
    public static void main(String[] args) throws IOException
    {
        java.io.File f = new File("D:/4WorkProject/CAST-ATG9/Stock/bigfile.txt");        
        FileWriter fw = new FileWriter(f);       
        for (int i = 1; i <111; i++)
        {
            for (int j = 10000000; j <10060000; j++)
            {
                String line = writeToFile(i, j);
                fw.write(line + "\n");
            }
            System.out.println("Store " + i);
        }
        fw.close();
       
    }
    
    private static String writeToFile(int pI, int pJ)
    {
        String line = pI + "," + pJ + "," + random.nextInt(2000) + "," + random.nextInt(2000) + ",08252010,123";
        return line;
    }
}
