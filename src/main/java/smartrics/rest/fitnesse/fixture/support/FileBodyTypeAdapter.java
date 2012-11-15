/*  Copyright 2012 PROS Pricing (www.prospricing.com)
 *
 *  This file is donated to RestFixture.
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
 *  If you want to contact the original author of RestFixture please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */

package smartrics.rest.fitnesse.fixture.support;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This Body adapter will take a binary response and save it to the file specified
 *
 * @author Adam Roberts (aroberts@alum.rit.edu)
 */
public class FileBodyTypeAdapter extends BodyTypeAdapter {

    @Override
    public boolean equals(Object exp, Object act) {
        if (exp == null || act == null) {
            return false;
        }
        if (!(act instanceof byte[]))
        {
            return false;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])act);
        File file = new File(exp.toString());
        try {
            OutputStream output = null;
            try {
                output = new FileOutputStream(file);
                int data = -1;
                while((data = inputStream.read()) != -1)
                {
                    output.write(data);
                    output.flush();
                }
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return file.exists() && (file.length() == ((byte[])act).length);
    }

    @Override
    public Object parse(String s) throws Exception
    {
        return "";
    }

    @Override
    public String toString(Object content) {
        return "";
    }

    @Override
    public String toXmlString(String content) {
        return "";
    }

    @Override
    public boolean isBinaryResponse()
    {
        return true;
    }

}
