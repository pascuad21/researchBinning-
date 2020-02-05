import java.io.*;
import java.util.*;

/* 
This will house the algorythm for both generating bell curve values

Authors: Dylan Pascua and Dylan Degrood 
Date: 1/31/2020
*/

public class biningAlgorythm{

    static ArrayList<ArrayList<Double>> bins = new ArrayList<>();
    static ArrayList<Double> firstBin = new ArrayList<>();
    static ArrayList<Double> secondBin = new ArrayList<>();
    static ArrayList<Double> mins = new ArrayList<>();
    static ArrayList<Double> maxs = new ArrayList<>();

    public static void main(String[] args){
        if(args.length < 5){
            System.out.println("Useage: <file name> <steepness> <number of Curves> <overflow decimal> <number of points>");
            System.exit(0);
        }
        int steepness = Integer.parseInt(args[1]);
        int numCurves = Integer.parseInt(args[2]);
        double overflowPercent = Double.parseDouble(args[3]);
        int numPoints = Integer.parseInt(args[4]);
        double MIN = 0.0;
        double MAX = 10.0;  //currently hard coded, could change in the future. 
        double range = MAX/numCurves; //this divided the number of points evenly by the number of curves
        double numbers[] = new double[numPoints]; //creates an array the size the user wanted
        double overflow = (overflowPercent * range)/2; //this is to be added to the left and right of the bell curve 
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


        //FOR TESTING
            // int w = 0;
            // double numbers[] = new double[200];
            // while(w < 200){
            //     numbers[w] = randNumGenerator(0,10); 
            //     w++;
            // }
        
        String data = "Raw Data \n";
        // FOR TESTING THE GENERATOR
        for (int i = 0; i < numbers.length; i++) {
            data = data + " " + numbers[i] + "\n";
        }
        data = data + "Binning Data \n";
        Collections.shuffle(Arrays.asList(numbers));
        binning(numbers);

        System.out.println(bins.size());
        //this prints out the data for the algorithm 
        for(int x = 0; x < bins.size(); x++){
			data = data + " Bin: " + x + "\n";
            for(int i = 0; i < bins.get(x).size(); i++){
                data = data + " " + bins.get(x).get(i) + "\n";  
            }
        }

		for(int k = 0; k < maxs.size(); k++){
			data = data + " Bin: " + k + "\n Max: " + maxs.get(k) + "\n";
			data = data + " Min: " + mins.get(k) + "\n";
		}

        // data = data + "CURVE 2";
        // for(int i = 0; i < bins.get(1).size(); i++){
        //     data = data + " " + bins.get(1).get(i) + "\n";  
        // }
        // data = data + "CURVE 3";
        // for (int i = 0; i < bins.get(2).size(); i++) {
        //     data = data + " " + bins.get(2).get(i) + "\n";
        // }
        // data = data + "CURVE 4";
        // for (int i = 0; i < bins.get(3).size(); i++) {
        //     data = data + " " + bins.get(3).get(i) + "\n";
        // }
        // data = data + "CURVE 5";
        // for (int i = 0; i < bins.get(4).size(); i++) {
        //     data = data + " " + bins.get(4).get(i) + "\n";
        // }

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

    public static void binning(double numbers[]){

        double beginAvg = 0;
        double difference = 0;
        bins.add(firstBin);
        bins.add(secondBin);

        if(numbers.length >= 4){
            for(int i = 0; i < 4; i++){
                beginAvg += numbers[i];
            }
            beginAvg = beginAvg/4;
        }
        else{
            System.out.println("Number Array Size too Small to Bin");
            return;
        }

        for(int i = 0; i < 4; i++){
            if(numbers[i] < beginAvg){
                bins.get(0).add(numbers[i]);
            }
            else if(numbers[i] > beginAvg){
                bins.get(1).add(numbers[i]);
            }
        }

        maxs.add(Collections.max(bins.get(0)));
        maxs.add(Collections.max(bins.get(1)));
        mins.add(Collections.min(bins.get(0)));
        mins.add(Collections.min(bins.get(1)));
        difference = mins.get(1) - maxs.get(0);

        for(int i = 4; i < numbers.length; i++){
            int whichBin = whichBin(numbers[i], difference);
            recalculate();

            if(whichBin == -1){
                ArrayList<Double> newBin = new ArrayList<>();
                newBin.add(numbers[i]);
                mins.add(numbers[i]);
                maxs.add(numbers[i]);
                bins.add(newBin);
            }
            else{
                bins.get(whichBin).add(numbers[i]);
            }
        }

    }

    public static int whichBin(double number, double difference){

        for(int i = 0; i < bins.size(); i++){
            // checking if number is in existing bins
            if(number >= mins.get(i) && number <= maxs.get(i)){
                return i;
            }
            // Within difference of original split on both ends of bin
            else if(number <= maxs.get(i) + difference && number >= mins.get(i) - difference){
                return i;
            }
            else if(bins.get(i).size() == 1){
                return i;
            }
        }

        // does not exist a bin in which the number can go into
        return -1;

    } 

    public static void recalculate(){

        for(int i = 0; i < bins.size(); i++){
            if(bins.get(i).size() == 1){
                continue;
            }
            else{
                maxs.set(i,Collections.max(bins.get(i)));
                mins.set(i, Collections.min(bins.get(i)));
            }
        }

    }


}