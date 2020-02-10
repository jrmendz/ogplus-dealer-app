package com.og.ogplus.dealerapp.controller.api;

import com.og.ogplus.common.model.GameIdentity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShuffleRequest {
    private GameIdentity gameIdentity;
}
