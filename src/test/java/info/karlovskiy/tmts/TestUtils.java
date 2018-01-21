package info.karlovskiy.tmts;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class TestUtils {

    public static String loadFile(String name) {
        return loadFile(name, "UTF-8");
    }

    public static String loadFile(String name, String encoding) {
        try (InputStreamReader input = new InputStreamReader(TestUtils.class.getResourceAsStream(name), encoding)) {
            StringWriter sw = new StringWriter();
            char[] buffer = new char[4 * 1024];
            int n;
            while (-1 != (n = input.read(buffer))) {
                sw.write(buffer, 0, n);
            }
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TestUtils() {
    }

}
