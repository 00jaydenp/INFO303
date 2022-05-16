/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package router;

import domain.Account;
import domain.Customer;

/**
 *
 * @author Jayden
 */
public class AccountConverter {

    public Customer accountToCustomer(Account account) {
        Customer customer = new Customer();
        customer.setId(account.getId());
        customer.setEmail(account.getEmail());
        customer.setFirstName(account.getFirstName());
        customer.setLastName(account.getLastName());
        customer.setCustomerCode(account.getUsername());
        customer.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");
        return customer;
    }
}
