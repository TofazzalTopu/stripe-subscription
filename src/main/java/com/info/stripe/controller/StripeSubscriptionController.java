package com.info.stripe.controller;

import com.info.stripe.model.ChargeRequest;
import com.info.stripe.model.SubscriptionInfo;
import com.info.stripe.service.StripeSubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.info.stripe.model.ChargeRequest.Currency.USD;

@Log
@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/stripe")
public class StripeSubscriptionController {

    @Autowired
    private StripeSubscriptionService stripeSubscriptionService;

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "result";
    }

    @RequestMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("amount", 5 * 100); // in cents
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", USD);
        return "checkout";
    }

    @PostMapping("/charge")
    public String charge(ChargeRequest chargeRequest, Model model) throws StripeException {
        SubscriptionInfo subscriptionInfo = stripeSubscriptionService.createSubscription(chargeRequest);
        System.out.println("subscriptionInfo: " + subscriptionInfo);
        model.addAttribute("customer", subscriptionInfo.getCustomer());
        model.addAttribute("subscription", subscriptionInfo.getSubscription());
        return "result";
    }

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
