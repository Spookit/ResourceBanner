package thito.resourcebanner;

import java.io.File;
import java.net.URL;

public class Debug {

	public static void main(String[]args) throws Throwable {
		for (File f : getResourceFolderFiles("fonts")) {
			System.out.println(f);
		}
	}
	private static File[] getResourceFolderFiles (String folder) {
	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    URL url = loader.getResource(folder);
	    String path = url.getPath();
	    return new File(path).listFiles();
	  }

}
