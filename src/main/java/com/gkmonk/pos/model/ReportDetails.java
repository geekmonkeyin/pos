package com.gkmonk.pos.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "sales_report")
public class ReportDetails {

    @CSVAnnotations(column = "id")
    @Id
    private String id;


    @CSVAnnotations(column = "product_title")
    private String productTitle;

    @CSVAnnotations(column = "product_type")
    private String productType;

    @CSVAnnotations(column = "day")
    private String day;

    @CSVAnnotations(column = "product_variant_sku")
    private String productVariantSku;

    @CSVAnnotations(column = "product_variant_title")
    private String productVariantTitle;

    @CSVAnnotations(column = "order_or_return")
    private String orderOrReturn;

    @CSVAnnotations(column = "order_id")
    private String orderId;

    @CSVAnnotations(column = "order_name")
    private String orderName;

    @CSVAnnotations(column = "billing_region")
    private String billingRegion;

    @CSVAnnotations(column = "shipping_region")
    private String shippingRegion;

    @CSVAnnotations(column = "net_items_sold")
    private int netItemsSold;

    @CSVAnnotations(column = "gross_sales")
    private double grossSales;

    @CSVAnnotations(column = "discounts")
    private double discounts;

    @CSVAnnotations(column = "returns")
    private double returns;

    @CSVAnnotations(column = "net_sales")
    private double netSales;

    @CSVAnnotations(column = "taxes")
    private double taxes;

    @CSVAnnotations(column = "total_sales")
    private double totalSales;

    private double gstRate;

    private String stateCode;

    private String hsnCode;

    private double igstPerc;
    private double cgstPerc;
    private double sgstPerc;
    private double igstAmount;
    private double cgstAmount;
    private double sgstAmount;


  }