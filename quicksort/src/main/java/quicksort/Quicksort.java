/**
 * This example formalizes and verifies the wellknown quicksort
 * algorithm for int-arrays algorithm.  It shows that the array
 * is sorted in  the end and that it contains  a permutation of
 * the original input.
 *
 * The   proofs   for   the  main   method   sort(int[])   runs
 * automatically   while   the   other  two   methods   require
 * interaction.  You   can  load   the  files   "sort.key"  and
 * "split.key"  from the  example's  directory  to execute  the
 * according proof scripts.
 *
 * The permutation property requires some interaction: The idea
 * is that the only actual modification on the array are swaps
 * within the "split" method. The sort method body contains
 * three method invocations which each maintain the permutation
 * property. By a repeated appeal to the transitivity of the
 * permutation property, the entire algorithm can be proved to
 * only permute the array.
 *
 * To establish  monotonicity, the key  is to specify  that the
 * currently  handled block  contains  only  numbers which  are
 * between   the    two   pivot   values    array[from-1]   and
 * array[to]. The first  and last block are exempt  from one of
 * these  conditions  since  they have  only  one  neighbouring
 * block.
 *
 * The  example has  been  added  to show  the  power of  proof
 * scripts.
 *
 * @author Mattias Ulbrich, 2015
 *
 * Modified  to run fully automatically for  Retrospecive AC by
 * Lukas Gra"tz, 2023
 */

package quicksort;

import tracer.Trace;

class Quicksort {

    /*@ public normal_behaviour
      @  //-- the following ensures with seqPerm cannot be shown in auto mode with default options:
      @  // ensures \dl_seqPerm(\dl_array2seq(array), \old(\dl_array2seq(array)));
      @  //-- equivalent ensures with \num_of:
      @  ensures (\forall int j; 0<=j && j < array.length;
      @               (\num_of int i; 0<=i && i < array.length; \old(array[i]) == array[j])
      @            == (\num_of int i; 0<=i && i < array.length;      array[i]  == array[j])
      @          );
      @  ensures (\forall int i; 0<=i && i<array.length-1; array[i] <= array[i+1]);
      @  assignable array[*];
      @*/
    public static void sort(int[] array) {
        Trace.trace_start();
        try {
            Trace.trace_next(1);
            if(array.length > 0) {
                Trace.trace_next(2);
                sort(array, 0, array.length-1);
            }
        } finally {
            Trace.trace_end();
        }
    }

    private static void sort(int[] array, int from, int to) {
        Trace.trace_next(3);
        if(from < to) {
            Trace.trace_next(4);
            int splitPoint = split(array, from, to);
            sort(array, from, splitPoint-1);
            sort(array, splitPoint+1, to);
        }
    }

    private static int split(int[] array, int from, int to) {
        Trace.trace_next(5);

        int i = from;
        int pivot = array[to];

        for(int j = from; j < to; j++) {
            Trace.trace_next(6);
            if(array[j] <= pivot) {
                Trace.trace_next(7);
                int t = array[i];
                array[i] = array[j];
                array[j] = t;
                i++;
            }
        }

        array[to] = array[i];
        array[i] = pivot;

        return i;

    }
}
