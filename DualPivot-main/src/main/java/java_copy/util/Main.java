package java_copy.util;

import java_copy.util.DualPivotQuicksort;

public class Main {
    public static void main(String[] args) {
        {
            int[] a = { 413 , 134 , 1, 41, -32, 0, -500, 413, 1 };
            prorunvis.Trace.trace_start();
            DualPivotQuicksort.sort(a, 0, a.length -1, null, 0, 0);
            prorunvis.Trace.trace_end();
            for (int i = 0; i < a.length; i++) {
                System.out.println("" + a[i]);
            }
        }
        {
            int[] a = { 413 , 134 , 1, 41, -32, 0, -500, 413, 1, 99, 700, 600, -2, 90, 80, 86, -500, 0, 100, 202 };
            prorunvis.Trace.trace_start();
            DualPivotQuicksort.sort(a, 0, a.length -1, null, 0, 0);
            prorunvis.Trace.trace_end();
            for (int i = 0; i < a.length; i++) {
                System.out.println("" + a[i]);
            }
        }
        {
            int[] a = { 413 , 134 , 1, 41, -32, 0, -500, 413, 1, 99, 700, 600, -2, 90, 80, 86, -500, 0, 100, 202, 413 , 134 , 1, 41, -32, 0, -500, 413, 1, 2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,25,28,30,31 };
            System.out.println("length a: " + a.length);
            prorunvis.Trace.trace_start();
            DualPivotQuicksort.sort(a, 0, a.length -1, null, 0, 0);
            prorunvis.Trace.trace_end();
            for (int i = 0; i < a.length; i++) {
                System.out.println("" + a[i]);
            }
        }

    }
}
