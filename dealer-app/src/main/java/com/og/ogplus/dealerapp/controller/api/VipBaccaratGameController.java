package com.og.ogplus.dealerapp.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.ErrorCode;
import com.og.ogplus.common.model.Response;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.controller.api.handler.VipBaccaratRequestHandler;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = {"baccaratvip-api"})
@RequestMapping(path = "/dealer-app-vip")
@RestController
public class VipBaccaratGameController {
	 	private final GameCategory gameCategory;

	    private final String tableNumber;

	    private VipBaccaratRequestHandler vipBaccaratRequestHandler;
	    
	    public VipBaccaratGameController(AppProperty appProperty) {
	        this.gameCategory = appProperty.getGameCategory();
	        this.tableNumber = appProperty.getTableNumber();
	    }
	    
	    @Autowired(required = false)
	    public void setVipBaccaratRequestHandler(VipBaccaratRequestHandler vipBaccaratRequestHandler) {
	        this.vipBaccaratRequestHandler = vipBaccaratRequestHandler;
	    }
	    
	    @ApiOperation(value="Start dealing", notes="根据shuffleRequest对象Start dealing")
	    @ApiResponses(value = { @ApiResponse(code = 0, message = "")})
	    @PostMapping(path = "/game/start")
	    public Response start(@ApiParam("ShuffleRequest对象") @RequestBody ShuffleRequest shuffleRequest) {
	    	 if (vipBaccaratRequestHandler == null) {
	             return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
	         }

	         if (gameCategory != shuffleRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(shuffleRequest.getGameIdentity().getTableNumber())) {
	             return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
	         }
	    	vipBaccaratRequestHandler.start();
	    	return new Response();
	    }
	    
	    @ApiOperation(value="Switch squeeze or not", notes="根据shuffleRequest对象Switch squeeze or not")
	    @ApiResponses(value = { @ApiResponse(code = 0, message = "")})
	    @PostMapping(path = "/game/squeeze")
	    public Response squeeze(@ApiParam("ShuffleRequest对象") @RequestBody ShuffleRequest shuffleRequest) {
	    	 if (vipBaccaratRequestHandler == null) {
	             return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
	         }

	         if (gameCategory != shuffleRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(shuffleRequest.getGameIdentity().getTableNumber())) {
	             return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
	         }
	    	vipBaccaratRequestHandler.turnOnTheSqueeze();
	    	return new Response();
	    }
	    
	    @ApiOperation(value="Skip round", notes="根据shuffleRequest对象Skip round")
	    @ApiResponses(value = { @ApiResponse(code = 0, message = "")})
	    @PostMapping(path = "/game/skip")
	    public Response skipRound(@ApiParam("ShuffleRequest对象") @RequestBody ShuffleRequest shuffleRequest) {
	    	 if (vipBaccaratRequestHandler == null) {
	             return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
	         }

	         if (gameCategory != shuffleRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(shuffleRequest.getGameIdentity().getTableNumber())) {
	             return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
	         }
	    	vipBaccaratRequestHandler.skipRound();
	    	return new Response();
	    }
	    
	    @ApiOperation(value="Shuffle", notes="根据shuffleRequest对象Shuffle")
	    @ApiResponses(value = { @ApiResponse(code = 0, message = "")})
	    @PostMapping(path = "/game/shuffle")
	    public Response shuffle(@ApiParam("ShuffleRequest对象") @RequestBody ShuffleRequest shuffleRequest) {
	    	 if (vipBaccaratRequestHandler == null) {
	             return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
	         }

	         if (gameCategory != shuffleRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(shuffleRequest.getGameIdentity().getTableNumber())) {
	             return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
	         }
	    	if(!vipBaccaratRequestHandler.shuffle()) {
	    		 return new Response(ErrorCode.FAILED_OPERATION_NOT_ALLOWED, "operation not allowed");
	    	}
	    	return new Response();
	    }
	    
	    @ApiOperation(value="Change deck", notes="根据shuffleRequest对象Change deck")
	    @ApiResponses(value = { @ApiResponse(code = 0, message = "")})
	    @PostMapping(path = "/game/changeDeck")
	    public Response changeDeck(@ApiParam("ShuffleRequest对象") @RequestBody ShuffleRequest shuffleRequest) {
	    	 if (vipBaccaratRequestHandler == null) {
	             return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
	         }

	         if (gameCategory != shuffleRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(shuffleRequest.getGameIdentity().getTableNumber())) {
	             return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
	         }
	         if(!vipBaccaratRequestHandler.changeDeck()) {
	    		 return new Response(ErrorCode.FAILED_OPERATION_NOT_ALLOWED, "operation not allowed");
	    	}
	    	return new Response();
	    }
	    
	    @ApiOperation(value="Change dealer", notes="根据shuffleRequest对象Change dealer")
	    @ApiResponses(value = { @ApiResponse(code = 0, message = "")})
	    @PostMapping(path = "/game/changeDealer")
	    public Response ChangeDealer(@ApiParam("ShuffleRequest对象") @RequestBody ShuffleRequest shuffleRequest) {
	    	 if (vipBaccaratRequestHandler == null) {
	             return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
	         }

	         if (gameCategory != shuffleRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(shuffleRequest.getGameIdentity().getTableNumber())) {
	             return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
	         }
	    	vipBaccaratRequestHandler.changeDealer();
	    	return new Response();
	    }
}
