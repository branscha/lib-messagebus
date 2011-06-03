/*
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

import com.sdicons.prop.IndexedProperty;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.beans.PropertyChangeEvent;

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
        Assert.assertEquals(lArr.length, 10);
        Assert.assertEquals(lArr[0], null);
        Assert.assertEquals(lArr[1], null);
        Assert.assertEquals(lArr[2], "chicken");
        Assert.assertEquals(lArr[3], "cow");
        Assert.assertEquals(lArr[4], null);
        Assert.assertEquals(lArr[5], "pig");
        Assert.assertEquals(lArr[6], null);
        Assert.assertEquals(lArr[7], "goat");
        Assert.assertEquals(lArr[8], null);
        Assert.assertEquals(lArr[9], "cat");
        Assert.assertEquals(counter, 5);

        counter = 0;
        prop.setValue(new String[]{"sheep", "dog", "chicken"});
        lArr = prop.getValues(String.class);
        Assert.assertEquals(lArr.length, 3);
        Assert.assertEquals(lArr[0], "sheep");
        Assert.assertEquals(lArr[1], "dog");
        Assert.assertEquals(lArr[2], "chicken");
        Assert.assertEquals(counter, 6);
    }

    @Notify
    public void callback(PropertyChangeEvent aEvent)
    {
        counter++;
    }
}
