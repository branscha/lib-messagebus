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

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import com.sdicons.prop.PropertyVetoException;

/**
 * <p>The message bus is an alternative mechanism for the event and event listener mechanism in JavaBeans, Swing, e.a.
 * Every object can {@link #publish(EventObject) publish} messages on the bus. An object can become a listener by {@link #register(Object) registering} on the bus, the bus will
 * automatically scan the potential listener for annotated handler methods. The bus will dispatch events to all
 * compatible handlers.
 * 
 * <p>Bus listeners are stored as weak references, so the bus will never prevent a listener from being garbage collected.
 * The bus cannot be the cause of a memory leak.
 * 
 * <p>The bus itself is not serializable, it is transient in all Property implementations. The bus is a communications medium, if a model object is restored from a stream, it should
 * get the active message bus, not the old serialized one. A model that uses a message bus should provide methods to set a new message bus.
 * 
 * <p>Example publishing an event.
 * <pre> MessageBus bus = new MessageBus();
 * PropertyChangeEvent event = new PropertyChangeEvent(this, "Hello World", 0, 1);
 * bus.publish(event);
 * </pre>
 * 
 * <p> Example registering the handlers of a bean.
 * <pre> bus.register(this); </pre
 */
public class MessageBus
{
	// Data structure to keep track of a bus listener.
	private static class SubscriberInfo
	{
		// We keep the listener in a weak reference so that the bus
		// does not prevent garbage collection. This is to prevent memory leaks.
		private WeakReference<Object> subscriberRef;
		// The handler method to be called.
		private Method method;
		// The event type accepted by the handler.
		private Class<?> parameterType;
		// The sender type accepted by the handler.
		private Class<?> sourceType;
		// Does the handler accept messages with unknown message source?
		private boolean allowNullSource;
		
		private SubscriberInfo(Object aSubscriber, Method aMethod, Class<?> aParameterType, Class<?> aSourceType, boolean aAllowNullSource)
		{
			this.subscriberRef = new WeakReference<Object>(aSubscriber);
			this.method = aMethod;
			this.parameterType = aParameterType;
			this.sourceType = aSourceType;
			this.allowNullSource = aAllowNullSource;
		}

		// Conditionally call a handler with the specified event.
		// If all conditions are met the handler will be called, otherwise the
		// method will do nothing.
		public boolean notify(EventObject aEvent)
		{
			final Object lSubscriber = this.subscriberRef.get();
			if (lSubscriber != null)
			{
				try
				{
					if (this.parameterType.isAssignableFrom(aEvent.getClass()))
					{
						Object lSource = aEvent.getSource();
						if (((lSource == null) && this.allowNullSource) || 
							((lSource != null) && this.sourceType.isAssignableFrom(lSource.getClass())))
						{
							// Invoke the notification method and remember the result.
							final Object lResult = this.method.invoke(lSubscriber, aEvent);
							// If the notification method gave us a boolean, we will interpret this value,
							// if we got 'true' this means that the event was handled completely, no other handlers will be invoked.
							// If we got a 'false' this means that we have to continue invoking the other handlers.
							return (lResult instanceof Boolean) && (Boolean)lResult;
						}
					}
				}
				catch (InvocationTargetException e)
				{
					if (e.getTargetException() instanceof PropertyVetoException)
					{
						throw (PropertyVetoException)e.getTargetException();
					}
					else
					{
						final String lMsg = "Error while invoking notification method '%s' on an instance of class '%s'.";
						throw new RuntimeException(String.format(lMsg, this.method.getName(), lSubscriber.getClass().getSimpleName()), e);
					}
				}
				catch (Exception e)
				{
					final String lMsg = "Error while invoking notification method '%s' on an instance of class '%s'.";
					throw new RuntimeException(String.format(lMsg, this.method.getName(), lSubscriber.getClass().getSimpleName()), e);
				}
			}
			return false;
		}

		// Check if the object that wants to receive notifications is 
		// garbage  collected, and is no longer available.
		public boolean isGarbage()
		{
			return this.subscriberRef.get() == null;
		}

		public boolean isForSpecifiedSubscriber(Object aSubscriber)
		{
			final Object lSubscriber = this.subscriberRef.get();
			return lSubscriber == aSubscriber;
		}
	}
	
	// Flag to see if we are publishing notifications or not
	private boolean isPublishing = false;

	// The list containing the observers.
	private List<SubscriberInfo> subscriberInfos = new ArrayList<SubscriberInfo>();
	
	// List of delayed additions.
	private List<SubscriberInfo> subscriberCandidates = new ArrayList<SubscriberInfo>();
	
	// List of delayed deletions.
    private List<SubscriberInfo> subscriberDeathrow = new ArrayList<SubscriberInfo>();
    
    // Delayed events.
    private Deque<EventObject> delayedEvents = new ArrayDeque<EventObject>();
	
	// The parent bus.
	private MessageBus parentBus;

	/**
	 * Construct a message bus that is connected to a parent bus. Messages will be sent to the parent
	 * bus as well. This makes it possible to create a hierarchy of messages busses. Messages published
	 * on the children will reach the parent bus, but parent messages will not be sent to the children.
	 * This can be used to reduce the traffic on the bus.
	 * 
	 * @param aParent  A parent bus, events will be sent to the parent bus as well.
	 */
	public MessageBus(MessageBus aParent)
	{
		this.parentBus = aParent;
	}

	/**
	 * Create a message bus.
	 */
	public MessageBus()
	{
		this(null);
	}

	/**
	 * Register a bean as a subscriber. The bus will scan the bean for {@link Notify annotated} methods and these
	 * methods will be called when a corresponding message is {@link #publish(EventObject) published} on the bus.
	 * 
	 * @param aSubscriber A POJO containing annotated call back methods.
	 * @see #unregister(Object)
	 * @see Notify
	 */
	public void register(Object aSubscriber)
	{
		final Method[] lMethods = aSubscriber.getClass().getMethods();
		for (Method lMethod : lMethods)
		{
			final Notify lAnnot = lMethod.getAnnotation(Notify.class);
			if (lAnnot != null)
			{
				final Class<?> lParamTypes[] = lMethod.getParameterTypes();
				if (lParamTypes.length == 1)
				{
					if (EventObject.class.isAssignableFrom(lParamTypes[0]))
					{
					    SubscriberInfo subscriber = new SubscriberInfo(aSubscriber, lMethod, lParamTypes[0], lAnnot.sourceType(), lAnnot.allowNullSource());
					    if(isPublishing) this.subscriberCandidates.add(subscriber);
					    else this.subscriberInfos.add(subscriber);
					}
					else
					{
						final String lMsg = "Class '%s' contains an annotated @Notify method '%s' with a parameter that is not an EventObject.";
						throw new IllegalArgumentException(String.format(lMsg, aSubscriber.getClass().getSimpleName(), lMethod.getName()));
					}
				}
				else
				{
					final String lMsg = "Class '%s' contains an annotated @Notify method '%s' with the wrong number of arguments, only a single EventObject (or subclass) is allowed.";
					throw new IllegalArgumentException(String.format(lMsg, aSubscriber.getClass().getSimpleName(), lMethod.getName()));
				}
			}
		}

		// Do some cleanup.
		cleanGarbageInfos();
	}

	/**
	 * Remove a bean as a listener. The bean will no longer receive messages published on the bus.
	 * 
	 * @param aSubscriber The POJO to be removed from the bus.
	 * @see #register(Object)
	 */
	public void unregister(Object aSubscriber)
	{
		final Iterator<SubscriberInfo> lIter = this.subscriberInfos.iterator();
		while (lIter.hasNext())
		{
			final SubscriberInfo lInfo = lIter.next();
			if (lInfo.isGarbage() || lInfo.isForSpecifiedSubscriber(aSubscriber))
			{
			    if(isPublishing) this.subscriberDeathrow.add(lInfo);
			    else lIter.remove();
			}
		}
		
		// Also delete from the candidate list.
		final Iterator<SubscriberInfo> lIterBis = this.subscriberCandidates.iterator();
		while(lIterBis.hasNext())
		{
		    final SubscriberInfo candidate = lIterBis.next();
		    if(candidate.isForSpecifiedSubscriber(aSubscriber))
		        lIterBis.remove();
		}
	}

	
	/**
	 * <p>Publish an event on the message bus. All {@link #register(Object) registered} handlers that are interested in this
	 * event will be called.
	 * 
	 * <p>All events should be derived from the standard Java java.util.EventObject as all events are. This is not really a restriction since
    * it contains the source of the event (which can be null), and we can filter on the type of this.
	 * 
	 * @param aEvent The event to be published on the bus.
	 */
	public void publish(EventObject aEvent)
	{
	    if(isPublishing) 
	    {
	        delayedEvents.offer(aEvent);
	        return;
	    }
	    
	    try 
	    {
	        isPublishing = true;
    		boolean lHandled = false;
    		final Iterator<SubscriberInfo> lIter = this.subscriberInfos.iterator();
    		while (lIter.hasNext())
    		{
    			final SubscriberInfo lInfo = lIter.next();
    			if (lInfo.isGarbage())
    			{
    				lIter.remove();
    			}
    			else if (!lHandled)
    			{
    				lHandled = lInfo.notify(aEvent);
    			}
    		}
    
    		// Ripple the event to the parent.
    		if (!lHandled && (this.parentBus != null))
    		{
    			this.parentBus.publish(aEvent);
    		}
	    }
	    finally 
	    {
	        // Delayed adding.
	        //
	        if(subscriberCandidates.size() > 0) 
	        {
	            subscriberInfos.addAll(subscriberCandidates);
	            subscriberCandidates.clear();
	        }
	        
	        // Delayed removal.
	        //
	        if(subscriberDeathrow.size () > 0) 
	        {
	            subscriberInfos.removeAll(subscriberDeathrow);
	            subscriberDeathrow.clear();
	        }
	        
	        // Reset the flag.
	        isPublishing = false;
	    }
	    
	    // Handle delayed events that were published
	    // in event handlers.
	    while(delayedEvents.size() > 0) 
	    {
	        this.publish(delayedEvents.poll());
	    }
	}

	// Remove all handler information concerning garbage collected listeners
	// from our listener list. We don't need these anymore, removing them
	// from the list from time to time will speed up the process of sending events.
	private void cleanGarbageInfos()
	{
		final Iterator<SubscriberInfo> lIter = this.subscriberInfos.iterator();
		while (lIter.hasNext())
		{
			final SubscriberInfo lInfo = lIter.next();
			if (lInfo.isGarbage())
			{
				lIter.remove();
			}
		}
	}

	/**
	 * The number of handler methods.
	 * If a bean has multiple handlers, each method will be counted separately.
	 * 
	 * @return The number of handler methods registered to this bus. 
	 */
    public int getNrSubscribers()
    {
        return subscriberInfos.size();
    }
}
