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

package com.sdicons.prop;

import java.util.EventObject;

/**
 * <p> VetoablePropertyChangeEvent was defined, in Swing veto notifications use the normal PropertyChangeEvent but with
 * another listener interface. Since we cannot differentiate on the interface, the event consumer must be able
 * to see the difference between a veto event and a change event, that is why I added this new type of event, our
 * property implementations will use this. The consumer can call PropertyVetoException to veto the change.
 *
 */
public class VetoablePropertyChangeEvent extends EventObject
{
	private String name;
	private Object oldValue;
	private Object newValue;

	public VetoablePropertyChangeEvent(Object aSource, String aName, Object aOldValue, Object aNewValue)
	{
		super(aSource);
		this.name = aName;
		this.oldValue = aOldValue;
		this.newValue = aNewValue;
	}

	public String getName()
	{
		return this.name;
	}

	public Object getOldValue()
	{
		return this.oldValue;
	}

	public Object getNewValue()
	{
		return this.newValue;
	}
}
