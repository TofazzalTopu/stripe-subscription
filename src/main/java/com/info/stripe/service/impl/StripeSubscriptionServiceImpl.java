package com.info.stripe.service.impl;

import com.google.gson.Gson;
import com.info.stripe.model.*;
import com.info.stripe.service.StripeSubscriptionService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeSubscriptionServiceImpl implements StripeSubscriptionService {

    @Value("${STRIPE_PRODUCT_ID}")
    String STRIPE_PRODUCT_ID;

    @Value("${STRIPE_SECRET_KEY}")
    String STRIPE_SECRET_KEY;

    @Value("${PRICE_ID}")
    private String PRICE_ID;

    @PostConstruct
    public void init() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    @Override
    public Customer createCustomer(CreateCustomerBody createCustomerBody) throws StripeException {
        CustomerCreateParams customerParams = CustomerCreateParams.builder().setEmail(createCustomerBody.getEmail()).build();
        Customer customer = Customer.create(customerParams);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("customer", customer);
        return customer;
    }

    @Override
    public PaymentIntent createPaymentIntent() throws StripeException {
        try {
            PaymentIntentCreateParams paramss =
                    PaymentIntentCreateParams.builder()
                            .setAmount(500L)
                            .setCurrency("gbp")
                            .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.ON_SESSION)
                            .setPaymentMethod("pm_card_visa")
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(paramss);
            return paymentIntent;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Price createPrice(Long amount) throws StripeException {

        PriceCreateParams params = PriceCreateParams.builder()
                .setProduct(STRIPE_PRODUCT_ID).setUnitAmount(amount * 1000).setCurrency("usd")
                .setRecurring(PriceCreateParams.Recurring.builder().setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                        .build()).build();

        Price price = Price.create(params);
        return price;
    }

    @Override
    public SubscriptionInfo createSubscription(ChargeRequest chargeRequest) throws StripeException {
        Gson gson = new Gson();
        try {
            PaymentMethod paymentMethod = createPaymentMethod(chargeRequest.getStripeToken());

            CustomerCreateParams customerParams = CustomerCreateParams.builder().setEmail(chargeRequest.getStripeEmail())
                    .build();
            Customer customer = Customer.create(customerParams);

            // Set the default payment method on the customer
            paymentMethod.attach(PaymentMethodAttachParams.builder().setCustomer(customer.getId()).build());
            CustomerUpdateParams customerUpdateParams = CustomerUpdateParams.builder()
                    .setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
                            .setDefaultPaymentMethod(paymentMethod.getId()).build()).build();
            customer.update(customerUpdateParams);

            SubscriptionCreateParams subCreateParams = SubscriptionCreateParams.builder().addItem(SubscriptionCreateParams.Item.builder()
                            .setPrice(PRICE_ID).setQuantity(1L).build()).setCustomer(customer.getId())
                    .addAllExpand(Arrays.asList("latest_invoice.payment_intent", "plan.product")).build();

            Subscription subscription = Subscription.create(subCreateParams);
            return SubscriptionInfo.builder().subscription(subscription).customer(customer).build();

        } catch (Exception e) {
            // Since it's a decline, CardException will be caught
            Map<String, String> responseErrorMessage = new HashMap<>();
            responseErrorMessage.put("message", e.getLocalizedMessage());
            Map<String, Object> responseError = new HashMap<>();
            responseError.put("error", responseErrorMessage);
            System.out.println(gson.toJson(responseError));
            return null;
        }
    }

    private PaymentMethod createPaymentMethod(String stripeToken) {
        try {
            PaymentMethodCreateParams params =
                    PaymentMethodCreateParams.builder()
                            .setType(PaymentMethodCreateParams.Type.CARD)
                            .setCard(
                                    PaymentMethodCreateParams.CardDetails.builder()
                                            .putExtraParam("token", stripeToken)
                                            .build()
                            )
                            .build();
            PaymentMethod paymentMethod = PaymentMethod.create(params);
            return paymentMethod;
        } catch (RuntimeException | StripeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SubscriptionCollection subscriptions() throws StripeException {
        SubscriptionListParams params = SubscriptionListParams.builder().setLimit(3L).build();
        SubscriptionCollection subscriptions = Subscription.list(params);
        return subscriptions;
    }

    @Override
    public Subscription retrieveSubscription(String subscriptionId) throws StripeException {
        Subscription subscription = Subscription.retrieve("sub_1PJDJZ09nk1tP83CnVwsum6t");
        return subscription;
    }

}