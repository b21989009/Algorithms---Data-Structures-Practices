import java.util.*;
import java.lang.*;

/***
 *  Methods to generate inputs and test sorting algorithms.
 */
public class Testing {

    // celebrities will be the subject of sorting.
    ArrayList<Celebrity> celebrities = new ArrayList<>();


    /* Copying celebrities so that different algorithms can run on same input sequence. */
    ArrayList<Celebrity> copyCelebrityList(){

        ArrayList<Celebrity> celebritiesCopied = new ArrayList<>();

        for (Celebrity celebrity : celebrities ){

            celebritiesCopied.add(  new Celebrity( celebrity.getName(), celebrity.getNumberOfFollowers() )  );
        }

        return celebritiesCopied;

    }



    void randomCelebrityGenerator(int inputSize){
        celebrities.clear();

        for (int celebrityCount = 0 ; celebrityCount < inputSize ; celebrityCount++ ){

            String celebrityName = celebrityNameGenerator();

            // Generate a random number of followers, up to a billion
            int numberOfFollowers = (new Random() ).nextInt(1000000000);

            celebrities.add( new Celebrity(celebrityName, numberOfFollowers));
        }

    }



    String celebrityNameGenerator(){

        String alphabet = "ABCDEFGHIJKLMNOPRSTUVWXYZ";

        /* Generate a name with less than 10 characters */

        int nameLength = (new Random() ).nextInt(10);
        StringBuilder celebrityName = new StringBuilder();

        for (int letterCount = 0 ; letterCount < nameLength ; letterCount++){

            // Choose a random letter from alphabet by choosing a random position on alphabet
            int alphabetPositionOfLetter = (new Random()).nextInt( alphabet.length() );

            celebrityName.append(  alphabet.charAt( alphabetPositionOfLetter )  );

        }

        return celebrityName.toString();

    }




    void generateInputsFor_StabilityTest(){

        // n = 8 inputs
        celebrities.add( new Celebrity("A", 21));
        celebrities.add( new Celebrity("B", 40));
        celebrities.add( new Celebrity("C", 3));
        celebrities.add( new Celebrity("D", 40));
        celebrities.add( new Celebrity("E", 21));
        celebrities.add( new Celebrity("F", 5));
        celebrities.add( new Celebrity("G", 564));
        celebrities.add( new Celebrity("H", 40));

    }


    void testStability (){

        generateInputsFor_StabilityTest();
        runSortingAlgorithms();
    }



    void testAlgorithms_AverageCase (int inputSize){

        randomCelebrityGenerator(inputSize);

        runSortingAlgorithms();

    }


    void testAlgorithms_DescendingOrderWorstCase (){

        celebrities.sort(Collections.reverseOrder());

        runSortingAlgorithms();

    }


    /***
     *  Runs all sorting algorithms once on copies of same input.
     */
    void runSortingAlgorithms (){


        CombSort<Celebrity> combSort = new CombSort<>(copyCelebrityList());

        GnomeSort<Celebrity> gnomeSort = new GnomeSort<>(copyCelebrityList());

        ShakerSort<Celebrity> shakerSort = new ShakerSort<>(copyCelebrityList());

        StoogeSort<Celebrity> stoogeSort = new StoogeSort<>(copyCelebrityList());

        BitonicSort<Celebrity> bitonicSort = new BitonicSort<>(copyCelebrityList());


    }


}
