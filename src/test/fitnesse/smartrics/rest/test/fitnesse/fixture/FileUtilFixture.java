package smartrics.rest.test.fitnesse.fixture;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fit.ActionFixture;

public class FileUtilFixture extends ActionFixture {
    private static Log LOG = LogFactory.getLog(FileUtilFixture.class);

    private String fileContents;
    private String fileName;

    public void content(String fileContents) {
        this.fileContents = fileContents;
    }

    public void name(String fileName) {
        this.fileName = fileName;
    }
	
    public boolean create() throws Exception {
        File f = new File(fileName);
        int pos = f.getPath().indexOf(f.getName());
        new File(f.getPath().substring(0, pos)).mkdirs();
		FileWriter fw = new FileWriter(f);
		fw.write(fileContents);
		fw.close();
        return true;
	}

    public boolean delete() throws Exception {
        new File(fileName).delete();
        return true;
    }

    public boolean exists() throws Exception {
        return new File(fileName).exists();
    }
}
