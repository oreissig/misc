package struct;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CopyOnWriteHashMap<K, V> extends AbstractMap<K, V> {
	
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

	private final float loadFactor;
	private volatile int threshold;
	private volatile Table table;

    public CopyOnWriteHashMap() {
    	this(DEFAULT_INITIAL_CAPACITY);
    }

    public CopyOnWriteHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public CopyOnWriteHashMap(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);

        this.loadFactor = loadFactor;
        table = new Table(initialCapacity);
    }

    public CopyOnWriteHashMap(Map<? extends K, ? extends V> m) {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
                      DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        putAll(m);
    }

	@Override
	public int size() {
		return table.size;
	}

	@Override
	public boolean containsKey(Object key) {
		final Entry<K, V>[] t = table.data;
		
		int i = findIndex(key, t);
		return t[i] != null;
	}

	@Override
	public V get(Object key) {
		final Entry<K, V>[] t = table.data;
		
		int i = findIndex(key, t);
		Entry<K,V> e = t[i];
		if (e == null)
			return null;
		else
			return e.getValue();
	}

	@Override
	public V put(K key, V value) {
		final Table t = table;
		Entry<K, V>[] data = grow(t, 1);
		int size = t.size;
		
		V old = putInternal(data, key, value);
		if (old != value) {
			if (old == null)
				size++; // new entry was added
			update(t, new Table(data, size));
		}
		return old;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		final Table t = table;
		Entry<K, V>[] data = grow(t, m.size());
		int size = t.size;
		boolean changed = false;
		
		for (Entry<? extends K, ? extends V> e : m.entrySet()) {
			V newV = e.getValue();
			V oldV = putInternal(data, e.getKey(), newV);
			if (oldV != newV) {
				changed = true;
				if (oldV == null)
					size++; // new entry was added
			}
		}
		
		if (changed)
			update(t, new Table(data, size));
	}

	private V putInternal(final Entry<K,V>[] data, final K k, final V v) {
		int i = findIndex(k, data);
		Entry<K,V> e = data[i];
		V old = e == null ? null : e.getValue();
		if (old == v)
			return old; // nothing to do here
		
		data[i] = new Entry<K, V>() {
			@Override
			public K getKey() {
				return k;
			}

			@Override
			public V getValue() {
				return v;
			}

			@Override
			public V setValue(V value) {
				Entry<K, V>[] newData = Arrays.copyOf(data, data.length);
				
				V old = putInternal(newData, k, v);
				if (old == null)
					throw new ConcurrentModificationException("entry has been removed");
				
				update(data, newData);
				return old;
			}
		};
		
		return old;
	}

	@Override
	public void clear() {
		table = new Table(table.size);
	}

	@Override
	public V remove(Object key) {
		return removeInternal(table, key);
	}

	private V removeInternal(final Table t, Object key) {
		int i = findIndex(key, t.data);
		Entry<K,V> e = t.data[i];
		if (e == null)
			return null;
		else {
			V old = e.getValue();
			Entry<K, V>[] newData = Arrays.copyOf(t.data, t.data.length);
			newData[i] = null;
			update(t, new Table(t.data, t.size - 1));
			return old;
		}
	}

	public boolean removeAll(Collection<K> keys) {
		final Table t = table;
		return removeAllInternal(t, keys) != t;
	}
	
	private Table removeAllInternal(final Table t, Collection<K> keys) {
		Entry<K, V>[] data = t.data;
		int removed = 0;

		for (K k : keys) {
		int i = findIndex(k, data);
			Entry<K,V> e = data[i];
			if (e != null) {
				if (removed == 0) {
					// first hit => create copy
					data = Arrays.copyOf(data, data.length);
				}
				removed++;
				data[i] = null;
			}
		}
		
		if (removed > 0) {
			Table newT = new Table(data, t.size - removed);
			update(t, newT);
			return newT;
		} else
			return t;
	}

	@Override
	public Set<K> keySet() {
		return new AbstractSet<K>() {
			Table t = table;
			
			@Override
			public Iterator<K> iterator() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int size() {
				return t.size;
			}
			
			@Override
			public boolean remove(Object o) {
				// TODO Auto-generated method stub
				//return removeInternal(o, t);
				return false;
			}
			
			public boolean removeAll(Collection<?> c) {
				// TODO
				return false;
			};
		};
	}

	@Override
	public Collection<V> values() {
		return new AbstractCollection<V>() {
			Table t = table;
			
			@Override
			public Iterator<V> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				return t.size;
			}
		};
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Entry<K,V>[] grow(Table copyOf, int by) {
		int newCapacity = copyOf.size + by;
		assert newCapacity > copyOf.size;
		
		if (newCapacity > threshold) {
			Entry<K, V>[] data = new Table(newCapacity).data;
	        threshold = (int) (table.data.length * loadFactor);

			// rehash
			for (Entry<K,V> e : copyOf.data)
				if (e != null)
					putInternal(data, e.getKey(), e.getValue());

			return data;
		} else {
			return Arrays.copyOf(copyOf.data, copyOf.data.length);
		}
	}
	
	private int findIndex(Object key, Entry<K, V>[] t) {
		int wrap = t.length;
		int i = hash(key, wrap);

		while (true) {
			Entry<K, V> e = t[i % wrap];
			if (e == null || e.getKey().equals(key))
				return i;
			else
				i++;
		}
	}
	
	// TODO move into Table?
	private int hash(Object key, int size) {
		return key.hashCode() % size;
	}
	
	private synchronized void update(Table oldTable, Table newTable) {
		if (table != oldTable)
			throw new ConcurrentModificationException();
		else
			table = newTable;
	}
	
	private synchronized void update(Entry<K,V>[] oldData, Entry<K,V>[] newData) {
		if (table.data != oldData)
			throw new ConcurrentModificationException();
		else
			table = new Table(newData, table.size);
	}

	private class Table {
		public final Entry<K, V>[] data;
		public final int size;

		public Table(Entry<K, V>[] data, int size) {
			this.data = data;
			this.size = size;
		}

		public Table(int initial) {
			if (initial < 0)
				throw new IllegalArgumentException("Illegal initial capacity: "
						+ initial);
			if (initial > MAXIMUM_CAPACITY)
				initial = MAXIMUM_CAPACITY;

			// Find a power of 2 >= initial
			int actual = 1;
			while (actual < initial)
				actual <<= 1;

			this.data = new Entry[actual];
			this.size = 0;
		}
	}
}
