/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package router;
import domain.Customer;
/**
 *
 * @author Jayden
 */
public class CustomerCreator {
    public Customer createCustomer(String id, String email, String group, String userName, String firstName, String lastName){
        Customer customer = new Customer();
        customer.setId(id);
        customer.setEmail(email);
        customer.setGroup(group);
        customer.setCustomerCode(userName);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        return customer;
    }
}
