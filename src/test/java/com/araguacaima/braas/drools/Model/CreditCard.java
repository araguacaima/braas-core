package com.araguacaima.braas.drools.Model;

import org.joda.time.DateTime;

public class CreditCard extends io.codearte.jfairy.producer.payment.CreditCard {
    public CreditCard(String cardVendor, DateTime expiryDate) {
        super(cardVendor, expiryDate);
    }
}
