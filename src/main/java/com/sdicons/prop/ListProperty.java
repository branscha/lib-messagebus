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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation support for properties with list semantics.
 * 
 * @see Property
 * @see java.util.List
 *
 */
public class ListProperty<E>
implements Iterable<E>, Serializable
{
	private String name;
	private Object source;
	transient private MessageBus bus;
	private List<E> values;
	private boolean constrained;

    private class IteratorWrapper
    implements Iterator<E>
    {
        private Iterator<E> target;
        private E lastSeen;

        private IteratorWrapper(Iterator<E> aTarget)
        {
            target = aTarget;
        }

        public boolean hasNext()
        {
            return target.hasNext();
        }

        public E next()
        {
            lastSeen = target.next();
            return lastSeen;
        }

        public void remove()
        {
            final int lIndex = values.indexOf(lastSeen);

            if (ListProperty.this.constrained && (ListProperty.this.bus != null))
            {
                final VetoableListPropertyChangeEvent lEvent = new VetoableListPropertyChangeEvent(ListProperty.this.source, ListProperty.this.name, lastSeen, lIndex, ChangeType.DELETE);
                ListProperty.this.bus.publish(lEvent);
            }

            target.remove();

            if (ListProperty.this.bus != null)
            {
                ListPropertyChangeEvent lEvent = new ListPropertyChangeEvent(ListProperty.this.source, ListProperty.this.name, lastSeen, lIndex, ChangeType.DELETE);
                ListProperty.this.bus.publish(lEvent);
            }
        }
    }

	public ListProperty(String aName, boolean aConstrained, Object aSource, MessageBus aBus)
	{
		this.name = aName;
		this.source = aSource;
		this.bus = aBus;
		this.values = new ArrayList<E>();
		this.constrained = aConstrained;		
	}
	
	public ListProperty(String aName, boolean aConstrained, Object aSource, MessageBus aBus, List<E> aImpl)
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
	
	public void addValue(int aIndex, E aValue)
	{
		if (aIndex < 0 || aIndex > values.size())
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		
		if (this.constrained && (this.bus != null))
		{
			final VetoableListPropertyChangeEvent lEvent = new VetoableListPropertyChangeEvent(this.source, this.name, aValue, aIndex, ChangeType.INSERT);
			this.bus.publish(lEvent);
		}
		
		values.add(aIndex, aValue);
		
		if (this.bus != null)
		{
			ListPropertyChangeEvent lEvent = new ListPropertyChangeEvent(this.source, this.name, aValue, aIndex, ChangeType.INSERT);
			this.bus.publish(lEvent);
		}		
	}	
	
	public void addValue(E aValue)
	{
		this.addValue(values.size(), aValue);
	}
	
	public void addAllValues(Collection<? extends E> aColl) 
	{
		for(E lVal: aColl)
		{
			addValue(lVal);
		}
	}
	
	public void addAllValues(int aIndex, Collection<? extends E> aColl) 
	{
		for(E lVal: aColl)
		{
			addValue(aIndex++, lVal);
		}
	}
	
	public E removeValue(int aIndex)
	{
		if (aIndex < 0 || aIndex > values.size())
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		
		final Object lValue = values.get(aIndex);
		
		if (this.constrained && (this.bus != null))
		{
			final VetoableListPropertyChangeEvent lEvent = new VetoableListPropertyChangeEvent(this.source, this.name, lValue, aIndex, ChangeType.DELETE);
			this.bus.publish(lEvent);
		}
		
		E lVal = values.remove(aIndex);
		
		if (this.bus != null)
		{
			ListPropertyChangeEvent lEvent = new ListPropertyChangeEvent(this.source, this.name, lValue, aIndex, ChangeType.DELETE);
			this.bus.publish(lEvent);
		}	
		return lVal;
	}
	
	public boolean removeValue(E aValue)
	{
		int lPos = values.indexOf(aValue);
		if(lPos < 0) return false;
		else removeValue(lPos);
		return true;
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
	
	public E getValue(int aIndex)
	{
		if (aIndex < 0 || aIndex > values.size())
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		
		return values.get(aIndex);
	}
	
	public List<E> getValue(List<E> aList)
	{
		if(aList == null) aList = new ArrayList<E>();
		aList.addAll(values);
		return aList;
	}
	
	public void clearValues()
	{
		while(values.size() > 0)
		{
			removeValue(0);
		}
	}
	
	public int indexOfValue(E aValue)
	{
		return values.indexOf(aValue);
	}
	
	public int lastIndexOff(E aValue)
	{
		return values.lastIndexOf(aValue);
	}

	public Iterator<E> iterator()
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
