package product.manager;

/*
class to hold the product
*/
public class Product {

    public int productId;
    public String productCode;
    public String productDescription;
    public String dateCreated;
    public String expiryDate;


    public Product() {

    }

    public Product(int productId, String productCode,String productDescription,
                   String dateCreated,String expiryDate) {
        this.productId = productId;
        this.productCode = productCode;
        this.productDescription=productDescription;
        this.dateCreated = dateCreated;
        this.expiryDate = expiryDate;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

}

