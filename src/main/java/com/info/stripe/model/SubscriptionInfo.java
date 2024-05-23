package com.info.stripe.model;

import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionInfo {

   Subscription subscription;

   Customer customer;

}
