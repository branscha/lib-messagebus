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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

public class MemoryTest
{
    private static final int NR_LISTENERS = 10000;

    public static class Listener
    {
        public static int counter = 0;

        @Notify
        public void callback(EventObject aEvent)
        {
            counter++;
        }
    }

    @Test
    public void leaksTest()
    {
        // Keep the listeners in memory, pervent collection here.
        final List<Listener> lStash = new LinkedList<Listener>();
        final MessageBus lBus = new MessageBus();
        // Register a large number of listeners.
        for(int i = 0; i < NR_LISTENERS; i++)
        {
            Listener lListener = new Listener();
            lStash.add(lListener);
            lBus.register(lListener);
        }

        // Try out the working of the listeners.
        lBus.publish(new EventObject(this));
        Assert.assertEquals(lBus.getNrSubscribers(), NR_LISTENERS);
        Assert.assertEquals(Listener.counter, NR_LISTENERS);

        // By clearing the cache, we allow the listeners to be
        // garbage collected.
        lStash.clear();
        // Explicitly start garbage collection.
        System.gc();
        // Event processing should still work, even if some of
        // the listeners have disappeared.
        Listener.counter = 0;
        lBus.publish(new EventObject(this));
        Assert.assertTrue(Listener.counter < NR_LISTENERS);
        // All listeners should have been garbage collected by now
        // so this proves that the messagebus will not be
        // the source of memory leaks.
        Assert.assertEquals(0, lBus.getNrSubscribers());
    }
}
