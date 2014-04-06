misc
====

just some misc. stuff

needs [Google Guava](https://code.google.com/p/guava-libraries/) 15.0


yield
-----

Contains a YieldingIterator, that helps implementing coroutines, that drop some instance via the "yield" method. Those instances can be iterated over in the usual Java fashion.

BoundedBlockingQueue
------------------

A decorator, that takes an arbitrary BlockingQueue and makes it bounded.
It is useful for implementations, that do not provide a bounded variant, such as PriorityBlockingQueue.

AbstractQueue2, AbstractDeque
-----------------------------

AbstractQueue2 extends the common java.util.AbstractQueue with default peek and poll implementations.

AbstractDeque is an abstract class to help implementing Deques in a similar fashion to java.util.AbstractQueue for Queues.

PriorityDeque, PriorityBlockingDeque
------------------------------------

PriorityDeque is a wrapper class for Guava's MinMaxPriorityQueue, that conforms to the standard Deque interface.
PriorityBlockingDeque takes the PriorityDeque and wraps it to make up an (unbounded) BlockingDeque.

CopyOnWriteHashMap
------------------

A Java implementation of a HashMap, that does copy on write. This allows for the Map to be traversed safely and consistently, while modifications take place.
Note that this Map does not allow multiple concurrent modifications, it just guarantees a consistent view for all readers.

AbstractMap2
-------------

An abstract base class for map, that is easier to implement than Java's standard AbstractMap.

ZipMap
-----------

This class provides a Map-like view of the contents of a zip file.
It is based on Java's standard ZipFile class, but provides a better API.

parallelfor
-----------

Contains a parallel implementation of the common for-each construct for Java 7, where each step is executed concurrently with other steps.

fix_nvram
---------

A JScript file for Windows, that automates the temporary fixing of dead NVRAM batteries of Sun workstations and servers over serial console.
