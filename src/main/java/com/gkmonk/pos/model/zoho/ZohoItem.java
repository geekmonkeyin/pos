package com.gkmonk.pos.model.zoho;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("zoho_item_names")
@Data
public class ZohoItem {

    @Field("_id")
    private ObjectId id;

    @Field("Item ID")
    private Long itemId;

    @Field("Item Name")
    private String itemName;

    @Field("HSN/SAC")
    private Integer hsnSac;

    @Field("Is Tax Calculated on Label Price")
    private Boolean isTaxCalculatedOnLabelPrice;

    @Field("Rate")
    private String rate;

    @Field("MRP")
    private String mrp;

    @Field("Taxable")
    private Boolean taxable;

    @Field("Product Type")
    private String productType;

    @Field("Intra State Tax Name")
    private String intraStateTaxName;

    @Field("Intra State Tax Rate")
    private Integer intraStateTaxRate;

    @Field("Intra State Tax Type")
    private String intraStateTaxType;

    @Field("Inter State Tax Name")
    private String interStateTaxName;

    @Field("Inter State Tax Rate")
    private Integer interStateTaxRate;

    @Field("Inter State Tax Type")
    private String interStateTaxType;

    @Field("Source")
    private Integer source;

    @Field("Status")
    private String status;

    @Field("Usage unit")
    private String usageUnit;

    @Field("Unit Name")
    private String unitName;

    @Field("Purchase Rate")
    private String purchaseRate;

    @Field("Purchase Account")
    private String purchaseAccount;

    @Field("Vendor")
    private String vendor;

    @Field("Item Type")
    private String itemType;

    @Field("Sellable")
    private Boolean sellable;

    @Field("Purchasable")
    private Boolean purchasable;

    @Field("Track Inventory")
    private Boolean trackInventory;

}