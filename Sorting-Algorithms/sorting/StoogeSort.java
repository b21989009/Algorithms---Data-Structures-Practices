import java.util.*;
import java.lang.*;

public class StoogeSort <T extends Comparable<T>> extends Sort<T> {


    public StoogeSort (ArrayList<T> listToSort) {
        super(listToSort);
        algorithmTitle = "Stooge";

        stoogeSort(listToSort, 0 , listSize - 1);

        stopTheTime();
    }


    private void stoogeSort(ArrayList<T> listToSort, int leftIndex , int rightIndex){

        // If the leftmost element is larger than the rightmost element, swap them.
        if (comparison(listToSort, leftIndex , rightIndex ) > 0)
            Collections.swap(listToSort, leftIndex, rightIndex);


        // If there are at least 3 elements in listToSort:
        if (rightIndex - leftIndex + 1 > 2){

            int offset = (rightIndex - leftIndex + 1) / 3;

            stoogeSort(listToSort, leftIndex , rightIndex - offset);  // Sort the first 2/3 of listToSort
            stoogeSort(listToSort, leftIndex + offset, rightIndex);   // Sort the last 2/3 of listToSort
            stoogeSort(listToSort, leftIndex , rightIndex - offset);  // Sort the first 2/3 of listToSort again

        }

    }


}

