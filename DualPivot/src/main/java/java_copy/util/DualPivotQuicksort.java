/*
 * Copyright (c) 2009, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java_copy.util;

import prorunvis.Trace;

/**
 * This class implements the Dual-Pivot Quicksort algorithm by
 * Vladimir Yaroslavskiy, Jon Bentley, and Josh Bloch. The algorithm
 * offers O(n log(n)) performance on many data sets that cause other
 * quicksorts to degrade to quadratic performance, and is typically
 * faster than traditional (one-pivot) Quicksort implementations.
 *
 * All exposed methods are package-private, designed to be invoked
 * from public methods (in class Arrays) after performing any
 * necessary array bounds checks and expanding parameters into the
 * required forms.
 *
 * @author Vladimir Yaroslavskiy
 * @author Jon Bentley
 * @author Josh Bloch
 *
 * @version 2011.02.11 m765.827.12i:5\7pm
 * @since 1.7
 */
final class DualPivotQuicksort {

    /**
     * Prevents instantiation.
     */
    private DualPivotQuicksort() {
    }

    /*
     * Tuning parameters.
     */
    /**
     * The maximum number of runs in merge sort.
     */
    private static final int MAX_RUN_COUNT = 67;

    /**
     * If the length of an array to be sorted is less than this
     * constant, Quicksort is used in preference to merge sort.
     */
    private static final int QUICKSORT_THRESHOLD = 286;

    /**
     * If the length of an array to be sorted is less than this
     * constant, insertion sort is used in preference to Quicksort.
     */
    private static final int INSERTION_SORT_THRESHOLD = 47;

    /**
     * If the length of a byte array to be sorted is greater than this
     * constant, counting sort is used in preference to insertion sort.
     */
    private static final int COUNTING_SORT_THRESHOLD_FOR_BYTE = 29;

    /**
     * If the length of a short or char array to be sorted is greater
     * than this constant, counting sort is used in preference to Quicksort.
     */
    private static final int COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR = 3200;

    /*
     * Sorting methods for seven primitive types.
     */
    /**
     * Sorts the specified range of the array using the given
     * workspace array slice if possible for merging
     *
     * @param a the array to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param work a workspace array (slice)
     * @param workBase origin of usable space in work array
     * @param workLen usable size of work array
     */
    /*@ public normal_behaviour
      @  requires Trace.r != a && work == null;
      @  requires 0 <= left && left <= right && right < a.length;
      @  // ensures (\forall int j; 0<=j && j < a.length;
      @  //             (\num_of int i; 0<=i && i < a.length; \old(a[i]) == a[j])
      @  //          == (\num_of int i; 0<=i && i < a.length;      a[i]  == a[j])
      @  //        );
      @  ensures (\forall int i; left <= i && i < right; a[i] <= a[i+1]);
      @  // assignable a[*];
      @*/
    static void sort(int[] a, int left, int right, /*@ nullable */ int[] work, int workBase, int workLen) {
        Trace.trace_start();
        Trace.next_elem(0);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            Trace.next_elem(1);
            sort(a, left, right, true);
            return;
        }
        /*
         * Index run[i] is the start of i-th run
         * (ascending or descending sequence).
         */
        int[] run = new int[MAX_RUN_COUNT + 1];
        int count = 0;
        run[0] = left;
        // Check if the array is nearly sorted
        for (int k = left; k < right; run[count] = k) {
            Trace.next_elem(2);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                Trace.next_elem(3);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                Trace.next_elem(4);
                break;
            }
            if (a[k] < a[k + 1]) {
                Trace.next_elem(5);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    Trace.next_elem(9);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                Trace.next_elem(6);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    Trace.next_elem(7);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    Trace.next_elem(8);
                    int t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                Trace.next_elem(10);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                Trace.next_elem(11);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            Trace.next_elem(12);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            Trace.next_elem(13);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            Trace.next_elem(14);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            Trace.next_elem(15);
            ;
        }
        // Use or create temporary array b for merging
        // temp array; alternates with a
        int[] b;
        // array offsets from 'left'
        int ao, bo;
        // space needed for b
        int blen = right - left;
        if (work == null || workLen < blen || workBase + blen > work.length) {
            Trace.next_elem(16);
            work = new int[blen];
            workBase = 0;
        }
        if (odd == 0) {
            Trace.next_elem(17);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            Trace.next_elem(18);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            Trace.next_elem(19);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                Trace.next_elem(20);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    Trace.next_elem(21);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        Trace.next_elem(22);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        Trace.next_elem(23);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                Trace.next_elem(24);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    Trace.next_elem(25);
                    ;
                }
                run[++last] = right;
            }
            int[] t = a;
            a = b;
            b = t;
            int o = ao;
            ao = bo;
            bo = o;
        }
    }

    /**
     * Sorts the specified range of the array by Dual-Pivot Quicksort.
     *
     * @param a the array to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param leftmost indicates if this part is the leftmost in the range
     */
    private static void sort(int[] a, int left, int right, boolean leftmost) {
        Trace.next_elem(26);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            Trace.next_elem(27);
            if (leftmost) {
                Trace.next_elem(28);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    Trace.next_elem(37);
                    int ai = a[i + 1];
                    while (ai < a[j]) {
                        Trace.next_elem(38);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            Trace.next_elem(39);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                Trace.next_elem(29);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    Trace.next_elem(30);
                    if (left >= right) {
                        Trace.next_elem(31);
                        return;
                    }
                } while (a[++left] >= a[left - 1]);
                /*
                 * Every element from adjoining part plays the role
                 * of sentinel, therefore this allows us to avoid the
                 * left range check on each iteration. Moreover, we use
                 * the more optimized algorithm, so called pair insertion
                 * sort, which is faster (in the context of Quicksort)
                 * than traditional implementation of insertion sort.
                 */
                for (int k = left; ++left <= right; k = ++left) {
                    Trace.next_elem(32);
                    int a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        Trace.next_elem(33);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        Trace.next_elem(34);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        Trace.next_elem(35);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                int last = a[right];
                while (last < a[--right]) {
                    Trace.next_elem(36);
                    a[right + 1] = a[right];
                }
                a[right + 1] = last;
            }
            return;
        }
        // Inexpensive approximation of length / 7
        int seventh = (length >> 3) + (length >> 6) + 1;
        /*
         * Sort five evenly spaced elements around (and including) the
         * center element in the range. These elements will be used for
         * pivot selection as described below. The choice for spacing
         * these elements was empirically determined to work well on
         * a wide variety of inputs.
         */
        // The midpoint
        int e3 = (left + right) >> 1;
        int e2 = e3 - seventh;
        int e1 = e2 - seventh;
        int e4 = e3 + seventh;
        int e5 = e4 + seventh;
        // Sort these elements using insertion sort
        if (a[e2] < a[e1]) {
            Trace.next_elem(40);
            int t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            Trace.next_elem(41);
            int t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                Trace.next_elem(42);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            Trace.next_elem(43);
            int t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                Trace.next_elem(44);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    Trace.next_elem(45);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            Trace.next_elem(46);
            int t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                Trace.next_elem(47);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    Trace.next_elem(48);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        Trace.next_elem(49);
                        a[e2] = a[e1];
                        a[e1] = t;
                    }
                }
            }
        }
        // Pointers
        // The index of the first element of center part
        int less = left;
        // The index before the first element of right part
        int great = right;
        if (a[e1] != a[e2] && a[e2] != a[e3] && a[e3] != a[e4] && a[e4] != a[e5]) {
            Trace.next_elem(50);
            /*
             * Use the second and fourth of the five sorted elements as pivots.
             * These values are inexpensive approximations of the first and
             * second terciles of the array. Note that pivot1 <= pivot2.
             */
            int pivot1 = a[e2];
            int pivot2 = a[e4];
            /*
             * The first and the last elements to be sorted are moved to the
             * locations formerly occupied by the pivots. When partitioning
             * is complete, the pivots are swapped back into their final
             * positions, and excluded from subsequent sorting.
             */
            a[e2] = a[left];
            a[e4] = a[right];
            /*
             * Skip elements, which are less or greater than pivot values.
             */
            while (a[++less] < pivot1) {
                Trace.next_elem(59);
                ;
            }
            while (a[--great] > pivot2) {
                Trace.next_elem(60);
                ;
            }
            /*
             * Partitioning:
             *
             *   left part           center part                   right part
             * +--------------------------------------------------------------+
             * |  < pivot1  |  pivot1 <= && <= pivot2  |    ?    |  > pivot2  |
             * +--------------------------------------------------------------+
             *               ^                          ^       ^
             *               |                          |       |
             *              less                        k     great
             *
             * Invariants:
             *
             *              all in (left, less)   < pivot1
             *    pivot1 <= all in [less, k)     <= pivot2
             *              all in (great, right) > pivot2
             *
             * Pointer k is the first index of ?-part.
             */
            outer: for (int k = less - 1; ++k <= great; ) {
                Trace.next_elem(61);
                int ak = a[k];
                if (ak < pivot1) {
                    Trace.next_elem(62);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    Trace.next_elem(63);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        Trace.next_elem(64);
                        if (great-- == k) {
                            Trace.next_elem(65);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        Trace.next_elem(66);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(67);
                        // pivot1 <= a[great] <= pivot2
                        a[k] = a[great];
                    }
                    /*
                     * Here and below we use "a[i] = b; i--;" instead
                     * of "a[i--] = b;" due to performance issue.
                     */
                    a[great] = ak;
                    --great;
                }
            }
            // Swap pivots into their final positions
            a[left] = a[less - 1];
            a[less - 1] = pivot1;
            a[right] = a[great + 1];
            a[great + 1] = pivot2;
            // Sort left and right parts recursively, excluding known pivots
            sort(a, left, less - 2, leftmost);
            sort(a, great + 2, right, false);
            /*
             * If center part is too large (comprises > 4/7 of the array),
             * swap internal pivot values to ends.
             */
            if (less < e1 && e5 < great) {
                Trace.next_elem(68);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    Trace.next_elem(69);
                    ++less;
                }
                while (a[great] == pivot2) {
                    Trace.next_elem(70);
                    --great;
                }
                /*
                 * Partitioning:
                 *
                 *   left part         center part                  right part
                 * +----------------------------------------------------------+
                 * | == pivot1 |  pivot1 < && < pivot2  |    ?    | == pivot2 |
                 * +----------------------------------------------------------+
                 *              ^                        ^       ^
                 *              |                        |       |
                 *             less                      k     great
                 *
                 * Invariants:
                 *
                 *              all in (*,  less) == pivot1
                 *     pivot1 < all in [less,  k)  < pivot2
                 *              all in (great, *) == pivot2
                 *
                 * Pointer k is the first index of ?-part.
                 */
                outer: for (int k = less - 1; ++k <= great; ) {
                    Trace.next_elem(71);
                    int ak = a[k];
                    if (ak == pivot1) {
                        Trace.next_elem(72);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        Trace.next_elem(73);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            Trace.next_elem(74);
                            if (great-- == k) {
                                Trace.next_elem(75);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            Trace.next_elem(76);
                            // a[great] < pivot2
                            a[k] = a[less];
                            /*
                             * Even though a[great] equals to pivot1, the
                             * assignment a[less] = pivot1 may be incorrect,
                             * if a[great] and pivot1 are floating-point zeros
                             * of different signs. Therefore in float and
                             * double sorting methods we have to use more
                             * accurate assignment a[less] = a[great].
                             */
                            a[less] = pivot1;
                            ++less;
                        } else {
                            Trace.next_elem(77);
                            // pivot1 < a[great] < pivot2
                            a[k] = a[great];
                        }
                        a[great] = ak;
                        --great;
                    }
                }
            }
            // Sort center part recursively
            sort(a, less, great, false);
        } else {
            Trace.next_elem(51);
            // Partitioning with one pivot
            /*
             * Use the third of the five sorted elements as pivot.
             * This value is inexpensive approximation of the median.
             */
            int pivot = a[e3];
            /*
             * Partitioning degenerates to the traditional 3-way
             * (or "Dutch National Flag") schema:
             *
             *   left part    center part              right part
             * +-------------------------------------------------+
             * |  < pivot  |   == pivot   |     ?    |  > pivot  |
             * +-------------------------------------------------+
             *              ^              ^        ^
             *              |              |        |
             *             less            k      great
             *
             * Invariants:
             *
             *   all in (left, less)   < pivot
             *   all in [less, k)     == pivot
             *   all in (great, right) > pivot
             *
             * Pointer k is the first index of ?-part.
             */
            for (int k = less; k <= great; ++k) {
                Trace.next_elem(52);
                if (a[k] == pivot) {
                    Trace.next_elem(53);
                    continue;
                }
                int ak = a[k];
                if (ak < pivot) {
                    Trace.next_elem(54);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    Trace.next_elem(55);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        Trace.next_elem(56);
                        --great;
                    }
                    if (a[great] < pivot) {
                        Trace.next_elem(57);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(58);
                        // a[great] == pivot
                        /*
                         * Even though a[great] equals to pivot, the
                         * assignment a[k] = pivot may be incorrect,
                         * if a[great] and pivot are floating-point
                         * zeros of different signs. Therefore in float
                         * and double sorting methods we have to use
                         * more accurate assignment a[k] = a[great].
                         */
                        a[k] = pivot;
                    }
                    a[great] = ak;
                    --great;
                }
            }
            /*
             * Sort left and right parts recursively.
             * All elements from center part are equal
             * and, therefore, already sorted.
             */
            sort(a, left, less - 1, leftmost);
            sort(a, great + 1, right, false);
        }
    }

    // we are not interested in the other functions for long[] a, float[] a, ... so removed them!
}
