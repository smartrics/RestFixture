package smartrics.rest.fitnesse.fixture.support;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import smartrics.rest.fitnesse.fixture.utils.PDFUtils;

public class PDFBodyTypeAdapter extends BodyTypeAdapter {

    private String pdfResponseCharset = "";

    @Override
    public String toXmlString(String content) {
        return "<content>" + content + "</content>";
    }

    @Override
    public Object parse(String s) throws Exception {

        if (s != null && s.matches(".*\\.pdf")) {
            return s;
        }
        return super.parse(s);
    }

    public void setPDFResponseCharset(String pdfResponseCharset) {
        this.pdfResponseCharset = pdfResponseCharset;
    }

    @Override
    public boolean equals(Object a, Object b) {
        if (a != null && b != null) {
            String filePath = (String) a;
            if (filePath.matches(".*\\.pdf")) {

                String expectedPdfContent = PDFUtils.getTxt(filePath);
                String actualPdfString = (String) b;
                String actualPdfContent = PDFUtils.getTxt(new ByteArrayInputStream(actualPdfString.getBytes(Charset.forName(this.pdfResponseCharset))));

                this.set(actualPdfContent);// set actual content.

                return expectedPdfContent.equals(actualPdfContent);
            }

        }
        return super.equals(a, b);
    }
}
