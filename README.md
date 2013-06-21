misc
====

just some misc. stuff

needs [Google Guava](https://code.google.com/p/guava-libraries/) 14.0


yield
-----

Contains a YieldingIterator, that allows for code to be written sequentially, while occasionally dropping some instance via the "yield" method. Those instances can be iterated over in the usual Java fashion.

BoundedBlockingQueue
------------------

A decorator, that takes an arbitrary BlockingQueue and makes it bounded.
It is useful for implementations, that do not provide a bounded variant, such as PriorityBlockingQueue.

CopyOnWriteHashMap
------------------

A Java implementation of a HashMap, that does copy on write. This allows for the Map to be traversed safely and consistently, while modifications take place.
Note that this Map does not allow multiple concurrent modifications, it just guarantees a consistent view for all readers.

parallelfor
-----------

Contains a parallel implementation of the common for-each construct, where each step is executed concurrently with other steps.

fix_nvram
---------

A JScript file for Windows, that automates the temporary fixing of dead NVRAM batteries of Sun workstations and servers over serial console.
