package no.delalt.back.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.locationtech.jts.geom.Point;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

  /**
   * Returns a MappingJackson2HttpMessageConverter bean configured with a custom ObjectMapper.
   *
   * @return  the configured MappingJackson2HttpMessageConverter bean
   */
  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(
      new SimpleModule().addSerializer(Point.class, new PointSerializer())
    );

    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    messageConverter.setObjectMapper(objectMapper);
    return messageConverter;
  }
}
