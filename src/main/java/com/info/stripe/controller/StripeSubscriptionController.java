package com.info.stripe.controller;

import com.info.stripe.model.ChargeRequest;
import com.info.stripe.model.SubscriptionInfo;
import com.info.stripe.service.StripeSubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    final String YOUR_DOMAIN = "http://localhost:8080";

    @GetMapping("create-checkout")
    public String createCheckout(Model model) {
        return "/stripe/checkout";
    }

    @GetMapping("/create-checkout-session")
    public String createCheckoutSession() {
        System.out.println();
        try {
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setSuccessUrl(YOUR_DOMAIN + "/stripe/success?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl(YOUR_DOMAIN + "/stripe/cancel.html")
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPrice("price_1PK0eyDjk1Y6IyXOgBfqFUVy")
                                            .setQuantity(2L)
                                            .build()
                            )
                            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                            .build();

            Session session = Session.create(params);
            System.out.println("session: " + session);
            return "redirect:" + session.getUrl();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    @GetMapping(value = "/success")
    public String success(@RequestParam String session_id, Model model) {
        model.addAttribute("session_id", session_id);
        return "/stripe/success";
    }

    @PostMapping("/create-portal-session/{session_id}")
    public String createPortalSession(@PathVariable String session_id, HttpServletRequest request, HttpServletResponse response) throws StripeException {
        Session checkoutSession = Session.retrieve(session_id);

        String customer = checkoutSession.getCustomer();
        // Authenticate user.
        com.stripe.param.billingportal.SessionCreateParams params = new com.stripe.param.billingportal.SessionCreateParams.Builder()
                .setReturnUrl(YOUR_DOMAIN).setCustomer(customer).build();

        com.stripe.model.billingportal.Session portalSession = com.stripe.model.billingportal.Session.create(params);
        return "redirect:" + portalSession.getUrl();
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
        model.addAttribute("success", "Stripe Subscription Created Successfully.");
        return "success";
    }

}
