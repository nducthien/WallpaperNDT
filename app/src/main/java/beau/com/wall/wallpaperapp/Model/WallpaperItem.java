package beau.com.wall.wallpaperapp.Model;

public class WallpaperItem {
    public String categoryId;
    public String imageLink;

    public WallpaperItem() {
    }

    public WallpaperItem(String categoryId, String imageLink) {
        this.categoryId = categoryId;
        this.imageLink = imageLink;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
