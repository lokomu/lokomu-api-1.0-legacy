package no.delalt.back.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenCache {
  private final Cache<String, String> cache;

  public TokenCache() {
    cache =
      CacheBuilder
        .newBuilder()
        .expireAfterWrite(15, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build();
  }

  /**
   * Puts a token and its associated account ID into the cache.
   *
   * @param  token     the token to be stored in the cache
   * @param  accountID the account ID associated with the token
   */
  public void put(String token, String accountID) {
    cache.put(token, accountID);
  }

  /**
   * Retrieves the value associated with the given token from the cache.
   *
   * @param  token  the token to retrieve the value for
   * @return        the value associated with the token, or null if no value is found
   */
  public String get(String token) {
    return cache.getIfPresent(token);
  }
}
