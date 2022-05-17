/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package router;

import domain.Sale;
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
                .unmarshal().json(JsonLibrary.Gson, Sale.class)
                .to("jms:queue:rest-sale");

        from("jms:queue:rest-sale")
                .marshal().json(JsonLibrary.Gson) // only necessary if object needs to be converted to JSON
                .removeHeaders("*") // remove headers to stop them being sent to the service
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("http://localhost:8083/api/sales")
                .to("jms:queue:http-response");  // HTTP response ends up in this queue
    }
}
