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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;

public class FitnesseResultSanitiser {

    private static String FITNESSE_CSS_TAG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"/files/css/fitnesse.css\" media=\"screen\"/>";
    private static String FITNESSE_JS_TAG = "<script src=\"/files/javascript/fitnesse.js\" type=\"text/javascript\"></script>";
    private static String FITNESSE_PRINT_CSS_TAG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"/files/css/fitnesse_print.css\" media=\"print\"/>";

    private String alreadyImported;

    public static void main(String[] args) {
        try {
            if (args.length != 2)
                throw new RuntimeException("You need to pass the file to sanitise and the location of the FitNesseRoot wiki root");
            FitnesseResultSanitiser sanitiser = new FitnesseResultSanitiser();
            String content = sanitiser.readFile(args[0]);
            content = sanitiser.removeNonHtmlGarbage(content);
            content = sanitiser.removeLinksToExternalPages(content);
            String fitnesseRootLocation = args[1];
            content = sanitiser.injectCssAndJs(fitnesseRootLocation, content);
            content = sanitiser.embedPictures(fitnesseRootLocation, content);
            String newName = sanitiser.generateNewName(args[0]);
            sanitiser.writeFile(newName, content);
            System.out.println("Input file has been sanitised. Result is: " + newName);
        } catch (Exception e) {
            System.out.println("Exception when sanitising file " + args[0]);
            e.printStackTrace(System.out);
        }
    }

    private String removeLinksToExternalPages(String content) {
        content = content.replace("<a style=\"font-size:small;\" href=\"RestFixtureTests?pageHistory\"> [history]</a>", "");
        int pos = content.indexOf("<div id=\"execution-status\">");
        if (pos >= 0) {
            int endPos = content.indexOf("</div>", pos) + 6;
            content = content.substring(0, pos - 1) + content.substring(endPos + 1);
        }
        return content;
    }

    private String generateNewName(String name) {
        int pos = name.lastIndexOf('.');
        final String suffix = "_sanitised";
        if (pos == -1)
            return name + suffix;
        String p0 = name.substring(0, pos - 1);
        String p1 = name.substring(pos);
        return p0 + suffix + p1;
    }

    private String writeFile(String name, String content) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(name));
            fos.write(content.getBytes());
            fos.flush();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return name;

    }

    private String injectCssAndJs(String fitnesseRootLocation, String content) throws Exception {
        String replContent = doReplacement(fitnesseRootLocation, FITNESSE_CSS_TAG, "<style>\n", "\n</style>\n", content);
        replContent = doReplacement(fitnesseRootLocation, FITNESSE_PRINT_CSS_TAG, "<style>\n", "\n</style>\n", replContent);
        replContent = doReplacement(fitnesseRootLocation, FITNESSE_JS_TAG, "\n<script language='JavaScript' type='text/javascript'>\n", "\n</script>\n", replContent);
        return replContent;
    }

    private String doReplacement(String filesRootLoc, String link, String pre, String post, String content) throws Exception {
        int len = link.length();
        int pos = content.indexOf(link);
        if (pos < 0)
            return content;
        String part1 = content.substring(0, pos - 1);
        String part2 = content.substring(pos + len);

        String pattern = "href=\"";
        int sPos = link.indexOf(pattern);
        if (sPos == -1) {
            pattern = "src=\"";
            sPos = link.indexOf(pattern);
        }
        if (sPos == -1) {
            return content;
        }
        int ePos = link.indexOf("\"", sPos + 1 + pattern.length());
        String resName = link.substring(sPos + pattern.length(), ePos);
        String fileName = filesRootLoc + resName;

        String resContent = readFile(fileName);

        if (resContent.indexOf("@import url") >= 0) {
            // looks like there's a css with some import to be resolved
            resContent = resolveImport(filesRootLoc, resContent);
        }

        return part1 + pre + resContent + post + part2;

    }

    private String resolveImport(String rootLoc, String content) throws Exception {
        String newContent = "";
        final String pattern = "@import url";
        int sPos = content.indexOf(pattern);
        int ePos = content.indexOf("\n", sPos);
        if (ePos == -1)
            ePos = content.length();
        String importString = content.substring(sPos, ePos);
        int iPos = importString.indexOf("\"");
        int lPos = importString.indexOf("\"", iPos + 1);
        String imported = importString.substring(iPos + 1, lPos);
        if (alreadyImported == null || !imported.equals(alreadyImported)) {
            String importedContent = readFile(rootLoc + imported);
            String part1 = content.substring(0, sPos);
            String part2 = content.substring(ePos, content.length());
            newContent = part1 + importedContent + part2 + "\n";
            return newContent;
        } else {
            return content;
        }
    }

    private String removeNonHtmlGarbage(String content) {
        int pos = content.indexOf("<html>");
        int endPos = content.indexOf("</html>") + 7;
        content = content.substring(pos, endPos);
        return content;
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

    private String embedPictures(String filesRootLoc, String content) {
        boolean foundAll = false;
        int posEnd = 0;
        while (!foundAll) {
            int pos = content.indexOf("<img", posEnd);
            foundAll = pos < 0;
            if (!foundAll) {
                posEnd = content.indexOf("/>", pos) + 1;
                String pre = content.substring(0, pos - 1);
                String tag = content.substring(pos, posEnd).trim();
                String post = content.substring(posEnd + 1);
                if (post.startsWith(" ")) {
                    post = post.substring(1);
                }
                content = pre + generateTagWithEmbeddedImage(filesRootLoc, tag) + post;
            }
        }
        // content =
        // generateEncodedImgFromFileName("var collapsableOpenImg = \"/files/images/collapsableOpen.gif\"",
        // filesRootLoc, content);
        // content =
        // generateEncodedImgFromFileName("var collapsableClosedImg = \"/files/images/collapsableClosed.gif\"",
        // filesRootLoc, content);

        return content;
    }

    @SuppressWarnings("unused")
    private String generateEncodedImgFromFileName(String matchingString, String filesRootLoc, String content) {
        int pos = content.indexOf(matchingString);
        pos = content.indexOf("\"", pos) + 1;
        int posEnd = content.indexOf('"', pos);
        String pre = content.substring(0, pos - 1);
        String fileName = content.substring(pos, posEnd);
        String post = content.substring(posEnd + 1);
        String enc = encodeBase64(filesRootLoc + fileName);
        content = pre + enc + post;
        return content;
    }

    private String generateTagWithEmbeddedImage(String filesRootLoc, String imgTag) {
        int pos = imgTag.indexOf("src=\"");
        if (pos == -1) {
            pos = imgTag.indexOf("src='");
        }
        pos = pos + 5;
        int posEnd = imgTag.indexOf("\"", pos + 1);
        if (posEnd == -1) {
            posEnd = imgTag.indexOf("'", pos + 1);
        }
        String file = imgTag.substring(pos, posEnd);
        String fileName = filesRootLoc + file;
        String encoded = encodeBase64(fileName);
        if (null != encoded)
            return encoded;
        return imgTag;
    }

    private String encodeBase64(String fileName) {
        int extPos = fileName.lastIndexOf('.') + 1;
        String ext = fileName.substring(extPos);
        try {
            String content = readFile(fileName);
            String encoded = new String(Base64.encodeBase64(content.getBytes()));
            return "<img src=\"data:image/" + ext + ";base64," + encoded + "\"/>";
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

}
