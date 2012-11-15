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

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adam Roberts (aroberts@alum.rit.edu)
 */
public class FileBodyTypeAdapterTest
{

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public final void testEqualsObjectObjectNullExp()
    {
        // Setup
        Object exp = null;
        Object act = new byte[]{};

        FileBodyTypeAdapter adapter = new FileBodyTypeAdapter();

        // Test
        boolean fileSaved = adapter.equals(exp, act);

        // Validate
        assertFalse("Should always fail to save if an argument is null", fileSaved);
    }

    @Test
    public final void testEqualsObjectObjectNullAct()
    {
        // Setup
        Object exp = "FileName.txt";
        Object act = null;

        FileBodyTypeAdapter adapter = new FileBodyTypeAdapter();

        // Test
        boolean fileSaved = adapter.equals(exp, act);

        // Validate
        assertFalse("Should always fail to save if an argument is null", fileSaved);
    }

    @Test
    public final void testEqualsObjectObjectActNotByteArray()
    {
        // Setup
        Object exp = "FileName.txt";
        Object act = "This should be a byte array";

        FileBodyTypeAdapter adapter = new FileBodyTypeAdapter();

        // Test
        boolean fileSaved = adapter.equals(exp, act);

        // Validate
        assertFalse("Can only save binary data that is in a byte array", fileSaved);
    }

    @Test
    public final void testEqualsObjectObjectShouldSucceed() throws Exception
    {
        // Setup
        final String expectedContents = "Expected File Contents";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(expectedContents.getBytes());

        final Object exp = "FileName.txt";
        final Object act = output.toByteArray();

        final File testSave = new File(exp.toString());
        testSave.delete();
        assertFalse("Failed to clear test file", testSave.exists());

        FileBodyTypeAdapter adapter = new FileBodyTypeAdapter();

        // Test
        boolean fileSaved = adapter.equals(exp, act);

        // Validate
        assertTrue("Should have saved the file", fileSaved);
        assertTrue("File failed to save", testSave.exists());

        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(testSave));
            assertEquals("Incorrect File Contents", expectedContents, reader.readLine());
            assertNull("Extra data in file", reader.readLine());
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
            testSave.delete();
        }
    }

    @Test
    public final void testToXmlString()
    {
        // Setup
        FileBodyTypeAdapter adapter = new FileBodyTypeAdapter();

        // Test & Validate
        assertEquals("Binary Responses should never be converted to xml", "", adapter.toXmlString("Test"));
    }

    @Test
    public final void testToStringObject()
    {
        // Setup
        FileBodyTypeAdapter adapter = new FileBodyTypeAdapter();

        // Test & Validate
        assertEquals("Binary Responses should never be converted to a string", "", adapter.toString("Test"));
    }

    @Test
    public final void testParseString() throws Exception
    {
        // Setup
        FileBodyTypeAdapter adapter = new FileBodyTypeAdapter();

        // Test & Validate
        assertEquals("Binary Responses should never be parsed", "", adapter.parse("Test"));
    }

    @Test
    public final void testIsBinaryResponse()
    {
        // Setup
        FileBodyTypeAdapter adapter = new FileBodyTypeAdapter();

        // Test & Validate
        assertTrue("A file is by definition a binary response", adapter.isBinaryResponse());
    }

}
