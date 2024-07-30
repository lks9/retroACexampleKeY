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
        prorunvis.Trace._FUNC();
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            prorunvis.Trace._IF();
            sort(a, left, right, true);
            {
                prorunvis.Trace._RETURN();
                return;
            }
        } else {
            prorunvis.Trace._ELSE();
        }
        /*
         * Index run[i] is the start of i-th run
         * (ascending or descending sequence).
         */
        int[] run = new int[MAX_RUN_COUNT + 1];
        int count = 0;
        run[0] = left;
        {
            // Check if the array is nearly sorted
            for (int k = left; k < right; run[count] = k) {
                prorunvis.Trace._LOOP_BODY();
                {
                    // Equal items in the beginning of the sequence
                    while (k < right && a[k] == a[k + 1]) {
                        prorunvis.Trace._LOOP_BODY();
                        k++;
                    }
                    prorunvis.Trace._LOOP_END();
                }
                // Sequence finishes with equal items
                if (k == right) {
                    prorunvis.Trace._IF();
                    {
                        prorunvis.Trace._BREAK();
                        break;
                    }
                } else {
                    prorunvis.Trace._ELSE();
                }
                if (a[k] < a[k + 1]) {
                    prorunvis.Trace._IF();
                    {
                        // ascending
                        while (++k <= right && a[k - 1] <= a[k]) {
                            prorunvis.Trace._LOOP_BODY();
                            ;
                        }
                        prorunvis.Trace._LOOP_END();
                    }
                } else {
                    prorunvis.Trace._ELSE();
                    if (a[k] > a[k + 1]) {
                        prorunvis.Trace._IF();
                        {
                            // descending
                            while (++k <= right && a[k - 1] >= a[k]) {
                                prorunvis.Trace._LOOP_BODY();
                                ;
                            }
                            prorunvis.Trace._LOOP_END();
                        }
                        {
                            // Transform into an ascending sequence
                            for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                                prorunvis.Trace._LOOP_BODY();
                                int t = a[lo];
                                a[lo] = a[hi];
                                a[hi] = t;
                            }
                            prorunvis.Trace._LOOP_END();
                        }
                    } else {
                        prorunvis.Trace._ELSE();
                    }
                }
                // Merge a transformed descending sequence followed by an
                // ascending sequence
                if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                    prorunvis.Trace._IF();
                    count--;
                } else {
                    prorunvis.Trace._ELSE();
                }
                /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
                if (++count == MAX_RUN_COUNT) {
                    prorunvis.Trace._IF();
                    sort(a, left, right, true);
                    {
                        prorunvis.Trace._RETURN();
                        return;
                    }
                } else {
                    prorunvis.Trace._ELSE();
                }
            }
            prorunvis.Trace._LOOP_END();
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            prorunvis.Trace._IF();
            {
                prorunvis.Trace._RETURN();
                // A single equal run
                return;
            }
        } else {
            prorunvis.Trace._ELSE();
            if (count == 1 && run[count] > right) {
                prorunvis.Trace._IF();
                // Either a single ascending or a transformed descending run.
                // Always check that a final run is a proper terminator, otherwise
                {
                    prorunvis.Trace._RETURN();
                    // we have an unterminated trailing run, to handle downstream.
                    return;
                }
            } else {
                prorunvis.Trace._ELSE();
            }
        }
        right++;
        if (run[count] < right) {
            prorunvis.Trace._IF();
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        } else {
            prorunvis.Trace._ELSE();
        }
        // Determine alternation base for merge
        byte odd = 0;
        {
            for (int n = 1; (n <<= 1) < count; odd ^= 1) {
                prorunvis.Trace._LOOP_BODY();
                ;
            }
            prorunvis.Trace._LOOP_END();
        }
        // Use or create temporary array b for merging
        // temp array; alternates with a
        int[] b;
        // array offsets from 'left'
        int ao, bo;
        // space needed for b
        int blen = right - left;
        if (work == null || workLen < blen || workBase + blen > work.length) {
            prorunvis.Trace._IF();
            work = new int[blen];
            workBase = 0;
        } else {
            prorunvis.Trace._ELSE();
        }
        if (odd == 0) {
            prorunvis.Trace._IF();
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            prorunvis.Trace._ELSE();
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        {
            // Merging
            for (int last; count > 1; count = last) {
                prorunvis.Trace._LOOP_BODY();
                {
                    for (int k = (last = 0) + 2; k <= count; k += 2) {
                        prorunvis.Trace._LOOP_BODY();
                        int hi = run[k], mi = run[k - 1];
                        {
                            for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                                prorunvis.Trace._LOOP_BODY();
                                if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                                    prorunvis.Trace._IF();
                                    b[i + bo] = a[p++ + ao];
                                } else {
                                    prorunvis.Trace._ELSE();
                                    b[i + bo] = a[q++ + ao];
                                }
                            }
                            prorunvis.Trace._LOOP_END();
                        }
                        run[++last] = hi;
                    }
                    prorunvis.Trace._LOOP_END();
                }
                if ((count & 1) != 0) {
                    prorunvis.Trace._IF();
                    {
                        for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                            prorunvis.Trace._LOOP_BODY();
                            ;
                        }
                        prorunvis.Trace._LOOP_END();
                    }
                    run[++last] = right;
                } else {
                    prorunvis.Trace._ELSE();
                }
                int[] t = a;
                a = b;
                b = t;
                int o = ao;
                ao = bo;
                bo = o;
            }
            prorunvis.Trace._LOOP_END();
        }
        prorunvis.Trace._RETURN();
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
        prorunvis.Trace._FUNC();
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            prorunvis.Trace._IF();
            if (leftmost) {
                prorunvis.Trace._IF();
                {
                    /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                    for (int i = left, j = i; i < right; j = ++i) {
                        prorunvis.Trace._LOOP_BODY();
                        int ai = a[i + 1];
                        {
                            while (ai < a[j]) {
                                prorunvis.Trace._LOOP_BODY();
                                a[j + 1] = a[j];
                                if (j-- == left) {
                                    prorunvis.Trace._IF();
                                    {
                                        prorunvis.Trace._BREAK();
                                        break;
                                    }
                                } else {
                                    prorunvis.Trace._ELSE();
                                }
                            }
                            prorunvis.Trace._LOOP_END();
                        }
                        a[j + 1] = ai;
                    }
                    prorunvis.Trace._LOOP_END();
                }
            } else {
                prorunvis.Trace._ELSE();
                {
                    prorunvis.Trace._BREAK();
                    /*
                 * Skip the longest ascending sequence.
                 */
                    do {
                        prorunvis.Trace._LOOP_BODY();
                        if (left >= right) {
                            prorunvis.Trace._IF();
                            {
                                prorunvis.Trace._RETURN();
                                return;
                            }
                        } else {
                            prorunvis.Trace._ELSE();
                        }
                    } while (a[++left] >= a[left - 1]);
                    prorunvis.Trace._LOOP_END();
                }
                {
                    /*
                 * Every element from adjoining part plays the role
                 * of sentinel, therefore this allows us to avoid the
                 * left range check on each iteration. Moreover, we use
                 * the more optimized algorithm, so called pair insertion
                 * sort, which is faster (in the context of Quicksort)
                 * than traditional implementation of insertion sort.
                 */
                    for (int k = left; ++left <= right; k = ++left) {
                        prorunvis.Trace._LOOP_BODY();
                        int a1 = a[k], a2 = a[left];
                        if (a1 < a2) {
                            prorunvis.Trace._IF();
                            a2 = a1;
                            a1 = a[left];
                        } else {
                            prorunvis.Trace._ELSE();
                        }
                        {
                            while (a1 < a[--k]) {
                                prorunvis.Trace._LOOP_BODY();
                                a[k + 2] = a[k];
                            }
                            prorunvis.Trace._LOOP_END();
                        }
                        a[++k + 1] = a1;
                        {
                            while (a2 < a[--k]) {
                                prorunvis.Trace._LOOP_BODY();
                                a[k + 1] = a[k];
                            }
                            prorunvis.Trace._LOOP_END();
                        }
                        a[k + 1] = a2;
                    }
                    prorunvis.Trace._LOOP_END();
                }
                int last = a[right];
                {
                    while (last < a[--right]) {
                        prorunvis.Trace._LOOP_BODY();
                        a[right + 1] = a[right];
                    }
                    prorunvis.Trace._LOOP_END();
                }
                a[right + 1] = last;
            }
            {
                prorunvis.Trace._RETURN();
                return;
            }
        } else {
            prorunvis.Trace._ELSE();
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
            prorunvis.Trace._IF();
            int t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        } else {
            prorunvis.Trace._ELSE();
        }
        if (a[e3] < a[e2]) {
            prorunvis.Trace._IF();
            int t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                prorunvis.Trace._IF();
                a[e2] = a[e1];
                a[e1] = t;
            } else {
                prorunvis.Trace._ELSE();
            }
        } else {
            prorunvis.Trace._ELSE();
        }
        if (a[e4] < a[e3]) {
            prorunvis.Trace._IF();
            int t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                prorunvis.Trace._IF();
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    prorunvis.Trace._IF();
                    a[e2] = a[e1];
                    a[e1] = t;
                } else {
                    prorunvis.Trace._ELSE();
                }
            } else {
                prorunvis.Trace._ELSE();
            }
        } else {
            prorunvis.Trace._ELSE();
        }
        if (a[e5] < a[e4]) {
            prorunvis.Trace._IF();
            int t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                prorunvis.Trace._IF();
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    prorunvis.Trace._IF();
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        prorunvis.Trace._IF();
                        a[e2] = a[e1];
                        a[e1] = t;
                    } else {
                        prorunvis.Trace._ELSE();
                    }
                } else {
                    prorunvis.Trace._ELSE();
                }
            } else {
                prorunvis.Trace._ELSE();
            }
        } else {
            prorunvis.Trace._ELSE();
        }
        // Pointers
        // The index of the first element of center part
        int less = left;
        // The index before the first element of right part
        int great = right;
        if (a[e1] != a[e2] && a[e2] != a[e3] && a[e3] != a[e4] && a[e4] != a[e5]) {
            prorunvis.Trace._IF();
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
            {
                /*
             * Skip elements, which are less or greater than pivot values.
             */
                while (a[++less] < pivot1) {
                    prorunvis.Trace._LOOP_BODY();
                    ;
                }
                prorunvis.Trace._LOOP_END();
            }
            {
                while (a[--great] > pivot2) {
                    prorunvis.Trace._LOOP_BODY();
                    ;
                }
                prorunvis.Trace._LOOP_END();
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
                prorunvis.Trace._LOOP_BODY();
                int ak = a[k];
                if (ak < pivot1) {
                    prorunvis.Trace._IF();
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else {
                    prorunvis.Trace._ELSE();
                    if (ak > pivot2) {
                        prorunvis.Trace._IF();
                        {
                            // Move a[k] to right part
                            while (a[great] > pivot2) {
                                prorunvis.Trace._LOOP_BODY();
                                if (great-- == k) {
                                    prorunvis.Trace._IF();
                                    {
                                        prorunvis.Trace._BREAK();
                                        break outer;
                                    }
                                } else {
                                    prorunvis.Trace._ELSE();
                                }
                            }
                            prorunvis.Trace._LOOP_END();
                        }
                        if (a[great] < pivot1) {
                            prorunvis.Trace._IF();
                            // a[great] <= pivot2
                            a[k] = a[less];
                            a[less] = a[great];
                            ++less;
                        } else {
                            prorunvis.Trace._ELSE();
                            // pivot1 <= a[great] <= pivot2
                            a[k] = a[great];
                        }
                        /*
                     * Here and below we use "a[i] = b; i--;" instead
                     * of "a[i--] = b;" due to performance issue.
                     */
                        a[great] = ak;
                        --great;
                    } else {
                        prorunvis.Trace._ELSE();
                    }
                }
            }
            prorunvis.Trace._LOOP_END();
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
                prorunvis.Trace._IF();
                {
                    /*
                 * Skip elements, which are equal to pivot values.
                 */
                    while (a[less] == pivot1) {
                        prorunvis.Trace._LOOP_BODY();
                        ++less;
                    }
                    prorunvis.Trace._LOOP_END();
                }
                {
                    while (a[great] == pivot2) {
                        prorunvis.Trace._LOOP_BODY();
                        --great;
                    }
                    prorunvis.Trace._LOOP_END();
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
                    prorunvis.Trace._LOOP_BODY();
                    int ak = a[k];
                    if (ak == pivot1) {
                        prorunvis.Trace._IF();
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else {
                        prorunvis.Trace._ELSE();
                        if (ak == pivot2) {
                            prorunvis.Trace._IF();
                            {
                                // Move a[k] to right part
                                while (a[great] == pivot2) {
                                    prorunvis.Trace._LOOP_BODY();
                                    if (great-- == k) {
                                        prorunvis.Trace._IF();
                                        {
                                            prorunvis.Trace._BREAK();
                                            break outer;
                                        }
                                    } else {
                                        prorunvis.Trace._ELSE();
                                    }
                                }
                                prorunvis.Trace._LOOP_END();
                            }
                            if (a[great] == pivot1) {
                                prorunvis.Trace._IF();
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
                                prorunvis.Trace._ELSE();
                                // pivot1 < a[great] < pivot2
                                a[k] = a[great];
                            }
                            a[great] = ak;
                            --great;
                        } else {
                            prorunvis.Trace._ELSE();
                        }
                    }
                }
                prorunvis.Trace._LOOP_END();
            } else {
                prorunvis.Trace._ELSE();
            }
            // Sort center part recursively
            sort(a, less, great, false);
        } else {
            prorunvis.Trace._ELSE();
            // Partitioning with one pivot
            /*
             * Use the third of the five sorted elements as pivot.
             * This value is inexpensive approximation of the median.
             */
            int pivot = a[e3];
            {
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
                    prorunvis.Trace._LOOP_BODY();
                    if (a[k] == pivot) {
                        prorunvis.Trace._IF();
                        continue;
                    } else {
                        prorunvis.Trace._ELSE();
                    }
                    int ak = a[k];
                    if (ak < pivot) {
                        prorunvis.Trace._IF();
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else {
                        prorunvis.Trace._ELSE();
                        {
                            // a[k] > pivot - Move a[k] to right part
                            while (a[great] > pivot) {
                                prorunvis.Trace._LOOP_BODY();
                                --great;
                            }
                            prorunvis.Trace._LOOP_END();
                        }
                        if (a[great] < pivot) {
                            prorunvis.Trace._IF();
                            // a[great] <= pivot
                            a[k] = a[less];
                            a[less] = a[great];
                            ++less;
                        } else {
                            prorunvis.Trace._ELSE();
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
                prorunvis.Trace._LOOP_END();
            }
            /*
             * Sort left and right parts recursively.
             * All elements from center part are equal
             * and, therefore, already sorted.
             */
            sort(a, left, less - 1, leftmost);
            sort(a, great + 1, right, false);
        }
        prorunvis.Trace._RETURN();
    }
    // we are not interested in the other functions for long[] a, float[] a, ... so removed them!
}
