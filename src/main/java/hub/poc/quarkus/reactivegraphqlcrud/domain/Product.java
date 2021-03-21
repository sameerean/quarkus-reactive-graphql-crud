package hub.poc.quarkus.reactivegraphqlcrud.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
        name="PRODUCT", 
        uniqueConstraints=
            @UniqueConstraint(columnNames={"BARCODE"}, name = "PRODUCT_BARCODE_UK")
    )
public class Product {

    private Long id;
    private String name;
    private String barCode;
    private String brandName;
    private String vendorName;

    
    @Id
    @SequenceGenerator(name = "productIdSeq", sequenceName = "productIdSeq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "productIdSeq")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBarCode() {
        return barCode;
    }
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
    public String getBrandName() {
        return brandName;
    }

    
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    public String getVendorName() {
        return vendorName;
    }
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
    @Override
    public String toString() {
        return "Product [barCode=" + barCode + ", brandName=" + brandName + ", id=" + id + ", name=" + name
                + ", vendorName=" + vendorName + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((barCode == null) ? 0 : barCode.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        if (barCode == null) {
            if (other.barCode != null)
                return false;
        } else if (!barCode.equals(other.barCode))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    
}
