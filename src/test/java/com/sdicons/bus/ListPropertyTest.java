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

import com.sdicons.prop.ListProperty;
import com.sdicons.prop.ListPropertyChangeEvent;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ListPropertyTest
{

    private ListProperty<String> prop;
    private int counter;

    @Test
    public void testIndexedProperty()
    {
        MessageBus lBus = new MessageBus();
        prop = new ListProperty<String>("farm", true, this, lBus);
        lBus.register(this);

        counter = 0;
        prop.addValue("chicken");
        prop.addValue("cow");
        prop.addValue("pig");
        prop.addValue("goat");
        prop.addValue("cat");

        Assert.assertEquals(prop.getSize(), 5);
        Assert.assertEquals(prop.getValue(0), "chicken");
        Assert.assertEquals(prop.getValue(1), "cow");
        Assert.assertEquals(prop.getValue(2), "pig");
        Assert.assertEquals(prop.getValue(3), "goat");
        Assert.assertEquals(prop.getValue(4), "cat");
        Assert.assertEquals(counter, 5);
        
        counter = 0;
        prop.removeValue("goat");
        prop.removeValue("cow");
        prop.addValue(1, "rooster");
        prop.removeValue(3);
        
        Assert.assertEquals(prop.getSize(), 3);
        Assert.assertEquals(prop.getValue(0), "chicken");
        Assert.assertEquals(prop.getValue(1), "rooster");
        Assert.assertEquals(prop.getValue(2), "pig");
        Assert.assertEquals(counter, 4);
    }

    @Notify
    public void callback(ListPropertyChangeEvent aEvent)
    {
        counter++;
    }
}
