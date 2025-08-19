package com.gkmonk.pos.services.shopify;

public final class ShopifyQueries {
    private ShopifyQueries(){}

    // page query
    public static final String UNFULFILLED_ORDERS = """
  query ($first:Int!, $after:String, $query:String!) {
    orders(first:$first, after:$after, sortKey:PROCESSED_AT, reverse:true, query:$query) {
      pageInfo { hasNextPage endCursor }
      nodes {
        id name processedAt
        displayFulfillmentStatus
        displayFinancialStatus
        customer { displayName email phone }
        shippingAddress { address1 city province country zip phone }
        lineItems(first:50) { nodes { id name sku quantity fulfillableQuantity } }
      }
    }
  }
""";
}
