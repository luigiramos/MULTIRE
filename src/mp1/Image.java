package mp1;

import java.awt.*;
import java.awt.event.*;
import com.sun.image.codec.jpeg.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.*;
import java.util.Arrays;
import java.util.*;

import javax.swing.*;
import java.awt.image.ColorModel;

class Image extends JFrame {

    JTextField jTextArea1 = new JTextField();
    JTextField jTextArea2 = new JTextField();
    static int input = 1169;
    static double[][] colorHist = new double[51][159]; //Histogram of input image and 50 images
    static int sum=0;
    static double[] rankings = new double[10];
    static HashMap<Double, Integer> h = new HashMap<Double, Integer>();
    
	//shows a JPEG on the screen on the screen at x,y

    public void showJPEG(int x, int y, Graphics2D g2, String path, String filename ) {
    	
        BufferedImage bi = null;        
        String outputFileName =  path +
                                  File.separatorChar + filename;

        try {


            File file = new File(outputFileName);
            FileInputStream in = new FileInputStream(file);

            // decodes the JPEG data stream into a BufferedImage

            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
            bi = decoder.decodeAsBufferedImage();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            
        }

        if (bi == null) {
            /* null file */
            return;
        }

        g2.drawImage(bi, x, y ,this);
    }
	
	public Image() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}


       public void init(int pos, int name){

	Graphics g = this.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        
        //displays 000.jpg at C:\ in the window
        showJPEG(pos,50,g2,"D:\\", name+".jpg");
        

    }
    
    public void getRGB(int x, int y, int z, String Path, String Filename){

   //gets the RGB and Luv value at x, y    	
       BufferedImage bi1 = null;
       int RGB1;
       int i,j;
       int totalPixels;

       try {

            File file = new File(Path, Filename);
            FileInputStream in = new FileInputStream(file);

            // decodes the JPEG data stream into a BufferedImage

            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
            bi1 = decoder.decodeAsBufferedImage();
            
        } catch (Exception ex) {
            /*file arror*/
        }

        if (bi1 == null) {
            /*null file*/
            return;
        }

        totalPixels = bi1.getHeight() * bi1.getWidth();

        ColorModel CM;
        CM = bi1.getColorModel();
        
        RGB1 = bi1.getRGB(x,y); //get the RGB value at x,y of the image
        
        double R, G, B;

        R = CM.getRed(RGB1);   //get the 8-bit values of RGB (0-255)
        G = CM.getGreen(RGB1);
        B = CM.getBlue(RGB1);	
	    cieConvert ColorCIE = new cieConvert();

		
	    ColorCIE.setValues(R/255.0, G/255.0, B/255.0);
	    
	    
	    colorHist[z][ColorCIE.IndexOf()]++; //Increment color index of image histogram. Refer to cieConvert.java IndexOf() comments
    }
    
        
	public static void main(String args[]) {
	    JPanel panelCenter = new JPanel();
   	    System.out.println("Starting Image...");
	    Image mainFrame = new Image();
	    
	    panelCenter.setSize(100,100);
	    mainFrame.getContentPane().add(panelCenter, BorderLayout.NORTH);
	    
	    mainFrame.jTextArea1.setLocation(20,230);
	    mainFrame.jTextArea1.setSize(400,100);
	    
	    mainFrame.getContentPane().add(mainFrame.jTextArea1);
		
	    mainFrame.jTextArea2.setLocation(20,331);
	    mainFrame.jTextArea2.setSize(200,100);
	    
	    mainFrame.getContentPane().add(mainFrame.jTextArea2);
		
		
		mainFrame.setSize(600, 400);
		mainFrame.setTitle("Image");
		mainFrame.setVisible(true);

		for (int x = 0; x<127; x++)
        	for(int y = 0; y<95; y++){
        		mainFrame.getRGB(x,y,0,"D:\\",input+".jpg");	//get RGB and LUV of every pixel of input image
        	}
		
		for(int image = 0; image<10; image++)
	        for (int x = 0; x<128; x++) //image x dimensions
	        	for(int y = 0; y<96; y++){ //image y dimensions
	        		String s = (image+100)+".jpg"; //uses images 1170-1180.jpg
	        		mainFrame.getRGB(x,y,image+1,"D:\\",s);	//get RGB and LUV of every pixel of every test image
	        	}
		
        for(int x = 0; x<10; x++)
	        for(int y = 0; y<158; y++)
	        	colorHist[x][y] = (colorHist[x][y])/12288; //every color histogram or all images divided by w*h
        
		mainFrame.init(1,input); //for displaying image to window
		int highest = 0;
		double simExactCol = 0,a,c;
		for(int imageNo = 1; imageNo<=10; imageNo++){
			simExactCol = 0;
	        for(int f = 0; f<158; f++){
	        	a = Math.abs(colorHist[0][f] - colorHist[imageNo][f]);
	        	c = Math.max(colorHist[0][f], colorHist[imageNo][f]);
	        	if(c>0){ //so no divided by 0
			        simExactCol = simExactCol + (1 - a/c);
	        	}
	        }
	        simExactCol = simExactCol*(0.00628930817); // times 1/N where N is number of colors, 159 according to cieCOnvert.java comments
	        rankings[imageNo-1] = -1*simExactCol;
	        h.put(simExactCol, 99+imageNo);
		}
		Arrays.sort(rankings); 
		
		for(int i = 0;i<rankings.length;i++){
			rankings[i] = rankings[i]*-1;
			System.out.println(i+1 +". "+h.get(rankings[i])+": "+rankings[i]);
		}
		mainFrame.init(300, h.get(rankings[0]));
	}
}
