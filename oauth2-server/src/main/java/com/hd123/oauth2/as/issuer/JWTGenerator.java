package com.hd123.oauth2.as.issuer;

import static com.google.common.collect.Lists.newArrayList;
import static com.hd123.oauth2.util.TokenUtil.generateOAuth2Token;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Collection;

import org.apache.oltu.oauth2.as.issuer.ValueGenerator;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

/**
 * Exemplar OAuth Token Generator
 *
 * @author liyue
 */
public class JWTGenerator implements ValueGenerator {

  private Collection<String> scopes;

  @Deprecated
  public JWTGenerator() {
    throw new UnsupportedOperationException();
  }

  public JWTGenerator(Collection<String> scopes) {
    this.scopes = scopes == null ? newArrayList() : scopes;
  }

  @Override
  public String generateValue() throws OAuthSystemException {
    return generateOAuth2Token(EMPTY, scopes);
  }

  @Override
  public String generateValue(String param) throws OAuthSystemException {
    return generateOAuth2Token(param, scopes);
  }

}