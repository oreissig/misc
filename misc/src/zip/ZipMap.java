package zip;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import struct.AbstractMap2;

public class ZipMap extends AbstractMap2<String, InputStream> implements Closeable {

	private final ZipFile zip;

	public ZipMap(ZipFile zipFile) {
		if (zipFile == null)
			throw new NullPointerException();
		else
			this.zip = zipFile;
	}

	public ZipMap(File zipFile) throws IOException {
		this(new ZipFile(zipFile));
	}

	public ZipMap(String path) throws IOException {
		this(new ZipFile(path));
	}

	@Override
	public InputStream get(Object name) {
		ZipEntry entry = zip.getEntry(name.toString());
		if (entry != null) {
			try {
				return zip.getInputStream(entry);
			} catch (IOException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public Set<String> keySet() {
		return new AbstractSet<String>() {
			@Override
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					final Enumeration<? extends ZipEntry> entries = zip
							.entries();

					@Override
					public boolean hasNext() {
						return entries.hasMoreElements();
					}

					@Override
					public String next() {
						return entries.nextElement().getName();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			@Override
			public int size() {
				return zip.size();
			}
		};
	}

	@Override
	public void close() throws IOException {
		zip.close();
	}
}
