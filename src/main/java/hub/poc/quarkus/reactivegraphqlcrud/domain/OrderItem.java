package hub.poc.quarkus.reactivegraphqlcrud.domain;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
        name="PURCHASE_ORDER_ITEM", 
        uniqueConstraints=
            @UniqueConstraint(columnNames={"ID", "PRODUCT_ID"}, name = "ORDERITEM_PRODUCT_UK")
    )
public class OrderItem {

    @Id
    @SequenceGenerator(name = "orderItemIdSeq", sequenceName = "orderItemIdSeq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "orderItemIdSeq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ORDER_ID", nullable = false)
    @JsonBackReference
    @JsonbTransient
    private Order parentOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    private Double quantity;
    private String instructions;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Order getParentOrder() {
        return parentOrder;
    }
    public void setParentOrder(Order parentOrder) {
        this.parentOrder = parentOrder;
    }

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Double getQuantity() {
        return quantity;
    }
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    @Override
    public String toString() {
        return "OrderItem [id=" + id + ", instructions=" + instructions
                + ", product=" + product + ", quantity=" + quantity + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((parentOrder == null) ? 0 : parentOrder.hashCode());
        result = prime * result + ((product == null) ? 0 : product.hashCode());
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
        OrderItem other = (OrderItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (parentOrder == null) {
            if (other.parentOrder != null)
                return false;
        } else if (!parentOrder.equals(other.parentOrder))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        return true;
    }


    
    
}
