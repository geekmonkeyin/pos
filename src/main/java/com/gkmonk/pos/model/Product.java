package com.gkmonk.pos.model;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
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

    //generate getter setter for all fields
    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getOption1Name() {
        return option1Name;
    }

    public void setOption1Name(String option1Name) {
        this.option1Name = option1Name;
    }

    public String getOption1Value() {
        return option1Value;
    }

    public void setOption1Value(String option1Value) {
        this.option1Value = option1Value;
    }

    public String getOption1LinkedTo() {
        return option1LinkedTo;
    }

    public void setOption1LinkedTo(String option1LinkedTo) {
        this.option1LinkedTo = option1LinkedTo;
    }

    public String getOption2Name() {
        return option2Name;
    }

    public void setOption2Name(String option2Name) {
        this.option2Name = option2Name;
    }

    public String getOption2Value() {
        return option2Value;
    }

    public void setOption2Value(String option2Value) {
        this.option2Value = option2Value;
    }

    public String getOption2LinkedTo() {
        return option2LinkedTo;
    }

    public void setOption2LinkedTo(String option2LinkedTo) {
        this.option2LinkedTo = option2LinkedTo;
    }

    public String getOption3Name() {
        return option3Name;
    }

    public void setOption3Name(String option3Name) {
        this.option3Name = option3Name;
    }

    public String getOption3Value() {
        return option3Value;
    }

    public void setOption3Value(String option3Value) {
        this.option3Value = option3Value;
    }

    public String getOption3LinkedTo() {
        return option3LinkedTo;
    }

    public void setOption3LinkedTo(String option3LinkedTo) {
        this.option3LinkedTo = option3LinkedTo;
    }

    public String getVariantSku() {
        return variantSku;
    }

    public void setVariantSku(String variantSku) {
        this.variantSku = variantSku;
    }

    public Double getVariantGrams() {
        return variantGrams;
    }

    public void setVariantGrams(Double variantGrams) {
        this.variantGrams = variantGrams;
    }

    public String getVariantInventoryTracker() {
        return variantInventoryTracker;
    }

    public void setVariantInventoryTracker(String variantInventoryTracker) {
        this.variantInventoryTracker = variantInventoryTracker;
    }

    public String getVariantInventoryPolicy() {
        return variantInventoryPolicy;
    }

    public void setVariantInventoryPolicy(String variantInventoryPolicy) {
        this.variantInventoryPolicy = variantInventoryPolicy;
    }

    public String getVariantFulfillmentService() {
        return variantFulfillmentService;
    }

    public void setVariantFulfillmentService(String variantFulfillmentService) {
        this.variantFulfillmentService = variantFulfillmentService;
    }

    public double getVariantPrice() {
        return variantPrice;
    }

    public void setVariantPrice(double variantPrice) {
        this.variantPrice = variantPrice;
    }

    public double getVariantCompareAtPrice() {
        return variantCompareAtPrice;
    }

    public void setVariantCompareAtPrice(double variantCompareAtPrice) {
        this.variantCompareAtPrice = variantCompareAtPrice;
    }

    public boolean isVariantRequiresShipping() {
        return variantRequiresShipping;
    }

    public void setVariantRequiresShipping(boolean variantRequiresShipping) {
        this.variantRequiresShipping = variantRequiresShipping;
    }

    public boolean isVariantTaxable() {
        return variantTaxable;
    }

    public void setVariantTaxable(boolean variantTaxable) {
        this.variantTaxable = variantTaxable;
    }

    public String getVariantBarcode() {
        return variantBarcode;
    }

    public void setVariantBarcode(String variantBarcode) {
        this.variantBarcode = variantBarcode;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public int getImagePosition() {
        return imagePosition;
    }

    public void setImagePosition(int imagePosition) {
        this.imagePosition = imagePosition;
    }

    public String getImageAltText() {
        return imageAltText;
    }

    public void setImageAltText(String imageAltText) {
        this.imageAltText = imageAltText;
    }

    public boolean isGiftCard() {
        return giftCard;
    }

    public void setGiftCard(boolean giftCard) {
        this.giftCard = giftCard;
    }

    public String getSeoTitle() {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle;
    }

    public String getSeoDescription() {
        return seoDescription;
    }

    public void setSeoDescription(String seoDescription) {
        this.seoDescription = seoDescription;
    }

    public String getGoogleProductCategory() {
        return googleProductCategory;
    }

    public void setGoogleProductCategory(String googleProductCategory) {
        this.googleProductCategory = googleProductCategory;
    }

    public String getGoogleGender() {
        return googleGender;
    }

    public void setGoogleGender(String googleGender) {
        this.googleGender = googleGender;
    }

    public String getGoogleAgeGroup() {
        return googleAgeGroup;
    }

    public void setGoogleAgeGroup(String googleAgeGroup) {
        this.googleAgeGroup = googleAgeGroup;
    }

    public String getGoogleMpn() {
        return googleMpn;
    }

    public void setGoogleMpn(String googleMpn) {
        this.googleMpn = googleMpn;
    }

    public String getGoogleCondition() {
        return googleCondition;
    }

    public void setGoogleCondition(String googleCondition) {
        this.googleCondition = googleCondition;
    }

    public String getGoogleCustomLabel0() {
        return googleCustomLabel0;
    }

    public void setGoogleCustomLabel0(String googleCustomLabel0) {
        this.googleCustomLabel0 = googleCustomLabel0;
    }

    public String getGoogleCustomLabel1() {
        return googleCustomLabel1;
    }

    public void setGoogleCustomLabel1(String googleCustomLabel1) {
        this.googleCustomLabel1 = googleCustomLabel1;
    }

    public String getGoogleCustomLabel2() {
        return googleCustomLabel2;
    }

    public void setGoogleCustomLabel2(String googleCustomLabel2) {
        this.googleCustomLabel2 = googleCustomLabel2;
    }

    public String getGoogleCustomLabel3() {
        return googleCustomLabel3;
    }

    public void setGoogleCustomLabel3(String googleCustomLabel3) {
        this.googleCustomLabel3 = googleCustomLabel3;
    }

    public String getGoogleCustomLabel4() {
        return googleCustomLabel4;
    }

    public void setGoogleCustomLabel4(String googleCustomLabel4) {
        this.googleCustomLabel4 = googleCustomLabel4;
    }

    public String getBundlyExtraData() {
        return bundlyExtraData;
    }

    public void setBundlyExtraData(String bundlyExtraData) {
        this.bundlyExtraData = bundlyExtraData;
    }

    public String getColumn1Description() {
        return column1Description;
    }

    public void setColumn1Description(String column1Description) {
        this.column1Description = column1Description;
    }

    public String getColumn1Header() {
        return column1Header;
    }

    public void setColumn1Header(String column1Header) {
        this.column1Header = column1Header;
    }

    public String getColumn2Description() {
        return column2Description;
    }

    public void setColumn2Description(String column2Description) {
        this.column2Description = column2Description;
    }

    public String getColumn2Header() {
        return column2Header;
    }

    public void setColumn2Header(String column2Header) {
        this.column2Header = column2Header;
    }

    public String getColumn3Description() {
        return column3Description;
    }

    public void setColumn3Description(String column3Description) {
        this.column3Description = column3Description;
    }

    public String getColumn3Header() {
        return column3Header;
    }

    public void setColumn3Header(String column3Header) {
        this.column3Header = column3Header;
    }

    public String getColumnHeadlines() {
        return columnHeadlines;
    }

    public void setColumnHeadlines(String columnHeadlines) {
        this.columnHeadlines = columnHeadlines;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getInformativeHeadline1() {
        return informativeHeadline1;
    }

    public void setInformativeHeadline1(String informativeHeadline1) {
        this.informativeHeadline1 = informativeHeadline1;
    }

    public String getInformativeSubheadline1() {
        return informativeSubheadline1;
    }

    public void setInformativeSubheadline1(String informativeSubheadline1) {
        this.informativeSubheadline1 = informativeSubheadline1;
    }

    public String getMakingTime() {
        return makingTime;
    }

    public void setMakingTime(String makingTime) {
        this.makingTime = makingTime;
    }

    public String getNocod() {
        return nocod;
    }

    public void setNocod(String nocod) {
        this.nocod = nocod;
    }

    public String getNonPersonalizedDelivery() {
        return nonPersonalizedDelivery;
    }

    public void setNonPersonalizedDelivery(String nonPersonalizedDelivery) {
        this.nonPersonalizedDelivery = nonPersonalizedDelivery;
    }

    public boolean isNonReturnableProduct() {
        return nonReturnableProduct;
    }

    public void setNonReturnableProduct(boolean nonReturnableProduct) {
        this.nonReturnableProduct = nonReturnableProduct;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getProductVideo() {
        return productVideo;
    }

    public void setProductVideo(String productVideo) {
        this.productVideo = productVideo;
    }

    public String getScs() {
        return scs;
    }

    public void setScs(String scs) {
        this.scs = scs;
    }

    public String getSizeOfProduct() {
        return sizeOfProduct;
    }

    public void setSizeOfProduct(String sizeOfProduct) {
        this.sizeOfProduct = sizeOfProduct;
    }

    public String getStorageRack() {
        return storageRack;
    }

    public void setStorageRack(String storageRack) {
        this.storageRack = storageRack;
    }

    public boolean isGoogleCustomProduct() {
        return googleCustomProduct;
    }

    public void setGoogleCustomProduct(boolean googleCustomProduct) {
        this.googleCustomProduct = googleCustomProduct;
    }

    public int getProductRatingCount() {
        return productRatingCount;
    }

    public void setProductRatingCount(int productRatingCount) {
        this.productRatingCount = productRatingCount;
    }

    public String getBagCaseFeatures() {
        return bagCaseFeatures;
    }

    public void setBagCaseFeatures(String bagCaseFeatures) {
        this.bagCaseFeatures = bagCaseFeatures;
    }

    public String getBagCaseMaterial() {
        return bagCaseMaterial;
    }

    public void setBagCaseMaterial(String bagCaseMaterial) {
        this.bagCaseMaterial = bagCaseMaterial;
    }

    public String getBagCaseStorageFeatures() {
        return bagCaseStorageFeatures;
    }

    public void setBagCaseStorageFeatures(String bagCaseStorageFeatures) {
        this.bagCaseStorageFeatures = bagCaseStorageFeatures;
    }

    public String getCarryOptions() {
        return carryOptions;
    }

    public void setCarryOptions(String carryOptions) {
        this.carryOptions = carryOptions;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCoverColor() {
        return coverColor;
    }

    public void setCoverColor(String coverColor) {
        this.coverColor = coverColor;
    }

    public String getDecorationMaterial() {
        return decorationMaterial;
    }

    public void setDecorationMaterial(String decorationMaterial) {
        this.decorationMaterial = decorationMaterial;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getGiftBagHandleDesign() {
        return giftBagHandleDesign;
    }

    public void setGiftBagHandleDesign(String giftBagHandleDesign) {
        this.giftBagHandleDesign = giftBagHandleDesign;
    }

    public String getLuggageBagClosure() {
        return luggageBagClosure;
    }

    public void setLuggageBagClosure(String luggageBagClosure) {
        this.luggageBagClosure = luggageBagClosure;
    }

    public String getMountStandFeatures() {
        return mountStandFeatures;
    }

    public void setMountStandFeatures(String mountStandFeatures) {
        this.mountStandFeatures = mountStandFeatures;
    }

    public String getRecommendedAgeGroup() {
        return recommendedAgeGroup;
    }

    public void setRecommendedAgeGroup(String recommendedAgeGroup) {
        this.recommendedAgeGroup = recommendedAgeGroup;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public String getSuitableSpace() {
        return suitableSpace;
    }

    public void setSuitableSpace(String suitableSpace) {
        this.suitableSpace = suitableSpace;
    }

    public String getToyFigureFeatures() {
        return toyFigureFeatures;
    }

    public void setToyFigureFeatures(String toyFigureFeatures) {
        this.toyFigureFeatures = toyFigureFeatures;
    }

    public String getToyGameMaterial() {
        return toyGameMaterial;
    }

    public void setToyGameMaterial(String toyGameMaterial) {
        this.toyGameMaterial = toyGameMaterial;
    }

    public String getVehicleApplicationArea() {
        return vehicleApplicationArea;
    }

    public void setVehicleApplicationArea(String vehicleApplicationArea) {
        this.vehicleApplicationArea = vehicleApplicationArea;
    }

    public String getVehicleDecorFeatures() {
        return vehicleDecorFeatures;
    }

    public void setVehicleDecorFeatures(String vehicleDecorFeatures) {
        this.vehicleDecorFeatures = vehicleDecorFeatures;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getComplementaryProducts() {
        return complementaryProducts;
    }

    public void setComplementaryProducts(String complementaryProducts) {
        this.complementaryProducts = complementaryProducts;
    }

    public String getRelatedProducts() {
        return relatedProducts;
    }

    public void setRelatedProducts(String relatedProducts) {
        this.relatedProducts = relatedProducts;
    }

    public String getRelatedProductsSettings() {
        return relatedProductsSettings;
    }

    public void setRelatedProductsSettings(String relatedProductsSettings) {
        this.relatedProductsSettings = relatedProductsSettings;
    }

    public String getSearchProductBoosts() {
        return searchProductBoosts;
    }

    public void setSearchProductBoosts(String searchProductBoosts) {
        this.searchProductBoosts = searchProductBoosts;
    }

    public String getVariantImage() {
        return variantImage;
    }

    public void setVariantImage(String variantImage) {
        this.variantImage = variantImage;
    }

    public String getVariantWeightUnit() {
        return variantWeightUnit;
    }

    public void setVariantWeightUnit(String variantWeightUnit) {
        this.variantWeightUnit = variantWeightUnit;
    }

    public String getVariantTaxCode() {
        return variantTaxCode;
    }

    public void setVariantTaxCode(String variantTaxCode) {
        this.variantTaxCode = variantTaxCode;
    }

    public double getCostPerItem() {
        return costPerItem;
    }

    public void setCostPerItem(double costPerItem) {
        this.costPerItem = costPerItem;
    }

    public boolean isIncludedIndia() {
        return includedIndia;
    }

    public void setIncludedIndia(boolean includedIndia) {
        this.includedIndia = includedIndia;
    }

    public double getPriceIndia() {
        return priceIndia;
    }

    public void setPriceIndia(double priceIndia) {
        this.priceIndia = priceIndia;
    }

    public double getCompareAtPriceIndia() {
        return compareAtPriceIndia;
    }

    public void setCompareAtPriceIndia(double compareAtPriceIndia) {
        this.compareAtPriceIndia = compareAtPriceIndia;
    }

    public boolean isIncludedInternational() {
        return includedInternational;
    }

    public void setIncludedInternational(boolean includedInternational) {
        this.includedInternational = includedInternational;
    }

    public double getPriceInternational() {
        return priceInternational;
    }

    public void setPriceInternational(double priceInternational) {
        this.priceInternational = priceInternational;
    }

    public double getCompareAtPriceInternational() {
        return compareAtPriceInternational;
    }

    public void setCompareAtPriceInternational(double compareAtPriceInternational) {
        this.compareAtPriceInternational = compareAtPriceInternational;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}