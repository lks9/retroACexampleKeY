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
        prorunvis.Trace.next_elem(0);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                prorunvis.Trace.next_elem(0);
                break;
            }
            if (a[k] < a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    prorunvis.Trace.next_elem(0);
                    int t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                prorunvis.Trace.next_elem(0);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                prorunvis.Trace.next_elem(0);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            prorunvis.Trace.next_elem(0);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            prorunvis.Trace.next_elem(0);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            prorunvis.Trace.next_elem(0);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            work = new int[blen];
            workBase = 0;
        }
        if (odd == 0) {
            prorunvis.Trace.next_elem(0);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            prorunvis.Trace.next_elem(0);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            prorunvis.Trace.next_elem(0);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                prorunvis.Trace.next_elem(0);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    prorunvis.Trace.next_elem(0);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                prorunvis.Trace.next_elem(0);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
            if (leftmost) {
                prorunvis.Trace.next_elem(0);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    prorunvis.Trace.next_elem(0);
                    int ai = a[i + 1];
                    while (ai < a[j]) {
                        prorunvis.Trace.next_elem(0);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            prorunvis.Trace.next_elem(0);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    prorunvis.Trace.next_elem(0);
                    if (left >= right) {
                        prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    int a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        prorunvis.Trace.next_elem(0);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                int last = a[right];
                while (last < a[--right]) {
                    prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            int t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            prorunvis.Trace.next_elem(0);
            int t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                prorunvis.Trace.next_elem(0);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            prorunvis.Trace.next_elem(0);
            int t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                prorunvis.Trace.next_elem(0);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    prorunvis.Trace.next_elem(0);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            prorunvis.Trace.next_elem(0);
            int t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                prorunvis.Trace.next_elem(0);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    prorunvis.Trace.next_elem(0);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                ;
            }
            while (a[--great] > pivot2) {
                prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                int ak = a[k];
                if (ak < pivot1) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        prorunvis.Trace.next_elem(0);
                        if (great-- == k) {
                            prorunvis.Trace.next_elem(0);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    prorunvis.Trace.next_elem(0);
                    ++less;
                }
                while (a[great] == pivot2) {
                    prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    int ak = a[k];
                    if (ak == pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            prorunvis.Trace.next_elem(0);
                            if (great-- == k) {
                                prorunvis.Trace.next_elem(0);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            prorunvis.Trace.next_elem(0);
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
                            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                if (a[k] == pivot) {
                    prorunvis.Trace.next_elem(0);
                    continue;
                }
                int ak = a[k];
                if (ak < pivot) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    prorunvis.Trace.next_elem(0);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        prorunvis.Trace.next_elem(0);
                        --great;
                    }
                    if (a[great] < pivot) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                prorunvis.Trace.next_elem(0);
                break;
            }
            if (a[k] < a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    prorunvis.Trace.next_elem(0);
                    long t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                prorunvis.Trace.next_elem(0);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                prorunvis.Trace.next_elem(0);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            prorunvis.Trace.next_elem(0);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            prorunvis.Trace.next_elem(0);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            prorunvis.Trace.next_elem(0);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            work = new long[blen];
            workBase = 0;
        }
        if (odd == 0) {
            prorunvis.Trace.next_elem(0);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            prorunvis.Trace.next_elem(0);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            prorunvis.Trace.next_elem(0);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                prorunvis.Trace.next_elem(0);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    prorunvis.Trace.next_elem(0);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                prorunvis.Trace.next_elem(0);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
            if (leftmost) {
                prorunvis.Trace.next_elem(0);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    prorunvis.Trace.next_elem(0);
                    long ai = a[i + 1];
                    while (ai < a[j]) {
                        prorunvis.Trace.next_elem(0);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            prorunvis.Trace.next_elem(0);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    prorunvis.Trace.next_elem(0);
                    if (left >= right) {
                        prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    long a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        prorunvis.Trace.next_elem(0);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                long last = a[right];
                while (last < a[--right]) {
                    prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            long t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            prorunvis.Trace.next_elem(0);
            long t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                prorunvis.Trace.next_elem(0);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            prorunvis.Trace.next_elem(0);
            long t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                prorunvis.Trace.next_elem(0);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    prorunvis.Trace.next_elem(0);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            prorunvis.Trace.next_elem(0);
            long t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                prorunvis.Trace.next_elem(0);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    prorunvis.Trace.next_elem(0);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                ;
            }
            while (a[--great] > pivot2) {
                prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                long ak = a[k];
                if (ak < pivot1) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        prorunvis.Trace.next_elem(0);
                        if (great-- == k) {
                            prorunvis.Trace.next_elem(0);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    prorunvis.Trace.next_elem(0);
                    ++less;
                }
                while (a[great] == pivot2) {
                    prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    long ak = a[k];
                    if (ak == pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            prorunvis.Trace.next_elem(0);
                            if (great-- == k) {
                                prorunvis.Trace.next_elem(0);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            prorunvis.Trace.next_elem(0);
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
                            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                if (a[k] == pivot) {
                    prorunvis.Trace.next_elem(0);
                    continue;
                }
                long ak = a[k];
                if (ak < pivot) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    prorunvis.Trace.next_elem(0);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        prorunvis.Trace.next_elem(0);
                        --great;
                    }
                    if (a[great] < pivot) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        // Use counting sort on large arrays
        if (right - left > COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR) {
            prorunvis.Trace.next_elem(0);
            int[] count = new int[NUM_SHORT_VALUES];
            for (int i = left - 1; ++i <= right; count[a[i] - Short.MIN_VALUE]++) {
                prorunvis.Trace.next_elem(0);
                ;
            }
            for (int i = NUM_SHORT_VALUES, k = right + 1; k > left; ) {
                prorunvis.Trace.next_elem(0);
                while (count[--i] == 0) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
                short value = (short) (i + Short.MIN_VALUE);
                int s = count[i];
                do {
                    prorunvis.Trace.next_elem(0);
                    a[--k] = value;
                } while (--s > 0);
            }
        } else {
            prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                prorunvis.Trace.next_elem(0);
                break;
            }
            if (a[k] < a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    prorunvis.Trace.next_elem(0);
                    short t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                prorunvis.Trace.next_elem(0);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                prorunvis.Trace.next_elem(0);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            prorunvis.Trace.next_elem(0);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            prorunvis.Trace.next_elem(0);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            prorunvis.Trace.next_elem(0);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            work = new short[blen];
            workBase = 0;
        }
        if (odd == 0) {
            prorunvis.Trace.next_elem(0);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            prorunvis.Trace.next_elem(0);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            prorunvis.Trace.next_elem(0);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                prorunvis.Trace.next_elem(0);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    prorunvis.Trace.next_elem(0);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                prorunvis.Trace.next_elem(0);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
            if (leftmost) {
                prorunvis.Trace.next_elem(0);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    prorunvis.Trace.next_elem(0);
                    short ai = a[i + 1];
                    while (ai < a[j]) {
                        prorunvis.Trace.next_elem(0);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            prorunvis.Trace.next_elem(0);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    prorunvis.Trace.next_elem(0);
                    if (left >= right) {
                        prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    short a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        prorunvis.Trace.next_elem(0);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                short last = a[right];
                while (last < a[--right]) {
                    prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            short t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            prorunvis.Trace.next_elem(0);
            short t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                prorunvis.Trace.next_elem(0);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            prorunvis.Trace.next_elem(0);
            short t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                prorunvis.Trace.next_elem(0);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    prorunvis.Trace.next_elem(0);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            prorunvis.Trace.next_elem(0);
            short t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                prorunvis.Trace.next_elem(0);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    prorunvis.Trace.next_elem(0);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                ;
            }
            while (a[--great] > pivot2) {
                prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                short ak = a[k];
                if (ak < pivot1) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        prorunvis.Trace.next_elem(0);
                        if (great-- == k) {
                            prorunvis.Trace.next_elem(0);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    prorunvis.Trace.next_elem(0);
                    ++less;
                }
                while (a[great] == pivot2) {
                    prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    short ak = a[k];
                    if (ak == pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            prorunvis.Trace.next_elem(0);
                            if (great-- == k) {
                                prorunvis.Trace.next_elem(0);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            prorunvis.Trace.next_elem(0);
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
                            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                if (a[k] == pivot) {
                    prorunvis.Trace.next_elem(0);
                    continue;
                }
                short ak = a[k];
                if (ak < pivot) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    prorunvis.Trace.next_elem(0);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        prorunvis.Trace.next_elem(0);
                        --great;
                    }
                    if (a[great] < pivot) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        // Use counting sort on large arrays
        if (right - left > COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR) {
            prorunvis.Trace.next_elem(0);
            int[] count = new int[NUM_CHAR_VALUES];
            for (int i = left - 1; ++i <= right; count[a[i]]++) {
                prorunvis.Trace.next_elem(0);
                ;
            }
            for (int i = NUM_CHAR_VALUES, k = right + 1; k > left; ) {
                prorunvis.Trace.next_elem(0);
                while (count[--i] == 0) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
                char value = (char) i;
                int s = count[i];
                do {
                    prorunvis.Trace.next_elem(0);
                    a[--k] = value;
                } while (--s > 0);
            }
        } else {
            prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                prorunvis.Trace.next_elem(0);
                break;
            }
            if (a[k] < a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    prorunvis.Trace.next_elem(0);
                    char t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                prorunvis.Trace.next_elem(0);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                prorunvis.Trace.next_elem(0);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            prorunvis.Trace.next_elem(0);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            prorunvis.Trace.next_elem(0);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            prorunvis.Trace.next_elem(0);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            work = new char[blen];
            workBase = 0;
        }
        if (odd == 0) {
            prorunvis.Trace.next_elem(0);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            prorunvis.Trace.next_elem(0);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            prorunvis.Trace.next_elem(0);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                prorunvis.Trace.next_elem(0);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    prorunvis.Trace.next_elem(0);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                prorunvis.Trace.next_elem(0);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
            if (leftmost) {
                prorunvis.Trace.next_elem(0);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    prorunvis.Trace.next_elem(0);
                    char ai = a[i + 1];
                    while (ai < a[j]) {
                        prorunvis.Trace.next_elem(0);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            prorunvis.Trace.next_elem(0);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    prorunvis.Trace.next_elem(0);
                    if (left >= right) {
                        prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    char a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        prorunvis.Trace.next_elem(0);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                char last = a[right];
                while (last < a[--right]) {
                    prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            char t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            prorunvis.Trace.next_elem(0);
            char t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                prorunvis.Trace.next_elem(0);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            prorunvis.Trace.next_elem(0);
            char t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                prorunvis.Trace.next_elem(0);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    prorunvis.Trace.next_elem(0);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            prorunvis.Trace.next_elem(0);
            char t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                prorunvis.Trace.next_elem(0);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    prorunvis.Trace.next_elem(0);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                ;
            }
            while (a[--great] > pivot2) {
                prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                char ak = a[k];
                if (ak < pivot1) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        prorunvis.Trace.next_elem(0);
                        if (great-- == k) {
                            prorunvis.Trace.next_elem(0);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    prorunvis.Trace.next_elem(0);
                    ++less;
                }
                while (a[great] == pivot2) {
                    prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    char ak = a[k];
                    if (ak == pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            prorunvis.Trace.next_elem(0);
                            if (great-- == k) {
                                prorunvis.Trace.next_elem(0);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            prorunvis.Trace.next_elem(0);
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
                            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                if (a[k] == pivot) {
                    prorunvis.Trace.next_elem(0);
                    continue;
                }
                char ak = a[k];
                if (ak < pivot) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    prorunvis.Trace.next_elem(0);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        prorunvis.Trace.next_elem(0);
                        --great;
                    }
                    if (a[great] < pivot) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        // Use counting sort on large arrays
        if (right - left > COUNTING_SORT_THRESHOLD_FOR_BYTE) {
            prorunvis.Trace.next_elem(0);
            int[] count = new int[NUM_BYTE_VALUES];
            for (int i = left - 1; ++i <= right; count[a[i] - Byte.MIN_VALUE]++) {
                prorunvis.Trace.next_elem(0);
                ;
            }
            for (int i = NUM_BYTE_VALUES, k = right + 1; k > left; ) {
                prorunvis.Trace.next_elem(0);
                while (count[--i] == 0) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
                byte value = (byte) (i + Byte.MIN_VALUE);
                int s = count[i];
                do {
                    prorunvis.Trace.next_elem(0);
                    a[--k] = value;
                } while (--s > 0);
            }
        } else {
            prorunvis.Trace.next_elem(0);
            // Use insertion sort on small arrays
            for (int i = left, j = i; i < right; j = ++i) {
                prorunvis.Trace.next_elem(0);
                byte ai = a[i + 1];
                while (ai < a[j]) {
                    prorunvis.Trace.next_elem(0);
                    a[j + 1] = a[j];
                    if (j-- == left) {
                        prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        /*
         * Phase 1: Move NaNs to the end of the array.
         */
        while (left <= right && Float.isNaN(a[right])) {
            prorunvis.Trace.next_elem(0);
            --right;
        }
        for (int k = right; --k >= left; ) {
            prorunvis.Trace.next_elem(0);
            float ak = a[k];
            if (ak != ak) {
                prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            int middle = (left + hi) >>> 1;
            float middleValue = a[middle];
            if (middleValue < 0.0f) {
                prorunvis.Trace.next_elem(0);
                left = middle + 1;
            } else {
                prorunvis.Trace.next_elem(0);
                hi = middle;
            }
        }
        /*
         * Skip the last negative value (if any) or all leading negative zeros.
         */
        while (left <= right && Float.floatToRawIntBits(a[left]) < 0) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            float ak = a[k];
            if (ak != 0.0f) {
                prorunvis.Trace.next_elem(0);
                break;
            }
            if (Float.floatToRawIntBits(ak) < 0) {
                prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                prorunvis.Trace.next_elem(0);
                break;
            }
            if (a[k] < a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    prorunvis.Trace.next_elem(0);
                    float t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                prorunvis.Trace.next_elem(0);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                prorunvis.Trace.next_elem(0);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            prorunvis.Trace.next_elem(0);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            prorunvis.Trace.next_elem(0);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            prorunvis.Trace.next_elem(0);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            work = new float[blen];
            workBase = 0;
        }
        if (odd == 0) {
            prorunvis.Trace.next_elem(0);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            prorunvis.Trace.next_elem(0);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            prorunvis.Trace.next_elem(0);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                prorunvis.Trace.next_elem(0);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    prorunvis.Trace.next_elem(0);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                prorunvis.Trace.next_elem(0);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
            if (leftmost) {
                prorunvis.Trace.next_elem(0);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    prorunvis.Trace.next_elem(0);
                    float ai = a[i + 1];
                    while (ai < a[j]) {
                        prorunvis.Trace.next_elem(0);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            prorunvis.Trace.next_elem(0);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    prorunvis.Trace.next_elem(0);
                    if (left >= right) {
                        prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    float a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        prorunvis.Trace.next_elem(0);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                float last = a[right];
                while (last < a[--right]) {
                    prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            float t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            prorunvis.Trace.next_elem(0);
            float t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                prorunvis.Trace.next_elem(0);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            prorunvis.Trace.next_elem(0);
            float t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                prorunvis.Trace.next_elem(0);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    prorunvis.Trace.next_elem(0);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            prorunvis.Trace.next_elem(0);
            float t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                prorunvis.Trace.next_elem(0);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    prorunvis.Trace.next_elem(0);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                ;
            }
            while (a[--great] > pivot2) {
                prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                float ak = a[k];
                if (ak < pivot1) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        prorunvis.Trace.next_elem(0);
                        if (great-- == k) {
                            prorunvis.Trace.next_elem(0);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    prorunvis.Trace.next_elem(0);
                    ++less;
                }
                while (a[great] == pivot2) {
                    prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    float ak = a[k];
                    if (ak == pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            prorunvis.Trace.next_elem(0);
                            if (great-- == k) {
                                prorunvis.Trace.next_elem(0);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            prorunvis.Trace.next_elem(0);
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
                            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                if (a[k] == pivot) {
                    prorunvis.Trace.next_elem(0);
                    continue;
                }
                float ak = a[k];
                if (ak < pivot) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    prorunvis.Trace.next_elem(0);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        prorunvis.Trace.next_elem(0);
                        --great;
                    }
                    if (a[great] < pivot) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        /*
         * Phase 1: Move NaNs to the end of the array.
         */
        while (left <= right && Double.isNaN(a[right])) {
            prorunvis.Trace.next_elem(0);
            --right;
        }
        for (int k = right; --k >= left; ) {
            prorunvis.Trace.next_elem(0);
            double ak = a[k];
            if (ak != ak) {
                prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            int middle = (left + hi) >>> 1;
            double middleValue = a[middle];
            if (middleValue < 0.0d) {
                prorunvis.Trace.next_elem(0);
                left = middle + 1;
            } else {
                prorunvis.Trace.next_elem(0);
                hi = middle;
            }
        }
        /*
         * Skip the last negative value (if any) or all leading negative zeros.
         */
        while (left <= right && Double.doubleToRawLongBits(a[left]) < 0) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            double ak = a[k];
            if (ak != 0.0d) {
                prorunvis.Trace.next_elem(0);
                break;
            }
            if (Double.doubleToRawLongBits(ak) < 0) {
                prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            // Equal items in the beginning of the sequence
            while (k < right && a[k] == a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                k++;
            }
            // Sequence finishes with equal items
            if (k == right) {
                prorunvis.Trace.next_elem(0);
                break;
            }
            if (a[k] < a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // ascending
                while (++k <= right && a[k - 1] <= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
            } else if (a[k] > a[k + 1]) {
                prorunvis.Trace.next_elem(0);
                // descending
                while (++k <= right && a[k - 1] >= a[k]) {
                    prorunvis.Trace.next_elem(0);
                    ;
                }
                // Transform into an ascending sequence
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    prorunvis.Trace.next_elem(0);
                    double t = a[lo];
                    a[lo] = a[hi];
                    a[hi] = t;
                }
            }
            // Merge a transformed descending sequence followed by an
            // ascending sequence
            if (run[count] > left && a[run[count]] >= a[run[count] - 1]) {
                prorunvis.Trace.next_elem(0);
                count--;
            }
            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                prorunvis.Trace.next_elem(0);
                sort(a, left, right, true);
                return;
            }
        }
        // These invariants should hold true:
        //    run[0] = 0
        //    run[<last>] = right + 1; (terminator)
        if (count == 0) {
            prorunvis.Trace.next_elem(0);
            // A single equal run
            return;
        } else if (count == 1 && run[count] > right) {
            prorunvis.Trace.next_elem(0);
            // Either a single ascending or a transformed descending run.
            // Always check that a final run is a proper terminator, otherwise
            // we have an unterminated trailing run, to handle downstream.
            return;
        }
        right++;
        if (run[count] < right) {
            prorunvis.Trace.next_elem(0);
            // Corner case: the final run is not a terminator. This may happen
            // if a final run is an equals run, or there is a single-element run
            // at the end. Fix up by adding a proper terminator at the end.
            // Note that we terminate with (right + 1), incremented earlier.
            run[++count] = right;
        }
        // Determine alternation base for merge
        byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1) {
            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            work = new double[blen];
            workBase = 0;
        }
        if (odd == 0) {
            prorunvis.Trace.next_elem(0);
            System.arraycopy(a, left, work, workBase, blen);
            b = a;
            bo = 0;
            a = work;
            ao = workBase - left;
        } else {
            prorunvis.Trace.next_elem(0);
            b = work;
            ao = 0;
            bo = workBase - left;
        }
        // Merging
        for (int last; count > 1; count = last) {
            prorunvis.Trace.next_elem(0);
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                prorunvis.Trace.next_elem(0);
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    prorunvis.Trace.next_elem(0);
                    if (q >= hi || p < mi && a[p + ao] <= a[q + ao]) {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[p++ + ao];
                    } else {
                        prorunvis.Trace.next_elem(0);
                        b[i + bo] = a[q++ + ao];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                prorunvis.Trace.next_elem(0);
                for (int i = right, lo = run[count - 1]; --i >= lo; b[i + bo] = a[i + ao]) {
                    prorunvis.Trace.next_elem(0);
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
        prorunvis.Trace.next_elem(0);
        int length = right - left + 1;
        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            prorunvis.Trace.next_elem(0);
            if (leftmost) {
                prorunvis.Trace.next_elem(0);
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    prorunvis.Trace.next_elem(0);
                    double ai = a[i + 1];
                    while (ai < a[j]) {
                        prorunvis.Trace.next_elem(0);
                        a[j + 1] = a[j];
                        if (j-- == left) {
                            prorunvis.Trace.next_elem(0);
                            break;
                        }
                    }
                    a[j + 1] = ai;
                }
            } else {
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    prorunvis.Trace.next_elem(0);
                    if (left >= right) {
                        prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    double a1 = a[k], a2 = a[left];
                    if (a1 < a2) {
                        prorunvis.Trace.next_elem(0);
                        a2 = a1;
                        a1 = a[left];
                    }
                    while (a1 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 2] = a[k];
                    }
                    a[++k + 1] = a1;
                    while (a2 < a[--k]) {
                        prorunvis.Trace.next_elem(0);
                        a[k + 1] = a[k];
                    }
                    a[k + 1] = a2;
                }
                double last = a[right];
                while (last < a[--right]) {
                    prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
            double t = a[e2];
            a[e2] = a[e1];
            a[e1] = t;
        }
        if (a[e3] < a[e2]) {
            prorunvis.Trace.next_elem(0);
            double t = a[e3];
            a[e3] = a[e2];
            a[e2] = t;
            if (t < a[e1]) {
                prorunvis.Trace.next_elem(0);
                a[e2] = a[e1];
                a[e1] = t;
            }
        }
        if (a[e4] < a[e3]) {
            prorunvis.Trace.next_elem(0);
            double t = a[e4];
            a[e4] = a[e3];
            a[e3] = t;
            if (t < a[e2]) {
                prorunvis.Trace.next_elem(0);
                a[e3] = a[e2];
                a[e2] = t;
                if (t < a[e1]) {
                    prorunvis.Trace.next_elem(0);
                    a[e2] = a[e1];
                    a[e1] = t;
                }
            }
        }
        if (a[e5] < a[e4]) {
            prorunvis.Trace.next_elem(0);
            double t = a[e5];
            a[e5] = a[e4];
            a[e4] = t;
            if (t < a[e3]) {
                prorunvis.Trace.next_elem(0);
                a[e4] = a[e3];
                a[e3] = t;
                if (t < a[e2]) {
                    prorunvis.Trace.next_elem(0);
                    a[e3] = a[e2];
                    a[e2] = t;
                    if (t < a[e1]) {
                        prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                ;
            }
            while (a[--great] > pivot2) {
                prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                double ak = a[k];
                if (ak < pivot1) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a[less] = ak;
                    ++less;
                } else if (ak > pivot2) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to right part
                    while (a[great] > pivot2) {
                        prorunvis.Trace.next_elem(0);
                        if (great-- == k) {
                            prorunvis.Trace.next_elem(0);
                            break outer;
                        }
                    }
                    if (a[great] < pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot2
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a[less] == pivot1) {
                    prorunvis.Trace.next_elem(0);
                    ++less;
                }
                while (a[great] == pivot2) {
                    prorunvis.Trace.next_elem(0);
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
                    prorunvis.Trace.next_elem(0);
                    double ak = a[k];
                    if (ak == pivot1) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to left part
                        a[k] = a[less];
                        a[less] = ak;
                        ++less;
                    } else if (ak == pivot2) {
                        prorunvis.Trace.next_elem(0);
                        // Move a[k] to right part
                        while (a[great] == pivot2) {
                            prorunvis.Trace.next_elem(0);
                            if (great-- == k) {
                                prorunvis.Trace.next_elem(0);
                                break outer;
                            }
                        }
                        if (a[great] == pivot1) {
                            prorunvis.Trace.next_elem(0);
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
                            prorunvis.Trace.next_elem(0);
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
            prorunvis.Trace.next_elem(0);
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
                prorunvis.Trace.next_elem(0);
                if (a[k] == pivot) {
                    prorunvis.Trace.next_elem(0);
                    continue;
                }
                double ak = a[k];
                if (ak < pivot) {
                    prorunvis.Trace.next_elem(0);
                    // Move a[k] to left part
                    a[k] = a[less];
                    a[less] = ak;
                    ++less;
                } else {
                    prorunvis.Trace.next_elem(0);
                    // a[k] > pivot - Move a[k] to right part
                    while (a[great] > pivot) {
                        prorunvis.Trace.next_elem(0);
                        --great;
                    }
                    if (a[great] < pivot) {
                        prorunvis.Trace.next_elem(0);
                        // a[great] <= pivot
                        a[k] = a[less];
                        a[less] = a[great];
                        ++less;
                    } else {
                        prorunvis.Trace.next_elem(0);
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
