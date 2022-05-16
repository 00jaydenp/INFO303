/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package router;

import domain.Account;
import domain.Customer;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 *
 * @author Jayden
 */
public class AccountBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // create HTTP endpoint for receiving messages via HTTP
        from("jetty:http://localhost:9000/account?enableCORS=true")
                // make message in-only so web browser doesn't have to wait on a non-existent response
                .setExchangePattern(ExchangePattern.InOnly)
                .convertBodyTo(String.class)
                .log("${body}")
                .unmarshal().json(JsonLibrary.Gson, Account.class)
                .to("jms:queue:account-converter");

        from("jms:queue:account-converter")
                .bean(AccountConverter.class, "accountToCustomer(${body})")
                .to("jms:queue:vend-account");

        from("jms:queue:vend-account")
                // remove headers so they don't get sent to Vend
                .removeHeaders("*")
                // add authentication token to authorization header
                .setHeader("Authorization", constant("Bearer KiQSsELLtocyS2WDN5w5s_jYaBpXa0h2ex1mep1a"))
                // marshal to JSON
                .marshal().json(JsonLibrary.Gson) // only necessary if the message is an object, not JSON
                .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
                // set HTTP method
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                // send it
                .to("https://info303otago.vendhq.com/api/2.0/customers?throwExceptionOnFailure=false")
                // handle response
                .choice()
                .when().simple("${header.CamelHttpResponseCode} == '201'") // change to 200 for PUT
                .convertBodyTo(String.class)
                .to("jms:queue:vend-response")
                .otherwise()
                .log("ERROR RESPONSE ${header.CamelHttpResponseCode} ${body}")
                .convertBodyTo(String.class)
                .to("jms:queue:vend-error")
                .endChoice();

        from("jms:queue:vend-response")
                .log("curr body: ${body}")
                .setBody().jsonpath("$.data")
                .marshal().json(JsonLibrary.Gson)
                .unmarshal().json(JsonLibrary.Gson, Customer.class)
                .to("jms:queue:customer-converter");
        
         from("jms:queue:customer-converter")
                .bean(CustomerConverter.class, "customerToAccount(${body})")
                .to("jms:queue:extracted-customer");

        from("jms:queue:extracted-customer")
                .log("curr body: ${body}")
                .log("curr username: ${body.username}")
                .to("graphql://http://localhost:8082/graphql?query=mutation{addAccount(account: {id:\"${body.id}\", email:\"${body.email}\", username:\"${body.username}\", firstName:\"${body.firstName}\",  lastName:\"${body.lastName}\",  group:\"${body.group}\"}) {id email username firstName lastName group}}")
                .log("GraphQL service called");

    }

}
