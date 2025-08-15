package com.gkmonk.pos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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



    // Getters and Setters

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getProductVariantSku() {
        return productVariantSku;
    }

    public void setProductVariantSku(String productVariantSku) {
        this.productVariantSku = productVariantSku;
    }

    public String getProductVariantTitle() {
        return productVariantTitle;
    }

    public void setProductVariantTitle(String productVariantTitle) {
        this.productVariantTitle = productVariantTitle;
    }

    public String getOrderOrReturn() {
        return orderOrReturn;
    }

    public void setOrderOrReturn(String orderOrReturn) {
        this.orderOrReturn = orderOrReturn;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getBillingRegion() {
        return billingRegion;
    }

    public void setBillingRegion(String billingRegion) {
        this.billingRegion = billingRegion;
    }

    public int getNetItemsSold() {
        return netItemsSold;
    }

    public void setNetItemsSold(int netItemsSold) {
        this.netItemsSold = netItemsSold;
    }

    public double getGrossSales() {
        return grossSales;
    }

    public void setGrossSales(double grossSales) {
        this.grossSales = grossSales;
    }

    public double getDiscounts() {
        return discounts;
    }

    public void setDiscounts(double discounts) {
        this.discounts = discounts;
    }

    public double getReturns() {
        return returns;
    }

    public void setReturns(double returns) {
        this.returns = returns;
    }

    public double getNetSales() {
        return netSales;
    }

    public void setNetSales(double netSales) {
        this.netSales = netSales;
    }

    public double getTaxes() {
        return taxes;
    }

    public void setTaxes(double taxes) {
        this.taxes = taxes;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

       public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public double getGstRate() {
        return gstRate;
    }

    public void setGstRate(double gstRate) {
        this.gstRate = gstRate;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public double getIgstAmount() {
        return igstAmount;
    }

    public void setIgstAmount(double igstAmount) {
        this.igstAmount = igstAmount;
    }

    public double getSgstPerc() {
        return sgstPerc;
    }

    public void setSgstPerc(double sgstPerc) {
        this.sgstPerc = sgstPerc;
    }

    public double getCgstPerc() {
        return cgstPerc;
    }

    public void setCgstPerc(double cgstPerc) {
        this.cgstPerc = cgstPerc;
    }

    public double getIgstPerc() {
        return igstPerc;
    }

    public void setIgstPerc(double igstPerc) {
        this.igstPerc = igstPerc;
    }

    public double getCgstAmount() {
        return cgstAmount;
    }

    public void setCgstAmount(double cgstAmount) {
        this.cgstAmount = cgstAmount;
    }

    public double getSgstAmount() {
        return sgstAmount;
    }

    public void setSgstAmount(double sgstAmount) {
        this.sgstAmount = sgstAmount;
    }
}