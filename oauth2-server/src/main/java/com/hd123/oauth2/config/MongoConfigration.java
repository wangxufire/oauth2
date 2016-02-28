package com.hd123.oauth2.config;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import java.net.UnknownHostException;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.convert.Jsr310Converters.DateToLocalDateConverter;
import org.springframework.data.convert.Jsr310Converters.DateToLocalDateTimeConverter;
import org.springframework.data.convert.Jsr310Converters.LocalDateTimeToDateConverter;
import org.springframework.data.convert.Jsr310Converters.LocalDateToDateConverter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;

/**
 * mongodb配置
 *
 * @author liyue
 */
@Configuration
public class MongoConfigration extends AbstractMongoConfiguration implements InitializingBean,
    DisposableBean {

  private static final String SYSTEM_PROFILE_DOCUMENT = "system.profile";

  @Autowired
  private MongoProperties props;

  @Bean
  @Role(ROLE_INFRASTRUCTURE)
  public MongoClient mongoClient() throws UnknownHostException {
    final Builder builder = new Builder().description("Mongo Client Settings")
        .minConnectionsPerHost(1000).connectionsPerHost(19999).maxConnectionIdleTime(1000 * 10)
        .maxConnectionLifeTime(1000 * 60).socketTimeout(1000 * 20);
    final MongoClientURI clientURI = new MongoClientURI(props.getUri(), builder);

    return new MongoClient(clientURI);
  }

  @Bean
  @Override
  @Role(ROLE_INFRASTRUCTURE)
  public MongoDbFactory mongoDbFactory() throws Exception {
    return new SimpleMongoDbFactory(mongoClient(), props.getAuthenticationDatabase());
  }

  @Override
  @Role(ROLE_INFRASTRUCTURE)
  @Bean(name = "mongoTemplate")
  public MongoTemplate mongoTemplate() throws Exception {
    final MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(
        mongoDbFactory()), new MongoMappingContext());
    // remove _class
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    return new MongoTemplate(mongoDbFactory(), converter);
  }

  @Bean
  @Override
  @Role(ROLE_INFRASTRUCTURE)
  @SuppressWarnings("unchecked")
  public CustomConversions customConversions() {
    return new CustomConversions(newArrayList(DateToLocalDateConverter.INSTANCE,
        LocalDateToDateConverter.INSTANCE, DateToLocalDateTimeConverter.INSTANCE,
        LocalDateTimeToDateConverter.INSTANCE));
  }

  // @Bean
  // @Role(ROLE_INFRASTRUCTURE)
  // public ValidatingMongoEventListener validatingMongoEventListener() {
  // return new ValidatingMongoEventListener(validator());
  // }
  //
  // @Bean
  // @Role(ROLE_INFRASTRUCTURE)
  // public LocalValidatorFactoryBean validator() {
  // return new LocalValidatorFactoryBean();
  // }

  @Override
  protected String getDatabaseName() {
    return props.getAuthenticationDatabase();
  }

  @Bean
  @Override
  @Role(ROLE_INFRASTRUCTURE)
  public Mongo mongo() throws Exception {
    return mongoClient();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    setProfilingLevel(1, mongoTemplate());
  }

  @Override
  public void destroy() throws Exception {
    setProfilingLevel(0, mongoTemplate());
    if (mongoTemplate().collectionExists(SYSTEM_PROFILE_DOCUMENT)) {
      mongoTemplate().dropCollection(SYSTEM_PROFILE_DOCUMENT);
    }
  }

  /**
   * 设置mongodb profile级别。0不保存， 1只保存慢查询，2保存所有查询。
   *
   * @param level
   *          level
   * @param operations
   *          operations
   */
  private void setProfilingLevel(int level, MongoOperations operations) {
    operations.executeCommand(new BasicDBObject("profile", level));
  }

}