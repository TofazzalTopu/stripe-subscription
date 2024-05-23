package com.info.stripe.model;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Card {

    @SerializedName("number")
    String number;

    @SerializedName("expMonth")
    Long expMonth;

    @SerializedName("expYear")
    Long expYear;

    @SerializedName("cvc")
    String cvc;
}
