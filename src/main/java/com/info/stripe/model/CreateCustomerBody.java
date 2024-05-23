package com.info.stripe.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCustomerBody {
   @SerializedName("name")
   String name;

   @SerializedName("email")
   String email;

}
