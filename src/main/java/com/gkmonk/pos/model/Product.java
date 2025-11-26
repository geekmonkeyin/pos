package com.gkmonk.pos.model;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Data
public class Product {

    @CSVAnnotations(column = "Handle")
    private String handle;

    @CSVAnnotations(column = "Title")
    private String title;

    @CSVAnnotations(column = "Body (HTML)")
    private String bodyHtml;

    @CSVAnnotations(column = "Vendor")
    private String vendor;

    @CSVAnnotations(column = "Product Category")
    private String productCategory;

    @CSVAnnotations(column = "Type")
    private String type;

    @CSVAnnotations(column = "Tags")
    private String tags;

    @CSVAnnotations(column = "Published")
    private boolean published;

    @CSVAnnotations(column = "Option1 Name")
    private String option1Name;

    @CSVAnnotations(column = "Option1 Value")
    private String option1Value;

    @CSVAnnotations(column = "Option1 Linked To")
    private String option1LinkedTo;

    @CSVAnnotations(column = "Option2 Name")
    private String option2Name;

    @CSVAnnotations(column = "Option2 Value")
    private String option2Value;

    @CSVAnnotations(column = "Option2 Linked To")
    private String option2LinkedTo;

    @CSVAnnotations(column = "Option3 Name")
    private String option3Name;

    @CSVAnnotations(column = "Option3 Value")
    private String option3Value;

    @CSVAnnotations(column = "Option3 Linked To")
    private String option3LinkedTo;

    @CSVAnnotations(column = "Variant SKU")
    private String variantSku;

    @CSVAnnotations(column = "Variant Grams")
    private Double variantGrams;

    @CSVAnnotations(column = "Variant Inventory Tracker")
    private String variantInventoryTracker;

    @CSVAnnotations(column = "Variant Inventory Policy")
    private String variantInventoryPolicy;

    @CSVAnnotations(column = "Variant Fulfillment Service")
    private String variantFulfillmentService;

    @CSVAnnotations(column = "Variant Price")
    private double variantPrice;

    @CSVAnnotations(column = "Variant Compare At Price")
    private double variantCompareAtPrice;

    @CSVAnnotations(column = "Variant Requires Shipping")
    private boolean variantRequiresShipping;

    @CSVAnnotations(column = "Variant Taxable")
    private boolean variantTaxable;

    @CSVAnnotations(column = "Variant Barcode")
    private String variantBarcode;

    @CSVAnnotations(column = "Image Src")
    private String imageSrc;

    @CSVAnnotations(column = "Image Position")
    private int imagePosition;

    @CSVAnnotations(column = "Image Alt Text")
    private String imageAltText;

    @CSVAnnotations(column = "Gift Card")
    private boolean giftCard;

    @CSVAnnotations(column = "SEO Title")
    private String seoTitle;

    @CSVAnnotations(column = "SEO Description")
    private String seoDescription;

    @CSVAnnotations(column = "Google Shopping / Google Product Category")
    private String googleProductCategory;

    @CSVAnnotations(column = "Google Shopping / Gender")
    private String googleGender;

    @CSVAnnotations(column = "Google Shopping / Age Group")
    private String googleAgeGroup;

    @CSVAnnotations(column = "Google Shopping / MPN")
    private String googleMpn;

    @CSVAnnotations(column = "Google Shopping / Condition")
    private String googleCondition;

    @CSVAnnotations(column = "Google Shopping / Custom Product")
    private boolean googleCustomProduct;

    @CSVAnnotations(column = "Google Shopping / Custom Label 0")
    private String googleCustomLabel0;

    @CSVAnnotations(column = "Google Shopping / Custom Label 1")
    private String googleCustomLabel1;

    @CSVAnnotations(column = "Google Shopping / Custom Label 2")
    private String googleCustomLabel2;

    @CSVAnnotations(column = "Google Shopping / Custom Label 3")
    private String googleCustomLabel3;

    @CSVAnnotations(column = "Google Shopping / Custom Label 4")
    private String googleCustomLabel4;

    @CSVAnnotations(column = "Bundly Extra Data (product.metafields.bundly.extra_data)")
    private String bundlyExtraData;

    @CSVAnnotations(column = "column 1 description (product.metafields.custom.column_1_description)")
    private String column1Description;

    @CSVAnnotations(column = "column 1 header (product.metafields.custom.column_1_header)")
    private String column1Header;

    @CSVAnnotations(column = "column 2 description (product.metafields.custom.column_2_description)")
    private String column2Description;

    @CSVAnnotations(column = "column 2 header (product.metafields.custom.column_2_header)")
    private String column2Header;

    @CSVAnnotations(column = "column 3 description (product.metafields.custom.column_3_description)")
    private String column3Description;

    @CSVAnnotations(column = "column 3 header (product.metafields.custom.column_3_header)")
    private String column3Header;

    @CSVAnnotations(column = "column headlines (product.metafields.custom.column_headlines)")
    private String columnHeadlines;

    @CSVAnnotations(column = "Delivery Time (product.metafields.custom.delivery_time)")
    private String deliveryTime;

    @CSVAnnotations(column = "informative headline1 (product.metafields.custom.informative_headline1)")
    private String informativeHeadline1;

    @CSVAnnotations(column = "informative subheadline1 (product.metafields.custom.informative_subheadline1)")
    private String informativeSubheadline1;

    @CSVAnnotations(column = "makingtime (product.metafields.custom.makingtime)")
    private String makingTime;

    @CSVAnnotations(column = "nocod (product.metafields.custom.nocod)")
    private String nocod;

    @CSVAnnotations(column = "Delivery Time for Non Personalized Product (product.metafields.custom.nonpersonalizeddelivery)")
    private String nonPersonalizedDelivery;

    @CSVAnnotations(column = "nonreturnableproduct (product.metafields.custom.nonreturnableproduct)")
    private boolean nonReturnableProduct;

    @CSVAnnotations(column = "premium (product.metafields.custom.premium)")
    private boolean premium;

    @CSVAnnotations(column = "productvideo (product.metafields.custom.productvideo)")
    private String productVideo;

    @CSVAnnotations(column = "scs (product.metafields.custom.scs)")
    private String scs;

    @CSVAnnotations(column = "sizeofproduct (product.metafields.custom.sizeofproduct)")
    private String sizeOfProduct;

    @CSVAnnotations(column = "storage_rack (product.metafields.custom.storage)")
    private String storageRack;

    @CSVAnnotations(column = "Product rating count (product.metafields.reviews.rating_count)")
    private int productRatingCount;

    @CSVAnnotations(column = "Bag/Case features (product.metafields.shopify.bag-case-features)")
    private String bagCaseFeatures;

    @CSVAnnotations(column = "Bag/Case material (product.metafields.shopify.bag-case-material)")
    private String bagCaseMaterial;

    @CSVAnnotations(column = "Bag/Case storage features (product.metafields.shopify.bag-case-storage-features)")
    private String bagCaseStorageFeatures;

    @CSVAnnotations(column = "Carry options (product.metafields.shopify.carry-options)")
    private String carryOptions;

    @CSVAnnotations(column = "Color (product.metafields.shopify.color-pattern)")
    private String color;

    @CSVAnnotations(column = "Cover color (product.metafields.shopify.cover-color)")
    private String coverColor;

    @CSVAnnotations(column = "Decoration material (product.metafields.shopify.decoration-material)")
    private String decorationMaterial;

    @CSVAnnotations(column = "Fabric (product.metafields.shopify.fabric)")
    private String fabric;

    @CSVAnnotations(column = "Gift bag handle design (product.metafields.shopify.gift-bag-handle-design)")
    private String giftBagHandleDesign;

    @CSVAnnotations(column = "Luggage/Bag closure (product.metafields.shopify.luggage-bag-closure)")
    private String luggageBagClosure;

    @CSVAnnotations(column = "Mount/Stand features (product.metafields.shopify.mount-stand-features)")
    private String mountStandFeatures;

    @CSVAnnotations(column = "Recommended age group (product.metafields.shopify.recommended-age-group)")
    private String recommendedAgeGroup;

    @CSVAnnotations(column = "Size (product.metafields.shopify.size)")
    private String size;

    @CSVAnnotations(column = "Skill level (product.metafields.shopify.skill-level)")
    private String skillLevel;

    @CSVAnnotations(column = "Suitable space (product.metafields.shopify.suitable-space)")
    private String suitableSpace;

    @CSVAnnotations(column = "Toy figure features (product.metafields.shopify.toy-figure-features)")
    private String toyFigureFeatures;

    @CSVAnnotations(column = "Toy/Game material (product.metafields.shopify.toy-game-material)")
    private String toyGameMaterial;

    @CSVAnnotations(column = "Vehicle application area (product.metafields.shopify.vehicle-application-area)")
    private String vehicleApplicationArea;

    @CSVAnnotations(column = "Vehicle decor features (product.metafields.shopify.vehicle-decor-features)")
    private String vehicleDecorFeatures;

    @CSVAnnotations(column = "Vehicle type (product.metafields.shopify.vehicle-type)")
    private String vehicleType;

    @CSVAnnotations(column = "Complementary products (product.metafields.shopify--discovery--product_recommendation.complementary_products)")
    private String complementaryProducts;

    @CSVAnnotations(column = "Related products (product.metafields.shopify--discovery--product_recommendation.related_products)")
    private String relatedProducts;

    @CSVAnnotations(column = "Related products settings (product.metafields.shopify--discovery--product_recommendation.related_products_display)")
    private String relatedProductsSettings;

    @CSVAnnotations(column = "Search product boosts (product.metafields.shopify--discovery--product_search_boost.queries)")
    private String searchProductBoosts;

    @CSVAnnotations(column = "Variant Image")
    private String variantImage;

    @CSVAnnotations(column = "Variant Weight Unit")
    private String variantWeightUnit;

    @CSVAnnotations(column = "Variant Tax Code")
    private String variantTaxCode;

    @CSVAnnotations(column = "Cost per item")
    private double costPerItem;

    @CSVAnnotations(column = "Included / India")
    private boolean includedIndia;

    @CSVAnnotations(column = "Price / India")
    private double priceIndia;

    @CSVAnnotations(column = "Compare At Price / India")
    private double compareAtPriceIndia;

    @CSVAnnotations(column = "Included / International")
    private boolean includedInternational;

    @CSVAnnotations(column = "Price / International")
    private double priceInternational;

    @CSVAnnotations(column = "Compare At Price / International")
    private double compareAtPriceInternational;

    @CSVAnnotations(column = "Status")
    private String status;

    private String hsncode;


}