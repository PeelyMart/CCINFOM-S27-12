package Model;

public class MenuItem {
  private int menuId;
  private int categoryId;
  private String menuName;
  private Double price;
  Boolean status;

  MenuItem(int menuId, int categoryId, String menuName, Double price, Boolean status){
    this.menuId = menuId;
    this.categoryId = categoryId;
    this.menuName = menuName;
    this.price = price;
    this.status = status;
  }

 // Getters
    public int getMenuId() {
        return menuId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getMenuName() {
        return menuName;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean getStatus() {
        return status;
    }

    // Setters
    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

  

}
