package no.delalt.back.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

public class PointSerializer extends JsonSerializer<Point> {

  /**
   * Serializes a Point object into a JSON representation.
   *
   * @param  point              the Point object to be serialized
   * @param  jsonGenerator      the JSON generator used for writing JSON content
   * @param  serializerProvider the provider for serializers
   * @throws IOException        if an I/O error occurs during the serialization process
   */
  @Override
  public void serialize(
    Point point,
    JsonGenerator jsonGenerator,
    SerializerProvider serializerProvider
  )
    throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeNumberField("x", point.getX());
    jsonGenerator.writeNumberField("y", point.getY());
    jsonGenerator.writeEndObject();
  }
}
