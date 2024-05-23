package com.info.stripe.controller;

import com.info.stripe.service.StripeSubscriptionService;
import com.stripe.model.Price;
import com.stripe.model.Subscription;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping(value = "/stripe")
public class StripeSubscriptionController {

    @Autowired
    private StripeSubscriptionService stripeSubscriptionService;


    @PostMapping("/price/{amount}")
    public Price createPrice(@PathVariable Long amount) throws Exception {
        return stripeSubscriptionService.createPrice(amount);
    }

    @GetMapping(value = "/{subscriptionId}")
    public Subscription retrieveSubscription(@PathVariable String subscriptionId) throws Exception {
        Subscription subscriptionInfo = stripeSubscriptionService.retrieveSubscription(subscriptionId);
        return subscriptionInfo;
    }

}
