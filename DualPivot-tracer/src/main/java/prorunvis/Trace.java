package prorunvis;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import prorunvis.KeYobligation;

public class Trace {

    static final String PROLOG =
        "package prorunvis;\n" +
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
        "    public static void next_elem(int elm) {\n" +
        "        //@ assume i < r.length;\n" +
        "        //@ assume r[i] == elm;\n" +
        "        //@ set i = i + 1;\n" +
        "    }\n" +
        "    public static void trace_end() {\n" +
        "        next_elem(0);\n" +
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

    static final String outDirName = "trace-out/";

    public static void trace_start() {

        // get current time
        // use LocalDateTime because we want the local time *without* time zone information
        LocalDateTime now = LocalDateTime.now();

        // unique trace name
        // <projectName>_<dateTime> but <dateTime> without the usual ":" and " "
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ssnnnnnnnnn");
        String traceName = KeYobligation.PROJECT_NAME + "_" + now.format(timeFormat);

        try {
            // construct the new unique trace directory
            String traceDirName = outDirName + traceName + "/prorunvis/";
            File traceDir = new File(traceDirName);
            traceDir.mkdirs();

            // write trace (in form of the retrace class) to out
            out = new OutputStreamWriter(new FileOutputStream(traceDirName + "Trace.java"));

            // symlink the original project
            Path orig = Paths.get(KeYobligation.TRACED_SOURCE_FOLDER);
            Path link = Paths.get(outDirName + traceName + "/src");
            Files.createSymbolicLink(link, orig);

            // write prolog
            write(PROLOG);

            // write key proof obligation file
            OutputStreamWriter keyout
                = new OutputStreamWriter(new FileOutputStream(outDirName + traceName + ".key"));
            keyout.write(KeYobligation.PROLOG);
            keyout.write("\\javaSource \"" + traceName + "/\";\n");
            keyout.write(KeYobligation.EPILOG);
            keyout.close();
        } catch (Exception e) {
            return;
        }
    }

    public static void next_elem(int elm) {
        // write new traced element
        write("        //@ assume Trace.r[len] == " + (elm) + ";\n");
        write("        //@ set len = len+1;\n\n");
    }

    public static void trace_end() {
        next_elem(0);
        write(EPILOG);
        try {
            out.close();
        } catch (Exception e) {}
    }
}
