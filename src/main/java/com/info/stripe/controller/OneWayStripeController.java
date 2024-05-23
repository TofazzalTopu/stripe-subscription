package com.info.stripe.controller;

import com.info.stripe.model.ChargeRequest;
import com.info.stripe.model.SubscriptionInfo;
import com.info.stripe.service.StripeSubscriptionService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.info.stripe.model.ChargeRequest.Currency.EUR;

@Log
@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/stripe")
public class OneWayStripeController {

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
        model.addAttribute("currency", EUR);
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
}
