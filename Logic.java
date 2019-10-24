import java.awt.image.BufferedImage;



public class Logic{



private static void printPixelValues(BufferedImage image){
    int w = image.getWidth();
    int h = image.getHeight();
    int[] RGBarray = image.getRGB(0,0,w,h,null,0,w);

    for(int i=0; i<=10; i++)
    {
        //print out the first 10 RGB int values - testing purposes
        System.out.println(RGBarray[i]);
    }
}



}
















// import java.io.*;
// import java.util.*;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import javax.imageio.ImageIO;
// import java.awt.image.DataBufferByte;

// public class Main{
//     public static void main(String[] args) throws Exception {

//         BufferedImage originalImage = ImageIO.read(new File("test.jpg"));
//         byte[] pixels = ((DataBufferByte)originalImage.getData().getDataBuffer()).getData();
//         BufferedImage bufferedImage = new BufferedImage(406, 260, BufferedImage.TYPE_INT_RGB);
//         ImageIO.write(bufferedImage, "jpg", new File("new-image.jpg"));
//         for(int i = 0; i < pixels.length; i++){
//             // Code for changing pixel data;
//             //pixels[i] = (byte) 37; // White
//             // Syntax for setting pixel color: 0x(HEX COLOR CODE)
//         }
//     }
// }
