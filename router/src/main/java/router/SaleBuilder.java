/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package router;

import domain.Sale;
import domain.Summary;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 *
 * @author Jayden
 */
public class SaleBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("jms:queue:new-sale") // "in" queue contains the JSON 
                .log("${body}")
                .setProperty("group").jsonpath("$.customer.customer_group_id") // extract ID from JSON, and store in header 
                .setProperty("id").jsonpath("$.customer.id")
                .setProperty("email").jsonpath("$.customer.email")
                .setProperty("username").jsonpath("$.customer.customer_code")
                .setProperty("firstname").jsonpath("$.customer.first_name")
                .setProperty("lastname").jsonpath("$.customer.last_name")
                .unmarshal().json(JsonLibrary.Gson, Sale.class)
                .to("jms:queue:post-sale");

        from("jms:queue:post-sale")
                .marshal().json(JsonLibrary.Gson) // only necessary if object needs to be converted to JSON
                .removeHeaders("*") // remove headers to stop them being sent to the service
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("http://localhost:8083/api/sales")
                .to("jms:queue:get-summary");  // HTTP response ends up in this queue

        from("jms:queue:get-summary")
                .removeHeaders("*") // remove headers to stop them being sent to the service
                .setBody(constant(null)) // doesn't usually make sense to pass a body in a GET request
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD("http://localhost:8083/api/sales/customer/${exchangeProperty.id}/summary")

                .unmarshal().json(JsonLibrary.Gson, Summary.class)
                .setProperty("calculatedGroup").method(groupGenerator.class, "generateGroupID(${body.group})")
                .log("total Payment =$ ${body.totalPayment}")
                .choice()
                    .when().simple("${exchangeProperty.calculatedGroup} != ${exchangeProperty.group}")
                    .bean(CustomerCreator.class, "createCustomer(${exchangeProperty.id}, ${exchangeProperty.email}, ${exchangeProperty.calculatedGroup}, ${exchangeProperty.username}, "
                            + "${exchangeProperty.firstname}, ${exchangeProperty.lastname})")
                    .to("jms:queue:created-customer");
        
        from("jms:queue:created-customer")
                .marshal().json(JsonLibrary.Gson) // only necessary if object needs to be converted to JSON
                .log("${body}");
    }
}
