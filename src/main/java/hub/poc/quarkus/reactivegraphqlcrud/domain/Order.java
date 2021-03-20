package hub.poc.quarkus.reactivegraphqlcrud.domain;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
        name="PURCHASE_ORDER", 
        uniqueConstraints=
            @UniqueConstraint(columnNames={"REF_NO"}, name = "ORDER_REFNO_UK")
    )
public class Order {

    private Long id;
    private String refNumber;
    private Customer customer;
    private Date orderDateTime;
    private String currentStatus;

    
    @Id
    @SequenceGenerator(name = "orderIdSeq", sequenceName = "orderIdSeq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "orderIdSeq")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "REF_NO")
    public String getRefNumber() {
        return refNumber;
    }
    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getOrderDateTime() {
        return orderDateTime;
    }
    public void setOrderDateTime(Date orderDateTime) {
        this.orderDateTime = orderDateTime;
    }
    public String getCurrentStatus() {
        return currentStatus;
    }
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    @Override
    public String toString() {
        return "Order [currentStatus=" + currentStatus + ", customer=" + customer + ", id=" + id + ", orderDateTime="
                + orderDateTime + ", reFNumber=" + refNumber + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((refNumber == null) ? 0 : refNumber.hashCode());
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
        Order other = (Order) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (refNumber == null) {
            if (other.refNumber != null)
                return false;
        } else if (!refNumber.equals(other.refNumber))
            return false;
        return true;
    }
  
    
    
}
