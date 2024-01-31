package no.delalt.back.configuration;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  /**
   * Returns a new instance of GeometryFactory.
   *
   * @return  a new instance of GeometryFactory
   */
  @Bean
  public GeometryFactory geometryFactory() {
    final PrecisionModel precisionModel = new PrecisionModel(
      PrecisionModel.FLOATING
    );
    final PackedCoordinateSequenceFactory coordinateSequenceFactory =
      PackedCoordinateSequenceFactory.DOUBLE_FACTORY;
    final int SRID = 4326;
    return new GeometryFactory(precisionModel, SRID, coordinateSequenceFactory);
  }
}
