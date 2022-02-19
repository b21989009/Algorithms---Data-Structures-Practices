import java.util.*;
import java.lang.*;

public class BitonicSort <T extends Comparable<T>> extends Sort<T> {

    // direction parameters
    private final int ascending = 1;
    private final int descending = 0;


    public BitonicSort (ArrayList<T> listToSort) {
        super(listToSort);
        algorithmTitle = "Bitonic";

        bitonicSort(listToSort,0, listSize, ascending);

        stopTheTime();
    }



    // Only works when the input size is a power of two. n = 2^c
    private void bitonicSort(ArrayList<T> listToSort, int indexToStartSorting, int elementsToBeSorted, int direction){

        if (elementsToBeSorted > 1){

            int offset = elementsToBeSorted / 2;

            // Left half
            bitonicSort(listToSort, indexToStartSorting, offset, ascending);
            // Right half
            bitonicSort(listToSort, indexToStartSorting + offset, offset, descending);

            // A bitonic sequence with an ascending half and a descending half is produced above, now it is time to merge them in one direction.
            bitonicMerge(listToSort, indexToStartSorting, elementsToBeSorted, direction);
        }

    }



    private void bitonicMerge(ArrayList<T> listToSort, int indexToStartSorting, int elementsToBeSorted, int direction){

        if (elementsToBeSorted > 1){

            int offset = elementsToBeSorted / 2;

            for (int i = indexToStartSorting ; i < indexToStartSorting + offset ; i++)
                compareAndSwap(listToSort, i, i + offset, direction);


            // Left half
            bitonicMerge(listToSort, indexToStartSorting, offset, direction);
            // Right half
            bitonicMerge(listToSort, indexToStartSorting + offset, offset, direction);

        }
    }



    private void compareAndSwap(ArrayList<T> listToSort, int i, int j, int direction){

        int i_j_Comparison = comparison(listToSort, i , j);

        if ((direction == ascending && i_j_Comparison > 0 ) || (direction == descending && i_j_Comparison < 0))
            Collections.swap(listToSort, i, j);

    }


}

