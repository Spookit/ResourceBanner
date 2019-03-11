package thito.resourcebanner.resource;

import thito.resourcebanner.Author;

public class SpigotResource {

	private Author au = null;
	private IDContainer author;
	private IDContainer category;
	private String currency;
	private int downloads;
	private IconContainer icon;
	private int id;
	private int likes;
	private String name;
	private boolean premium;
	private double price;
	private ResourceRating rating;
	private long releaseDate;
	private IDContainer[] reviews;
	private String tag;
	private IDContainer[] updates;
	private IDContainer version;
	private IDContainer[] versions;

	public Author getAu() {
		return au;
	}

	public Author getAuthor() {
		return au == null ? au = Author.getAuthor(author.id + "") : au;
	}

	public IDContainer getCategory() {
		return category;
	}

	public String getCurrency() {
		return currency;
	}

	public int getDownloads() {
		return downloads;
	}

	public IconContainer getIcon() {
		return icon;
	}

	public int getId() {
		return id;
	}

	public int getLikes() {
		return likes;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public ResourceRating getRating() {
		return rating;
	}

	public long getReleaseDate() {
		return releaseDate;
	}

	public IDContainer[] getReviews() {
		return reviews;
	}

	public String getTag() {
		return tag;
	}

	public IDContainer[] getUpdates() {
		return updates;
	}

	public IDContainer getVersion() {
		return version;
	}

	public IDContainer[] getVersions() {
		return versions;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setName(String name) {
		this.name = name;
	}
}
