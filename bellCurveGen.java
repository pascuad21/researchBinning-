import java.io.*;
import java.util.*;

/* 
    The program that generates test files for binning algorithm testing
*/

public class bellCurveGen{

    public static void main(String[] args){
        if(args.length < 5 || args.length > 5){
            System.out.println("Usage: <output file name> <steepness> <number of Curves> <overflow decimal> <number of points>");
        }
        int steepness = Integer.parseInt(args[1]);
        int numCurves = Integer.parseInt(args[2]);
        double overflowPercent = Double.parseDouble(args[3]);
        int numPoints = Integer.parseInt(args[4]);
        double MIN = 0.0;
        double MAX = 10.0;  //currently hard coded, could change in the future. 
        double range = MAX/numCurves; //this divided the number of points evenly by the number of curves
        Double numbers[] = new Double[numPoints]; //creates an array the size the user wanted
        double overflow = (overflowPercent * range)/2; //this is to be added to the left and right of the bell curve 

         //creates the file that we will be writing to.
         File file = null; 
         try {
            file = new File(args[0]);
            if (file.createNewFile()) {
              System.out.println("File created: " + file.getName());
            } 
            else {
               System.out.println("File already exists.");
              }
            } catch (IOException e) {
                 System.out.println("An error occurred.");
                e.printStackTrace();
          }

         //MULTIPLE CURVES FUNTIONALITY 
        //this will keep generating numbers in a bell curve based on the 
        //min an max of a range. 
        double start = 0.0; //the min for the rand num generator 
        int j = 0; //to keep track of the location in the array
        while(start < MAX){
            int w = 0;
            //divided the number of points evenly based on the range
            while(w < numPoints/numCurves){

                //OVERFLOW FUNCTIONALITY 
                //CURRENTLY NOT THE BEST
                if(start != 0.0 && (start + range) != MAX){
                    //This adds in the overflow 
                    //System.out.println("overflow");
                    numbers[j] = randNumGenerator(start- overflow, start + range + overflow);
                }
                else if ((start + range) >= MAX){
                    //overflow for the end
                    //System.out.println("left overflow");
                    numbers[j] = randNumGenerator(start - overflow, start + range);
                }
                else{
                    //overflow for the beginning 
                    //System.out.println("right overflow");
                    numbers[j] = randNumGenerator(start, start + range + overflow);
                }
                j++;
                w++;
            }
            start = start + range;
        }

        String data = "";

        //shuffles the data
        Collections.shuffle(Arrays.asList(numbers));

        //formats the data into a string that will be written to the file
        for (int i = 0; i < numbers.length; i++) {
            data = data + " " + numbers[i] + "\n";
        }

        //printing the data to the file
        FileWriter fr = null;
        try{
            fr = new FileWriter(file);
            fr.write(data);
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                 fr.close();
           }catch(IOException e){
                e.printStackTrace();
            }
        }         


    }

    //algorythm that we found on the interwebs 
    //https://stackoverflow.com/questions/25582882/javascript-math-random-normal-distribution-gaussian-bell-curve
    public static double randNumGenerator(double min, double max){
        double u = 0, v = 0;
        while(u == 0) u = Math.random(); //Converting [0,1) to (0,1)
        while(v == 0) v = Math.random();
        double num = Math.sqrt( -2.0 * Math.log( u ) ) * Math.cos( 2.0 * Math.PI * v );

        num = num / 10.0 + 0.5; // Translate to 0 -> 1
        if (num > 1 || num < 0) num = randNumGenerator(min, max); // resample between 0 and 1 if out of range
        num *= max - min; // Stretch to fill range
        num += min; // offset to min
        return num;
    }

}