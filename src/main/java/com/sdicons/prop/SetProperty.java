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

import java.io.Serializable;
import java.util.*;

/**
 * Implementation support for properties with set semantics.
 * 
 * @see Property
 * @see java.util.Set
 *
 */
public class SetProperty<E>
implements Iterable<E>, Serializable
{
	private String name;
	private Object source;
	transient private MessageBus bus;
	private Set<E> values;
	private boolean constrained;

	private class IteratorWrapper implements Iterator
	{
		private Iterator target;

		private Object lastSeen;

		private IteratorWrapper(Iterator aTarget)
		{
			target = aTarget;
		}

		public boolean hasNext()
		{
			return target.hasNext();
		}

		public Object next()
		{
			lastSeen = target.next();
			return lastSeen;
		}

		public void remove()
		{
			if (SetProperty.this.constrained && (SetProperty.this.bus != null))
			{
				final VetoableSetPropertyChangeEvent lEvent = new VetoableSetPropertyChangeEvent(SetProperty.this.source,SetProperty.this.name, lastSeen, ChangeType.DELETE);
				SetProperty.this.bus.publish(lEvent);
			}

			target.remove();

			if (SetProperty.this.bus != null)
			{
				SetPropertyChangeEvent lEvent = new SetPropertyChangeEvent(SetProperty.this.source, SetProperty.this.name, lastSeen,ChangeType.DELETE);
				SetProperty.this.bus.publish(lEvent);
			}
		}
	}

	public SetProperty(String aName, boolean aConstrained, Object aSource, MessageBus aBus)
	{
		this.name = aName;
		this.source = aSource;
		this.bus = aBus;
		this.values = new HashSet<E>();
		this.constrained = aConstrained;
	}

	public SetProperty(String aName, boolean aConstrained, Object aSource, MessageBus aBus, Set<E> aImpl)
	{
		this.name = aName;
		this.source = aSource;
		this.bus = aBus;
		this.values = aImpl;
		this.constrained = aConstrained;		
	}

	public void setBus(MessageBus aBus)
	{
		this.bus = aBus;
	}
	
	public boolean addValue(E aValue)
	{
      boolean lResult = false;
		if (this.constrained && (this.bus != null))
		{
			final VetoableSetPropertyChangeEvent lEvent = new VetoableSetPropertyChangeEvent(this.source, this.name, aValue, ChangeType.INSERT);
			this.bus.publish(lEvent);
		}
		
		lResult = values.add(aValue);
		
		if (this.bus != null && lResult)
		{
			SetPropertyChangeEvent lEvent = new SetPropertyChangeEvent(this.source, this.name, aValue, ChangeType.INSERT);
			this.bus.publish(lEvent);
		}
      return lResult;
	}
		
	public boolean addAllValues(Collection<? extends E> aColl) 
	{
		boolean lResult = false;
		for(E lVal: aColl)
		{
			lResult = lResult || addValue(lVal);
		}
		return lResult;
	}
	
	public boolean removeValue(E aValue)
	{
		boolean lResult = false;

		if (this.constrained && (this.bus != null))
		{
			final VetoableSetPropertyChangeEvent lEvent = new VetoableSetPropertyChangeEvent(this.source, this.name, aValue, ChangeType.DELETE);
			this.bus.publish(lEvent);
		}
		
		lResult = values.remove(aValue);
		
		if (this.bus != null && lResult)
		{
			SetPropertyChangeEvent lEvent = new SetPropertyChangeEvent(this.source, this.name, aValue, ChangeType.DELETE);
			this.bus.publish(lEvent);
		}
        return lResult;
	}
	
	public boolean removeAllValues(Collection<? extends E> aColl) 
	{
		boolean lResult = false;
		for(E lVal: aColl)
		{
			lResult = lResult || removeValue(lVal);
		}
		return lResult;
	}
	
	public int getSize()
	{
		return values.size();
	}
	
	public List<E> getValue(List<E> aList)
	{
		if(aList == null) aList = new ArrayList<E>();
		aList.addAll(values);
		return aList;
	}
	
	@SuppressWarnings("unchecked")
	public void clearValues()
	{
		for (Object lVal : values.toArray())
		{
			removeValue((E)lVal);
		}
	}

    @SuppressWarnings("unchecked")
	 public Iterator iterator()
    {
        return new IteratorWrapper(values.iterator());
    }
    
    public boolean containsValue(Object aVal)
    {
   	 return values.contains(aVal);
    }
    
    public boolean containsAllValues(Collection<?> aColl)
    {
 		for(Object lVal: aColl)
 		{
 			if(!containsValue(lVal)) return false;
 		}
 		return true;
    }
    
    public boolean isEmpty()
    {
   	 return getSize() <= 0;
    }
}
