package com.hd123.oauth2;

import static com.alibaba.fastjson.JSON.toJSONString;
import static com.hd123.oauth2.OAuth2ApplicationTests.RANDOM_PORT;
import static org.apache.logging.log4j.LogManager.getLogger;

import java.awt.print.Book;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSON;

import com.hd123.oauth2.entity.App;
import com.hd123.oauth2.entity.Product;
import com.hd123.oauth2.entity.User;
import com.hd123.oauth2.main.ServletInitializer;
import com.hd123.oauth2.repository.AppRepository;
import com.hd123.oauth2.repository.ProductRepository;
import com.hd123.oauth2.repository.specification.SpecificationQuery;

@Ignore
// @IntegrationTest
@WebIntegrationTest
@EnableConfigurationProperties
@WebAppConfiguration(RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServletInitializer.class)
public class OAuth2ApplicationTests {
  public static final String RANDOM_PORT = "server.port:0";
  private static final String LOCALHOST = "http://127.0.0.1:";
  @ClassRule
  public static OutputCapture outputCapture = new OutputCapture();
  private final Logger logger = getLogger(OAuth2ApplicationTests.class);
  @Value("${local.server.port}")
  private int port;
  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;
  private RestTemplate restTemplate = new TestRestTemplate();

  @Autowired
  private SpecificationQuery query;
  @Autowired
  private AppRepository appDao;
  @Autowired
  private ProductRepository productDao;

  @Test
  public void query() throws Exception {
    if (logger.isInfoEnabled()) {
      logger.info(toJSONString(query.query("4ce96082a14e698a40e", App.class), true));
    }
    if (logger.isInfoEnabled()) {
      logger.info(toJSONString(query.query("madoka", User.class), true));
    }
    if (logger.isInfoEnabled()) {
      logger.info(toJSONString(query.query("/dfs/fgg", Product.class), true));
    }
  }

  @Test
  public void tests() throws Exception {
    System.out.println(JSON.toJSONString(
        appDao.findDistinctByAppSecret("2a13GTAoCDhwGK6h8juSIXK6iu104ezx7BVmkut7TAVMGZyAV1YcxFe6y")
            .get(), true));
    System.out
        .println(JSON.toJSONString(
            appDao
                .findDistinctByAccessToken("eyJJKWMrXFxIaiZ2ZiI6IjJiNDMxMGEyMThlNDRhYzU4OTRkYjk5OGViYmRkMjAyIiwiYWxnIjoibm9uZSIsImNhbGciOiJHWklQIn0$H4sIAAAAAAAAAKtWKi5NUrJSUqoFABzGzw4KAAAA$"),
            true));
    Book book = restTemplate.getForObject(LOCALHOST + port + "/books/9876-5432-1111", Book.class);
    // MockHttpServletRequestBuilder.accept方法是设置客户端可识别的内容类型
    // MockHttpServletRequestBuilder.contentType,设置请求头中的Content-Type字段,表示请求体的内容类型
    // mockMvc.perform(get("/publishers/1")
    // .accept(MediaType.APPLICATION_JSON_UTF8))
    // .andExpect(status().isOk())
    // .andExpect(content().string(containsString("中文测试")))
    // .andExpect(jsonPath("$.name").value("中文测试"));
  }

  @Before
  public void before() throws Exception {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    System.out.println("\nbefore");
  }

  @After
  public void after() throws Exception {
    System.out.println("\nafter");
  }

}
