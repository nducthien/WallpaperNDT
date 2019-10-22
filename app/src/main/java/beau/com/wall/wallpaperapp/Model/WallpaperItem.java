package beau.com.wall.wallpaperapp.Model;

public class WallpaperItem {
    public String categoryId;
    public String imageLink;
    public long viewCount;

    public WallpaperItem() {
    }

    public WallpaperItem(String categoryId, String imageLink, long viewCount) {
        this.categoryId = categoryId;
        this.imageLink = imageLink;
        this.viewCount = viewCount;
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

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }
}
