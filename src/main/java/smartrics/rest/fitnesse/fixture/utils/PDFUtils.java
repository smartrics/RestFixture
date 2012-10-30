package smartrics.rest.fitnesse.fixture.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public final class PDFUtils {

    public static String getTxt(String pdfFilePath) {

        File f = new File(pdfFilePath);

        String ts = "";
        try {
            PDDocument pdfdocument = PDDocument.load(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText(pdfdocument, writer);
            pdfdocument.close();
            out.close();
            writer.close();
            byte[] contents = out.toByteArray();
            ts = new String(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ts;
    }

    public static String getTxt(InputStream is) {

        String ts = "";
        try {
            PDDocument pdfdocument = PDDocument.load(is);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText(pdfdocument, writer);
            pdfdocument.close();
            out.close();
            writer.close();
            byte[] contents = out.toByteArray();
            ts = new String(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ts;
    }
}
