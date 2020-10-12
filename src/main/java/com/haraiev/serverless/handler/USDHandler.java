package com.haraiev.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.haraiev.serverless.model.ApiGatewayResponse;
import java.util.Map;

public class USDHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

  private static final Double CURRENT_USD_RATE = 28.5;

  public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
    try {
      Map<String, String> parameters = (Map<String, String>) input.get("queryStringParameters");
      Double amount = Double.valueOf(parameters.get("amount"));
      return ApiGatewayResponse.builder()
          .setStatusCode(200)
          .setObjectBody(amount * CURRENT_USD_RATE)
          .build();
    } catch (Exception e) {
      return ApiGatewayResponse.builder()
          .setStatusCode(500)
          .setObjectBody(e)
          .build();
    }
  }
}
