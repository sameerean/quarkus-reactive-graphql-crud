package hub.poc.quarkus.reactivegraphqlcrud.customer;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import hub.poc.quarkus.reactivegraphqlcrud.domain.Customer;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Transactional
public class CustomerService {
    
    @Inject
    private CustomerRepo customerRepo;

    public Uni<List<Customer>> findAllCustomers() {
        return customerRepo.findAll().list();
    }

    public Uni<Customer> createCustomer(Customer customer) {
        return this.customerRepo.persist(customer).chain(customerRepo :: flush).onItem().transform(none -> customer);
    }

    public Uni<Customer> updateCustomerById(Long id, Customer customer) {
        return (Uni<Customer>) this.customerRepo.findById(id)
                .onItem().ifNotNull().transformToUni(cust -> 
                    this.customerRepo.persist(this.updateCustomerObject(customer, cust))
                        .chain(customerRepo :: flush).onItem().transform(none -> cust));
    }

    public Uni<Customer> updateCustomerByEmail(String email, Customer customer) {
        return (Uni<Customer>) this.customerRepo.findByEmail(email)
                .onItem().ifNotNull().transformToUni(cust -> 
                    this.customerRepo.persist(this.updateCustomerObject(customer, cust))
                        .chain(customerRepo :: flush).onItem().transform(none -> cust));
    }

    private Customer updateCustomerObject(Customer source, Customer target) {
        target.setAddress(source.getAddress());
        target.setEmail(source.getEmail());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setPhone(source.getPhone());
        return target;
    }

    public Uni<Void> deleteCustomerById(Long id) {
        return this.customerRepo.findById(id)
                .onItem().ifNotNull().transformToUni(cust -> 
                    this.customerRepo.delete(cust)
                        .chain(customerRepo :: flush));
    }

}
