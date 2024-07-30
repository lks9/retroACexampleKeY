package prorunvis;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
public final class ProRunVis {
    public static void proRunVisTrace(String trace) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
"/home/lukas/Software/retroACexampleKeY/DualPivot/src/main/java/Trace.tr"
            , true));
            writer.write(trace + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
