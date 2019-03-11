package thito.resourcebanner.utils;

/**
 * @author Plajer
 *         <p>
 *         Created at 18.02.2019
 */
public class Utils {

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (final Exception ex) {
			return false;
		}
	}

}
