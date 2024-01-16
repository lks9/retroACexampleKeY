package tracer;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.time.Clock;
import java.time.Instant;

public class Trace {

    static final String PROLOG =
        "package tracer;\n" +
        "\n" +
        "public class Trace {\n" +
        "\n" +
        "    // array of trace IDs\n" +
        "    //@ public static ghost int[] r;\n" +
        "\n" +
        "    // current index\n" +
        "    //@ public static ghost int i;\n" +
        "\n" +
        "\n" +
        "    public static void trace_start() {\n" +
        "        //@ assume r != null;\n" +
        "        //@ ghost int len = 0;\n" +
        "\n";
    static final String EPILOG =
        "        //@ assume Trace.r.length == len;\n" +
        "        //@ assume i == 0;\n" +
        "    }\n" +
        "\n" +
        "    public static void trace_next(int elm) {\n" +
        "        //@ assume i < r.length;\n" +
        "        //@ assume r[i] == elm;\n" +
        "        //@ set i = i + 1;\n" +
        "    }\n" +
        "    public static void trace_end() {\n" +
        "        trace_next(0);\n" +
        "        //@ assume i == r.length;\n" +
        "        //@ set i = 0;\n" +
        "    }\n" +
        "}\n";

    static OutputStreamWriter out;

    static void write(String s) {
        try {
            out.write(s);
        } catch (Exception e) {}
    }

    public static void trace_start() {

        // get current time
        Clock clock = Clock.systemDefaultZone();
        Instant instant = clock.instant();
        long seconds = instant.getEpochSecond();
        long nano = instant.getNano();

        // unique trace name
        String traceName = "traceSec" + seconds + "Nano" + nano;

        // construct the new unique trace directory
        String traceDirName = "trace-out/" + traceName + "/tracer/";
        File traceDir = new File(traceDirName);
        traceDir.mkdirs();

        // write trace (in form of the retrace class) to out
        try {
            out = new OutputStreamWriter(new FileOutputStream(traceDirName + "Trace.java"));
        } catch (Exception e) {
            return;
        }

        // write prolog
        write(PROLOG);
    }

    public static void trace_next(int elm) {
        // write new traced element
        write("        //@ assume Trace.r[len] == " + (elm) + ";\n");
        write("        //@ set len = len+1;\n\n");
    }

    public static void trace_end() {
        trace_next(0);
        write(EPILOG);
        try {
            out.close();
        } catch (Exception e) {}
    }
}
