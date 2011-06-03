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

package com.sdicons.prop;

import com.sdicons.bus.MessageBus;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
/**
 * <p>Implementation support class that can be used to implement event JavaBean properties that post messages on a bus in stead of
 * notifying listeners.
 * 
 * <pre>public class MyBean
 * {
 *    private Property<String> name = new Property<String>("name", bus, ...);
 *    ...
 *    public String getName(){return name.getValue();}
 *    public void setName(String aName){name.setValue(aName};}
 *    ...   
 * } </pre>
 *
 * @param <E> The property type.
 * @see IndexedProperty
 * @see ClientProperties
 * @see ListProperty
 * @see SetProperty
 */
public class Property<E>
implements Serializable
{
    private String name;
    private Object source;
    transient private MessageBus bus;
    private E value;
    private boolean constrained;

    public Property(String aName, boolean aConstrained, Object aSource, MessageBus aBus, E aValue)
    {
        name = aName;
        source = aSource;
        bus = aBus;
        value = aValue;
        constrained = aConstrained;
    }

    public void setBus(MessageBus aBus)
    {
        bus = aBus;
    }

    public void setValue(E aValue)
    {
        // Only take action when the new value is different from the old value.
        // If nothing changes nobody should be asked or notified about the change.
        if(((aValue == null) && (aValue != value)) ||
           ((aValue != null) && (!aValue.equals(value))))
        {
            if(constrained && (bus != null))
            {
                final VetoablePropertyChangeEvent lEvent = new VetoablePropertyChangeEvent(source, name, value, aValue);
                bus.publish(lEvent);
            }

            E lOldValue = value;
            value = aValue;

            if(bus != null)
            {
                PropertyChangeEvent lEvent = new PropertyChangeEvent(source, name, lOldValue, value);
                bus.publish(lEvent);
            }
        }
    }

    public E getValue()
    {
        return value;
    }
}
