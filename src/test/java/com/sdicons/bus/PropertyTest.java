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

import com.sdicons.prop.Property;
import com.sdicons.prop.PropertyVetoException;
import com.sdicons.prop.VetoablePropertyChangeEvent;

public class PropertyTest
{
    private Property<Integer> prop;
    private int counter;

    @Test
    public void testProperty()
    {
        MessageBus lBus = new MessageBus();
        prop = new Property<Integer>("oele", true, this, lBus, 13);
        lBus.register(this);
        counter = 0;

        try
        {
            prop.setValue(-100);
            Assert.fail();
        }
        catch(PropertyVetoException e)
        {
            // Should arrive here.
        }

        Assert.assertTrue(prop.getValue() >= 0);

        prop.setValue(101);
        Assert.assertEquals( new Integer(101), prop.getValue());
        Assert.assertEquals(1, counter);
    }

    @Notify
    public void negativeStopper(VetoablePropertyChangeEvent aEvent)
    {
       if(((Integer) aEvent.getNewValue()) < 0)
       {
           System.out.println("Block value " + aEvent.getNewValue());
           throw new PropertyVetoException("We dont allow negative values!");
       }
        System.out.println("Allow value " + aEvent.getNewValue());
    }

    @Notify
    public void callback(PropertyChangeEvent aEvent)
    {
        counter++;
    }
}
