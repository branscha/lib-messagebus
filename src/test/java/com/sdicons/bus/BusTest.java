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

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;

import org.junit.Assert;
import org.junit.Test;

public class BusTest
{
	private int counter = 0;

	@Test
	public void Oele()
	{
		MessageBus lBus = new MessageBus();
		lBus.register(this);
		PropertyChangeEvent lEvent = new PropertyChangeEvent(this, "oele", 0, 1);

		this.counter = 0;
		lBus.publish(lEvent);
		Assert.assertEquals(4, this.counter);

		lBus.unregister(this);
		this.counter = 0;
		lBus.publish(lEvent);
		Assert.assertEquals(0, this.counter);
	}

	@Notify
	public void callback1(EventObject aEvent)
	{
		// Should be called.
		this.counter++;
	}

	@Notify(sourceType = BusTest.class)
	public void callback2(EventObject aEvent)
	{
		// Should be called.
		this.counter++;
	}

	@Notify(allowNullSource = false)
	public void callback3(EventObject aEvent)
	{
		// Should be called.
		this.counter++;
	}

	@Notify(sourceType = String.class)
	public void callback4(EventObject aEvent)
	{
		// Should NOT be called.
		// The source type is not compatible, the event can never be thrown from a String instance.
		this.counter++;
		Assert.fail();
	}

	@Notify
	public void callback4(IndexedPropertyChangeEvent aEvent)
	{
		// Should NOT be called.
		// The test event is a PropertyChangeEvent and not the Indexed variant.
		this.counter++;
		Assert.fail();
	}
	
	@Notify(propertyName="oele")
	public void callback5(EventObject aEvent)
	{
		// Should be called.
		this.counter++;
	}
	
	@Notify(propertyName="xxxx")
	public void callback6(EventObject aEvent)
	{
		// Should NOT be called.
		// The name of the property was specified and it does not correspond 
		// with the property name in the event.
		this.counter++;
		Assert.fail();
	}
}
