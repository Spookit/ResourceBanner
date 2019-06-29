package thito.resourcebanner;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class LibraryLoader {
	private static Method a;
	static {
		try {
			a = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			a.setAccessible(true);
		} catch (Exception e) {
		}
	}
	private File d;
	public LibraryLoader(File dir) {
		d = dir;
	}
	public void loadAll() {
		File[] list = d.listFiles();
		ClassLoader loader = getRootClassLoader();
		if (loader == null) throw new UnsupportedOperationException("does not support this kind of class loader");
		if (list != null) {
			for (File f : list) {
				if (!f.getName().toLowerCase().endsWith(".jar")) continue;
				try {
					a.invoke(loader, f.toURI().toURL());
					System.out.println("Loaded library: "+f);
				} catch (Throwable t) {
					throw new RuntimeException("Failed to load Library",t);
				}
			}
		}
	}
	public ClassLoader getRootClassLoader() {
		ClassLoader loader = getClass().getClassLoader();
		if (!(loader instanceof URLClassLoader)) return null;
		while (loader.getParent() != null) {
			if (!(loader.getParent() instanceof URLClassLoader)) break;
			loader = loader.getParent();
		}
		return loader;
	}
}
