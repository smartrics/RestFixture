package smartrics.rest.test.fitnesse.fixture;

import java.io.File;
import java.io.FileWriter;

import fit.ActionFixture;

public class FileUtilFixture extends ActionFixture {
	private String fileContents;

	public void fileContents(String fileContents) {
		this.fileContents = fileContents;
	}
	
	public void createTempFile(String filename) throws Exception {
		File f = new File(filename);
		FileWriter fw = new FileWriter(f);
		fw.write(fileContents);
		fw.close();
		f.deleteOnExit();
	}
}
