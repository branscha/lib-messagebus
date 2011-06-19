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

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation support for client properties.
 * @see Property
 *
 */
public class ClientProperties
implements Serializable
{
    private Object source;
    transient private MessageBus bus;
    private Map<Object, Object> values;
    private boolean constrained;

    public ClientProperties(boolean aConstrained, Object aSource, MessageBus aBus)
    {
        source = aSource;
        bus = aBus;
        values = new HashMap<Object, Object>();
        constrained = aConstrained;
    }

    public void setBus(MessageBus aBus)
    {
        bus = aBus;
    }

    public void setValue(Object aKey, Object aValue)
    {
        if(aKey == null) throw new IllegalArgumentException("Client property key cannot be null.");
        final Object lOldValue = values.get(aKey);

        // Only take action when the new value is different from the old value.
        // If nothing changes nobody should be asked or notified about the change.
        if(((aValue == null) && (aValue != lOldValue)) ||
           ((aValue != null) && (!aValue.equals(lOldValue))))
        {
            if(constrained && (bus != null))
            {
                final  VetoablePropertyChangeEvent lEvent = new VetoablePropertyChangeEvent(source, aKey.toString(), lOldValue, aValue);
                bus.publish(lEvent);
            }

            if(aValue == null) values.remove(aKey);
            else values.put(aKey, aValue);

            if(bus != null)
            {
                PropertyChangeEvent lEvent = new PropertyChangeEvent(source, aKey.toString(), lOldValue, aValue);
                bus.publish(lEvent);
            }
        }
    }

    public Object getValue(Object aKey)
    {
        if(aKey == null) throw new IllegalArgumentException("Client property key cannot be null.");
        return values.get(aKey);
    }
}
