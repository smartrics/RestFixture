/*  Copyright 2015 Fabrizio Cannizzo
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
package smartrics.rest.test.fitnesse.fixture;

import java.io.File;
import java.io.FileWriter;

import fit.ActionFixture;

/**
 * Action fixture to support file upload CATs in RestFixture.
 * 
 * @author fabrizio
 * 
 */
public class FileUtilFixture extends ActionFixture {

    private String fileContents;

    private String fileName;

    public FileUtilFixture() throws Exception {
        super();
    }

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
