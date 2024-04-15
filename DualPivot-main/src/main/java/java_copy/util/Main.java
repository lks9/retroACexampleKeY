package java_copy.util;

import java_copy.util.DualPivotQuicksort;

public class Main {
    public static void main(String[] args) {
        int[] a = { 413 , 134 , 1, 41, -32, 0, -500, 413, 1 };
        DualPivotQuicksort.sort(a, 0, a.length -1, null, 0, 0);
        prorunvis.Trace.trace_end();
        for (int i = 0; i < a.length; i++) {
            System.out.println("" + a[i]);
        }
    }
}
