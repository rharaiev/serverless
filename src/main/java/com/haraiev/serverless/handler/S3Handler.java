package com.haraiev.serverless.handler;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public class S3Handler implements RequestHandler<S3Event, String> {

  public String handleRequest(S3Event event, Context context) {
    try {
      S3Entity s3Entity = event.getRecords().get(0).getS3();

      AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
      S3Object s3Object = s3Client
          .getObject(new GetObjectRequest(
              s3Entity.getBucket().getName(), s3Entity.getObject().getUrlDecodedKey()));
      InputStream objectData = s3Object.getObjectContent();
      String objectString = IOUtils.toString(objectData, StandardCharsets.UTF_8.name());
      String[] userAttr = objectString.split(" "); //id name

      AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient()
          .withRegion(Region.getRegion(Regions.US_EAST_1));
      DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
      Table table = dynamoDB.getTable("users");
      table.putItem(new Item().with("id", userAttr[0]).with("name", userAttr[1]));

      return "OK";
    } catch (IOException e) {
      return e.getMessage();
    }
  }
}
