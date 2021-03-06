package environments.fsm;

import framework.*;

import java.io.File;//!Dylans Test
import java.io.IOException;//!Dylans Test 
import java.util.*;

/**
 * An FSMEnvironment is an environment modeled as a Finite State Machine.
 *
 * 
 * TODO -- Add back in some variation of the transition age sensor.
 * TODO -- Add a configuration that allows the FSM to tweak its transition table every N goals.
 *
 * @author Zachary Paul Faltersack
 * @version 0.95
 */
public class FSMEnvironment implements IEnvironment {
    //region Class Variables
    private FSMTransitionTable transitionTable;
    private Action[] actions;
    private EnumSet<Sensor> sensorsToInclude;
    private Random random;
    private int randomSeed;

    private int currentState;

    Double numbers[] = new Double[1000];//!Dylan's Test
    ArrayList<Double> usedNumbers = new ArrayList<Double>();//!Dylans test
    //endregion

    //region Constructors
    /**
     * Create an instance of a {@link FSMEnvironment}.
     * @param randomSeed Allows the external configuration of a seed for the randomizer.
     * @param transitionTable The transition table that indicates the structure of a FSM.
     */
    public FSMEnvironment(int randomSeed, FSMTransitionTable transitionTable) {
        this(randomSeed, transitionTable, EnumSet.noneOf(Sensor.class));
    }

    /**
     * Create an instance of a {@link FSMEnvironment} that includes possible sensor data.
     * @param randomSeed Allows the external configuration of a seed for the randomizer.
     * @param transitionTable The transition table that indicates the structure of a FSM.
     * @param sensorsToInclude The sensors to include when navigating the FSM.
     */
    public FSMEnvironment(int randomSeed, FSMTransitionTable transitionTable, EnumSet<Sensor> sensorsToInclude) {
        if (transitionTable == null)
            throw new IllegalArgumentException("transitionTable cannot be null");
        if (sensorsToInclude == null)
            throw new IllegalArgumentException("sensorsToInclude cannot be null");
        this.randomSeed = randomSeed;
        this.random = new Random(randomSeed);
        this.transitionTable = transitionTable;
        this.sensorsToInclude = sensorsToInclude;
        this.actions = this.transitionTable.getTransitions()[0].keySet().toArray(new Action[0]);
        this.currentState = this.getRandomState();

        //! Opening the file to read from (Begin of Dylans Test)
        File inFile = null;
        Scanner sc = null;
        try {
            inFile = new File("EpSemTestNums.txt");
            sc = new Scanner(inFile);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // parsing the file
        int p = 0;
        while (sc.hasNextDouble()) {
            numbers[p] = sc.nextDouble();
            p++;
        }
        usedNumbers.add(15.0);
        //!end Dylans test

        //DEBUG: print the transition table
        //System.err.println("Transition table:");
        //System.err.println(this.transitionTable);
    }

    private FSMEnvironment(FSMEnvironment toCopy) {
        this.randomSeed = toCopy.randomSeed;
        this.random = new Random(this.randomSeed);
        this.transitionTable = toCopy.transitionTable;
        this.sensorsToInclude = toCopy.sensorsToInclude;
        this.actions = toCopy.actions;
        this.currentState = toCopy.currentState;
        //!Dylans testing
        this.numbers = toCopy.numbers;
        this.usedNumbers = toCopy.usedNumbers;
        //!end of Dylan's testing
    }

    //endregion

    //region IEnvironment Members
    /**
     * Get the {@link Action}s for this environment description.
     * @return The array of valid {@link Action}s.
     */
    @Override
    public Action[] getActions() {
        return this.actions;
    }

    /**
     *
     * @param action null or invalid action return sensor data for current state with the GOAL flag set.
     * @return
     */
    @Override
    public SensorData applyAction(Action action) {
        SensorData sensorData;
        HashMap<Action, Integer> transitions = this.transitionTable.getTransitions()[this.currentState];
        if (action == null || !transitions.containsKey(action)) {
            sensorData = new SensorData(true);
            this.applySensors(this.currentState, sensorData);
            return sensorData;
        }
        this.currentState = transitions.get(action);
        if (this.transitionTable.isGoalState(this.currentState)) {
            sensorData = new SensorData(true);
            this.currentState = this.getRandomState();
        }
        else
            sensorData = new SensorData(false);
        this.applySensors(this.currentState, sensorData);

        //DEBUG: print the current state
        //System.err.println("new state: " + this.currentState);
        
        return sensorData;
    }

    /**
     * Creates a copy of this {@link IEnvironment}.
     *
     * @return A copy of the environment. Be mindful of shallow vs deep copy when implementing to prevent contaminating
     * test runs.
     */
    @Override
    public IEnvironment copy() {
        return new FSMEnvironment(this);
    }

    @Override
    public boolean validateSequence(Sequence sequence) {
        if (sequence == null)
            throw new IllegalArgumentException("sequence cannot be null.");
        int tempState = this.currentState;
        for (Action action : sequence.getActions()) {
            tempState = this.transitionTable.getTransitions()[tempState].get(action);
            if (this.transitionTable.isGoalState(tempState))
                return true;
        }
        return false;
    }
    //endregion

    //region Private Methods
    private int getRandomState() {
        int nonGoalStates = this.transitionTable.getNumberOfStates() - 1;
        return random.nextInt(nonGoalStates);
    }

    /**
     * Apply sensor data for the given state to the provided {@link SensorData}.
     * @param currentState The state that was transitioned to.
     * @param sensorData The {@link SensorData} to apply sensors to.
     */
    private void applySensors(int currentState, SensorData sensorData) {
        //!Uncomment below
        if (this.sensorsToInclude.contains(Sensor.IS_EVEN))
            this.applyEvenOddSensor(currentState, sensorData);
        if (this.sensorsToInclude.contains(Sensor.MOD_3)){
            this.applyMod3Sensor(currentState, sensorData);
        }
        this.applyWithinNSensors(currentState, sensorData);
        this.applyNoiseSensors(sensorData);
        this.applyCactiSensors(currentState, sensorData);
        //!Uncomment above

        //!Dylans testing
        this.applyBinSensors(currentState, sensorData);
    }

    private void applyEvenOddSensor(int state, SensorData sensorData) {
        sensorData.setSensor(Sensor.IS_EVEN.toString(), state % 2 == 0);
    }

    private void applyMod3Sensor(int state, SensorData sensorData){
        sensorData.setSensor(Sensor.MOD_3.toString(), state % 3 == 0);
    }

    private void applyNoiseSensors(SensorData sensorData) {
        if (this.sensorsToInclude.contains(Sensor.NOISE1))
            sensorData.setSensor(Sensor.NOISE1.toString(), Math.random() > 0.5);
        if (this.sensorsToInclude.contains(Sensor.NOISE2))
            sensorData.setSensor(Sensor.NOISE2.toString(), Math.random() > 0.5);
        if (this.sensorsToInclude.contains(Sensor.NOISE3))
            sensorData.setSensor(Sensor.NOISE3.toString(), Math.random() > 0.5);
        if (this.sensorsToInclude.contains(Sensor.NOISE4))
            sensorData.setSensor(Sensor.NOISE4.toString(), Math.random() > 0.5);
    }

    private void applyCactiSensors(int state, SensorData sensorData) {
        if (this.sensorsToInclude.contains(Sensor.CACTUS1))
            sensorData.setSensor(Sensor.CACTUS1.toString(), state == 0);
        if (this.sensorsToInclude.contains(Sensor.CACTUS2))
            sensorData.setSensor(Sensor.CACTUS2.toString(), state == 1);
        if (this.sensorsToInclude.contains(Sensor.CACTUS3))
            sensorData.setSensor(Sensor.CACTUS3.toString(), state == 2);
        if (this.sensorsToInclude.contains(Sensor.CACTUS4))
            sensorData.setSensor(Sensor.CACTUS4.toString(), state == 3);
        if (this.sensorsToInclude.contains(Sensor.CACTUS5))
            sensorData.setSensor(Sensor.CACTUS5.toString(), state == 4);
        if (this.sensorsToInclude.contains(Sensor.CACTUS6))
            sensorData.setSensor(Sensor.CACTUS6.toString(), state == 5);
        if (this.sensorsToInclude.contains(Sensor.CACTUS7))
            sensorData.setSensor(Sensor.CACTUS7.toString(), state == 6);
        if (this.sensorsToInclude.contains(Sensor.CACTUS8))
            sensorData.setSensor(Sensor.CACTUS8.toString(), state == 7);
        if (this.sensorsToInclude.contains(Sensor.CACTUS9))
            sensorData.setSensor(Sensor.CACTUS9.toString(), state == 8);
    }

	//!Discretization testing 4 bins ~Dylan^2
	private void applyBinSensors(int state, SensorData sensorData) {

        int index = 0;
        //Take random num check if <= 5 then its bin 0
        Double testingNum = 0.0;
        Double numberToUse = 0.0;
        if(state % 2 == 0){//in even state
            //go through numbers list and find one that is in the "even" curve
            while(index < this.numbers.length){
                if(index == 200){
                    this.usedNumbers.clear();
                    index = 0;
                }
                testingNum = this.numbers[index];
                //System.out.println("Testing Num: " + index);
                if(testingNum <= 5.0 && !this.usedNumbers.contains(testingNum)){
                    numberToUse = testingNum;
                    this.usedNumbers.add(testingNum);
                    break;
                }
                index++;
            }
        }
        else{//in odd state
            // go through numbers list and find one that is in the "even" curve
            while(index < this.numbers.length){
                if (index == 200) {
                    this.usedNumbers.clear();
                    index = 0;
                }
                testingNum = this.numbers[index];
                //System.out.println("Testing Num ODD: " + index);
                if(testingNum > 5.0 && !this.usedNumbers.contains(testingNum)){
                    numberToUse = testingNum;
                    this.usedNumbers.add(testingNum);
                    break;
                }
                index++;
            }
        }
        //If Even(bin 0-1) or Odd(bin 2-3) state check randome num pulled
        //fits this description if not get another one

        //check what bin 1-4 it would be in (0-2.5)(2.5-5.0)(5.0-7.5)(7.5-10.0)

		if (this.sensorsToInclude.contains(Sensor.BIN1))
            sensorData.setSensor(Sensor.BIN1.toString(), numberToUse >= 0.0 && numberToUse <= 2.5);
        if (this.sensorsToInclude.contains(Sensor.BIN2))
            sensorData.setSensor(Sensor.BIN2.toString(), false);
        if (this.sensorsToInclude.contains(Sensor.BIN3))
            sensorData.setSensor(Sensor.BIN3.toString(), numberToUse > 2.5 && numberToUse <= 2.8);
        if (this.sensorsToInclude.contains(Sensor.BIN4))
            sensorData.setSensor(Sensor.BIN4.toString(), false);
        if (this.sensorsToInclude.contains(Sensor.BIN5))
            sensorData.setSensor(Sensor.BIN5.toString(), numberToUse > 2.8 && numberToUse <= 5.0);
        if (this.sensorsToInclude.contains(Sensor.BIN6))
            sensorData.setSensor(Sensor.BIN6.toString(), numberToUse > 5.0 && numberToUse <= 7.4);
        if (this.sensorsToInclude.contains(Sensor.BIN7))
            sensorData.setSensor(Sensor.BIN7.toString(), false);
        if (this.sensorsToInclude.contains(Sensor.BIN8))
            sensorData.setSensor(Sensor.BIN8.toString(), numberToUse > 7.4 && numberToUse <= 7.8);
        if (this.sensorsToInclude.contains(Sensor.BIN9))
            sensorData.setSensor(Sensor.BIN9.toString(), false);
        if (this.sensorsToInclude.contains(Sensor.BIN10))
            sensorData.setSensor(Sensor.BIN10.toString(), numberToUse > 7.8 && numberToUse <= 10.0);
    }
    
    //!End Dylans Discretization

    private void applyWithinNSensors(int state, SensorData sensorData){
//        for(int i = 0; i< 20; i++){
//            if(sensorsToInclude.contains(Sensor.fromString("WITHIN_"+i))){
//                sensorData.setSensor("WITHIN_" + i, this.transitionTable.getShortestSequences().get(state).size() <= i);
//            }
//        }
    }
    //endregion

    //region Enums
    /**
     * Define the available sensors in the environment.
     */
    public enum Sensor {
        /**
         * Identifies the sensor that determines if the current state is even or odd.
         */
        IS_EVEN,
        MOD_3,

        //!Dylans Testng
		/** Identifies the sensor that determines which bin the generated number is in */
		BIN1,
		BIN2,
		BIN3,
        BIN4,
        BIN5,
        BIN6,
        BIN7,
        BIN8,
        BIN9,
        BIN10,
        //!End Dylans Testing

        /** Identifies the sensor that turns on for exactly one state in the FSM */
        CACTUS1, //On if in state 0
        CACTUS2, //On if in state 1
        CACTUS3, // ...
        CACTUS4,
        CACTUS5,
        CACTUS6,
        CACTUS7,
        CACTUS8, // ...
        CACTUS9, //On in state 8

        WITHIN_1,
        WITHIN_2,
        WITHIN_4,
        WITHIN_8,
        WITHIN_10,
        WITHIN_20,
        /**
         * Identifies the noise sensor that can randomly be applied to a state.
         */
        NOISE1,
        NOISE2,
        NOISE3,
        NOISE4,
        TRANSITION_AGE;

        /**
         * Identifies the complete sensor set for the environment.
         */
        public static final EnumSet<Sensor> ALL_SENSORS = EnumSet.allOf(Sensor.class);

        public static final EnumSet<Sensor> NO_SENSORS = EnumSet.noneOf(Sensor.class);

        public static final EnumSet<Sensor> WITHIN_SENSORS = EnumSet.of(WITHIN_1,
                WITHIN_2,
                WITHIN_4,
                WITHIN_8,
                WITHIN_10,
                WITHIN_20);

        public static final EnumSet<Sensor> CACTI_SENSORS = EnumSet.of(CACTUS1,
                CACTUS2,
                CACTUS3,
                CACTUS4,
                CACTUS5,
                CACTUS6,
                CACTUS7,
                CACTUS8,
                CACTUS9);
		//!Dylans Testing
		//Discretization Testing Dylan^2
		public static final EnumSet<Sensor> BIN_SENSORS = EnumSet.of(BIN1,
				BIN2,
				BIN3,
                BIN4,
                BIN5,
                BIN6,
                BIN7,
                BIN8,
                BIN9,
                BIN10);
        //!End of Dylans Testing

        public static Sensor fromString(String in){
            for(Sensor s : Sensor.values()){
                if(s.toString().equals(in)){
                    return s;
                }
            }
            return null;
        }
    }
    //endregion
}
