import java.io.*;
import java.util.*;
/* 
    This algorithm takes in a input file, parses the data, and outputs a file with all the binned data.
    
*/

public class binningAlgorithm_V2{

    static ArrayList<ArrayList<Double>> bins = new ArrayList<>();
    static ArrayList<Double> mins = new ArrayList<>();
    static ArrayList<Double> maxs = new ArrayList<>();

    public static void main(String[] args){
        int MAXDATA = 1000; // can be subject to change 
        Double numbers[] = new Double[MAXDATA];
        if(args.length < 2 || args.length > 2 ){
            System.out.println("Usage <input file name> <output file>");
            System.exit(0);
        }

        //Creating the file to write to 
        File outFile = null; 
        try{
            outFile = new File(args[1]);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
              } else {
                System.out.println("File already exists.");
              }
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
        //Opening the file to read from 
        File inFile = null;
        Scanner sc = null;
        try{
            inFile = new File(args[0]);
            sc = new Scanner(inFile);
        }catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //parsing the file
        int p = 0; 
        while(sc.hasNextDouble()){
            numbers[p] = sc.nextDouble();
            p++;
        }

        //for testing purposes 
        for(int i = 0; i < numbers.length; i++){
            if(numbers[i] == null){
                break;
            }
            System.out.println(numbers[i]);
        }

        


    }

    //the binning algorithm 
    public static void binning(Double numbers[]){

        for(int i = 0; i < numbers.length; i++){

            //there is nothing left in the array 
            if(numbers[i] == null){
                break; 
            }

             //base case 
            if(bins.size() == 0){
                ArrayList<Double> newBin = new ArrayList<>();
                newBin.add(numbers[i]);
                mins.add(numbers[i]);
                max.add(numbers[i]);
                bins.add(newBin);
            }


        }


    }

    //the method that will determine whether or not we split the bins
    public static boolean split(Double num){

    }
    
}