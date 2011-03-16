package smartrics.rest.test.fitnesse.fixture;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fit.ActionFixture;

public class FileUtilFixture extends ActionFixture {
    private static Log LOG = LogFactory.getLog(FileUtilFixture.class);

	private String fileContents;

	public void fileContents(String fileContents) {
		this.fileContents = fileContents;
	}
	
	public void createTempFile(String filename) throws Exception {
		File f = new File(filename);
        int pos = f.getPath().indexOf(f.getName());
        new File(f.getPath().substring(0, pos)).mkdirs();
		FileWriter fw = new FileWriter(f);
		fw.write(fileContents);
		fw.close();
	}

    public void deleteTempFile(String filename) throws Exception {
        new File(filename).delete();
    }
}
