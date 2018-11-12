package thito.resourcebanner;

public class Sort {
	public enum SortType {
		RATING,DOWNLOADS,NAME,PREMIUM;
		public static SortType DEFAULT = DOWNLOADS;
		public static SortType _valueOf(String s) {
			for (SortType t : values()) if (t.name().equalsIgnoreCase(s)) return t;
			return DEFAULT;
		}
	}
	public enum SortDirection {
		ASCEND("+"),DESCEND("-");
		public static SortDirection DEFAULT = ASCEND;
		String a;
		SortDirection(String c) {
			a = c;
		}
		public static SortDirection _valueOf(String s) {
			for (SortDirection t : values()) if (t.name().equalsIgnoreCase(s)) return t;
			return DEFAULT;
		}
	}
	public static String toString(SortType type,SortDirection dir) {
		return (dir == null ? SortDirection.DEFAULT.a: dir.a)+
				(type == null ? SortType.DEFAULT.name().toLowerCase() : type.name().toLowerCase());
	}
}
