import java.io.*;
import java.util.*;

/* 
This will house the algorythm for both generating bell curve values

Authors: Dylan Pascua and Dylan Degrood 
Date: 1/31/2020
*/

public class biningAlgorythm{
    public static void main(String[] args){
        if(args.length < 5){
            System.out.println("Useage: <file name> <steepness> <number of Curves> <overflow decimal> <number of points>");
            System.exit(0);
        }
        int steepness = Integer.parseInt(args[1]);
        int numCurves = Integer.parseInt(args[2]);
        double overflow = Double.parseDouble(args[3]);
        int numPoints = Integer.parseInt(args[4]);
        double MIN = 0.0;
        double MAX = 10.0;  //currently hard coded, could change in the future. 
        double range = MAX/numCurves; //this divided the number of points evenly by the number of curves
        double numbers[] = new double[numPoints]; //creates an array the size the user wanted

        //creates the file that we will be writing to.
        File file = null; 
        try {
            file = new File(args[0]);
            if (file.createNewFile()) {
              System.out.println("File created: " + file.getName());
            } else {
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
                numbers[j] = randNumGenerator(start, start + range);
                j++;
                w++;
            }
            start = start + range;
        }


        //FOR TESTING
            // int w = 0;
            // double numbers[] = new double[200];
            // while(w < 200){
            //     numbers[w] = randNumGenerator(0,10); 
            //     w++;
            // }

        

        String data = "";
        for(int i = 0; i < numbers.length; i++){
            data = data + " " + numbers[i] + "\n";  
        }

        //printing the date to the file
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