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

   private String descs[] = {
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
            "Combine filters",
            "Mean and standard deviation",
            "Simple thresholding",
            "Automated thresholding",
    };

    private int opIndex;  //option index for
    private int lastOp;
    private static int [] LUT;
    private  BufferedImage bi, biFiltered;   // the input image saved as bi;//
    private int w, h;
    private  ArrayList<BufferedImage> undoList = new ArrayList<>();
    private String filepath = "";

    public Demo() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Read 1)original or 2)raw ?");
            int switcher = scanner.nextInt();
            if(switcher==1) {
                this.filepath = "/Users/muradahmed/IdeaProjects/ImageProcessing/src//baboonrgb.bmp";
                bi = ImageIO.read(new File(this.filepath));
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
                filepath = "/Users/muradahmed/IdeaProjects/ImageProcessing/src//baboon.raw";
                bi = rawImage(filepath);
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


    private String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    private String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }



    private void setOpIndex(int i) {
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
    private BufferedImage convertToBimage(int[][][] TmpArray){

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
    private BufferedImage ImageNegative(BufferedImage timg){
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


    public BufferedImage rawImage(String file)
    {

        try
        {
            File rawFile = new File(file);
            FileInputStream inputStream = new FileInputStream(file);
            int i = 0, readBuffer = 0;
            byte[] buffer = new byte[512];
            String[] ImageData = new String[512];
            while( ( readBuffer = inputStream.read(buffer) ) != -1 )
            {
                ImageData[i++] = Arrays.toString(buffer);
            }
            return convertToBimage((convertImageData(ImageData)));
        }
        catch( Exception e )
        {
            System.out.println("error");
            e.printStackTrace();
            return null;
        }
    }

    public static int[][][] convertImageData(String[] ImageData)
    {
        int x = 0;
        int y = 0;
        int[][][] imageArray = new int[512][512][4];
        for( String lineImageData : ImageData )
        {
            int[] imageLine = convertIntoInt(lineImageData);
            y =0;
            for( int rgb : imageLine )
            {
                imageArray[y][x][0] = 255;    //a
                imageArray[y][x][1] = rgb;  //r
                imageArray[y][x][2] = rgb;  //g
                imageArray[y][x][3] = rgb;  //b
                y++;
            }
            x++;
        }
        return imageArray;
    }

    public static int[] convertIntoInt(String line)
    {
        int[] intImageData = new int[512];
        if( line.length() == 0 || line.charAt(0) != '[' || line.charAt(line.length() - 1 ) != ']')
        {
            return new int[]{-1};
        }
        String[] nums = (line.substring(1,line.length()-1).trim()).split(", ");
        for(int z = 0;z<nums.length;z++)
        {
            intImageData[z] = Math.abs(Integer.parseInt(nums[z]));
        }
        return intImageData;
    }
    //************************************
    //  RESCALE PIXEL VALUES
    //************************************
    private BufferedImage Rescale(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        float scale = 1;
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
    private BufferedImage ShiftImage(BufferedImage timg){
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
    private BufferedImage RescaleAndShift(BufferedImage timg){

        int width = timg.getWidth();
        int height = timg.getHeight();
        int [][][] ImageArray1 = convertToArray(timg);
        int [] [] [] ImageArray2 = new int[width][height][4];
        int t=5;
        int s = (int) (Math.random() *255);
        int rmin, rmax, gmin, gmax, bmin, bmax;
        rmin = s*(ImageArray1[0][0][1]+t); rmax = rmin;
        gmin = s*(ImageArray1[0][0][2]+t); gmax = gmin;
        bmin = s*(ImageArray1[0][0][3]+t); bmax = bmin;
        for(int y=0; y<height; y++){ for(int x=0; x<width; x++){
            ImageArray2[x][y][0]=     ImageArray1[x][y][0];
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
            {
                ImageArray2[x][y][0] = ImageArray1[x][y][0];
                ImageArray2[x][y][1]=255*(ImageArray2[x][y][1]-rmin)/(rmax-rmin);
                ImageArray2[x][y][2]=255*(ImageArray2[x][y][2]-gmin)/(gmax-gmin);
                ImageArray2[x][y][3]=255*(ImageArray2[x][y][3]-bmin)/(bmax-bmin);
            }}
        undoList.add(convertToBimage(ImageArray2));
        return convertToBimage(ImageArray2);
    }


    //************************************
    //ARITHMETICAL ADDITION OF 2 IMAGES
    //************************************
    private BufferedImage addImage(BufferedImage timg) {
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
    private BufferedImage subtractImage(BufferedImage timg) {
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
    private BufferedImage MultiplyImage(BufferedImage timg) {
        try {
            BufferedImage bi2 = ImageIO.read(new File("/Users/muradahmed/IdeaProjects/ImageProcessing/src//PeppersRGB.bmp"));
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
    private BufferedImage Divided(BufferedImage timg) {
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
                        r = firstImage[x][y][1]/secondImage[x][y][1] ;
                        firstImage[x][y][1] = checkBoundary(r);
                    }
                    if (firstImage [x][y][2]!=0) {
                        g =firstImage[x][y][2]/ secondImage[x][y][2] ;
                        firstImage[x][y][2] = checkBoundary(g);
                    }
                    if (firstImage[x][y][3]!=0) {
                        b =firstImage[x][y][3] /secondImage[x][y][3] ;
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
    private BufferedImage bitwiseNOT(BufferedImage timg){
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
    private BufferedImage bitwiseAND(BufferedImage timg) {
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
    //************************************
    //Combine two filters (previous image + current image)
    //************************************
    private BufferedImage combineFilters(BufferedImage timg, BufferedImage timg2) {
        try {
            BufferedImage bi2 = timg2;
            int[][][] secondImage = convertToArray(bi2);
            int[][][] firstImage = convertToArray(timg);
            int height = timg.getHeight();
            int width = timg.getWidth();
            int r, g, b;
            for(int y=0;y<height;y++) {

                for (int x = 0; x < width; x++) {
                    r =  secondImage[x][y][1] & firstImage[x][y][1] ;
                    g =  secondImage[x][y][2] & firstImage[x][y][1] ;
                    b =  secondImage[x][y][3] & firstImage[x][y][1] ;
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
    private BufferedImage bitwiseOR(BufferedImage timg){
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
    private BufferedImage bitwiseXOR(BufferedImage timg){
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

    //************************************
    //Combine region of interest with filter(s)
    //************************************
    private BufferedImage RegionOfInterestComb(BufferedImage timg)
    {
        // x min limit as 80 and x max as 150 , y min = 50, ymax = 100 = face region
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                if (!(x > 90 && x < 160 && y > 50 && y < 100))
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
    private BufferedImage Logarithm(BufferedImage timg){
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
    private BufferedImage PowerLaw(BufferedImage timg){
        int [][][] ImageArray = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r,g,b;
        double gamma = 0.45;
        double c = 255/Math.pow(255, gamma);
                //(int) Math.pow(255,1-gamma);
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
    private static void GenerateLookUpTable(){
        LUT = new int[256];
        Random r = new Random();
        for(int k=0; k<=255; k++) {
            LUT[k] = r.nextInt(255);
        }}


    //************************************
    // Using the generated LUT
    //************************************
    private BufferedImage useLookUpTable(BufferedImage timg){
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
    private BufferedImage bitplaneslice(BufferedImage timg){
        try {
            int width = timg.getWidth();
            int height = timg.getHeight();
            int[][][] ImageArray = convertToArray(timg); //  Convert the image to array
            // Image Negative Operation:
            Scanner scanner = new Scanner(System.in);
            System.out.println("Choose a bit to slice");
            int k;
            k = scanner.nextInt();
            // 0,1,2,3...7
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
        catch (NumberFormatException e)
        {
            System.out.println("Choose a number!!!");
            return null;
        }
    }
    //************************************
    //Find histogram, normalise and equalise
    //************************************
    @SuppressWarnings("Duplicates")
    private BufferedImage histogram(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);
        double[] redHistogram = new double[256];
        double[] greenHistogram = new double[256];
        double[] blueHistogram = new double[256];
        double[] redNormHist = new double[256];
        double[] greenNormHist = new double[256];
        double[] blueNormHist = new double[256];
        double[] cumRedHist = new double[256];
        double[] cumGreenHist = new double[256];
        double[] cumBlueHist = new double[256];
        double[] valueToApplyR = new double[256];
        double[] valueToApplyG = new double[256];
        double[] valueToApplyB = new double[256];
        double cumR = 0;
        double cumG = 0;
        double cumB = 0;
        double pixelCount = width*height;

        for (int i = 0; i < 256; i++) {
            redHistogram[i] = 0;
            greenHistogram[i] = 0;
            blueHistogram[i] = 0;
            redNormHist[i] = 0;
            greenNormHist[i] = 0;
            blueNormHist[i] = 0;
            cumRedHist[i] = 0;
            cumGreenHist[i] = 0;
            cumBlueHist[i] = 0;
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
                redHistogram[r]++;
                greenHistogram[g]++;
                blueHistogram[b]++;
                System.out.println("Histogram value for red pixel value " + r + redHistogram[r]);
                System.out.println("Histogram value for green pixel value " + r + greenHistogram[r]);
                System.out.println("Histogram value for blue pixel value " + r + blueHistogram[r]);

            }
        }
        // System.out.println(pixelCount);

        // normalize
        for (int i = 0; i < 256; i++) {
            redNormHist[i] = (redHistogram[i] / pixelCount);
            greenNormHist[i] = (greenHistogram[i] / pixelCount);
            blueNormHist[i] = (blueHistogram[i] / pixelCount);
            System.out.println("Red normalised histogram: "+redNormHist[i]);
            System.out.println("Green normalised histogram: " + greenNormHist[i]);
            System.out.println("Blue normalised histogram: "+ blueNormHist[i]);
        }

        // cumulative
        for (int i = 0; i < 256; i++) {
            cumR += redNormHist[i];
            cumG += greenNormHist[i];
            cumB += blueNormHist[i];
            cumRedHist[i] = cumR;
            cumGreenHist[i] = cumG;
            cumBlueHist[i] = cumB;
            System.out.println("Cumulative histogram value for red :" + cumRedHist[i]);
            System.out.println("Cumulative histogram value for green :" + cumGreenHist[i]);
            System.out.println("Cumulative histogram value for blue :" + cumBlueHist[i]);
        }

        //multiply cumulative by 255
        for (int i = 0; i < 256; i++) {
            valueToApplyR[i] = Math.round(cumRedHist[i] * 255);
            valueToApplyG[i] = Math.round(cumGreenHist[i] * 255);
            valueToApplyB[i] = Math.round(cumBlueHist[i] * 255);
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

    private BufferedImage mask(BufferedImage timg){
        int switcher;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which mask 1) avg 2)weighted avg 3)4 nb lap 4)8 nb lap 5)4 nb lap enhance 6)8 nb lap enhance 7)Roberts 1 8)roberts2 9)sobel x 10) sobel y");
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
                            mask33[i][j] = (float) 1 / 9 ;
                        }
                    }

                    return applyMask(mask33, timg,0);
                //weighted avg
                case 2:

                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (i == 1 && j == 1) {
                                mask33[i][j]=4;
                                mask33[i][j] =  mask33[i][j] * 1 / 16;
                            } else if (i == 1 || j == 1) {
                                mask33[i][j] = 2;
                                mask33[i][j] = mask33[i][j]* 1 / 16 ;
                            } else {
                                mask33[i][j] = 1;
                                mask33[i][j] = mask33[i][j]* 1 / 16;
                            }
                        }
                    }
                    return applyMask(mask33, timg,0);
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
                    return applyMask(mask33, timg,0);
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
                    return applyMask(mask33, timg,0);
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
                    return applyMask(mask33, timg,0);
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
                    return applyMask(mask33, timg,0);
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
                    return applyMask(mask33, timg,1);
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
                    return applyMask(mask33, timg,1);
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
                    return applyMask(mask33, timg,1);
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
                    return applyMask(mask33, timg,1);
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
    @SuppressWarnings("Duplicates")
    private BufferedImage applyMask(float [][]Mask, BufferedImage timg, int option){
        int switcher;
        Scanner scanner = new Scanner(System.in);
        System.out.println("1) Correlation 2) Convolution");
        switcher = scanner.nextInt();
        int [][][]ImageArray = convertToArray(timg);
        int [][][]ImageArray2= new int[timg.getWidth()][timg.getHeight()][4];
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
                        if(option == 0) {
                            ImageArray2[x][y][0] = ImageArray[x][y][0];
                            ImageArray2[x][y][1] = checkBoundary(r);
                            ImageArray2[x][y][2] = checkBoundary(g);
                            ImageArray2[x][y][3] = checkBoundary(b);
                        }
                        else{
                            ImageArray2[x][y][0] = ImageArray[x][y][0];
                            ImageArray2[x][y][1] = Math.abs(r);
                            ImageArray2[x][y][2] = Math.abs(g);
                            ImageArray2[x][y][3] = Math.abs(b);
                        }
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
                        if(option == 0) {
                            ImageArray2[x][y][0] = ImageArray[x][y][0];
                            ImageArray2[x][y][1] = checkBoundary(r);
                            ImageArray2[x][y][2] = checkBoundary(g);
                            ImageArray2[x][y][3] = checkBoundary(b);
                        }
                        else{
                            ImageArray2[x][y][0] = ImageArray[x][y][0];
                            ImageArray2[x][y][1] = Math.abs(r);
                            ImageArray2[x][y][2] = Math.abs(g);
                            ImageArray2[x][y][3] = Math.abs(b);
                        }
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
    private BufferedImage median_filter(BufferedImage timg){
        int [] rWindow = new int[9];
        int [] gWindow = new int[9];
        int [] bWindow = new int[9];
        int [][][] ImageArray1 = convertToArray(timg);
        int [][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
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
                ImageArray2[x][y][0] = ImageArray1[x][y][0];
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
    private BufferedImage min_filter(BufferedImage timg){
        int [] rWindow = new int[9];
        int [] gWindow = new int[9];
        int [] bWindow = new int[9];
        int [][][] ImageArray1 = convertToArray(timg);
        int [][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
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
                ImageArray2[x][y][0] = ImageArray1[x][y][0];
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
    private BufferedImage max_filter(BufferedImage timg){
        int [] rWindow = new int[9];
        int [] gWindow = new int[9];
        int [] bWindow = new int[9];
        int [][][] ImageArray1 = convertToArray(timg);
        int [][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
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
                ImageArray2[x][y][0]=ImageArray1[x][y][0];
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
    private BufferedImage midpoint_filter(BufferedImage timg){
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[timg.getWidth()][timg.getHeight()][4];
        int k;
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                k = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        rWindow[k] = ImageArray1[x + s][y + t][1];
                        gWindow[k] = ImageArray1[x + s][y + t][2];
                        bWindow[k] = ImageArray1[x + s][y + t][3];
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                int r_midpoint = (rWindow[0] + rWindow[(rWindow.length - 1) / 2]);
                int g_midpoint = (gWindow[0] + gWindow[(gWindow.length - 1) / 2]);
                int b_midpoint = (bWindow[0] + bWindow[(bWindow.length - 1) / 2]);
                ImageArray2[x][y][0] = ImageArray1[x][y][0];
                ImageArray2[x][y][1] = checkBoundary(r_midpoint);
                ImageArray2[x][y][2] = checkBoundary(g_midpoint);
                ImageArray2[x][y][3] = checkBoundary(b_midpoint);
            }
        }
        undoList.add(convertToBimage(ImageArray2));
        return convertToBimage(ImageArray2);
    }

    @SuppressWarnings("Duplicates")
    private BufferedImage histogramMeanStd(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);


        double[] redHistogram = new double[256];
        double[] greenHistogram = new double[256];
        double[] blueHistogram = new double[256];

        double redSum = 0, greenSum = 0, blueSum = 0;
        int pixelCount = timg.getHeight()*timg.getWidth();

        for (int i = 0; i < 256; i++) {
            redHistogram[i] = 0;
            greenHistogram[i] = 0;
            blueHistogram[i] = 0;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = ImageArray[x][y][1];
                int g = ImageArray[x][y][2];
                int b = ImageArray[x][y][3];
                redHistogram[r]++;
                greenHistogram[g]++;
                blueHistogram[b]++;
            }
        }

        for (int i = 0; i < 256; i++) {
            redSum += redHistogram[i] * i;
            greenSum += greenHistogram[i] * i;
            blueSum += blueHistogram[i] * i;
        }

        double redMean = redSum / pixelCount;
        double greenMean = greenSum / pixelCount;
        double blueMean = blueSum / pixelCount;

        // print mean
        System.out.println("Mean value of red: " + redMean);
        System.out.println("Mean value of green: " + greenMean);
        System.out.println("Mean value of blue: " + blueMean);


        double redVarianceSum = 0;
        double greenVarianceSum = 0;
        double blueVarianceSum = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = ImageArray[x][y][1];
                double g = ImageArray[x][y][2];
                double b = ImageArray[x][y][3];

                redVarianceSum += Math.pow(r - redMean, 2);
                greenVarianceSum += Math.pow(g - greenMean, 2);
                blueVarianceSum += Math.pow(b - blueMean, 2);
            }
        }

        double redVariance = redVarianceSum / pixelCount;
        double greenVariance = greenVarianceSum / pixelCount;
        double blueVariance = blueVarianceSum / pixelCount;

        double redStdD = Math.sqrt(redVariance);
        double greenStdD = Math.sqrt(greenVariance);
        double blueStdD = Math.sqrt(blueVariance);

        System.out.println("Red standard deviation: " + redStdD);
        System.out.println("Green standard deviation: " + greenStdD);
        System.out.println("Blue standard deviation: " + blueStdD);

        return convertToBimage(ImageArray);
    }

    public BufferedImage simpleThresholding(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);

        int threshV = 70;//variation from 0-255

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int grey_scaled = (ImageArray[x][y][1] + ImageArray[x][y][2] + ImageArray[x][y][3]) / 3;

                if (grey_scaled > threshV) {
                    grey_scaled = 255;
                } else {
                    grey_scaled = 0;
                }

                ImageArray[x][y][1] = grey_scaled;
                ImageArray[x][y][2] = grey_scaled;
                ImageArray[x][y][3] = grey_scaled;

            }
        }

        return convertToBimage(ImageArray);
    }


    private BufferedImage automatedThresh(BufferedImage timg) {
        int mean_back_r_value = 0;
        int mean_back_g_value = 0;
        int mean_back_b_value = 0;
        int mean_obj_r_value = 0;
        int mean_obj_g_value = 0;
        int mean_obj_b_value = 0;
        int thresh_r, thresh_g, thresh_b, r_object_count, green_object_count, blue_object_count, r_background_count,
                green_background_count, blue_background_count, t_r, t_g, t_b;
        int t_0r = 1;
        int t_0g =1 ;
        int t_0b = 1;
        int[][][] ImageArray = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r, g, b;


        //set the four corner pixels as bg and find mean values of objects and bg
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = ImageArray[x][y][1];
                g = ImageArray[x][y][2];
                b = ImageArray[x][y][3];

                if ((y == 0 && x == 0)
                        || (y == width - 1 && x == 0)
                        || (y == 0 && x == height - 1)
                        || (y == width - 1 && x == height - 1)) {
                    mean_back_r_value += r;
                    mean_back_g_value += g;
                    mean_back_b_value += b;

                } else {
                    mean_obj_r_value += r;
                    mean_obj_g_value += g;
                    mean_obj_b_value += b;
                }
            }
        }

        mean_back_r_value = mean_back_r_value / 4;
        mean_back_g_value = mean_back_g_value / 4;
        mean_back_b_value = mean_back_b_value / 4;
        mean_obj_r_value = mean_obj_r_value / ((width * height) - 4);
        mean_obj_g_value = mean_obj_g_value / ((width * height) - 4);
        mean_obj_b_value = mean_obj_b_value / ((width * height) - 4);
        thresh_r = (mean_back_r_value + mean_obj_r_value) / 2;
        thresh_g = (mean_back_g_value + mean_obj_g_value) / 2;
        thresh_b = (mean_back_b_value + mean_obj_b_value) / 2;
        System.out.println("Initial background mean for red: " + mean_back_r_value + " green: " + mean_back_g_value + " blue: " + mean_back_b_value);
        System.out.println("Initial object mean: red: " + mean_obj_r_value + " green: " + mean_obj_g_value + " blue: " + mean_obj_b_value);
        System.out.println("Initial T_(t+1): " + thresh_r + " " + thresh_g + " " + thresh_b);


        /*
         Iterate through red, green and blue pixel values and find optimum thresh value for red, green and blue (respectively)
         steps = find all objects and bg pixel values
         calculate mean of obj and bg pixel values
         when the difference between the current thresh value and previous
         value is less than 1
         break out of the loop
         */

        while (true) {
            mean_obj_r_value = 0;
            mean_back_r_value = 0;
            r_object_count = 0;
            r_background_count = 0;
            t_r = thresh_r;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    r = ImageArray[x][y][1];
                    //compute background and object mean over segmented image
                    if (r >= t_r) {
                        mean_obj_r_value += r;
                        r_object_count += 1;
                    } else if (r < t_r) {
                        mean_back_r_value += r;
                        r_background_count += 1;
                    }
                }
            }

            if (mean_back_r_value > 0) {
                mean_back_r_value = mean_back_r_value / r_background_count;
            }
            if (mean_obj_r_value > 0) {
                mean_obj_r_value = mean_obj_r_value / r_object_count;
            }


            thresh_r = (mean_back_r_value + mean_obj_r_value) / 2;
            //Stop loop
            if (Math.abs(thresh_r - t_r) < t_0r) {
                break;


            }
        }
        //green
        while (true) {
            mean_obj_g_value = 0;
            mean_back_g_value = 0;
            green_object_count = 0;
            green_background_count = 0;
            t_g = thresh_g;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    g = ImageArray[x][y][2];
                    //compute background and object mean over segmented image
                    if (g >= t_g) {
                        mean_obj_g_value += g;
                        green_object_count += 1;
                    } else if (g < t_g) {
                        mean_back_g_value += g;
                        green_background_count += 1;
                    }
                }
            }

            if (mean_back_g_value > 0) {
                mean_back_g_value = mean_back_g_value / green_background_count;
            }
            if (mean_obj_g_value > 0) {
                mean_obj_g_value = mean_obj_g_value / green_object_count;
            }

            thresh_g = (mean_back_g_value + mean_obj_g_value) / 2;
            //Stop loop
            if (Math.abs(thresh_g - t_g) < t_0g) {
                break;
            }
        }
        //blue
        while (true) {
            mean_obj_b_value = 0;
            mean_back_b_value = 0;
            blue_object_count = 0;
            blue_background_count = 0;
            t_b = thresh_b;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    b = ImageArray[x][y][3];
                    //compute background and object mean over segmented image
                    if (b >= t_b) {
                        mean_obj_b_value += b;
                        blue_object_count += 1;
                    } else if (b < t_b) {
                        mean_back_b_value += b;
                        blue_background_count += 1;
                    }
                }
            }
            if (mean_back_b_value > 0) {
                mean_back_b_value = mean_back_b_value / blue_background_count;
            }
            if (mean_obj_b_value > 0) {
                mean_obj_b_value = mean_obj_b_value / blue_object_count;
            }
            thresh_b = (mean_back_b_value + mean_obj_b_value) / 2;
            //Stop loop
            if (Math.abs(thresh_b - t_b) < t_0b) {
                break;
            }
        }

        //Iterate through image and segment using the calculated thresh value
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = ImageArray[x][y][1];
                g = ImageArray[x][y][2];
                b = ImageArray[x][y][3];
                if (r >= thresh_r) {
                    r = 255;
                } else if (r < thresh_r) {
                    r = 0;
                }
                if (g >= thresh_g) {
                    g = 255;
                } else if (g < thresh_g) {
                    g = 0;
                }
                if (b >= thresh_b) {
                    b = 255;
                } else if (b < thresh_b) {
                    b = 0;
                }
                ImageArray[x][y][1] = checkBoundary(r);
                ImageArray[x][y][2] = checkBoundary(g);
                ImageArray[x][y][3] = checkBoundary(b);
            }

        }
        undoList.add(convertToBimage(ImageArray));
        return convertToBimage(ImageArray);

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
    private void filterImage() {

        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0: biFiltered = bi; /* original */
                return;
            case 1: biFiltered = ImageNegative(bi); /* Image Negative */
                return;
            case 2: biFiltered = Rescale(bi);/* rescaling pixel values */
                return;
            case 3: biFiltered = ShiftImage(bi);/* shifting pixel values */
                return;
            case 4: biFiltered = RescaleAndShift(bi);/* randomly rescaling + shift pixel values */
                return;
            case 5: biFiltered = addImage(bi); /* adding two images together */
                return;
            case 6: biFiltered = subtractImage(bi); /* subtracting two images together */
                return;
            case 7: biFiltered = MultiplyImage(bi); /* multiplying two images together */
                return;
            case 8: biFiltered = Divided(bi);  /* dividing two images together */
                return;
            case 9: biFiltered = bitwiseNOT(bi);  /* bitwise not on image*/
                return;
            case 10: biFiltered = bitwiseAND(bi);  /* bitwise AND on two images together */
                return;
            case 11: biFiltered = bitwiseOR(bi);  /* bitwise OR on two images */
                return;
            case 12: biFiltered = bitwiseXOR(bi); /* bitwise XOR on two images */
                return;
            case 13: biFiltered = Logarithm(bi); /* Log law on image */
                return;
            case 14: biFiltered = PowerLaw(bi); /* Power law on image */
                return;
            case 15: biFiltered = useLookUpTable(bi); /* LUT with power law or log law */
                return;
            case 16: biFiltered = bitplaneslice(bi); /* bitplane slicing */
                return;
            case 17: biFiltered = histogram(bi); /* find histogram, normalise, and equalise */
                return;
            case 18: biFiltered = mask(bi); /* applying masks on image */
                return;
            case 19: biFiltered = median_filter(bi); /* median filter on image */
                return;
            case 20: biFiltered = min_filter(bi); /* min filter on image */
                return;
            case 21: biFiltered = max_filter(bi); /* max filter on image */
                return;
            case 22: biFiltered = midpoint_filter(bi); /* midpoint filter on image */
                return;
            case 23: biFiltered = addSaltAndPepper(bi); /* adding salt and pepper on image */
                return;
            case 24: /* combining two filters together */
                if(undoList.size()-2>=0) {
                    biFiltered = combineFilters(bi, undoList.get(undoList.size() - 2));
                    return;
                }
            case 25: biFiltered = histogramMeanStd(bi); /* mean and standard deviation of image */
                return;
            case 26: biFiltered = simpleThresholding(bi); /* simple thresholding */
                return;
            case 27: biFiltered = automatedThresh(bi); /* automated thresholding */
            return;
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

    private void undoImage(){
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
                BufferedImage bi2 = ImageIO.read(new File(this.filepath));
                Graphics big = bi2.getGraphics();
                big.drawImage(bi2, 0, 0, null);
                biFiltered = bi = bi2;
                repaint();
            }

        }
        catch (Exception e){e.printStackTrace();}
    }

    private void combineROI(){
        try {
            BufferedImage bi3 = this.RegionOfInterestComb(this.undoList.get(this.undoList.size() - 2));
            Graphics big = bi3.getGraphics();
            big.drawImage(bi3, 0, 0, null);
            this.biFiltered = this.bi = bi3;
            this.repaint();
        }
        catch (Exception e){
            //e.printStackTrace();
            System.out.println("No filter added");
             }
    }

    private void reset(){
        try {
            BufferedImage original = ImageIO.read(new File(this.filepath));
            this.undoList.removeAll(this.undoList);
            BufferedImage bi4 = original;
            Graphics big = bi4.getGraphics();
            big.drawImage(bi4, 0, 0, null);
            this.biFiltered = this.bi = bi4;
            this.repaint();
        }
        catch (Exception e){
            e.printStackTrace();
        }

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