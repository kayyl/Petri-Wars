package petriwars;

import java.io.FileInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileParser
{
    //for testing purposes only
    public static void main(String[] args) throws Exception
    {
        Scanner s = new Scanner(System.in);
        System.out.print("Enter name of map file: ");
        String name = s.nextLine();
        s.close();
        if (name.equals(""))
            name = "map_test.bmp";
        File f = new File(name);
                
        printFile(f);
    }
    
    public static byte[][] getByteMap(File f) throws Exception
    {
        byte[] contents = new byte[(int)f.length()];
        FileInputStream in = new FileInputStream(f);
        in.read(contents);
        in.close();
     
        int width = getIntVal(0x12, contents);
        //width = width + 3 & -4;
        int pad = (width + 3 & -4) - width;
        //System.out.println(width + " " + pad);
        int length = getIntVal(0x16, contents);
        //System.out.println(length);
        
        byte[][] mapBytes = new byte[length][width];
        
        int pos = 1078;
        for (int i = length - 1; i >= 0; i--)
        {
            for (int i2 = 0; i2 < mapBytes[i].length; i2++)
                mapBytes[i][i2] = contents[pos++];
            pos += pad;
        }    
        return mapBytes;        
    }
    
    //for testing purposes only
    public static void printFile(File f) throws Exception
    {
        byte[] contents = new byte[(int)f.length()];
        FileInputStream in = new FileInputStream(f);
        in.read(contents);
        in.close();
        
        char[] mapVals = new char[256];
        mapVals[0x00] = '#';
        mapVals[0xF9] = '.';
        mapVals[0xFF] = ' ';
        mapVals[0xFC] = 'N';
        mapVals[0xFA] = 'S';
        mapVals[0xFB] = 'G';
        
        int width = getIntVal(0x12, contents);
        //width = width + 3 & -4;
        int pad = (width + 3 & -4) - width;
        //System.out.println(width + " " + pad);
        int length = getIntVal(0x16, contents);
        //System.out.println(length);
        
        char[][] mapText = new char[length][width];
        
        int pos = 1078;
        for (int i = length - 1; i >= 0; i--)
        {
            for (int i2 = 0; i2 < mapText[i].length; i2++)
                mapText[i][i2] = mapVals[contents[pos++] & 255];
            pos += pad;
        }
        
        PrintWriter out = new PrintWriter(new File("output.txt"));
        for (int i = 0; i < mapText.length; i++)
        {
            for (int i2 = 0; i2 < mapText[i].length; i2++)
            {
                out.print(mapText[i][i2]);
            }
            out.println();
        }
        out.close();
    }
    
    public static int getIntVal(int startAddress, byte[] b)
    {
        return (b[startAddress + 3] << 24)
            + ((b[startAddress + 2] & 0xFF) << 16)
            + ((b[startAddress + 1] & 0xFF) << 8)
            + (b[startAddress] & 0xFF);
    }//end getIntVal
}