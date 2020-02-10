package com.og.ogplus.dealerapp.service;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.Stage;

import java.util.Optional;

public interface GameService {

    Optional<Table> getTable(GameCategory gameCategory, String tableNumber);

    Stage getStage(GameCategory gameCategory);

}
