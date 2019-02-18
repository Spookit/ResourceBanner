package thito.resourcebanner;

public class Sort {
  public static String toString(SortType type, SortDirection dir) {
    return (dir == null ? SortDirection.DEFAULT.a : dir.a)
        + (type == null ? SortType.DEFAULT.name().toLowerCase() : type.name().toLowerCase());
  }

  public enum SortDirection {
    ASCEND("+"), DESCEND("-");
    public static SortDirection DEFAULT = ASCEND;
    String a;

    SortDirection(String c) {
      a = c;
    }

    public static SortDirection _valueOf(String s) {
      for (SortDirection t : values()) {
        if (t.name().equalsIgnoreCase(s)) {
          return t;
        }
      }
      return DEFAULT;
    }
  }

  public enum SortType {
    rating, downloads, name, premium, likes, id, category, releaseDate, updateDate;
    public static SortType DEFAULT = downloads;

    public static SortType _valueOf(String s) {
      for (SortType t : values()) {
        if (t.name().equalsIgnoreCase(s)) {
          return t;
        }
      }
      return DEFAULT;
    }
  }
}
