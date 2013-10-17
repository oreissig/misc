package struct;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This is a more convenient default {@link Map} implementation than
 * {@link AbstractMap}, for which developers only need to implement
 * {@link #keySet()} and {@link #get(Object)}.
 * <p>
 * This map is modifiable only if the {@link #keySet()} is modifiable.
 * {@link #put(Object, Object)} results in {@link Set#add(Object)} and
 * {@link #remove(Object)} results in {@link Set#remove(Object)}. The value
 * given for {@link #put(Object, Object)} is ignored.
 * 
 * @author OReissig
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public abstract class AbstractMap2<K, V> implements Map<K, V> {

	@Override
	public int size() {
		return keySet().size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return keySet().contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	@Override
	public V put(K key, V value) {
		V oldValue = get(key);
		keySet().add(key);
		return oldValue;
	}

	@Override
	public V remove(Object key) {
		V oldValue = get(key);
		keySet().remove(key);
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	@Override
	public void clear() {
		keySet().clear();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new AbstractSet<Map.Entry<K, V>>() {

			@Override
			public Iterator<Map.Entry<K, V>> iterator() {
				return new Iterator<Map.Entry<K, V>>() {
					private final Iterator<K> keys = keySet().iterator();

					@Override
					public boolean hasNext() {
						return keys.hasNext();
					}

					@Override
					public Map.Entry<K, V> next() {
						final K key = keys.next();
						return new Map.Entry<K, V>() {
							@Override
							public K getKey() {
								return key;
							}

							@Override
							public V getValue() {
								return get(key);
							}

							@Override
							public V setValue(V value) {
								return put(key, value);
							}
						};
					}

					@Override
					public void remove() {
						keys.remove();
					}
				};
			}

			@Override
			public int size() {
				return AbstractMap2.this.size();
			}
		};
	}

	@Override
	public Collection<V> values() {
		return new AbstractCollection<V>() {
			@Override
			public Iterator<V> iterator() {
				return new Iterator<V>() {
					private final Iterator<K> keys = keySet().iterator();

					@Override
					public boolean hasNext() {
						return keys.hasNext();
					}

					@Override
					public V next() {
						return get(keys.next());
					}

					@Override
					public void remove() {
						keys.remove();
					}
				};
			}

			@Override
			public int size() {
				return AbstractMap2.this.size();
			}
		};
	}
}
