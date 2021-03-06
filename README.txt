MessageBus
==========

I. Core message bus.

The message bus is an alternative mechanism for the event and event listener mechanism in JavaBeans, Swing e.a.
Every object can publish messages on the bus. An object can become a listener by registering on the bus, the bus will
automatically scan the potential listener for annotated handler methods. The bus will dispatch events to all
compatible handlers.

Example: Publishing is easy
--------

MessageBus bus = new MessageBus();
PropertyChangeEvent event = new PropertyChangeEvent(this, "Hello World", 0, 1);
bus.publish(event);

Example: A handler
--------

@Notify
public void callback(EventObject aEvent)
{
    // Event handling code.
}

Example: Registering the handlers
--------

bus.register(this);

All events should be derived from the standard Java java.util.EventObject as all events are. This is not really a restriction since
it contains the source of the event (which can be null), and we can filter on the type of this.

@Notify to mark notification handlers. The parameter can be an EventObject or a derived class, the bus will take
the type of the parameter into account to call the handler or not.
Other filtering is possible:
- 'sourceType' the class of the source of the event. The handler will be called if the event is assignable to this parameter.
- 'allowNullSource' indicating if the handler can be called if the source of the event is not filled in.

If the return value of the handler is a boolean, and it is 'true' than the other handlers will not be called.
It means that the event is handled and should not be handled by another handler, the event was 'consumed' by the handler.

Bus listeners are stored as weak references, so the bus will never prevent a listener from being garbage collected.
The bus cannot be the cause of a memory leak.

The bus itself is not serializable, it is transient in all Property implementations.
The bus is a communications medium, if a model object is restored from a stream, it should
get the active message bus, not the old serialized one. A model that uses a message bus should provide methods to set a new message bus.

II. Support for JavaBean properties.

Property, IndexedProperty can be used to implement event JavaBean properties that post messages on a bus in stead of
notifying listeners.

VetoablePropertyChangeEvent was defined, in Swing veto notifications use the normal PropertyChangeEvent but with
another listener interface. Since we cannot differentiate on the interface, the event consumer must be able
to see the difference between a veto event and a change event, that is why I added this new type of event, our
property implementations will use this. The consumer can call PropertyVetoException to veto the change.

Example: Using the modeling support classes.
--------

public class MyBean
{
   private Property<String> name = new Property<String>("name", bus, ...);
   ...
   public String getName(){return name.getValue();}
   public void setName(String aName){name.setValue(aName};}
   ...   
}

III. Support for building domain models.

Completely new property types are defined: ListProperty which follows list semantics whereas IndexedProperties have array semantics, SetProperty and ClientProperties.
These new type are not compatible with the existing JavaBean specification. It can be handy for modeling  a one-to-many in your domain model if
you want to make use of the message bus. Since this is a new type we have to define our own ListPropertyChangeEvent and VetoableListPropertyChangeEvent and others.
We cannot use something from Swing here.
 
References:
- http://beust.com/weblog/2010/07/26/local-message-bus/.
- GWT contains a similar mechanism.
- A more frequent term is 'local message bus'.
