import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

public class Demo extends Component implements ActionListener {

    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************

    String descs[] = {
            "Original",
            "Negative",
            "Rescale",
            "Shift",
            "Randomly Rescale",
            "Add Image",
            "Subtract Image",
            "Multiply Image",
            "Divide Image",
            "Bitwise NOT",
            "Bitwise AND",
            "Bitwise OR",
            "Bitwise XOR",
            "Logarithm",
            "Power law",
            "Look Up table",
            "Bit plane slice",
            "Histogram calculation",
            "Apply masks",
            "Median filter",
            "Min filter",
            "Max filter",
            "Midpoint filter",
            "Salt and pepper",
            "Region of interest combination",
            "Combine filters",
    };

    private int opIndex;  //option index for
    private int lastOp;
    private static int [] LUT;
    private  BufferedImage bi, biFiltered;   // the input image saved as bi;//
    private int w, h;
    private  ArrayList<BufferedImage> undoList = new ArrayList<>();

    public Demo() {
        try {
            String filepath;
            Scanner scanner = new Scanner(System.in);
            System.out.println("Read 1)original or 2)raw ?");
            int switcher = scanner.nextInt();
            if(switcher==1) {
                filepath = "/Users/muradahmed/IdeaProjects/ImageProcessing/src//Cameraman.bmp";
                bi = ImageIO.read(new File(filepath));
                //System.out.println(bi);
                w = bi.getWidth(null);
                h = bi.getHeight(null);
                System.out.println(bi.getType());
                if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                    BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                    Graphics big = bi2.getGraphics();
                    big.drawImage(bi, 0, 0, null);
                    biFiltered = bi = bi2;
                    undoList.add(biFiltered);

                }
            }
            else {
                filepath = "/Users/muradahmed/IdeaProjects/ImageProcessing/src//Baboon.raw";
                bi = readRawImage(filepath);
                w = bi.getWidth(null);
                h = bi.getHeight(null);
                System.out.println(bi.getType());
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
                undoList.add(biFiltered);

            }
        } catch (IOException e) {      // deal with the situation that the image has problem;/
            System.out.println("Image could not be read");
            System.exit(1);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }


    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }



    void setOpIndex(int i) {
        opIndex = i;
    }

    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();
        g.drawImage(biFiltered, 0, 0, null);
    }


    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                result[x][y][0]=a;
                result[x][y][1]=r;
                result[x][y][2]=g;
                result[x][y][3]=b;
            }
        }
        return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }


    //************************************
    //  Example:  Image Negative
    //************************************
    public BufferedImage ImageNegative(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }
        this.undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }


    //************************************
    //  Your turn now:  Add more function below
    //************************************


    public BufferedImage readRawImage(String imageFilePath )
    {
        try
        {

            // create a stream obj. to read in bytes from .raw file
            FileInputStream fileToRead = new FileInputStream( imageFilePath );

            int i     = 0;
            int total = 0;
            int nRead ;

            /* initialise a new byte array to store each line ( 512 x 512 ) */
            byte[] buffer = new byte[ 512 ];

            /* initialise a new string array to store each line ( 512 x 512 ) */
            String[] imageData = new String[ 512 ];


            // the .read(buffer b) method fills buffer with data ( reads <= b.length of data into buffer )
            while( ( nRead = fileToRead.read(buffer) ) != -1 )
            {

                //System.out.println( new String( buffer ) );

                imageData[i++] = Arrays.toString(buffer);
                total = total + nRead;

            }

            return convertToBimage(( parseImage( imageData )));
        }

        catch( Exception e )
        {

            // e.printStackTrace();
            return null;
        }
    }

    public static int[][][] parseImage(String[] stringImageData )
    {
        int x = 0;
        int y = 0;
        int[][][] imageData = new int[512][512][4];


        for( String strImageLine : stringImageData )
        {
            int[] intImageLine = parseStringArray( strImageLine );
            y =0;

            for( int pixel : intImageLine )
            {
                imageData [   y ] [x] [0] = 255;    //a

                imageData[y][x][1] = pixel;  //r
                imageData[y][x][2] = pixel;  //g
                imageData[y][x][3] = pixel;  //b
                y = y + 1;
            }

            // increment x AND y to move a row down
            x = x + 1;


        }
        return imageData;

    }

    public static int[] parseStringArray( String line )
    {

        int[] stringsToReturn = new int[ 512 ];

        // check if the line is in the correct format
        if( line.length() == 0 || line.charAt( 0 ) != '[' || line.charAt( line.length() - 1 ) != ']' )
        {

            return new int[]{ -1 };

        }

        // cut out the square brackets [] from the string
        String contents = line.substring( 1 , line.length() - 1 ).trim();

        // split the strings
        String[] nums = contents.split(", ");

        // for each integer, replace it with its ABSOLUTE integer counterpart
        for( int z = 0 ; z < nums.length ; z++ )
        {

            String temp = nums[z];
            stringsToReturn[z] = Math.abs( Integer.parseInt( temp ) );

        }
        //printIntArray(stringsToReturn);
        return stringsToReturn;

    }

    //************************************
    //  RESCALE PIXEL VALUES
    //************************************
    public BufferedImage Rescale(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        float scale = 2;
        int [][][] ImageArray = convertToArray(timg);
        int r,g,b;

        for(int y=0;y<height;y++){

            for(int x=0;x<width;x++){
                r = (int) scale * ImageArray[x][y][1];
                g = (int) scale * ImageArray[x][y][2];
                b = (int) scale * ImageArray[x][y][3];

                ImageArray[x][y][0] = 255;
                ImageArray[x][y][1] = checkBoundary(r);
                ImageArray[x][y][2] = checkBoundary(g);
                ImageArray[x][y][3] = checkBoundary(b);
            }
        }

        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);
    }

    //************************************
    // SHIFT PIXEL VALUES
    //************************************
    public BufferedImage ShiftImage(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        float resize = 50;
        int [][][] ImageArray = convertToArray(timg);
        int r,g,b;

        for(int y=0;y<height;y++){

            for(int x=0;x<width;x++){
                r = (int) resize + ImageArray[x][y][1];
                g = (int) resize + ImageArray[x][y][2];
                b = (int) resize + ImageArray[x][y][3];


                ImageArray[x][y][0] = 255;
                ImageArray[x][y][1] = checkBoundary(r);
                ImageArray[x][y][2] = checkBoundary(g);
                ImageArray[x][y][3] = checkBoundary(b);
            }
        }

        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);
    }


    //****************************************
    // RANDOMLY RESCALE AND SHIFT PIXEL VALUES
    //****************************************
    public BufferedImage RescaleAndShift(BufferedImage timg){

        int width = timg.getWidth();
        int height = timg.getHeight();
        int [][][] ImageArray1 = convertToArray(timg);
        int [] [] [] ImageArray2 = convertToArray(timg);
        int t=5;
        int s = (int) (Math.random() *255);
        System.out.println(s);
        int rmin, rmax, gmin, gmax, bmin, bmax;
        rmin = s*(ImageArray1[0][0][1]+t); rmax = rmin;
        gmin = s*(ImageArray1[0][0][2]+t); gmax = gmin;
        bmin = s*(ImageArray1[0][0][3]+t); bmax = bmin;
        for(int y=0; y<height; y++){ for(int x=0; x<width; x++){
            ImageArray2[x][y][1] = s*(ImageArray1[x][y][1]+t); //r
            ImageArray2[x][y][2] = s*(ImageArray1[x][y][2]+t); //g
            ImageArray2[x][y][3] = s*(ImageArray1[x][y][3]+t); //b
            if (rmin>ImageArray2[x][y][1]) { rmin = ImageArray2[x][y][1]; }
            if (gmin>ImageArray2[x][y][2]) { gmin = ImageArray2[x][y][2]; }
            if (bmin>ImageArray2[x][y][3]) { bmin = ImageArray2[x][y][3]; }
            if (rmax<ImageArray2[x][y][1]) { rmax = ImageArray2[x][y][1]; }
            if (gmax<ImageArray2[x][y][2]) { gmax = ImageArray2[x][y][2]; }
            if (bmax<ImageArray2[x][y][3]) { bmax = ImageArray2[x][y][3]; }
        }}
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++)
            { ImageArray2[x][y][1]=255*(ImageArray2[x][y][1]-rmin)/(rmax-rmin);
                ImageArray2[x][y][2]=255*(ImageArray2[x][y][2]-gmin)/(gmax-gmin);
                ImageArray2[x][y][3]=255*(ImageArray2[x][y][3]-bmin)/(bmax-bmin);
            }}
        undoList.add(convertToBimage(ImageArray2));
        return convertToBimage(ImageArray2);
    }


    //************************************
    //ARITHMETICAL ADDITION OF 2 IMAGES
    //************************************
    public BufferedImage addImage(BufferedImage timg) {
        try {
            BufferedImage bi2 = ImageIO.read(new File("/Users/muradahmed/IdeaProjects/ImageProcessing/src//LenaRGB.bmp"));
            int[][][] secondImage = convertToArray(bi2);
            int[][][] firstImage = convertToArray(timg);
            int first_image_width = timg.getWidth();
            int first_image_height = timg.getHeight();
            int r,g,b;
            for (int y = 0; y < first_image_height; y++) {
                for (int x = 0; x < first_image_width; x++) {
                    r = firstImage[x][y][1] += secondImage[x][y][1];
                    g = firstImage[x][y][2] += secondImage[x][y][2];
                    b = firstImage[x][y][3] += secondImage[x][y][3];

                    firstImage[x][y][0] = 255;
                    firstImage[x][y][1] = checkBoundary(r);
                    firstImage[x][y][2] = checkBoundary(g);
                    firstImage[x][y][3] = checkBoundary(b);
                }

            }
            undoList.add(convertToBimage(firstImage));
            return convertToBimage(firstImage);
        }
        catch (IOException e) {
            return null;
        }
    }


    //************************************
    //ARITHMETICAL SUBTRACTION OF 2 IMAGES
    //************************************
    public BufferedImage subtractImage(BufferedImage timg) {
        try {
            BufferedImage bi2 = ImageIO.read(new File("/Users/muradahmed/IdeaProjects/ImageProcessing/src//LenaRGB.bmp"));
            int[][][] secondImage = convertToArray(bi2);
            int[][][] firstImage = convertToArray(timg);
            int first_image_width = timg.getWidth();
            int first_image_height = timg.getHeight();
            int r,g,b;
            for (int y = 0; y < first_image_height; y++) {
                for (int x = 0; x < first_image_width; x++) {
                    r = secondImage[x][y][1] - firstImage[x][y][1];
                    g = secondImage[x][y][2] - firstImage[x][y][2];
                    b = secondImage[x][y][3] - firstImage[x][y][3];

                    firstImage[x][y][0] = 255;
                    firstImage[x][y][1] = checkBoundary(r);
                    firstImage[x][y][2] = checkBoundary(g);
                    firstImage[x][y][3] = checkBoundary(b);
                }

            }
            undoList.add(convertToBimage(firstImage));
            return convertToBimage(firstImage);
        }
        catch (IOException e) {
            return null;
        }
    }


    //****************************************
    // ARITHMETICAL MULTIPLICATION OF 2 IMAGES
    //****************************************
    public BufferedImage MultiplyImage(BufferedImage timg) {
        try {
            BufferedImage bi2 = ImageIO.read(new File("/Users/muradahmed/IdeaProjects/ImageProcessing/src//LenaRGB.bmp"));
            int[][][] secondImage = convertToArray(bi2);
            int[][][] firstImage = convertToArray(timg);
            int first_image_width = timg.getWidth();
            int first_image_height = timg.getHeight();
            int r,g,b;
            for (int y = 0; y < first_image_height; y++) {
                for (int x = 0; x < first_image_width; x++) {
                    r = secondImage[x][y][1] *= firstImage[x][y][1];
                    g = secondImage[x][y][2] *= firstImage[x][y][2];
                    b = secondImage[x][y][3] *= firstImage[x][y][3];

                    firstImage[x][y][0] = 255;
                    firstImage[x][y][1] = checkBoundary(r);
                    firstImage[x][y][2] = checkBoundary(g);
                    firstImage[x][y][3] = checkBoundary(b);
                }

            }
            undoList.add(convertToBimage(firstImage));
            return convertToBimage(firstImage);
        }
        catch (IOException e) {
            return null;
        }
    }


    //************************************
    //ARITHMETICAL DIVISION OF 2 IMAGES
    //************************************
    public BufferedImage Divided(BufferedImage timg) {
        try {
            BufferedImage bi2 = ImageIO.read(new File("/Users/muradahmed/IdeaProjects/ImageProcessing/src//LenaRGB.bmp"));
            int[][][] secondImage = convertToArray(bi2);
            int[][][] firstImage = convertToArray(timg);
            int first_image_width = timg.getWidth();
            int first_image_height = timg.getHeight();
            int r,g,b =0;
            for (int y = 0; y < first_image_height; y++) {
                for (int x = 0; x < first_image_width; x++) {
                    if (firstImage[x][y][1]!=0) {
                        r = secondImage[x][y][1] / firstImage[x][y][1];

                        firstImage[x][y][1] = checkBoundary(r);
                    }
                    if (firstImage [x][y][2]!=0) {
                        g = secondImage[x][y][2] / firstImage[x][y][2];

                        firstImage[x][y][2] = checkBoundary(g);
                    }
                    if (firstImage[x][y][3]!=0) {
                        b = secondImage[x][y][3] / firstImage[x][y][3];

                        firstImage[x][y][3] = checkBoundary(b);
                    }

                    firstImage[x][y][0] = 255;
                }

            }
            undoList.add(convertToBimage(firstImage));
            return convertToBimage(firstImage);
        }
        catch (IOException e) {
            return null;
        }
    }



    //************************************
    //BITWISE NOT ON IMAGE
    //************************************
    public BufferedImage bitwiseNOT(BufferedImage timg){
        int ImageArray[][][] = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r,g,b;
        for(int y=0;y<height;y++){

            for(int x=0;x<width;x++){

                r = ImageArray[x][y][1];
                g = ImageArray[x][y][2];
                b = ImageArray[x][y][3];

                r = ~r;
                g = ~g;
                b = ~b;

                ImageArray[x][y][1]= r&0xff ;
                ImageArray[x][y][2] = g&0xff ;
                ImageArray[x][y][3] = b&0xff ;
            }
        }
        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);
    }


    //************************************
    //BITWISE AND ON 2 IMAGES
    //************************************
    @SuppressWarnings("Duplicates")
    public BufferedImage bitwiseAND(BufferedImage timg) {
        try {
            BufferedImage bi2 = ImageIO.read(new File("/Users/muradahmed/IdeaProjects/ImageProcessing/src//LenaRGB.bmp"));
            int[][][] secondImage = convertToArray(bi2);
            int[][][] firstImage = convertToArray(timg);
            int height = timg.getHeight();
            int width = timg.getWidth();
            int r, g, b;
            for(int y=0;y<height;y++) {

                for (int x = 0; x < width; x++) {
                    r = firstImage[x][y][1] & secondImage[x][y][1];
                    g = firstImage[x][y][1] & secondImage[x][y][2];
                    b = firstImage[x][y][1] & secondImage[x][y][3];
                    firstImage[x][y][1] = r&0xff;
                    firstImage[x][y][2] = g&0xff;
                    firstImage[x][y][3] = b&0xff;
                }

            }
            undoList.add(convertToBimage(firstImage));
            return convertToBimage(firstImage);
        }
        catch (IOException e){
            return null;
        }
    }

    @SuppressWarnings("Duplicates")
    public BufferedImage combineFilters(BufferedImage timg, BufferedImage timg2) {
        try {
            BufferedImage bi2 = timg2;
            int[][][] secondImage = convertToArray(bi2);
            int[][][] firstImage = convertToArray(timg);
            int height = timg.getHeight();
            int width = timg.getWidth();
            int r, g, b;
            for(int y=0;y<height;y++) {

                for (int x = 0; x < width; x++) {
                    r = firstImage[x][y][1] & secondImage[x][y][1];
                    g = firstImage[x][y][1] & secondImage[x][y][2];
                    b = firstImage[x][y][1] & secondImage[x][y][3];
                    firstImage[x][y][1] = r&0xff;
                    firstImage[x][y][2] = g&0xff;
                    firstImage[x][y][3] = b&0xff;
                }

            }
            undoList.add(convertToBimage(firstImage));
            return convertToBimage(firstImage);
        }
        catch (Exception e){
            return null;
        }
    }

    //************************************
    //BITWISE OR ON 2 IMAGES
    //************************************
    public BufferedImage bitwiseOR(BufferedImage timg){
        try {
            BufferedImage bi2 = ImageIO.read(new File("/Users/muradahmed/IdeaProjects/ImageProcessing/src//LenaRGB.bmp"));
            int[][][] secondImage = convertToArray(bi2);
            int[][][] firstImage = convertToArray(timg);
            int height = timg.getHeight();
            int width = timg.getWidth();
            int r, g, b;
            for(int y=0;y<height;y++) {

                for (int x = 0; x < width; x++) {
                    r = firstImage[x][y][1] | secondImage[x][y][1];
                    g = firstImage[x][y][1] | secondImage[x][y][2];
                    b = firstImage[x][y][1] | secondImage[x][y][3];
                    firstImage[x][y][1] = r;
                    firstImage[x][y][2] = g;
                    firstImage[x][y][3] = b;
                }

            }
            undoList.add(convertToBimage(firstImage));
            return convertToBimage(firstImage);
        }
        catch (IOException e){
            return null;
        }

    }


    //************************************
    //BITWISE XOR ON 2 IMAGES
    //************************************
    public BufferedImage bitwiseXOR(BufferedImage timg){
        try {
            BufferedImage bi2 = ImageIO.read(new File("/Users/muradahmed/IdeaProjects/ImageProcessing/src//LenaRGB.bmp"));
            int[][][] secondImage = convertToArray(bi2);
            int[][][] firstImage = convertToArray(timg);
            int height = timg.getHeight();
            int width = timg.getWidth();
            int r, g, b;
            for(int y=0;y<height;y++) {

                for (int x = 0; x < width; x++) {
                    r = firstImage[x][y][1] ^ secondImage[x][y][1];
                    g = firstImage[x][y][1] ^ secondImage[x][y][2];
                    b = firstImage[x][y][1] ^ secondImage[x][y][3];
                    firstImage[x][y][1] = r&0xff;
                    firstImage[x][y][2] = g&0xff;
                    firstImage[x][y][3] = b&0xff;
                }

            }
            undoList.add(convertToBimage(firstImage));
            return convertToBimage(firstImage);
        }
        catch (IOException e){
            return null;
        }

    }

    public BufferedImage RegionOfInterestComb(BufferedImage timg)
    {
        // x min limit as 80 and x max as 150 , y min = 50, ymax = 100 = face region

        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                if (!(x > 80 && x < 150 && y > 50 && y < 100))
                {

                    ImageArray[x][y][0] = 0;
                    ImageArray[x][y][1] = 0;
                    ImageArray[x][y][2] = 0;
                    ImageArray[x][y][3]=  0;
                }

            }
        }
        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);
    }

    //************************************
    //Log Law
    //************************************
    public BufferedImage Logarithm(BufferedImage timg){
        int [][][] ImageArray = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r,g,b;
        int c = (int) (255.0/Math.log(256.0));
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                r = (int) (c * Math.log(ImageArray[x][y][1] +1));
                g = (int) (c * Math.log(ImageArray[x][y][2] +1));
                b = (int) (c * Math.log(ImageArray[x][y][3] +1));

                ImageArray[x][y][1] = checkBoundary(r);
                ImageArray[x][y][2] = checkBoundary(g);
                ImageArray[x][y][3] = checkBoundary(b);
            }
        }
        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);
    }

    //************************************
    //Power law
    //************************************
    public BufferedImage PowerLaw(BufferedImage timg){
        int [][][] ImageArray = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r,g,b;
        double gamma = 0.45;
        int c = (int) Math.pow(255,1-gamma);
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                r = (int) (c * Math.pow(ImageArray[x][y][1], gamma));
                g = (int) (c * Math.pow(ImageArray[x][y][2], gamma));
                b = (int) (c * Math.pow(ImageArray[x][y][3], gamma));

                ImageArray[x][y][1] = r;
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }
        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);
    }

    //************************************
    //Generate Look up table
    //************************************
    public static void GenerateLookUpTable(){
        LUT = new int[256];
        double p = 0.4;
        for(int k=0; k<=255; k++) {
            LUT[k] = (int) (Math.pow(255, 1 - p) * Math.pow(k, p));



        }}

    //************************************
    // Using the generated LUT
    //************************************
    public BufferedImage useLookUpTable(BufferedImage timg){
        int [][][] ImageArray  = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r,g,b;
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                r = LUT[ImageArray[x][y][1]];
                g = LUT[ImageArray[x][y][2]];
                b = LUT[ImageArray[x][y][3]];

                ImageArray[x][y][1] = checkBoundary(r);
                ImageArray[x][y][2] = checkBoundary(g);
                ImageArray[x][y][3] = checkBoundary(b);

            }
        }
        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);
    }

    //************************************
    //Perform bit plane slicing
    //************************************
    public BufferedImage bitplaneslice(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        // Image Negative Operation:
        int k = 5; // 0,1,2,3...7
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = ImageArray[x][y][1];
                int g = ImageArray[x][y][2];
                int b = ImageArray[x][y][3];

                ImageArray[x][y][1] = (r >> k) & 1;
                ImageArray[x][y][2] = (g >> k) & 1;
                ImageArray[x][y][3] = (b >> k) & 1;

                if (ImageArray[x][y][1] == 1) {
                    ImageArray[x][y][1] = 255;
                }

                if (ImageArray[x][y][2] == 1) {
                    ImageArray[x][y][2] = 255;
                }

                if (ImageArray[x][y][3] == 1) {
                    ImageArray[x][y][3] = 255;
                }
            }
        }
        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);
    }
    //************************************
    //Find histogram, normalise and equalise
    //************************************
    public BufferedImage histogram(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);

        double[] histogramR = new double[256];
        double[] histogramG = new double[256];
        double[] histogramB = new double[256];
        double[] normalisedHistogramR = new double[256];
        double[] normalisedHistogramG = new double[256];
        double[] normalisedHistogramB = new double[256];
        double[] cumulativeHistogramR = new double[256];
        double[] cumulativeHistogramG = new double[256];
        double[] cumulativeHistogramB = new double[256];
        double[] valueToApplyR = new double[256];
        double[] valueToApplyG = new double[256];
        double[] valueToApplyB = new double[256];

        double cumR = 0;
        double cumG = 0;
        double cumB = 0;
        double pixelCount = width*height;

        for (int i = 0; i < 256; i++) {
            histogramR[i] = 0;
            histogramG[i] = 0;
            histogramB[i] = 0;
            normalisedHistogramR[i] = 0;
            normalisedHistogramG[i] = 0;
            normalisedHistogramB[i] = 0;
            cumulativeHistogramR[i] = 0;
            cumulativeHistogramG[i] = 0;
            cumulativeHistogramB[i] = 0;
            valueToApplyR[i] = 0;
            valueToApplyG[i] = 0;
            valueToApplyB[i] = 0;
        }

        // find histogram values
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = ImageArray[x][y][1];
                int g = ImageArray[x][y][2];
                int b = ImageArray[x][y][3];
                histogramR[r]++;
                histogramG[g]++;
                histogramB[b]++;

            }
        }
        // System.out.println(pixelCount);

        // normalize
        for (int i = 0; i < 256; i++) {
            normalisedHistogramR[i] = (histogramR[i] / pixelCount);
            normalisedHistogramG[i] = (histogramG[i] / pixelCount);
            normalisedHistogramB[i] = (histogramB[i] / pixelCount);
        }

        // cumulative
        for (int i = 0; i < 256; i++) {
            cumR += normalisedHistogramR[i];
            cumG += normalisedHistogramG[i];
            cumB += normalisedHistogramB[i];
            cumulativeHistogramR[i] = cumR;
            cumulativeHistogramG[i] = cumG;
            cumulativeHistogramB[i] = cumB;
        }

        //multiply cumulative by 255
        for (int i = 0; i < 256; i++) {
            valueToApplyR[i] = Math.round(cumulativeHistogramR[i] * 255);
            valueToApplyG[i] = Math.round(cumulativeHistogramG[i] * 255);
            valueToApplyB[i] = Math.round(cumulativeHistogramB[i] * 255);
        }

        //apply to image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ImageArray[x][y][1] = checkBoundary((int) valueToApplyR[ImageArray[x][y][1]]);
                ImageArray[x][y][2] = checkBoundary((int) valueToApplyG[ImageArray[x][y][2]]);
                ImageArray[x][y][3] = checkBoundary((int) valueToApplyB[ImageArray[x][y][3]]);
            }
        }
        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray); // Convert the array to BufferedImage
    }

    public BufferedImage mask(BufferedImage timg){
        int switcher;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which mask 1) avg 2)weighted avg 3)4 nb lap 4)8 nb lap 5)4 nb lap enhance 6)8 nb lap enhance 7)Roberts 1 8) roberts2 9)sobel x 10) sobel y");
        switcher = scanner.nextInt();
        float [][] mask33 = {  { 0, 0, 0 },
                { 0, 0, 0 },
                { 0, 0, 0 }
        };

        if(switcher>10){
            System.out.println("Invalid number");
            return timg;}
        else {
            switch (switcher) {
                //average
                case 1:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            mask33[i][j] = (float) 1 / 9 * 9;
                        }
                    }

                    return applyMask(mask33, timg);
                //weighted avg
                case 2:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (i == 1 && j == 1) {
                                mask33[i][j] = (float) 4 / 16 * 16;
                            } else if (i == 1 || j == 1) {
                                mask33[i][j] = (float) 2/16 * 16;
                            } else {
                                mask33[i][j] = (float) 1 / 16 * 16;
                            }
                        }
                    }
                    return applyMask(mask33, timg);
                //4 neighbour laplacian
                case 3:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (i == 1 && j == 1) {
                                mask33[i][j] = 4;
                            } else if (i == 1 || j == 1) {
                                mask33[i][j] = -1;
                            } else {
                                mask33[i][j] = 0;
                            }
                        }
                    }
                    return applyMask(mask33, timg);
                //8 neighbour laplacian
                case 4:
                    for (int i = 0; i<3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (i == 1 && j == 1) {
                                mask33[i][j] = 8;
                            } else {
                                mask33[i][j] = -1;
                            }
                        }
                    }
                    return applyMask(mask33, timg);
                //4 neighbour laplacian enhancement
                case 5:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (i == 1 && j == 1) {
                                mask33[i][j] = 5;
                            } else if (i == 1 || j == 1) {
                                mask33[i][j] = -1;
                            } else {
                                mask33[i][j] = 0;
                            }
                        }
                    }
                    return applyMask(mask33, timg);
                //8 neighbour laplacian enhancement
                case 6:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (i == 1 && j == 1) {
                                mask33[i][j] = 9;
                            } else {
                                mask33[i][j] = -1;
                            }
                        }
                    }
                    return applyMask(mask33, timg);
                //roberts 1
                case 7:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if(i==1 && j==2){
                                mask33[i][j]=-1;
                            }
                            if(i==2&&j==1){
                                mask33[i][j]=1;
                            }
                        }
                    }
                    return applyMask(mask33, timg);
                //roberts 2
                case 8:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if(i==1&&j==1){
                                mask33[i][j]=-1;
                            }
                            if(i==2&&j==2){
                                mask33[i][j]=1;
                            }
                        }
                    }
                    return applyMask(mask33, timg);
                //sobel x
                case 9:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if(i==0&&j==0){
                                mask33[i][j]=-1;
                            }
                            if(i==1&&j==0){
                                mask33[i][j]=-2;
                            }
                            if(i==2&&j==0){
                                mask33[i][j]=-1;
                                mask33[j][i]=1;
                            }
                            if(i==1&&j==2){
                                mask33[i][j]=2;
                            }
                            if(i==2&&j==2){
                                mask33[i][j]=1;
                            }
                        }
                    }
                    return applyMask(mask33, timg);
                //sobel y
                case 10:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if(i==0&&j==0){
                                mask33[i][j]=-1;
                            }
                            if(i==0&&j==1){
                                mask33[i][j]=-2;
                            }
                            if(i==0&&j==2){
                                mask33[i][j]=-1;
                                mask33[j][i]=1;
                            }
                            if(i==2&&j==1){
                                mask33[i][j]=2;
                            }
                            if(i==2&&j==2){
                                mask33[i][j]=1;
                            }
                        }
                    }
                    return applyMask(mask33, timg);
            }

             /*
                    for (int i = 0; i < mask33.length; i++) {
                        for (int j = 0; j < mask33[i].length; j++) {
                            System.out.print(mask33[i][j] + " ");
                        }
                        System.out.println();
                    }*/
        }
        return timg;
    }



    //********************************************
    //Apply mask either correlation or convolution
    //********************************************
    private BufferedImage applyMask(float [][]Mask, BufferedImage timg){
        int switcher;
        Scanner scanner = new Scanner(System.in);
        System.out.println("1) Correlation 2) Convolution");
        switcher = scanner.nextInt();
        int [][][]ImageArray = convertToArray(timg);
        int [][][]ImageArray2= convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int r,g,b;
        switch (switcher){
            case 1:
                for(int y=1;y<height-1;y++) {
                    for (int x = 1; x < width - 1; x++) {
                        r=0;g=0;b=0;
                        for(int s=-1;s<=1;s++){
                            for(int t=-1;t<=1;t++){
                                r = (int)(r+Mask[1+s][1+t]*ImageArray[x+s][y+t][1]);
                                g = (int) (g+Mask[1+s][1+t]*ImageArray[x+s][y+t][2]);
                                b = (int) (b+Mask[1+s][1+t]*ImageArray[x+s][y+t][3]);

                            }
                        }

                        ImageArray2[x][y][1] = checkBoundary(r);
                        ImageArray2[x][y][2] = checkBoundary(g);
                        ImageArray2[x][y][3] = checkBoundary(b);
                    }
                }
            case 2:
                for(int y=1;y<height-1;y++) {
                    for (int x = 1; x < width - 1; x++) {
                        r=0;g=0;b=0;
                        for(int s=-1;s<=1;s++){
                            for(int t=-1;t<=1;t++){
                                r = (int)(r+Mask[1-s][1-t]*ImageArray[x+s][y+t][1]);
                                g = (int) (g+Mask[1-s][1-t]*ImageArray[x+s][y+t][2]);
                                b = (int) (b+Mask[1-s][1-t]*ImageArray[x+s][y+t][3]);

                            }
                        }
                        ImageArray2[x][y][1] = checkBoundary(r);
                        ImageArray2[x][y][2] = checkBoundary(g);
                        ImageArray2[x][y][3] = checkBoundary(b);
                    }
                }

        }
        undoList.add(convertToBimage(ImageArray2));
        return convertToBimage(ImageArray2);
    }

    //************************************
    //Adding salt and pepper
    //************************************
    public BufferedImage addSaltAndPepper(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double random = Math.random() * 1;

                if (random < 0.05) {
                    ImageArray[x][y][0] = 255;
                    ImageArray[x][y][1] = 0;
                    ImageArray[x][y][2] = 0;
                    ImageArray[x][y][3] = 0;
                } else if (random > 0.95) {
                    ImageArray[x][y][0] = 255;
                    ImageArray[x][y][1] = 255;
                    ImageArray[x][y][2] = 255;
                    ImageArray[x][y][3] = 255;
                }
            }
        }
        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);
    }

    //************************************
    //Median filter
    //************************************
    @SuppressWarnings("Duplicates")
    public BufferedImage median_filter(BufferedImage timg){
        int [] rWindow = new int[9];
        int [] gWindow = new int[9];
        int [] bWindow = new int[9];
        int [][][] ImageArray1 = convertToArray(timg);
        int [][][] ImageArray2 = convertToArray(timg);
        int k;
        int width = timg.getWidth();
        int height = timg.getHeight();
        for(int y=1;y<height-1;y++){
            for(int x=1;x<width-1;x++){
                k=0;
                for(int s=-1;s<=1;s++){
                    for(int t=-1;t<=1;t++){
                        rWindow[k]= ImageArray1[x+s][y+t][1];
                        gWindow[k]= ImageArray1[x+s][y+t][2];
                        bWindow[k]= ImageArray1[x+s][y+t][3];
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                ImageArray2[x][y][1]=checkBoundary(rWindow[4]);
                ImageArray2[x][y][2]=checkBoundary(gWindow[4]);
                ImageArray2[x][y][3]=checkBoundary(bWindow[4]);
            }
        }
        undoList.add(convertToBimage(ImageArray2));
        return convertToBimage(ImageArray2);
    }

    //************************************
    //Min filter
    //************************************
    @SuppressWarnings("Duplicates")
    public BufferedImage min_filter(BufferedImage timg){
        int [] rWindow = new int[9];
        int [] gWindow = new int[9];
        int [] bWindow = new int[9];
        int [][][] ImageArray1 = convertToArray(timg);
        int [][][] ImageArray2 = convertToArray(timg);
        int k;
        int width = timg.getWidth();
        int height = timg.getHeight();
        for(int y=1;y<height-1;y++){
            for(int x=1;x<width-1;x++){
                k=0;
                for(int s=-1;s<=1;s++){
                    for(int t=-1;t<=1;t++){
                        rWindow[k]= ImageArray1[x+s][y+t][1];
                        gWindow[k]= ImageArray1[x+s][y+t][2];
                        bWindow[k]= ImageArray1[x+s][y+t][3];
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                ImageArray2[x][y][1]=checkBoundary(rWindow[0]);
                ImageArray2[x][y][2]=checkBoundary(gWindow[0]);
                ImageArray2[x][y][3]=checkBoundary(bWindow[0]);
            }
        }
        undoList.add(convertToBimage(ImageArray2));
        return convertToBimage(ImageArray2);
    }

    //************************************
    //Max filter
    //************************************
    @SuppressWarnings("Duplicates")
    public BufferedImage max_filter(BufferedImage timg){
        int [] rWindow = new int[9];
        int [] gWindow = new int[9];
        int [] bWindow = new int[9];
        int [][][] ImageArray1 = convertToArray(timg);
        int [][][] ImageArray2 = convertToArray(timg);
        int k;
        int width = timg.getWidth();
        int height = timg.getHeight();
        for(int y=1;y<height-1;y++){
            for(int x=1;x<width-1;x++){
                k=0;
                for(int s=-1;s<=1;s++){
                    for(int t=-1;t<=1;t++){
                        rWindow[k]= ImageArray1[x+s][y+t][1];
                        gWindow[k]= ImageArray1[x+s][y+t][2];
                        bWindow[k]= ImageArray1[x+s][y+t][3];
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                ImageArray2[x][y][1]=checkBoundary(rWindow[rWindow.length-1]);
                ImageArray2[x][y][2]=checkBoundary(gWindow[rWindow.length-1]);
                ImageArray2[x][y][3]=checkBoundary(bWindow[rWindow.length-1]);
            }
        }
        undoList.add(convertToBimage(ImageArray2));
        return convertToBimage(ImageArray2);
    }

    //************************************
    //mid point filter
    //************************************
    @SuppressWarnings("Duplicates")
    public BufferedImage midpoint_filter(BufferedImage timg){
        int [] rWindow = new int[9];
        int [] gWindow = new int[9];
        int [] bWindow = new int[9];
        int [][][] ImageArray1 = convertToArray(timg);
        int [][][] ImageArray2 = convertToArray(timg);
        int k;
        int width = timg.getWidth();
        int height = timg.getHeight();
        for(int y=1;y<height-1;y++){
            for(int x=1;x<width-1;x++){
                k=0;
                for(int s=-1;s<=1;s++){
                    for(int t=-1;t<=1;t++){
                        rWindow[k]= ImageArray1[x+s][y+t][1];
                        gWindow[k]= ImageArray1[x+s][y+t][2];
                        bWindow[k]= ImageArray1[x+s][y+t][3];
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                int r_midpoint = (rWindow[0] + rWindow[(rWindow.length-1)/2]);
                int g_midpoint = (gWindow[0] + gWindow[(gWindow.length-1)/2]);
                int b_midpoint = (bWindow[0] + bWindow[(bWindow.length-1)/2]);
                ImageArray2[x][y][1]=checkBoundary(r_midpoint);
                ImageArray2[x][y][2]=checkBoundary(g_midpoint);
                ImageArray2[x][y][3]=checkBoundary(b_midpoint);
            }
        }
        undoList.add(convertToBimage(ImageArray2));
        return convertToBimage(ImageArray2);
    }

    private int checkBoundary(int colourValue){
        if(colourValue>255)
            return 255;
        if(colourValue<0)
            return 0;
        else
            return colourValue;
    }



    //************************************
    //  You need to register your function here
    //************************************
    public void filterImage() {

        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0: biFiltered = bi; /* original */
                return;
            case 1: biFiltered = ImageNegative(bi); /* Image Negative */
                return;
            case 2: biFiltered = Rescale(bi);
                return;
            case 3: biFiltered = ShiftImage(bi);
                return;
            case 4: biFiltered = RescaleAndShift(bi);
                return;
            case 5: biFiltered = addImage(bi);
                return;
            case 6: biFiltered = subtractImage(bi);
                return;
            case 7: biFiltered = MultiplyImage(bi);
                return;
            case 8: biFiltered = Divided(bi);
                return;
            case 9: biFiltered = bitwiseNOT(bi);
                return;
            case 10: biFiltered = bitwiseAND(bi);
                return;
            case 11: biFiltered = bitwiseOR(bi);
                return;
            case 12: biFiltered = bitwiseXOR(bi);
                return;
            case 13: biFiltered = Logarithm(bi);
                return;
            case 14: biFiltered = PowerLaw(bi);
                return;
            case 15: biFiltered = useLookUpTable(bi);
                return;
            case 16: biFiltered = bitplaneslice(bi);
                return;
            case 17: biFiltered = histogram(bi);
                return;
            case 18: biFiltered = mask(bi);
                return;
            case 19: biFiltered = median_filter(bi);
                return;
            case 20: biFiltered = min_filter(bi);
                return;
            case 21: biFiltered = max_filter(bi);
                return;
            case 22: biFiltered = midpoint_filter(bi);
                return;
            case 23: biFiltered = addSaltAndPepper(bi);
                return;
            case 24: biFiltered = RegionOfInterestComb(bi);
            case 25:
                if(undoList.size()-2>=0) {
                    biFiltered = combineFilters(bi, undoList.get(undoList.size() - 2));
                }
                //************************************
                // case 2:
                //      return;
                //************************************

        }

    }

    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();

        if (cb.getActionCommand().equals("SetFilter")) {
            setOpIndex(cb.getSelectedIndex());
            repaint();
        }
        else if (cb.getActionCommand().equals("Formats")) {
            String format = (String)cb.getSelectedItem();
            File saveFile = new File("savedimage."+format);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(saveFile);
            int rval = chooser.showSaveDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                saveFile = chooser.getSelectedFile();
                try {
                    ImageIO.write(biFiltered, format, saveFile);
                } catch (IOException ex) {
                }
            }
        }
    }

    public void undoImage(){
        try {
            if(undoList.size()-1>=1) {
                undoList.remove(undoList.size() - 1);
                BufferedImage bi2 = undoList.get(undoList.size() - 1);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi2, 0, 0, null);
                biFiltered = bi = bi2;
                repaint();
            }
            else{
                BufferedImage bi2 = undoList.get(0);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi2, 0, 0, null);
                biFiltered = bi = bi2;
                repaint();
            }

        }
        catch (Exception e){}
    }

    public void combineROI(){
        try {
            BufferedImage bi3 = this.RegionOfInterestComb(this.undoList.get(this.undoList.size() - 2));
            Graphics big = bi3.getGraphics();
            big.drawImage(bi3, 0, 0, null);
            this.biFiltered = this.bi = bi3;
            this.repaint();

        }
        catch (Exception e2){}

    }

    public void reset(){
        try {
            String filepath = "/Users/muradahmed/IdeaProjects/ImageProcessing/src//Cameraman.bmp";
            BufferedImage original = ImageIO.read(new File(filepath));
            this.undoList.removeAll(this.undoList);
            BufferedImage bi4 = original;
            Graphics big = bi4.getGraphics();
            big.drawImage(bi4, 0, 0, null);
            this.biFiltered = this.bi = bi4;
            this.repaint();
        }
        catch (Exception e1){}

    }

    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        GenerateLookUpTable();
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        Demo de = new Demo();
        f.add("Center", de);
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        JButton undo = new JButton("Undo");
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                de.undoImage();
            }
        });
        JButton combine = new JButton("Combine");
        combine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                de.combineROI();
            }
        });
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                de.reset();
            }
        });
        panel.add(undo);
        panel.add(combine);
        panel.add(reset);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}