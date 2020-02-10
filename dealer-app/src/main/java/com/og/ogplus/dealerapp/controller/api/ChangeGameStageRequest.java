package com.og.ogplus.dealerapp.controller.api;

import com.og.ogplus.common.model.GameIdentity;
import com.og.ogplus.common.model.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeGameStageRequest {
    private GameIdentity gameIdentity;

    private Stage stage;
}
