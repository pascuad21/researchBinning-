import java.util.ArrayList;
import java.util.Collections;

public class Bin{
    Double n;
    Double m;
    Double total;
    Double min;
    Double max;
    ArrayList<Double> numbers;

    public Bin(){
        this.n = 0.0;
        this.m = 0.0;
        this.total = 0.0;
        this.min = 0.0;
        this.max = 0.0;
        numbers = null;
    }

    public Bin(Double n, Double m, Double min, Double max, Double num){
        this.n = n;
        this.m = m;
        this.min = min;
        this.max = max;
        numbers.add(num);
        this.total = n + m;
    }
    //GETTERS
    public Double getN() {
        return m;
    }

    public Double getM(){
        return n;
    }

    public Double getTotal() {
        return total;
    }

    public Double getMin(){
        return min;
    }

    public Double getMax(){
        return max;
    }

    public ArrayList<Double> getList(){
        return numbers;
    }

    //SETTERS
    public void addNum(Double num){
        numbers.add(num);
    }

    public void setN(Double n){
        this.n = n;
    }

    public void setM(Double m){
        this.m = m;
    }

    public void setTotal(Double total){
        this.total = total;
    }

    public void setMin(Double min){
        this.min = min;
    }

    public void setMax(Double max){
        this.max = max;
    }

    //Other methods
    public void sort(){
        Collections.sort(numbers);
    }

    public void recalMinMax(){
        if (numbers.size() == 1) {
            this.max = numbers.get(0);
            this.min = numbers.get(0);
        } 
        else {
            this.max = Collections.max(numbers);
            this.min = Collections.min(numbers);
        }
    }

}