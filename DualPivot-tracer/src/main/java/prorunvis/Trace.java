package prorunvis;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Trace {

    static final String PROLOG =
        "";
    static final String EPILOG =
        "E";

    static OutputStreamWriter out;

    static boolean breakBefore = false;

    public static void write(String s) {
        try {
            out.write(s);
        } catch (Exception e) {}
    }
    public static void _IF() {
        write("I");
    }
    public static void _ELSE() {
        write("O");
    }
    public static void _LOOP_BODY() {
        if (!breakBefore) write("I");
        breakBefore = false;
    }
    public static void _LOOP_END() {
        if (!breakBefore) write("O");
        breakBefore = false;
    }
    public static void _BREAK() {
        breakBefore = true;
    }
    public static void _FUNC() {
        write("C");
    }
    public static void _RETURN() {
        write("R");
    }
    static final String outDirName = "trace-out/";

    public static void trace_start() {

        // get current time
        // use LocalDateTime because we want the local time *without* time zone information
        LocalDateTime now = LocalDateTime.now();

        // unique trace name
        // <projectName>_<dateTime> but <dateTime> without the usual ":" and " "
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ssnnnnnnnnn");
        String traceName = "DualPivot" + "_" + now.format(timeFormat);

        try {
            // construct the new unique trace directory
            File traceDir = new File(outDirName);
            traceDir.mkdirs();

            // write prolog
            write(PROLOG);

            // open trace, write prolog
            out
                = new OutputStreamWriter(new FileOutputStream(outDirName + traceName + ".trace.txt"));
            write(PROLOG);
            System.out.println("Trace to: " + outDirName + traceName + ".trace.txt");
        } catch (Exception e) {
            return;
        }
    }

    public static void trace_end() {
        write(EPILOG);
        try {
            out.close();
        } catch (Exception e) {}
    }
}
