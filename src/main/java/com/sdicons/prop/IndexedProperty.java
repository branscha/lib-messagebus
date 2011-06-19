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

import com.sdicons.bus.MessageBus;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Implementation support for indexed properties.
 * @see Property
 *
 */
public class IndexedProperty<E>
implements Serializable
{
	private String name;

	private Object source;

	transient private MessageBus bus;

	private Map<Integer, E> values;

	private boolean constrained;

	public IndexedProperty(String aName, boolean aConstrained, Object aSource, MessageBus aBus)
	{
		this.name = aName;
		this.source = aSource;
		this.bus = aBus;
		this.values = new HashMap<Integer, E>();
		this.constrained = aConstrained;
	}

	public void setBus(MessageBus aBus)
	{
		this.bus = aBus;
	}

	public void setValue(int aIndex, E aValue)
	{
		if (aIndex < 0)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		final E lOldValue = this.values.get(aIndex);

		// Only take action when the new value is different from the old value.
		// If nothing changes nobody should be asked or notified about the change.
		if (((aValue == null) && (aValue != lOldValue)) || ((aValue != null) && (!aValue.equals(lOldValue))))
		{
			if (this.constrained && (this.bus != null))
			{
				final VetoableIndexedPropertyChangeEvent lEvent = new VetoableIndexedPropertyChangeEvent(this.source, this.name, lOldValue, aValue, aIndex);
				this.bus.publish(lEvent);
			}

			if (aValue == null)
			{
				this.values.remove(aIndex);
			}
			else
			{
				this.values.put(aIndex, aValue);
			}

			if (this.bus != null)
			{
				PropertyChangeEvent lEvent = new IndexedPropertyChangeEvent(this.source, this.name, lOldValue, aValue, aIndex);
				this.bus.publish(lEvent);
			}
		}
	}

	public E getValue(int aIndex)
	{
		return this.values.get(aIndex);
	}

	public void setValue(E[] aValues)
	{
		for (int i = 0; i < aValues.length; i++)
		{
			setValue(i, aValues[i]);
		}

		Set<Integer> lIndices = new HashSet<Integer>();
		lIndices.addAll(this.values.keySet());
		for (int i : lIndices)
		{
			if (i >= aValues.length)
			{
				setValue(i, null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public E[] getValues(Class aType)
	{
		// Find the maximum index.
		int max = -1;
		for (int i : this.values.keySet())
		{
			if (i > max)
			{
				max = i;
			}
		}

		Object[] lResult = (Object[])Array.newInstance(aType, max + 1);
		Arrays.fill(lResult, null);

		for (int i : this.values.keySet())
		{
			lResult[i] = this.values.get(i);
		}

		return (E[])lResult;
	}
}
