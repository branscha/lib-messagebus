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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Annotation for notification handlers. The parameter can be an {@link java.util.EventObject EventObject} or a derived class, the bus will take
 * the type of the parameter into account to call the handler or not.
 * Other filtering is possible:
 * <ul><li><b>'sourceType'</b> the class of the source of the event. The handler will be called if the event is assignable to this parameter.</li>
 *     <li><b>'allowNullSource'</b> indicating if the handler can be called if the source of the event is not filled in.</li>
 * </ul>
 * <p> If the return value of the handler is a boolean, and it is 'true' than the other handlers will not be called.
 * It means that the event is handled and should not be handled by another handler, the event was 'consumed' by the handler.
 * 
 * <p>All events should be derived from the standard Java java.util.EventObject as all events are. This is not really a restriction since
 * it contains the source of the event (which can be null), and we can filter on the type of this.
 * 
 * <pre> @Notify
 * public void callback(EventObject aEvent)
 * {
 *     // Event handling code.
 * } </pre>
 * 
 */
@Retention(value= RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Notify
{
    Class<?> sourceType() default Object.class;
    boolean allowNullSource() default true;
}