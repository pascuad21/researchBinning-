import java.io.*;
import java.util.*;
/* 
    This algorithm takes in a input file, parses the data, and outputs a file with all the binned data.
    
*/

public class binningAlgorithm_V2 extends Bin{

    ArrayList<Bin> bins = new ArrayList<>();

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
            if (outFile.createNewFile()) {
                System.out.println("File created: " + outFile.getName());
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
    public void binning(Double numbers[]){

        for(int i = 0; i < numbers.length; i++){

            //there is nothing left in the array 
            if(numbers[i] == null){
                break; 
            }

             //base case, the first number will make the first bin
            if(bins.size() == 0){
                Bin newBin = new Bin(0.0, 0.0, numbers[i], numbers[i], numbers[i]);
                bins.add(newBin);
                continue;
            }

            System.out.println("Size of Bin " + bins.get(0).getList().size());

            //we need to wait for at least 6 values
            if(bins.size() == 1 && i < 6){
                bins.get(0).addNum(numbers[i]);
                continue;
            }

            //At this point we have 6 numbers in one bin

            //adds the new number before deciding to split or not
            boolean added = false;
            for(int k = 0; k < bins.size(); k++){
                if(numbers[i] > bins.get(k).getMin() && numbers[i] < bins.get(k).getMax()){
                    bins.get(k).addNum(numbers[i]);
                    added = true;
                    break;
                }
            }

            if(!added){
                if(numbers[i] < bins.get(0).getMin()){
                    bins.get(0).addNum(numbers[i]);
                }
                else{
                    bins.get(bins.size()-1).addNum(numbers[i]);
                }
            }


            // start of binning process

            recalculate(); // recalc mins and maxs
            calcNM(); // (re)calc all n's and m's
            // sort all bins
            for (Bin bin : bins) {
                bin.sort();
            }

            for(int j = 0; j < bins.size(); i++){
                if(bins.get(j).getList().size() >= 6){
                    determineSplit(j);
                }
            }

        }


    }
    
    public void calcNM(){
        for(int i = 0; i < bins.size(); i++){
            Bin currBin = bins.get(i);
            Double avg = 0.0;
            int dividend = 0;
            for(int j = 0; i < currBin.getList().size(); i++){
                avg += currBin.getList().get(j);
                dividend++;
            }
            avg = avg/dividend;
            currBin.setN(Math.abs(avg - currBin.getMin()));
            currBin.setM(Math.abs(avg - currBin.getMax()));
        }
    }

    //the method that will determine whether or not we split the bin
    public boolean determineSplit(int binIndex){
        
        return true;
    }

    //recalculates all the bins mins and maxs
    public void recalculate(){

        for(int i = 0; i < bins.size(); i++){
            bins.get(i).recalMinMax();
        }

    }
    
}