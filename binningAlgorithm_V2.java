import java.io.*;
import java.util.*;
/* 
    This algorithm takes in a input file, parses the data, and outputs a file with all the binned data.
    
*/

public class binningAlgorithm_V2 extends Bin{

    static ArrayList<Bin> bins = new ArrayList<>();

    public static void main(String[] args){
        int MAXDATA = 1000; // can be subject to change 
        Double numbers[] = new Double[MAXDATA];
        if(args.length < 2 || args.length > 2 ){
            System.out.println("Usage <input file name> <output file>");
            System.exit(0);
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

        String data = "";
        data = data + "Binning Data \n";
        binning(numbers);

        System.out.println(bins.size());
        // this prints out the data for the algorithm
        for (int x = 0; x < bins.size(); x++) {
            data = data + " Bin: " + x + "\n";
            for (int i = 0; i < bins.get(x).getList().size(); i++) {
                data = data + " " + bins.get(x).getList().get(i) + "\n";
            }
        }

        for (int k = 0; k < bins.size(); k++) {
            data = data + " Bin: " + k + "\n Max: " + bins.get(k).getMax() + "\n";
            data = data + " Min: " + bins.get(k).getMin() + "\n";
        }

        // Creating the file to write to
        File outFile = null;
        FileWriter fr = null;
        try {
            outFile = new File(args[1]);
            if (outFile.createNewFile()) {
                System.out.println("File created: " + outFile.getName());
                fr = new FileWriter(outFile);
                fr.write(data);
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }

    //the binning algorithm 
    public static void binning(Double numbers[]){

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
            for(int x = 0; x < bins.size(); x++){
                calcNM(bins.get(x)); // (re)calc all n's and m's
            }
            // sort all bins
            for (Bin bin : bins) {
                bin.sort();
            }

            //goes through each bin to determine if we should split
            for(int j = 0; j < bins.size(); j++){
                if(bins.get(j).getList().size() >= 6){
                    if(determineSplit(j)){
                        break;
                    }
                }
            }

        }


    }
    
    public static void calcNM(Bin currBin){
        Double avg = 0.0;
        int dividend = 0;
        for(int j = 0; j < currBin.getList().size(); j++){
            avg += currBin.getList().get(j);
            dividend++;
        }
        avg = avg/dividend;
        currBin.setN(Math.abs(avg - currBin.getMin()));
        currBin.setM(Math.abs(avg - currBin.getMax()));
        currBin.setTotal(currBin.getN()+currBin.getM());
    }

    //the method that will determine whether or not we split the bin
    public static boolean determineSplit(int binIndex){
        ArrayList<Bin> temp = new ArrayList<Bin>();
        Bin currBin = bins.get((binIndex));
        int i = 3; //potential splits
        while(i <= currBin.getList().size() - 3){
            Bin bin1 = new Bin();
            Bin bin2 = new Bin();
            bin1.setList(currBin.getList().subList(0, i));
            bin2.setList(currBin.getList().subList(i, currBin.getList().size()));
            bin1.recalMinMax();
            bin2.recalMinMax();
            calcNM(bin1);
            calcNM(bin2);
            temp.add(bin1);
            temp.add(bin2);
            i++;
        }

        Double avg = 0.0;
        int count = 0;
        for(int k = 0; k < bins.size(); k++){
            avg += bins.get(k).total;
            count ++;
        }
        avg = avg/count;

        Bin bestBin = null;
        for(int j = 0; j < temp.size(); j++){
            if(temp.get(j).total < currBin.total){
                if(temp.get(j).total > avg){
                    if(bestBin == null){
                        bestBin = temp.get(j);
                    }
                    else if(temp.get(j).total < bestBin.total){
                        bestBin = temp.get(j);
                    }
                }
            }
        }

        if(bestBin == null){
            return false;
        }

        bins.remove(binIndex);
        if(temp.indexOf(bestBin) % 2 == 0){
            bins.add(temp.get(temp.indexOf(bestBin) - 1));
            bins.add(temp.get(temp.indexOf(bestBin) - 2));
        }
        else{
            bins.add(temp.get(temp.indexOf(bestBin) - 1));
            bins.add(temp.get(temp.indexOf(bestBin) + 1));
        }

        return true;
    }

    //recalculates all the bins mins and maxs
    public static void recalculate(){

        for(int i = 0; i < bins.size(); i++){
            bins.get(i).recalMinMax();
        }

    }
    
}