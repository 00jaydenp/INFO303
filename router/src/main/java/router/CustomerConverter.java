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
public class CustomerConverter {
    	public Account customerToAccount(Customer cust) {
		Account account = new Account();
		account.setId(cust.getId());
		account.setEmail(cust.getEmail());
		account.setFirstName(cust.getFirstName());
		account.setLastName(cust.getLastName());
		account.setUsername(cust.getCustomerCode());
		account.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");

		return account;
	}
}

