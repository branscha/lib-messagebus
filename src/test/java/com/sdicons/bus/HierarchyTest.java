package com.sdicons.bus;

import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class HierarchyTest {
	
	private Map<String, Integer> counters = new HashMap<String, Integer>();
	
	private class Listener 
	{
		private String name;
		
		public Listener(String name)
		{
			this.name = name;
		}
		
		@Notify
		@SuppressWarnings("unused")
		public void listen(EventObject e)
		{
			if(counters.containsKey(name)) 
			{
				counters.put(name, counters.get(name) + 1);
			}
			else
			{
				counters.put(name,  1);
			}
		}
	}
	
	@Test
	public void testBuilder() 
	{
		MessageBus chan0 = MessageBus.getMessageBus("uno");
		MessageBus chan1 = MessageBus.getMessageBus("uno.duo.tres.quattuor");
		MessageBus chan2 = MessageBus.getMessageBus("uno.duo.tree.four");
		MessageBus chan3 = MessageBus.getMessageBus("uno.duo.drie");
		
		chan0.register(new Listener("uno"));
		chan1.register(new Listener("quattuor"));
		chan2.register(new Listener("four"));
		chan3.register(new Listener("drie"));
		
		PropertyChangeEvent ping = new PropertyChangeEvent(this, "ping", 0, 1);
		
		chan1.publish(ping);
		chan2.publish(ping);
		chan0.publish(ping);
		
		Assert.assertNotNull(counters.get("quattuor"));
		Assert.assertTrue( 1 == counters.get("quattuor"));
		//
		Assert.assertNotNull(counters.get("four"));
		Assert.assertTrue( 1 == counters.get("four"));
		//
		Assert.assertNotNull(counters.get("uno"));
		Assert.assertTrue( 3 == counters.get("uno"));
		//
		Assert.assertNull(counters.get("drie"));
	}

}
