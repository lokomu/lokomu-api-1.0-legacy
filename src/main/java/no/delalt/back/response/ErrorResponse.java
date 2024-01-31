package no.delalt.back.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

  public ErrorResponse(int status, String message) {
    long timestamp = System.currentTimeMillis();
  }
}
