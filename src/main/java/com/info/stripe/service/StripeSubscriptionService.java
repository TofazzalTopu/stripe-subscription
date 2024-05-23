package com.info.stripe.service;

import com.info.stripe.model.ChargeRequest;
import com.info.stripe.model.CreateCustomerBody;
import com.info.stripe.model.SubscriptionInfo;
import com.stripe.exception.StripeException;
import com.stripe.model.*;

public interface StripeSubscriptionService {

   Customer createCustomer(CreateCustomerBody createCustomerBody) throws StripeException;

   PaymentIntent createPaymentIntent() throws StripeException;

   Price createPrice(Long amount) throws StripeException;
   SubscriptionInfo createSubscription(ChargeRequest chargeRequest) throws StripeException;

   SubscriptionCollection subscriptions() throws StripeException;

   Subscription retrieveSubscription(String subscriptionId) throws StripeException;
}
