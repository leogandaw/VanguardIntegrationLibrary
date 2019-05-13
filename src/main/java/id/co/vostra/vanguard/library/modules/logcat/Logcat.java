package id.co.vostra.vanguard.library.modules.logcat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Logcat {

    public static String getLog(){
        Process logcat;
        StringBuilder log = new StringBuilder();
        try {
            logcat = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
            BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 4 * 1024);
            String line;
            String separator = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log = new StringBuilder();
            log.append(e.getMessage());
        }
        finally {
            return String.valueOf(log);
        }

    }
}
