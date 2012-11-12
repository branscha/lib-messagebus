/*
 * Library "lib-messagebus".
 * Copyright (c) 2011 Bruno Ranschaert, SDI-Consulting BVBA.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sdicons.bus;

import java.beans.PropertyChangeEvent;

import org.junit.Assert;
import org.junit.Test;

import com.sdicons.prop.IndexedProperty;

public class IndexedPropertyTest
{

    private IndexedProperty<String> prop;
    private int counter;

    @Test
    public void testIndexedProperty()
    {
        MessageBus lBus = new MessageBus();
        prop = new IndexedProperty<String>("farm", true, this, lBus);
        lBus.register(this);

        counter = 0;
        prop.setValue(2, "chicken");
        prop.setValue(3, "cow");
        prop.setValue(5, "pig");
        prop.setValue(7, "goat");
        prop.setValue(9, "cat");

        String[] lArr = prop.getValues(String.class);
        Assert.assertEquals(10, lArr.length);
        Assert.assertEquals(null, lArr[0]);
        Assert.assertEquals(null, lArr[1]);
        Assert.assertEquals("chicken", lArr[2]);
        Assert.assertEquals("cow", lArr[3]);
        Assert.assertEquals(null, lArr[4]);
        Assert.assertEquals("pig", lArr[5]);
        Assert.assertEquals(null, lArr[6]);
        Assert.assertEquals("goat", lArr[7]);
        Assert.assertEquals(null, lArr[8]);
        Assert.assertEquals("cat", lArr[9]);
        Assert.assertEquals(5, counter);

        counter = 0;
        prop.setValue(new String[]{"sheep", "dog", "chicken"});
        lArr = prop.getValues(String.class);
        Assert.assertEquals(3, lArr.length);
        Assert.assertEquals("sheep", lArr[0]);
        Assert.assertEquals("dog", lArr[1]);
        Assert.assertEquals("chicken", lArr[2]);
        Assert.assertEquals(6, counter);
    }

    @Notify
    public void callback(PropertyChangeEvent aEvent)
    {
        counter++;
    }
}
