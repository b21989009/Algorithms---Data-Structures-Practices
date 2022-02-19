import java.util.*;

public class Scheduler {

    private Assignment[] assignmentArray;
    private Integer[] C;
    private Double[] max;
    private ArrayList<Assignment> solutionDynamic;
    private ArrayList<Assignment> solutionGreedy;


    public Scheduler(Assignment[] assignmentArray) throws IllegalArgumentException {

        // throw IllegalArgumentException if assignmentArray is empty
        if (assignmentArray==null  || assignmentArray.length < 1)
            throw new IllegalArgumentException();


        this.assignmentArray = assignmentArray;
        Arrays.sort(assignmentArray);

        C = new Integer[assignmentArray.length];

        max = new Double[assignmentArray.length];
        Arrays.fill(max, -1.0);

        solutionDynamic = new ArrayList<Assignment>();
        solutionGreedy = new ArrayList<Assignment>();

    }


    /* Since Assignments are sorted based on their finish times,
       using binary search to find first compatible assignment is more efficient than linear search.  */
    private int binarySearch(int index) {

        if (index < 1)
            return -1;

        /*
         assignmentArray can be viewed as:
         [compatible, compatible, ... , FIRST compatible, incompatible, ... , incompatible, the assignment with "index" , ... ]

         indexes:        ...                       ...                          index-1     ,     index                    */

        return recursiveBinarySearch_FindFirstCompatibleAssignment(0, index-1, index);

    }


    private int recursiveBinarySearch_FindFirstCompatibleAssignment (int startOfScope, int endOfScope, int indexOfAssignment_In_C){

        if (startOfScope > endOfScope)
            return -1;

        int medianIndex = (endOfScope + startOfScope + 1 ) / 2 ;
        boolean isMedianCompatible = assignmentArray[indexOfAssignment_In_C].isCompatible(assignmentArray[medianIndex]);


        // BASE CASE
        if (startOfScope == endOfScope){

            if (isMedianCompatible)
                return endOfScope;  // The first compatible Assignment is reached
            else
                return -1;         // There is no compatible Assignment

        }


        // RIGHT CASE
        if (isMedianCompatible)

            // Search the RIGHT HALF  to find the first compatible Assignment
            return recursiveBinarySearch_FindFirstCompatibleAssignment( medianIndex, endOfScope, indexOfAssignment_In_C);

        // LEFT CASE
        else
            // Search the LEFT HALF  to find the first compatible Assignment
            return recursiveBinarySearch_FindFirstCompatibleAssignment( startOfScope, medianIndex-1, indexOfAssignment_In_C);

    }



    /*   C[i] represents the index of the first compatible assignment before Assignment i.     */
    private void calculateC() {

        for (int assignmentIndex=0 ; assignmentIndex < assignmentArray.length ; assignmentIndex++){

            C[assignmentIndex] = binarySearch(assignmentIndex);
        }

    }



    /* Returns a list of scheduled Assignments, using dynamic solution method.
    *
    *  This is the main method of Scheduler class.
    */
    public ArrayList<Assignment> scheduleDynamic() {

        calculateC();
        calculateMax(max.length -1);

        findSolutionDynamic(max.length -1);

        Collections.reverse(solutionDynamic);
        return solutionDynamic;

    }


    /* Recursively finds the best schedule.
    *   from last job to be done, towards first job to be done.
    */
    private void findSolutionDynamic(int i) {

        if (i < 0)
            return;

        System.out.println("findSolutionDynamic(" + i + ")");

        // BASE CASE
        if (i == 0){
            solutionDynamic.add(assignmentArray[0]);
            System.out.println("Adding " + assignmentArray[0] + " to the dynamic schedule");
            return;
        }

        double max_C_i = C[i]<0  ?  0.0  :  max[C[i]] ; // otherwise, max[-1] is an exception.

        double weight_WhenAssignment_i_Is_InSolution = assignmentArray[i].getWeight() + max_C_i;
        double weight_WhenAssignment_i_Is_NOT_InSolution = max[i-1];

        // BETTER TO INCLUDE Assignment i to solution
        if (weight_WhenAssignment_i_Is_InSolution > weight_WhenAssignment_i_Is_NOT_InSolution){

            System.out.println("Adding " + assignmentArray[i] + " to the dynamic schedule");
            solutionDynamic.add(assignmentArray[i]);

            findSolutionDynamic(C[i]);
        }

        // WORSE TO INCLUDE Assignment i to solution
        else{
            findSolutionDynamic(i-1);
        }


    }


    /*** max[i] is the maximum possible weight value, if Assignment i  would be chosen. */
    private Double calculateMax(int i) {

        if (i < 0)
            return 0.0;

        System.out.print("calculateMax(" + i + "): ");

        // BASE CASE
        // Assignment 0  has the maximum total weight of itself.
        if (i == 0){
            System.out.println("Zero");
            max[0] = assignmentArray[0].getWeight();
            return max[0];
        }


        if (max[i] < 0){    // max[] was filled with -1.0  in Scheduler constructor. Correct values are not assigned yet.
            System.out.println("Prepare");
        }
        else{
            System.out.println("Present");
            return max[i];
        }

        // Is it better to include Assignment i ?
        double weight_WhenAssignment_i_Is_InSolution = assignmentArray[i].getWeight() + calculateMax(C[i]);
        double weight_WhenAssignment_i_Is_NOT_InSolution = calculateMax(i-1);

        double maxWeight = Math.max( weight_WhenAssignment_i_Is_InSolution, weight_WhenAssignment_i_Is_NOT_InSolution);
        max[i] = maxWeight;

        return maxWeight;


    }



    /*
     *   Returns a list of scheduled Assignments, using GREEDY solution method.
     *
     *   From first job to be done, towards last job to be done; iteratively finds the best schedule.
     */
    public ArrayList<Assignment> scheduleGreedy() {

        if (assignmentArray==null)
            return null;

        // FIRST Assignment is always picked.
        solutionGreedy.add(assignmentArray[0]);
        System.out.println("Adding " + assignmentArray[0] + " to the greedy schedule");


        for (int assignmentIndex=1 ; assignmentIndex < assignmentArray.length ; assignmentIndex++){

            Assignment mostRecentlySelected_Assignment = solutionGreedy.get( solutionGreedy.size() - 1 );

            if (assignmentArray[assignmentIndex].isCompatible(mostRecentlySelected_Assignment)){

                solutionGreedy.add(assignmentArray[assignmentIndex]);
                System.out.println("Adding " + assignmentArray[assignmentIndex] + " to the greedy schedule");
            }
        }

        return solutionGreedy;

    }


}
