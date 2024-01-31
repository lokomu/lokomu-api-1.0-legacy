package no.delalt.back.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class SpatialIndexCreator {
  private static final Logger LOGGER = LoggerFactory.getLogger(
    SpatialIndexCreator.class
  );
  private final DataSource dataSource;

  public SpatialIndexCreator(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Creates a spatial index.
   *
   * @EventListener(ApplicationReadyEvent)
   */
  @EventListener(ApplicationReadyEvent.class)
  public void createSpatialIndex() {
    String createIndexSQL =
      "CREATE INDEX IF NOT EXISTS community_coordinates_idx ON public.community USING gist(coordinates);";

    try (
      Connection connection = dataSource.getConnection();
      Statement statement = connection.createStatement()
    ) {
      statement.execute(createIndexSQL);
    } catch (SQLException e) {
      LOGGER.error(
        "An unexpected exception occurred while creating a spatial index",
        e
      );
    }
  }
}
