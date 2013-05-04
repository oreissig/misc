misc
====

just some misc. stuff

needs [Google Guava](https://code.google.com/p/guava-libraries/) 14.0


yield
-----

Contains a YieldingIterator, that allows for code to be written sequentially, while occasionally dropping some instance via the "yield" method. Those instances can be iterated over in the usual Java fashion.


parallelfor
-----------

Contains a parallel implementation of the common for-each construct, where each step is executed concurrently with other steps.
