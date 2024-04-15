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
    static void sort(int[] a, int left, int right, int[] work, int workBase, int workLen) {
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
        int e3 = (left + right) >>> 1;
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
    static void sort(long[] a, int left, int right, long[] work, int workBase, int workLen) {
        Trace.next_elem(78);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            Trace.next_elem(79);
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
            Trace.next_elem(80);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                Trace.next_elem(81);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                Trace.next_elem(82);
                break;
            }
            if (a[k] < a[k + 1]) {
                Trace.next_elem(83);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    Trace.next_elem(87);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                Trace.next_elem(84);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    Trace.next_elem(85);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    Trace.next_elem(86);
                    long t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                Trace.next_elem(88);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                Trace.next_elem(89);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            Trace.next_elem(90);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            Trace.next_elem(91);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            Trace.next_elem(92);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            Trace.next_elem(93);
            ;
        }
        // Use or create temporary array b for merging
        // temp array; alternates with a
        long[] b;
        // array offsets from 'left'
        int ao, bo;
        // space needed for b
        int blen = right - left;
        if (work == null || workLen < blen || workBase + blen > work.length) {
            Trace.next_elem(94);
            work = new long[blen];
            workBase = 0;
        }
        if (odd == 0) {
            Trace.next_elem(95);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            Trace.next_elem(96);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            Trace.next_elem(97);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                Trace.next_elem(98);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    Trace.next_elem(99);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        Trace.next_elem(100);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        Trace.next_elem(101);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                Trace.next_elem(102);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    Trace.next_elem(103);
                    ;
                }
                run[++last] = right;
            }
            long[] t = a;
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
    private static void sort(long[] a, int left, int right, boolean leftmost) {
        Trace.next_elem(104);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            Trace.next_elem(105);
            if (leftmost) {
                Trace.next_elem(106);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    Trace.next_elem(115);
                    long ai = a[i + 1];
                    while (ai < a[j]) {
                        Trace.next_elem(116);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            Trace.next_elem(117);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                Trace.next_elem(107);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    Trace.next_elem(108);
                    if (left >= right) {
                        Trace.next_elem(109);
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
                    Trace.next_elem(110);
                    long a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        Trace.next_elem(111);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        Trace.next_elem(112);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        Trace.next_elem(113);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                long last = a[right];
                while (last < a[--right]) {
                    Trace.next_elem(114);
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
        int e3 = (left + right) >>> 1;
        int e2 = e3 - seventh;
        int e1 = e2 - seventh;
        int e4 = e3 + seventh;
        int e5 = e4 + seventh;
        // Sort these elements using insertion sort
        if (a[e2] < a[e1]) {
            Trace.next_elem(118);
            long t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            Trace.next_elem(119);
            long t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                Trace.next_elem(120);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            Trace.next_elem(121);
            long t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                Trace.next_elem(122);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    Trace.next_elem(123);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            Trace.next_elem(124);
            long t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                Trace.next_elem(125);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    Trace.next_elem(126);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        Trace.next_elem(127);
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
            Trace.next_elem(128);
            /*
             * Use the second and fourth of the five sorted elements as pivots.
             * These values are inexpensive approximations of the first and
             * second terciles of the array. Note that pivot1 <= pivot2.
             */
            long pivot1 = a[e2];
            long pivot2 = a[e4];
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
                Trace.next_elem(137);
                ;
            }
            while (a[--great] > pivot2) {
                Trace.next_elem(138);
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
                Trace.next_elem(139);
                long ak = a[k];
                if (ak < pivot1) {
                    Trace.next_elem(140);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    Trace.next_elem(141);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        Trace.next_elem(142);
                        if (great-- == k) {
                            Trace.next_elem(143);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        Trace.next_elem(144);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(145);
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
                Trace.next_elem(146);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    Trace.next_elem(147);
                    ++less;
                }
                while (a[great] == pivot2) {
                    Trace.next_elem(148);
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
                    Trace.next_elem(149);
                    long ak = a[k];
                    if (ak == pivot1) {
                        Trace.next_elem(150);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        Trace.next_elem(151);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            Trace.next_elem(152);
                            if (great-- == k) {
                                Trace.next_elem(153);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            Trace.next_elem(154);
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
                            Trace.next_elem(155);
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
            Trace.next_elem(129);
            // Partitioning with one pivot
            /*
             * Use the third of the five sorted elements as pivot.
             * This value is inexpensive approximation of the median.
             */
            long pivot = a[e3];
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
                Trace.next_elem(130);
                if (a[k] == pivot) {
                    Trace.next_elem(131);
                    continue;
                }
                long ak = a[k];
                if (ak < pivot) {
                    Trace.next_elem(132);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    Trace.next_elem(133);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        Trace.next_elem(134);
                        --great;
                    }
                    if (a[great] < pivot) {
                        Trace.next_elem(135);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(136);
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
    static void sort(short[] a, int left, int right, short[] work, int workBase, int workLen) {
        Trace.next_elem(156);
        // Use counting sort on large arrays
        if (right - left > COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR) {
            Trace.next_elem(157);
            int[] count = new int[NUM_SHORT_VALUES];
            for (int i = left - 1; ++i <= right; count[a[i] - Short.MIN_VALUE]++) {
                Trace.next_elem(159);
                ;
            }
            for (int i = NUM_SHORT_VALUES, k = right + 1; k > left; ) {
                Trace.next_elem(160);
                while (count[--i] == 0) {
                    Trace.next_elem(161);
                    ;
                }
                short value = (short) (i + Short.MIN_VALUE);
                int s = count[i];
                do {
                    Trace.next_elem(162);
                    a[--k] = value;
                } while (--s > 0);
            }
        } else {
            Trace.next_elem(158);
            // Use Dual-Pivot Quicksort on small arrays
            doSort(a, left, right, work, workBase, workLen);
        }
    }

    /**
     * The number of distinct short values.
     */
    private static final int NUM_SHORT_VALUES = 1 << 16;

    /**
     * Sorts the specified range of the array.
     *
     * @param a the array to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param work a workspace array (slice)
     * @param workBase origin of usable space in work array
     * @param workLen usable size of work array
     */
    private static void doSort(short[] a, int left, int right, short[] work, int workBase, int workLen) {
        Trace.next_elem(163);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            Trace.next_elem(164);
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
            Trace.next_elem(165);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                Trace.next_elem(166);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                Trace.next_elem(167);
                break;
            }
            if (a[k] < a[k + 1]) {
                Trace.next_elem(168);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    Trace.next_elem(172);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                Trace.next_elem(169);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    Trace.next_elem(170);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    Trace.next_elem(171);
                    short t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                Trace.next_elem(173);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                Trace.next_elem(174);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            Trace.next_elem(175);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            Trace.next_elem(176);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            Trace.next_elem(177);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            Trace.next_elem(178);
            ;
        }
        // Use or create temporary array b for merging
        // temp array; alternates with a
        short[] b;
        // array offsets from 'left'
        int ao, bo;
        // space needed for b
        int blen = right - left;
        if (work == null || workLen < blen || workBase + blen > work.length) {
            Trace.next_elem(179);
            work = new short[blen];
            workBase = 0;
        }
        if (odd == 0) {
            Trace.next_elem(180);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            Trace.next_elem(181);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            Trace.next_elem(182);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                Trace.next_elem(183);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    Trace.next_elem(184);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        Trace.next_elem(185);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        Trace.next_elem(186);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                Trace.next_elem(187);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    Trace.next_elem(188);
                    ;
                }
                run[++last] = right;
            }
            short[] t = a;
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
    private static void sort(short[] a, int left, int right, boolean leftmost) {
        Trace.next_elem(189);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            Trace.next_elem(190);
            if (leftmost) {
                Trace.next_elem(191);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    Trace.next_elem(200);
                    short ai = a[i + 1];
                    while (ai < a[j]) {
                        Trace.next_elem(201);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            Trace.next_elem(202);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                Trace.next_elem(192);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    Trace.next_elem(193);
                    if (left >= right) {
                        Trace.next_elem(194);
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
                    Trace.next_elem(195);
                    short a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        Trace.next_elem(196);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        Trace.next_elem(197);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        Trace.next_elem(198);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                short last = a[right];
                while (last < a[--right]) {
                    Trace.next_elem(199);
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
        int e3 = (left + right) >>> 1;
        int e2 = e3 - seventh;
        int e1 = e2 - seventh;
        int e4 = e3 + seventh;
        int e5 = e4 + seventh;
        // Sort these elements using insertion sort
        if (a[e2] < a[e1]) {
            Trace.next_elem(203);
            short t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            Trace.next_elem(204);
            short t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                Trace.next_elem(205);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            Trace.next_elem(206);
            short t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                Trace.next_elem(207);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    Trace.next_elem(208);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            Trace.next_elem(209);
            short t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                Trace.next_elem(210);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    Trace.next_elem(211);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        Trace.next_elem(212);
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
            Trace.next_elem(213);
            /*
             * Use the second and fourth of the five sorted elements as pivots.
             * These values are inexpensive approximations of the first and
             * second terciles of the array. Note that pivot1 <= pivot2.
             */
            short pivot1 = a[e2];
            short pivot2 = a[e4];
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
                Trace.next_elem(222);
                ;
            }
            while (a[--great] > pivot2) {
                Trace.next_elem(223);
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
                Trace.next_elem(224);
                short ak = a[k];
                if (ak < pivot1) {
                    Trace.next_elem(225);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    Trace.next_elem(226);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        Trace.next_elem(227);
                        if (great-- == k) {
                            Trace.next_elem(228);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        Trace.next_elem(229);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(230);
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
                Trace.next_elem(231);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    Trace.next_elem(232);
                    ++less;
                }
                while (a[great] == pivot2) {
                    Trace.next_elem(233);
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
                    Trace.next_elem(234);
                    short ak = a[k];
                    if (ak == pivot1) {
                        Trace.next_elem(235);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        Trace.next_elem(236);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            Trace.next_elem(237);
                            if (great-- == k) {
                                Trace.next_elem(238);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            Trace.next_elem(239);
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
                            Trace.next_elem(240);
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
            Trace.next_elem(214);
            // Partitioning with one pivot
            /*
             * Use the third of the five sorted elements as pivot.
             * This value is inexpensive approximation of the median.
             */
            short pivot = a[e3];
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
                Trace.next_elem(215);
                if (a[k] == pivot) {
                    Trace.next_elem(216);
                    continue;
                }
                short ak = a[k];
                if (ak < pivot) {
                    Trace.next_elem(217);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    Trace.next_elem(218);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        Trace.next_elem(219);
                        --great;
                    }
                    if (a[great] < pivot) {
                        Trace.next_elem(220);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(221);
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
    static void sort(char[] a, int left, int right, char[] work, int workBase, int workLen) {
        Trace.next_elem(241);
        // Use counting sort on large arrays
        if (right - left > COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR) {
            Trace.next_elem(242);
            int[] count = new int[NUM_CHAR_VALUES];
            for (int i = left - 1; ++i <= right; count[a[i]]++) {
                Trace.next_elem(244);
                ;
            }
            for (int i = NUM_CHAR_VALUES, k = right + 1; k > left; ) {
                Trace.next_elem(245);
                while (count[--i] == 0) {
                    Trace.next_elem(246);
                    ;
                }
                char value = (char) i;
                int s = count[i];
                do {
                    Trace.next_elem(247);
                    a[--k] = value;
                } while (--s > 0);
            }
        } else {
            Trace.next_elem(243);
            // Use Dual-Pivot Quicksort on small arrays
            doSort(a, left, right, work, workBase, workLen);
        }
    }

    /**
     * The number of distinct char values.
     */
    private static final int NUM_CHAR_VALUES = 1 << 16;

    /**
     * Sorts the specified range of the array.
     *
     * @param a the array to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param work a workspace array (slice)
     * @param workBase origin of usable space in work array
     * @param workLen usable size of work array
     */
    private static void doSort(char[] a, int left, int right, char[] work, int workBase, int workLen) {
        Trace.next_elem(248);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            Trace.next_elem(249);
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
            Trace.next_elem(250);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                Trace.next_elem(251);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                Trace.next_elem(252);
                break;
            }
            if (a[k] < a[k + 1]) {
                Trace.next_elem(253);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    Trace.next_elem(257);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                Trace.next_elem(254);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    Trace.next_elem(255);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    Trace.next_elem(256);
                    char t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                Trace.next_elem(258);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                Trace.next_elem(259);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            Trace.next_elem(260);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            Trace.next_elem(261);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            Trace.next_elem(262);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            Trace.next_elem(263);
            ;
        }
        // Use or create temporary array b for merging
        // temp array; alternates with a
        char[] b;
        // array offsets from 'left'
        int ao, bo;
        // space needed for b
        int blen = right - left;
        if (work == null || workLen < blen || workBase + blen > work.length) {
            Trace.next_elem(264);
            work = new char[blen];
            workBase = 0;
        }
        if (odd == 0) {
            Trace.next_elem(265);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            Trace.next_elem(266);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            Trace.next_elem(267);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                Trace.next_elem(268);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    Trace.next_elem(269);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        Trace.next_elem(270);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        Trace.next_elem(271);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                Trace.next_elem(272);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    Trace.next_elem(273);
                    ;
                }
                run[++last] = right;
            }
            char[] t = a;
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
    private static void sort(char[] a, int left, int right, boolean leftmost) {
        Trace.next_elem(274);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            Trace.next_elem(275);
            if (leftmost) {
                Trace.next_elem(276);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    Trace.next_elem(285);
                    char ai = a[i + 1];
                    while (ai < a[j]) {
                        Trace.next_elem(286);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            Trace.next_elem(287);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                Trace.next_elem(277);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    Trace.next_elem(278);
                    if (left >= right) {
                        Trace.next_elem(279);
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
                    Trace.next_elem(280);
                    char a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        Trace.next_elem(281);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        Trace.next_elem(282);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        Trace.next_elem(283);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                char last = a[right];
                while (last < a[--right]) {
                    Trace.next_elem(284);
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
        int e3 = (left + right) >>> 1;
        int e2 = e3 - seventh;
        int e1 = e2 - seventh;
        int e4 = e3 + seventh;
        int e5 = e4 + seventh;
        // Sort these elements using insertion sort
        if (a[e2] < a[e1]) {
            Trace.next_elem(288);
            char t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            Trace.next_elem(289);
            char t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                Trace.next_elem(290);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            Trace.next_elem(291);
            char t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                Trace.next_elem(292);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    Trace.next_elem(293);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            Trace.next_elem(294);
            char t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                Trace.next_elem(295);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    Trace.next_elem(296);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        Trace.next_elem(297);
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
            Trace.next_elem(298);
            /*
             * Use the second and fourth of the five sorted elements as pivots.
             * These values are inexpensive approximations of the first and
             * second terciles of the array. Note that pivot1 <= pivot2.
             */
            char pivot1 = a[e2];
            char pivot2 = a[e4];
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
                Trace.next_elem(307);
                ;
            }
            while (a[--great] > pivot2) {
                Trace.next_elem(308);
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
                Trace.next_elem(309);
                char ak = a[k];
                if (ak < pivot1) {
                    Trace.next_elem(310);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    Trace.next_elem(311);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        Trace.next_elem(312);
                        if (great-- == k) {
                            Trace.next_elem(313);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        Trace.next_elem(314);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(315);
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
                Trace.next_elem(316);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    Trace.next_elem(317);
                    ++less;
                }
                while (a[great] == pivot2) {
                    Trace.next_elem(318);
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
                    Trace.next_elem(319);
                    char ak = a[k];
                    if (ak == pivot1) {
                        Trace.next_elem(320);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        Trace.next_elem(321);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            Trace.next_elem(322);
                            if (great-- == k) {
                                Trace.next_elem(323);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            Trace.next_elem(324);
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
                            Trace.next_elem(325);
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
            Trace.next_elem(299);
            // Partitioning with one pivot
            /*
             * Use the third of the five sorted elements as pivot.
             * This value is inexpensive approximation of the median.
             */
            char pivot = a[e3];
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
                Trace.next_elem(300);
                if (a[k] == pivot) {
                    Trace.next_elem(301);
                    continue;
                }
                char ak = a[k];
                if (ak < pivot) {
                    Trace.next_elem(302);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    Trace.next_elem(303);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        Trace.next_elem(304);
                        --great;
                    }
                    if (a[great] < pivot) {
                        Trace.next_elem(305);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(306);
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

    /**
     * The number of distinct byte values.
     */
    private static final int NUM_BYTE_VALUES = 1 << 8;

    /**
     * Sorts the specified range of the array.
     *
     * @param a the array to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     */
    static void sort(byte[] a, int left, int right) {
        Trace.next_elem(326);
        // Use counting sort on large arrays
        if (right - left > COUNTING_SORT_THRESHOLD_FOR_BYTE) {
            Trace.next_elem(327);
            int[] count = new int[NUM_BYTE_VALUES];
            for (int i = left - 1; ++i <= right; count[a[i] - Byte.MIN_VALUE]++) {
                Trace.next_elem(332);
                ;
            }
            for (int i = NUM_BYTE_VALUES, k = right + 1; k > left; ) {
                Trace.next_elem(333);
                while (count[--i] == 0) {
                    Trace.next_elem(334);
                    ;
                }
                byte value = (byte) (i + Byte.MIN_VALUE);
                int s = count[i];
                do {
                    Trace.next_elem(335);
                    a[--k] = value;
                } while (--s > 0);
            }
        } else {
            Trace.next_elem(328);
            // Use insertion sort on small arrays
            for (int i = left, j = i; i < right; j = ++i) {
                Trace.next_elem(329);
                byte ai = a[i + 1];
                while (ai < a[j]) {
                    Trace.next_elem(330);
                    a[j + 1] = a[j];
                    if (j-- == left) {
                        Trace.next_elem(331);
                        break;
                    }
                }
                a[j + 1] = ai;
            }
        }
    }

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
    static void sort(float[] a, int left, int right, float[] work, int workBase, int workLen) {
        Trace.next_elem(336);
        /*
         * Phase 1: Move NaNs to the end of the array.
         */
        while (left <= right && Float.isNaN(a[right])) {
            Trace.next_elem(337);
            --right;
        }
        for (int k = right; --k >= left; ) {
            Trace.next_elem(338);
            float ak = a[k];
            if (ak != ak) {
                Trace.next_elem(339);
                // a[k] is NaN
                a[k] = a[right];
                a[right] = ak;
                --right;
            }
        }
        /*
         * Phase 2: Sort everything except NaNs (which are already in place).
         */
        doSort(a, left, right, work, workBase, workLen);
        /*
         * Phase 3: Place negative zeros before positive zeros.
         */
        int hi = right;
        /*
         * Find the first zero, or first positive, or last negative element.
         */
        while (left < hi) {
            Trace.next_elem(340);
            int middle = (left + hi) >>> 1;
            float middleValue = a[middle];
            if (middleValue < 0.0f) {
                Trace.next_elem(341);
                left = middle + 1;
            } else {
                Trace.next_elem(342);
                hi = middle;
            }
        }
        /*
         * Skip the last negative value (if any) or all leading negative zeros.
         */
        while (left <= right && Float.floatToRawIntBits(a[left]) < 0) {
            Trace.next_elem(343);
            ++left;
        }
        /*
         * Move negative zeros to the beginning of the sub-range.
         *
         * Partitioning:
         *
         * +----------------------------------------------------+
         * |   < 0.0   |   -0.0   |   0.0   |   ?  ( >= 0.0 )   |
         * +----------------------------------------------------+
         *              ^          ^         ^
         *              |          |         |
         *             left        p         k
         *
         * Invariants:
         *
         *   all in (*,  left)  <  0.0
         *   all in [left,  p) == -0.0
         *   all in [p,     k) ==  0.0
         *   all in [k, right] >=  0.0
         *
         * Pointer k is the first index of ?-part.
         */
        for (int k = left, p = left - 1; ++k <= right; ) {
            Trace.next_elem(344);
            float ak = a[k];
            if (ak != 0.0f) {
                Trace.next_elem(345);
                break;
            }
            if (Float.floatToRawIntBits(ak) < 0) {
                Trace.next_elem(346);
                // ak is -0.0f
                a[k] = 0.0f;
                a[++p] = -0.0f;
            }
        }
    }

    /**
     * Sorts the specified range of the array.
     *
     * @param a the array to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param work a workspace array (slice)
     * @param workBase origin of usable space in work array
     * @param workLen usable size of work array
     */
    private static void doSort(float[] a, int left, int right, float[] work, int workBase, int workLen) {
        Trace.next_elem(347);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            Trace.next_elem(348);
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
            Trace.next_elem(349);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                Trace.next_elem(350);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                Trace.next_elem(351);
                break;
            }
            if (a[k] < a[k + 1]) {
                Trace.next_elem(352);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    Trace.next_elem(356);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                Trace.next_elem(353);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    Trace.next_elem(354);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    Trace.next_elem(355);
                    float t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                Trace.next_elem(357);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                Trace.next_elem(358);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            Trace.next_elem(359);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            Trace.next_elem(360);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            Trace.next_elem(361);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            Trace.next_elem(362);
            ;
        }
        // Use or create temporary array b for merging
        // temp array; alternates with a
        float[] b;
        // array offsets from 'left'
        int ao, bo;
        // space needed for b
        int blen = right - left;
        if (work == null || workLen < blen || workBase + blen > work.length) {
            Trace.next_elem(363);
            work = new float[blen];
            workBase = 0;
        }
        if (odd == 0) {
            Trace.next_elem(364);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            Trace.next_elem(365);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            Trace.next_elem(366);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                Trace.next_elem(367);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    Trace.next_elem(368);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        Trace.next_elem(369);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        Trace.next_elem(370);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                Trace.next_elem(371);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    Trace.next_elem(372);
                    ;
                }
                run[++last] = right;
            }
            float[] t = a;
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
    private static void sort(float[] a, int left, int right, boolean leftmost) {
        Trace.next_elem(373);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            Trace.next_elem(374);
            if (leftmost) {
                Trace.next_elem(375);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    Trace.next_elem(384);
                    float ai = a[i + 1];
                    while (ai < a[j]) {
                        Trace.next_elem(385);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            Trace.next_elem(386);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                Trace.next_elem(376);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    Trace.next_elem(377);
                    if (left >= right) {
                        Trace.next_elem(378);
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
                    Trace.next_elem(379);
                    float a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        Trace.next_elem(380);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        Trace.next_elem(381);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        Trace.next_elem(382);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                float last = a[right];
                while (last < a[--right]) {
                    Trace.next_elem(383);
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
        int e3 = (left + right) >>> 1;
        int e2 = e3 - seventh;
        int e1 = e2 - seventh;
        int e4 = e3 + seventh;
        int e5 = e4 + seventh;
        // Sort these elements using insertion sort
        if (a[e2] < a[e1]) {
            Trace.next_elem(387);
            float t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            Trace.next_elem(388);
            float t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                Trace.next_elem(389);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            Trace.next_elem(390);
            float t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                Trace.next_elem(391);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    Trace.next_elem(392);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            Trace.next_elem(393);
            float t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                Trace.next_elem(394);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    Trace.next_elem(395);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        Trace.next_elem(396);
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
            Trace.next_elem(397);
            /*
             * Use the second and fourth of the five sorted elements as pivots.
             * These values are inexpensive approximations of the first and
             * second terciles of the array. Note that pivot1 <= pivot2.
             */
            float pivot1 = a[e2];
            float pivot2 = a[e4];
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
                Trace.next_elem(406);
                ;
            }
            while (a[--great] > pivot2) {
                Trace.next_elem(407);
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
                Trace.next_elem(408);
                float ak = a[k];
                if (ak < pivot1) {
                    Trace.next_elem(409);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    Trace.next_elem(410);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        Trace.next_elem(411);
                        if (great-- == k) {
                            Trace.next_elem(412);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        Trace.next_elem(413);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(414);
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
                Trace.next_elem(415);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    Trace.next_elem(416);
                    ++less;
                }
                while (a[great] == pivot2) {
                    Trace.next_elem(417);
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
                    Trace.next_elem(418);
                    float ak = a[k];
                    if (ak == pivot1) {
                        Trace.next_elem(419);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        Trace.next_elem(420);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            Trace.next_elem(421);
                            if (great-- == k) {
                                Trace.next_elem(422);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            Trace.next_elem(423);
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
                            a[less] = a[great];
                            ++less;
                        } else {
                            Trace.next_elem(424);
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
            Trace.next_elem(398);
            // Partitioning with one pivot
            /*
             * Use the third of the five sorted elements as pivot.
             * This value is inexpensive approximation of the median.
             */
            float pivot = a[e3];
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
                Trace.next_elem(399);
                if (a[k] == pivot) {
                    Trace.next_elem(400);
                    continue;
                }
                float ak = a[k];
                if (ak < pivot) {
                    Trace.next_elem(401);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    Trace.next_elem(402);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        Trace.next_elem(403);
                        --great;
                    }
                    if (a[great] < pivot) {
                        Trace.next_elem(404);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(405);
                        // a[great] == pivot
                        /*
                         * Even though a[great] equals to pivot, the
                         * assignment a[k] = pivot may be incorrect,
                         * if a[great] and pivot are floating-point
                         * zeros of different signs. Therefore in float
                         * and double sorting methods we have to use
                         * more accurate assignment a[k] = a[great].
                         */
                        a[k] = a[great];
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
    static void sort(double[] a, int left, int right, double[] work, int workBase, int workLen) {
        Trace.next_elem(425);
        /*
         * Phase 1: Move NaNs to the end of the array.
         */
        while (left <= right && Double.isNaN(a[right])) {
            Trace.next_elem(426);
            --right;
        }
        for (int k = right; --k >= left; ) {
            Trace.next_elem(427);
            double ak = a[k];
            if (ak != ak) {
                Trace.next_elem(428);
                // a[k] is NaN
                a[k] = a[right];
                a[right] = ak;
                --right;
            }
        }
        /*
         * Phase 2: Sort everything except NaNs (which are already in place).
         */
        doSort(a, left, right, work, workBase, workLen);
        /*
         * Phase 3: Place negative zeros before positive zeros.
         */
        int hi = right;
        /*
         * Find the first zero, or first positive, or last negative element.
         */
        while (left < hi) {
            Trace.next_elem(429);
            int middle = (left + hi) >>> 1;
            double middleValue = a[middle];
            if (middleValue < 0.0d) {
                Trace.next_elem(430);
                left = middle + 1;
            } else {
                Trace.next_elem(431);
                hi = middle;
            }
        }
        /*
         * Skip the last negative value (if any) or all leading negative zeros.
         */
        while (left <= right && Double.doubleToRawLongBits(a[left]) < 0) {
            Trace.next_elem(432);
            ++left;
        }
        /*
         * Move negative zeros to the beginning of the sub-range.
         *
         * Partitioning:
         *
         * +----------------------------------------------------+
         * |   < 0.0   |   -0.0   |   0.0   |   ?  ( >= 0.0 )   |
         * +----------------------------------------------------+
         *              ^          ^         ^
         *              |          |         |
         *             left        p         k
         *
         * Invariants:
         *
         *   all in (*,  left)  <  0.0
         *   all in [left,  p) == -0.0
         *   all in [p,     k) ==  0.0
         *   all in [k, right] >=  0.0
         *
         * Pointer k is the first index of ?-part.
         */
        for (int k = left, p = left - 1; ++k <= right; ) {
            Trace.next_elem(433);
            double ak = a[k];
            if (ak != 0.0d) {
                Trace.next_elem(434);
                break;
            }
            if (Double.doubleToRawLongBits(ak) < 0) {
                Trace.next_elem(435);
                // ak is -0.0d
                a[k] = 0.0d;
                a[++p] = -0.0d;
            }
        }
    }

    /**
     * Sorts the specified range of the array.
     *
     * @param a the array to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param work a workspace array (slice)
     * @param workBase origin of usable space in work array
     * @param workLen usable size of work array
     */
    private static void doSort(double[] a, int left, int right, double[] work, int workBase, int workLen) {
        Trace.next_elem(436);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            Trace.next_elem(437);
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
            Trace.next_elem(438);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                Trace.next_elem(439);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                Trace.next_elem(440);
                break;
            }
            if (a[k] < a[k + 1]) {
                Trace.next_elem(441);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    Trace.next_elem(445);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                Trace.next_elem(442);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    Trace.next_elem(443);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    Trace.next_elem(444);
                    double t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                Trace.next_elem(446);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                Trace.next_elem(447);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            Trace.next_elem(448);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            Trace.next_elem(449);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            Trace.next_elem(450);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            Trace.next_elem(451);
            ;
        }
        // Use or create temporary array b for merging
        // temp array; alternates with a
        double[] b;
        // array offsets from 'left'
        int ao, bo;
        // space needed for b
        int blen = right - left;
        if (work == null || workLen < blen || workBase + blen > work.length) {
            Trace.next_elem(452);
            work = new double[blen];
            workBase = 0;
        }
        if (odd == 0) {
            Trace.next_elem(453);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            Trace.next_elem(454);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            Trace.next_elem(455);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                Trace.next_elem(456);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    Trace.next_elem(457);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        Trace.next_elem(458);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        Trace.next_elem(459);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                Trace.next_elem(460);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    Trace.next_elem(461);
                    ;
                }
                run[++last] = right;
            }
            double[] t = a;
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
    private static void sort(double[] a, int left, int right, boolean leftmost) {
        Trace.next_elem(462);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            Trace.next_elem(463);
            if (leftmost) {
                Trace.next_elem(464);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    Trace.next_elem(473);
                    double ai = a[i + 1];
                    while (ai < a[j]) {
                        Trace.next_elem(474);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            Trace.next_elem(475);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                Trace.next_elem(465);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    Trace.next_elem(466);
                    if (left >= right) {
                        Trace.next_elem(467);
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
                    Trace.next_elem(468);
                    double a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        Trace.next_elem(469);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        Trace.next_elem(470);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        Trace.next_elem(471);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                double last = a[right];
                while (last < a[--right]) {
                    Trace.next_elem(472);
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
        int e3 = (left + right) >>> 1;
        int e2 = e3 - seventh;
        int e1 = e2 - seventh;
        int e4 = e3 + seventh;
        int e5 = e4 + seventh;
        // Sort these elements using insertion sort
        if (a[e2] < a[e1]) {
            Trace.next_elem(476);
            double t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            Trace.next_elem(477);
            double t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                Trace.next_elem(478);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            Trace.next_elem(479);
            double t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                Trace.next_elem(480);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    Trace.next_elem(481);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            Trace.next_elem(482);
            double t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                Trace.next_elem(483);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    Trace.next_elem(484);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        Trace.next_elem(485);
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
            Trace.next_elem(486);
            /*
             * Use the second and fourth of the five sorted elements as pivots.
             * These values are inexpensive approximations of the first and
             * second terciles of the array. Note that pivot1 <= pivot2.
             */
            double pivot1 = a[e2];
            double pivot2 = a[e4];
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
                Trace.next_elem(495);
                ;
            }
            while (a[--great] > pivot2) {
                Trace.next_elem(496);
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
                Trace.next_elem(497);
                double ak = a[k];
                if (ak < pivot1) {
                    Trace.next_elem(498);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    Trace.next_elem(499);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        Trace.next_elem(500);
                        if (great-- == k) {
                            Trace.next_elem(501);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        Trace.next_elem(502);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(503);
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
                Trace.next_elem(504);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    Trace.next_elem(505);
                    ++less;
                }
                while (a[great] == pivot2) {
                    Trace.next_elem(506);
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
                    Trace.next_elem(507);
                    double ak = a[k];
                    if (ak == pivot1) {
                        Trace.next_elem(508);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        Trace.next_elem(509);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            Trace.next_elem(510);
                            if (great-- == k) {
                                Trace.next_elem(511);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            Trace.next_elem(512);
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
                            a[less] = a[great];
                            ++less;
                        } else {
                            Trace.next_elem(513);
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
            Trace.next_elem(487);
            // Partitioning with one pivot
            /*
             * Use the third of the five sorted elements as pivot.
             * This value is inexpensive approximation of the median.
             */
            double pivot = a[e3];
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
                Trace.next_elem(488);
                if (a[k] == pivot) {
                    Trace.next_elem(489);
                    continue;
                }
                double ak = a[k];
                if (ak < pivot) {
                    Trace.next_elem(490);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    Trace.next_elem(491);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        Trace.next_elem(492);
                        --great;
                    }
                    if (a[great] < pivot) {
                        Trace.next_elem(493);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        Trace.next_elem(494);
                        // a[great] == pivot
                        /*
                         * Even though a[great] equals to pivot, the
                         * assignment a[k] = pivot may be incorrect,
                         * if a[great] and pivot are floating-point
                         * zeros of different signs. Therefore in float
                         * and double sorting methods we have to use
                         * more accurate assignment a[k] = a[great].
                         */
                        a[k] = a[great];
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
}
