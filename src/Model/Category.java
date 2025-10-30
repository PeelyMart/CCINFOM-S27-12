package Model;

public class Category {
    private int categoryId;
    private String name;

    // Constructor
    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    // Getters
    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
