/*
 * Copyright (c) 2016 Farooq Khan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.jsondb.tests;

import static org.junit.Assert.*;

import io.jsondb.InvalidJsonDbApiUsageException;
import io.jsondb.JsonDBTemplate;
import io.jsondb.Util;
import io.jsondb.tests.model.PojoForPrivateGetIdTest;
import io.jsondb.tests.model.PojoForPrivateSetIdTest;
import java.io.File;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * A unit test class for methods in Util class
 * @version 1.0 27-Oct-2016
 */
public class UtilTests {
    private String dbFilesLocation = "src/test/resources/dbfiles/utilTests";
    private File dbFilesFolder = new File(dbFilesLocation);

    private JsonDBTemplate jsonDBTemplate = null;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        dbFilesFolder.mkdir();
        jsonDBTemplate = new JsonDBTemplate(dbFilesLocation, "io.jsondb.tests.model");
    }

    @After
    public void tearDown() throws Exception {
        Util.delete(dbFilesFolder);
    }

    @Test
    public void test_determineCollectionName() {
        expectedException.expect(InvalidJsonDbApiUsageException.class);
        expectedException.expectMessage("No class parameter provided, entity collection can't be determined");
        jsonDBTemplate.getCollectionName(null);
    }

    @Test
    public void test_getIdForEntity_1() {
        expectedException.expect(InvalidJsonDbApiUsageException.class);
        expectedException.expectMessage("Failed to invoke getter method for a idAnnotated field due to permissions");

        jsonDBTemplate.createCollection(PojoForPrivateGetIdTest.class);

        PojoForPrivateGetIdTest s = new PojoForPrivateGetIdTest("001");
        jsonDBTemplate.insert(s);
    }

    @Test
    public void test_setIdForEntity_1() {
        expectedException.expect(InvalidJsonDbApiUsageException.class);
        expectedException.expectMessage("Failed to invoke setter method for a idAnnotated field due to permissions");

        jsonDBTemplate.createCollection(PojoForPrivateSetIdTest.class);

        PojoForPrivateSetIdTest s = new PojoForPrivateSetIdTest();
        jsonDBTemplate.insert(s);
    }

    @Test
    public void test_IllegalSliceText_1() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal slice argument, expected format is i:j:k");

        Util.getSliceIndexes("1:2:3:4", 2);
    }

    @Test
    public void test_IllegalSliceText_2() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal slice argument, expected format is i:j:k");

        Util.getSliceIndexes("1:2::4", 2);
    }

    @Test
    public void test_IllegalSliceText_3() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal slice argument, expected format is i:j:k");

        Util.getSliceIndexes(":::4", 2);
    }

    @Test
    public void test_IllegalSliceText_4() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal slice argument, k cannot be zero");

        Util.getSliceIndexes("1:2:0", 2);
    }

    @Test
    public void test_getSliceIndexes_Nulls() {
        List<Integer> indexes = null;

        indexes = Util.getSliceIndexes(null, 3);
        assertNull(indexes);

        indexes = Util.getSliceIndexes("", 5);
        assertNull(indexes);

        indexes = Util.getSliceIndexes(":", 5);
        assertNull(indexes);

        indexes = Util.getSliceIndexes("::", 5);
        assertNull(indexes);

        indexes = Util.getSliceIndexes("2", 0);
        assertNull(indexes);
    }

    @Test
    public void test_getSliceIndexes_PositiveDefaults() {
        List<Integer> indexes = null;

        indexes = Util.getSliceIndexes("::2", 5);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {0, 2, 4}, indexes.toArray());

        indexes = Util.getSliceIndexes(":5", 7);
        assertNotNull(indexes);
        assertEquals(5, indexes.size());
        assertArrayEquals(new Integer[] {0, 1, 2, 3, 4}, indexes.toArray());

        indexes = Util.getSliceIndexes(":5:", 7);
        assertNotNull(indexes);
        assertEquals(5, indexes.size());
        assertArrayEquals(new Integer[] {0, 1, 2, 3, 4}, indexes.toArray());

        indexes = Util.getSliceIndexes(":5:2", 7);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {0, 2, 4}, indexes.toArray());

        indexes = Util.getSliceIndexes("1", 5);
        assertNotNull(indexes);
        assertEquals(4, indexes.size());
        assertArrayEquals(new Integer[] {1, 2, 3, 4}, indexes.toArray());

        indexes = Util.getSliceIndexes("2:", 5);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {2, 3, 4}, indexes.toArray());

        indexes = Util.getSliceIndexes("2::", 5);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {2, 3, 4}, indexes.toArray());

        indexes = Util.getSliceIndexes(":2", 5);
        assertNotNull(indexes);
        assertEquals(2, indexes.size());
        assertArrayEquals(new Integer[] {0, 1}, indexes.toArray());

        indexes = Util.getSliceIndexes("2::2", 7);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {2, 4, 6}, indexes.toArray());

        indexes = Util.getSliceIndexes("2:5:", 7);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {2, 3, 4}, indexes.toArray());
    }

    @Test
    public void test_getSliceIndexes_NegativeDefaults() {
        List<Integer> indexes = null;

        indexes = Util.getSliceIndexes("::-1", 5);
        assertNotNull(indexes);
        assertEquals(5, indexes.size());
        assertArrayEquals(new Integer[] {4, 3, 2, 1, 0}, indexes.toArray());

        indexes = Util.getSliceIndexes("::-2", 5);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {4, 2, 0}, indexes.toArray());

        indexes = Util.getSliceIndexes("-2:10", 10);
        assertNotNull(indexes);
        assertEquals(2, indexes.size());
        assertArrayEquals(new Integer[] {8, 9}, indexes.toArray());

        indexes = Util.getSliceIndexes("-1", 5);
        assertNotNull(indexes);
        assertEquals(1, indexes.size());
        assertArrayEquals(new Integer[] {4}, indexes.toArray());
    }

    @Test
    public void test_getSliceIndexes_PositiveSlice() {
        List<Integer> indexes = null;

        indexes = Util.getSliceIndexes("0:7:3", 7);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {0, 3, 6}, indexes.toArray());

        indexes = Util.getSliceIndexes("0:7:2", 5);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {0, 2, 4}, indexes.toArray());

        indexes = Util.getSliceIndexes("4:3:1", 7);
        assertNotNull(indexes);
        assertEquals(0, indexes.size());
    }

    @Test
    public void test_getSliceIndexes_NegativeSlice() {
        List<Integer> indexes = null;

        indexes = Util.getSliceIndexes("-1:-4:-1", 6);
        assertNotNull(indexes);
        assertEquals(3, indexes.size());
        assertArrayEquals(new Integer[] {5, 4, 3}, indexes.toArray());

        indexes = Util.getSliceIndexes("-1:-5:-2", 6);
        assertNotNull(indexes);
        assertEquals(2, indexes.size());
        assertArrayEquals(new Integer[] {5, 3}, indexes.toArray());

        indexes = Util.getSliceIndexes("-3:3:-1", 10);
        assertNotNull(indexes);
        assertEquals(4, indexes.size());
        assertArrayEquals(new Integer[] {7, 6, 5, 4}, indexes.toArray());
    }
}
