/*  Copyright 2011 Fabrizio Cannizzo
 *
 *  This file is part of RestFixture.
 *
 *  RestFixture (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  RestFixture is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RestFixture.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */
package smartrics.rest.support.fitnesse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FitnesseResultVerifier {

    private static String FITNESSE_RESULTS_REGEX = "<strong>Test Pages:</strong> (\\d+) right, (\\d+) wrong, (\\d+) ignored, (\\d+) exceptions.+<strong>Assertions:</strong> (\\d+) right, (\\d+) wrong, (\\d+) ignored, (\\d+) exceptions";

    public static void main(String[] args) {
        if (args.length != 1)
            throw new RuntimeException("You need to pass the path to th eHTML file produced by FitNesse containing the results of test execution");
        try {
            FitnesseResultVerifier verifier = new FitnesseResultVerifier();
            System.out.println("Processing " + args[0]);
            String content = verifier.readFile(args[0]);
            Pattern p = Pattern.compile(FITNESSE_RESULTS_REGEX);
            Matcher m = p.matcher(content);
            int count = m.groupCount();
            if (count != 8) {
                System.out.println("The file doesn't look like a result produced by FitNesse");
                System.out.println("It should contain something matching:\n\t" + FITNESSE_RESULTS_REGEX);
            }
            int tRight = -1;
            int tWrong = -1;
            int tIgnored = -1;
            int tExc = -1;
            int aRight = -1;
            int aWrong = -1;
            int aIgnored = -1;
            int aExc = -1;

            boolean found = m.find();

            if (!found) {
                System.out.println("Unable to find tests result string matching " + FITNESSE_RESULTS_REGEX);
            } else {
                tRight = verifier.toInt("Tests right", m.group(1));
                tWrong = verifier.toInt("Tests wrong", m.group(2));
                tIgnored = verifier.toInt("Tests ignored", m.group(3));
                tExc = verifier.toInt("Tests exceptions", m.group(4));
                aRight = verifier.toInt("Assertions right", m.group(5));
                aWrong = verifier.toInt("Assertions wrong", m.group(6));
                aIgnored = verifier.toInt("Assertions ignored", m.group(7));
                aExc = verifier.toInt("Assertions exceptions", m.group(8));

                System.out.println("Results:");
                System.out.println("\tTests right:" + tRight);
                System.out.println("\tTests wrong:" + tWrong);
                System.out.println("\tTests ignored:" + tIgnored);
                System.out.println("\tTests exceptions:" + tExc);
                System.out.println("\tAssertions right:" + aRight);
                System.out.println("\tAssertions wrong:" + aWrong);
                System.out.println("\tAssertions ignored:" + aIgnored);
                System.out.println("\tAssertions exceptions:" + aExc);
            }
            System.exit(tWrong + tExc);
        } catch (Exception e) {
            System.out.println("Exception when processing file " + args[0]);
            e.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    private int toInt(String text, String num) {
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            System.out.println(text + " is not a number: " + num);
            return -1;
        }
    }

    private String readFile(String fileLocation) throws Exception {
        FileInputStream fis = null;
        try {
            File f = new File(fileLocation);
            if (!f.exists())
                throw new RuntimeException("File '" + f.getAbsolutePath() + "' doesn't exist");
            fis = new FileInputStream(f);
            String content = toString(fis);
            return content;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private String toString(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        byte[] buff = new byte[1024];
        while (true) {
            int r = is.read(buff);
            if (r == -1)
                break;
            sb.append(new String(buff, 0, r));
        }
        return sb.toString();
    }

}
