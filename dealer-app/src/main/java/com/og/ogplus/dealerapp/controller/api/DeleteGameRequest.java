package com.og.ogplus.dealerapp.controller.api;

import com.og.ogplus.common.model.GameIdentity;
import com.og.ogplus.common.model.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteGameRequest {
    private GameIdentity gameIdentity;

    private Stage stage;
}
