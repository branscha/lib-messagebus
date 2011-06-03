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

import com.sdicons.prop.ClientProperties;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;
import java.beans.PropertyChangeEvent;

public class ClientPropertiesTest
{
    ClientProperties props;
    int counter;

    @Test
    public void testClientProperties()
    {
        MessageBus lBus = new MessageBus();
        props = new ClientProperties(false, this, lBus);
        lBus.register(this);
        counter = 0;

        props.setValue("oele", "zebra");
        props.setValue("boele", 5);
        props.setValue("makkis", new Rectangle(5, 3));

        Assert.assertEquals(props.getValue("oele"), "zebra");
        Assert.assertEquals(props.getValue("boele"), 5);
        Assert.assertEquals(props.getValue("makkis"), new Rectangle(5, 3));
        Assert.assertEquals(props.getValue("voele"), null);
        Assert.assertEquals(counter, 3);
    }

    @Notify
    public void callback(PropertyChangeEvent aEvent)
    {
        counter++;
    }
}
