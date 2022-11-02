package org.openapitools.openapidiff.sample.boundary;


import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class DeltaResourceIT {

  private static final String RESOURCE = "/api/resources/delta";

  //  @Test
  //  public void listResourcesWithoutJwtShouldFail() {
  //    given()
  //        .when()
  //        .get(RESOURCE)
  //        .then()
  //        .statusCode(Status.UNAUTHORIZED.getStatusCode()); // Unauthorized
  //  }
}
