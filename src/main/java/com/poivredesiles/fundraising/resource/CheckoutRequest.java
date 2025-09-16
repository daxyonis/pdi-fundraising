package com.poivredesiles.fundraising.resource;

import java.util.List;

public record CheckoutRequest(Customer customer, ShoppingCart shoppingCart) {

    public record Customer(
            String email,
            String firstName,
            String lastName,
            String phoneNumber
    ) {}

    public record ShoppingCart(List<LineItem> lineItems) {}

    public record LineItem(
            String note,
            String name,
            int price,
            int unitQty
    ) {}
}


