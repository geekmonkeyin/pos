package com.gkmonk.pos.services.shopify;

public final class ShopifyQueries {
    private ShopifyQueries(){}

    // page query
    public static final String UNFULFILLED_ORDERS = """
  query ($first:Int!, $after:String, $query:String!) {
                orders(first:$first, after:$after, sortKey:PROCESSED_AT, reverse:true, query:$query) {
                  pageInfo { hasNextPage endCursor }
                  nodes {
                    id
                    name
                    processedAt
                    displayFulfillmentStatus
                    displayFinancialStatus
            
                    customer {
                      id
                      displayName
                      email
                      phone
                      amountSpent{
                        amount
                        currencyCode
                      }
                      numberOfOrders
                    }
            
                    shippingAddress {
                      address1
                      city
                      province
                      country
                      zip
                      phone
                    }
            
                    # 👇 New: fetch Fulfillment Orders
                    fulfillmentOrders(first: 10) {
                      nodes {
                        id
                        status   # OPEN, IN_PROGRESS, etc.
                        destination {
                          address1
                          city
                          countryCode
                        }
                        lineItems(first: 50) {
                          nodes {
                            id
                            remainingQuantity
                            lineItem {
                              id
                              name
                              sku
                              quantity
                              fulfillableQuantity
                              discountedUnitPriceSet {
                                shopMoney { amount}
                              }
                              variant {
                                    id
                                    image { id url}
                                    product {
                                      id
                                      featuredImage { id url}
                                      images(first: 1) { nodes { id url} }
                                    }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
""";
}
