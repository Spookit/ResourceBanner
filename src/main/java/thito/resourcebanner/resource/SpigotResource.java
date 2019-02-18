package thito.resourcebanner.resource;

import thito.resourcebanner.Author;

public class SpigotResource {

  private int likes;
  private IDContainer[] versions;
  private IDContainer[] updates;
  private IDContainer[] reviews;
  private String name;
  private String tag;
  private IDContainer version;
  private IDContainer author;
  private IDContainer category;
  private ResourceRating rating;
  private long releaseDate;
  private int downloads;
  private boolean premium;
  private double price;
  private String currency;
  private int id;
  private IconContainer icon;
  private Author au = null;

  public int getLikes() {
    return likes;
  }

  public IDContainer[] getVersions() {
    return versions;
  }

  public IDContainer[] getUpdates() {
    return updates;
  }

  public IDContainer[] getReviews() {
    return reviews;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTag() {
    return tag;
  }

  public IDContainer getVersion() {
    return version;
  }

  public IDContainer getCategory() {
    return category;
  }

  public ResourceRating getRating() {
    return rating;
  }

  public long getReleaseDate() {
    return releaseDate;
  }

  public int getDownloads() {
    return downloads;
  }

  public boolean isPremium() {
    return premium;
  }

  public double getPrice() {
    return price;
  }

  public String getCurrency() {
    return currency;
  }

  public int getId() {
    return id;
  }

  public IconContainer getIcon() {
    return icon;
  }

  public Author getAu() {
    return au;
  }

  public Author getAuthor() {
    return au == null ? au = Author.getAuthor(author.id + "") : au;
  }
}
