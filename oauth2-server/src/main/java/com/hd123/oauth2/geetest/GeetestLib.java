package com.hd123.oauth2.geetest;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Integer.toHexString;
import static java.lang.Math.abs;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.net.InetAddress.getByName;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.logging.log4j.LogManager.getLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

/**
 * Geetest Java SDK
 * <p>
 * http://geetest.com
 * </p>
 *
 * @author liyue
 */
@SuppressWarnings("all")
public final class GeetestLib {

  private static final Logger logger = getLogger(GeetestLib.class);

  /**
   * SDK版本编号
   */
  // private final int verCode = 8;

  /**
   * SDK版本名称
   */
  private final String verName = "2.15.12.10.1";
  private final String sdkLang = "java";// SD的语言类型

  private final static String GT_SESSION_KEY = "geetest";// geetest对象存储的session的key值(单实例)
  private final static String GT_CHALLENGE_SESSION_KEY = "geetest_challenge";// challenge的key值(单实例)
  private final static String GT_SERVER_STATUS_SESSION_KEY = "gt_server_status";// 极验服务器状态key值（单实例）

  private final String BASE_URL = "api.geetest.com";
  private final String API_URL = "http://" + BASE_URL;
  private final String HTTPS_API_URL = "https://api.geetest.com";// 一些页面是https
  private final int COM_PORT = 80;// 通讯端口号

  private final int DEFAULT_IS_MOBILE = 0;
  // the default width of the mobile id
  // private final int defaultMobileWidth = 260;

  // 一些常量
  public static final String SUCCESS_RES = "success";
  public static final String FAIL_RES = "fail";
  public static final String FORBIDDEN_RES = "forbidden";

  // 前端验证的表单值--属于接口，不允许修改
  private final String FN_GEETEST_CHALLENGE = "geetest_challenge";
  private final String FN_GEETEST_VALIDATE = "geetest_validate";
  private final String FN_GEETEST_SECCODE = "geetest_seccode";

  private Boolean debugCode = TRUE;// 调试开关，是否输出调试日志
  private String validateLogPath = EMPTY;// 服务器端保存日志的目录//var/log/，请确保有可读写权限

  /**
   * 公钥
   */
  private String captchaId = EMPTY;

  /**
   * 私钥
   */
  private String privateKey = EMPTY;

  /**
   * the challenge
   */
  private String challengeId = EMPTY;

  /**
   * set the own private pictures,default is ""
   */
  private String picId = EMPTY;

  /**
   * the captcha product type,default is 'embed'
   */
  private String productType = "float";

  /**
   * is secure
   */
  private Boolean isHttps = FALSE;

  public Boolean getIsHttps() {
    return isHttps;
  }

  public void setIsHttps(Boolean isHttps) {
    this.isHttps = isHttps;
  }

  /**
   * when the productType is popup,it needs to set the submitbutton
   */
  private String submitBtnId = "submit-button";

  public String getSubmitBtnId() {
    return submitBtnId;
  }

  public void setSubmitBtnId(String submitBtnId) {
    this.submitBtnId = submitBtnId;
  }

  /**
   * 是否是移动端的
   */
  private int isMobile = DEFAULT_IS_MOBILE;// 1--true,0-false

  public String getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(String challengeId) {
    this.challengeId = challengeId;
  }

  public final Boolean getDebugCode() {
    return debugCode;
  }

  public final void setDebugCode(Boolean debugCode) {
    this.debugCode = debugCode;
  }

  /**
   * 获取版本编号
   *
   * @return verName
   */
  public String getVersionInfo() {
    return verName;
  }

  public String getValidateLogPath() {
    return validateLogPath;
  }

  public void setValidateLogPath(String validateLogPath) {
    this.validateLogPath = validateLogPath;
  }

  /**
   * 一个无参构造函数
   */
  public GeetestLib() {
  }

  /**
   * 将当前实例设置到session中
   *
   * @param request
   *          request
   */
  public void setGtSession(HttpServletRequest request) {
    request.getSession().setAttribute(GT_SESSION_KEY, this);// set session
  }

  /**
   * 同一会话多实例时，设置session
   *
   * @param request
   *          request
   * @param gtInstanceSessionKey
   *          不同验证实例设置的key
   */
  public void setGtSession(HttpServletRequest request, String gtInstanceSessionKey) {
    request.getSession().setAttribute(gtInstanceSessionKey, this);// set
  }

  /**
   * 极验服务器的gt-server状态值
   *
   * @param request
   *          request
   */
  public void setGtServerStatusSession(HttpServletRequest request, int statusCode) {
    request.getSession().setAttribute(GT_SERVER_STATUS_SESSION_KEY, statusCode);// set
    // session
  }

  /**
   * 极验服务器的gt-server状态值（多实例）
   *
   * @param request
   *          request
   * @param statusCode
   *          statusCode
   * @param gtInstanceServerStatusSessionKey
   *          gt_instance_server_status_session_key
   */
  public void setGtServerStatusSession(HttpServletRequest request, int statusCode,
      String gtInstanceServerStatusSessionKey) {
    request.getSession().setAttribute(gtInstanceServerStatusSessionKey, statusCode);
  }

  /**
   * 获取session
   *
   * @param request
   *          request
   * @return GtSession
   */
  public static GeetestLib getGtSession(HttpServletRequest request) {
    return (GeetestLib) request.getSession().getAttribute(GT_SESSION_KEY);
  }

  /**
   * 获取session(用于同一会话多实例模式下，做的区分)
   *
   * @param request
   *          request
   * @param gtInstanceSessionKey
   *          gt_instance_session_key
   * @return GtSession
   */
  public static GeetestLib getGtSession(HttpServletRequest request, String gtInstanceSessionKey) {
    return (GeetestLib) request.getSession().getAttribute(gtInstanceSessionKey);
  }

  /**
   * 0表示不正常，1表示正常，2表示本次请求没有按照流程来请求，直接不予理会
   *
   * @param request
   *          request
   * @return GtServerStatusSession
   */
  public static int getGtServerStatusSession(HttpServletRequest request) {
    final Object serverStatus = request.getSession().getAttribute(GT_SERVER_STATUS_SESSION_KEY);

    if (serverStatus == null) {
      return 2;// 没有按照流程，直接向客户服务器提交数据
    }
    return (Integer) serverStatus;
  }

  /**
   * 获取session(用于同一会话多实例模式下，做的区分)
   *
   * @param request
   *          request
   * @param gtInstanceServerStatusSessionKey
   *          gt_instance_server_status_session_key
   * @return GtServerStatusSession
   */
  public static int getGtServerStatusSession(HttpServletRequest request,
      String gtInstanceServerStatusSessionKey) {
    final Object serverStatus = request.getSession().getAttribute(gtInstanceServerStatusSessionKey);

    if (serverStatus == null) {
      return 2;// 没有按照流程，直接向客户服务器提交数据
    }

    return (Integer) serverStatus;
  }

  /**
   * 获取本次验证的challenge
   *
   * @param request
   *          request
   */
  public void getChallengeSession(HttpServletRequest request) {
    challengeId = (String) request.getSession().getAttribute(GT_CHALLENGE_SESSION_KEY);
  }

  /**
   * 获取本次验证的challenge(用于同一会话多实例模式下)
   *
   * @param request
   *          request
   * @param gtInstanceServerStatusSessionKey
   *          gt_instance_server_status_session_key
   */
  public void getChallengeSession(HttpServletRequest request,
      String gtInstanceServerStatusSessionKey) {
    challengeId = (String) request.getSession().getAttribute(gtInstanceServerStatusSessionKey);
  }

  /**
   * 预处理失败后的返回格式串
   *
   * @return FailPreProcessRes
   */
  public String getFailPreProcessRes() {
    final Long rnd1 = round(random() * 100);
    final Long rnd2 = round(random() * 100);
    final String md5Str1 = md5Encode(rnd1 + "");
    final String md5Str2 = md5Encode(rnd2 + "");
    final String challenge = md5Str1 + md5Str2.substring(0, 2);
    setChallengeId(challenge);

    return format("{\"success\":%s,\"gt\":\"%s\",\"challenge\":\"%s\"}", 0, getCaptchaId(),
        getChallengeId());
  }

  /**
   * 预处理成功后的标准串
   *
   * @return SuccessPreProcessRes
   */
  public String getSuccessPreProcessRes() {
    return format("{\"success\":%s,\"gt\":\"%s\",\"challenge\":\"%s\"}", 1, getCaptchaId(),
        getChallengeId());
  }

  /**
   * 保存验证的日志，方便后续和极验做一些联调工作,用于可能有前端验证通过，但是后面验证失败的情况
   *
   * @param challenge
   *          challenge
   * @param validate
   *          validate
   * @param seccode
   *          seccode
   * @param gtUser
   *          用户页面的cookie标识
   * @param sdkResult
   *          sdkResult
   */
  public void saveValidateLog(String challenge, String validate, String seccode, String sdkResult) {
    final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");
    final String date = sDateFormat.format(new java.util.Date());

    final String logFormat = format("date:%s,challenge:%s,validate:%s,seccode:%s,sdkResult:%s",
        date, challenge, validate, seccode, sdkResult);
  }

  public String getPicId() {
    return picId;
  }

  public void setPicId(String picId) {
    this.picId = picId;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public int getIsMobile() {
    return isMobile;
  }

  public void setIsMobile(int isMobile) {
    this.isMobile = isMobile;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  public GeetestLib(String privateKey) {
    this.privateKey = privateKey;
  }

  public String getVerName() {
    return verName;
  }

  public String getCaptchaId() {
    return captchaId;
  }

  public void setCaptchaId(String captchaId) {
    this.captchaId = captchaId;
  }

  /**
   * processing before the captcha display on the web front
   *
   * @return
   */
  public int preProcess() {
    // just checkPassword the server side register
    if (registerChallenge() != 1) {
      return 0;
    }

    return 1;
  }

  /**
   * generate the dynamic front source
   *
   * @param different
   *          product display mode :float,embed,popup
   * @return GtFrontSource
   */
  public String getGtFrontSource() {

    String basePath = EMPTY;
    if (isHttps) {
      basePath = HTTPS_API_URL;
    } else {
      basePath = API_URL;
    }

    String frontSource = format("<script type=\"text/javascript\" src=\"%s/get.php?"
        + "gt=%s&challenge=%s", basePath, captchaId, challengeId);

    if (productType.equals("popup")) {
      frontSource += format("&product=%s&popupbtnid=%s", productType, submitBtnId);
    } else {
      frontSource += format("&product=%s", productType);
    }

    frontSource += "\"></script>";

    return frontSource;
  }

  /**
   * 获取极验的服务器状态
   *
   * @return GtServerStatus
   */
  public int getGtServerStatus() {
    try {
      final String GET_URL = API_URL + "/check_status.php";
      if (readContentFromGet(GET_URL).equals("ok")) {
        return 1;
      } else {
        return 0;
      }
    } catch (Exception ex) {
      if (logger.isErrorEnabled()) {
        logger.error("", ex);
      }
    }

    return 0;
  }

  /**
   * generate a random num
   *
   * @return RandomNum
   */
  public int getRandomNum() {
    return (int) (random() * 100);
  }

  /**
   * Register the challenge
   *
   * @return registerChallenge
   */
  public int registerChallenge() {
    try {
      final String GET_URL = API_URL + "/register.php?gt=" + captchaId;

      // if (this.productType.equals("popup")) {
      // GET_URL += String.format("&product=%s&popupbtnid=%s",
      // this.productType, this.submitBtnId);
      // } else {
      // GET_URL += String.format("&product=%s", this.productType);
      // }

      // System.out.print(GET_URL);
      final String resultStr = readContentFromGet(GET_URL);
      // System.out.println(result_str);
      if (resultStr.length() == 32) {
        challengeId = resultStr;
        return 1;
      } else {
        return 0;
      }
    } catch (Exception ex) {
      if (logger.isErrorEnabled()) {
        logger.error("", ex);
      }
    }

    return 0;
  }

  /**
   * 读取服务器
   *
   * @param getURL
   *          getURL
   * @return readContentFromGet
   */
  private String readContentFromGet(String getURL) throws IOException {
    final URL getUrl = new URL(getURL);
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setConnectTimeout(2000);// 设置连接主机超时（单位：毫秒）
      connection.setReadTimeout(2000);// 设置从主机读取数据超时（单位：毫秒）
      // 建立与服务器的连接，并未发送数据
      connection.connect();
      try (final InputStream inStream = connection.getInputStream()) {
        // 发送数据到服务器并使用Reader读取返回的数据
        final StringBuilder sBuffer = new StringBuilder();
        final byte[] buf = new byte[1024];
        for (int n; (n = inStream.read(buf)) != -1;) {
          sBuffer.append(new String(buf, 0, n, "UTF-8"));
        }
        return sBuffer.toString();
      }
    } finally {
      if (connection != null) {
        connection.disconnect();// 断开连接
      }
    }
  }

  /**
   * 判断一个表单对象值是否为空
   *
   * @param gtObj
   *          gtObj
   * @return objIsEmpty
   */
  private boolean objIsEmpty(Object gtObj) {
    if (gtObj == null) {
      return true;
    }

    if (gtObj.toString().trim().length() == 0) {
      return true;
    }
    // && gtObj.toString().trim().length() > 0

    return false;
  }

  /**
   * 检验验证请求 传入的参数为request--vCode 8之后不再更新,不推荐使用
   *
   * @param request
   *          request
   * @return validateRequest
   */
  public boolean validateRequest(HttpServletRequest request) {
    return validate(request.getParameter(FN_GEETEST_CHALLENGE),
        request.getParameter(FN_GEETEST_VALIDATE), request.getParameter(FN_GEETEST_SECCODE));
  }

  /**
   * failback使用的验证方式
   *
   * @param request
   *          request
   * @return failbackValidateRequest
   */
  public String failbackValidateRequest(HttpServletRequest request) {
    final String body = extractRequestBody(request);
    if (objIsEmpty(body)) {
      return FAIL_RES;
    }

    final Geetest geetest = parseObject(body, Geetest.class);
    final String challenge = geetest.getGeetestChallenge();
    final String validate = geetest.getGeetestValidate();

    if (objIsEmpty(challenge) || objIsEmpty(validate)) {
      return FAIL_RES;
    }

    if (!challenge.equals(getChallengeId())) {
      return FAIL_RES;
    }

    final String[] validateStr = validate.split("_");
    final String encodeAns = validateStr[0];
    final String encodeFullBgImgIndex = validateStr[1];
    final String encodeImgGrpIndex = validateStr[2];

    final int decodeAns = decodeResponse(getChallengeId(), encodeAns);
    final int decodeFullBgImgIndex = decodeResponse(getChallengeId(), encodeFullBgImgIndex);
    final int decodeImgGrpIndex = decodeResponse(getChallengeId(), encodeImgGrpIndex);

    final String validateResult = validateFailImage(decodeAns, decodeFullBgImgIndex,
        decodeImgGrpIndex);

    if (!validateResult.equals(FAIL_RES)) {
      // 使用一随机标识来丢弃掉此次验证，防止重放
      final Long rnd1 = round(random() * 100);
      final String md5Str1 = md5Encode(rnd1 + "");
      setChallengeId(md5Str1);
    }

    return validateResult;
  }

  /**
   * @param ans
   *          ans
   * @param fullBgIndex
   *          full_bg_index
   * @param imgGrpIndex
   *          img_grp_index
   * @return is_valid
   */
  private String validateFailImage(int ans, int fullBgIndex, int imgGrpIndex) {
    final int thread = 3;// 容差值

    final String fullBgName = md5Encode(fullBgIndex + "").substring(0, 9);
    final String bgName = md5Encode(imgGrpIndex + "").substring(10, 19);

    String answerDecode = "";
    // 通过两个字符串奇数和偶数位拼接产生答案位
    for (int i = 0; i < 9; i++) {
      if (i % 2 == 0) {
        answerDecode += fullBgName.charAt(i);
      } else if (i % 2 == 1) {
        answerDecode += bgName.charAt(i);
      } else {
        // do nothing
      }
    }

    final String xDecode = answerDecode.substring(4, answerDecode.length());
    int xInt = Integer.valueOf(xDecode, 16);// 16 to 10

    int result = xInt % 200;
    if (result < 40) {
      result = 40;
    }

    if (abs(ans - result) <= thread) {
      return SUCCESS_RES;
    } else {
      return FAIL_RES;
    }
  }

  /**
   * 输入的两位的随机数字,解码出偏移量
   *
   * @param randStr
   *          randStr
   * @return decodeRandBase
   */
  public int decodeRandBase(String challenge) {
    final String base = challenge.substring(32, 34);
    final ArrayList<Integer> tempArray = newArrayList();

    for (int i = 0, max = base.length(); i < max; i++) {
      char tempChar = base.charAt(i);
      final Integer tempAscii = (int) (tempChar);
      final Integer result = (tempAscii > 57) ? (tempAscii - 87) : (tempAscii - 48);
      tempArray.add(result);
    }

    int decodeRes = tempArray.get(0) * 36 + tempArray.get(1);
    return decodeRes;

  }

  /**
   * 解码随机参数
   *
   * @param encodeStr
   *          encodeStr
   * @param challenge
   *          challenge
   * @return decodeResponse
   */
  public int decodeResponse(String challenge, String string) {
    if (string.length() > 100) {
      return 0;
    }

    final int[] shuzi = new int[] {
        1, 2, 5, 10, 50 };
    String chongfu = EMPTY;
    final HashMap<String, Integer> key = newHashMap();
    int count = 0;

    for (int i = 0, max = challenge.length(); i < max; i++) {
      final String item = challenge.charAt(i) + "";
      if (!chongfu.contains(item)) {
        final int value = shuzi[count % 5];
        chongfu += item;
        count++;
        key.put(item, value);
      }
    }

    int res = 0;
    for (int j = 0, max = string.length(); j < max; j++) {
      res += key.get(string.charAt(j) + "");
    }
    res = res - decodeRandBase(challenge);

    return res;
  }

  /**
   * 增强版的验证信息,提供了更多的验证返回结果信息，以让客户服务器端有不同的数据处理。
   *
   * @param challenge
   *          challenge
   * @param validate
   *          validate
   * @param seccode
   *          seccode
   * @return enhencedValidateRequest
   */
  public String enhencedValidateRequest(HttpServletRequest request) {
    final String body = extractRequestBody(request);
    if (objIsEmpty(body)) {
      return FAIL_RES;
    }

    final Geetest geetest = parseObject(body, Geetest.class);
    final String challenge = geetest.getGeetestChallenge();
    final String validate = geetest.getGeetestValidate();
    final String seccode = geetest.getGeetestSeccode();

    if (objIsEmpty(challenge) || objIsEmpty(validate) || objIsEmpty(seccode)) {
      return FAIL_RES;
    }

    // String gtuser = "";

    // Cookie[] cookies = request.getCookies();
    //
    // if (cookies != null) {
    // for (int i = 0; i < cookies.length; i++) {
    // Cookie cookie = cookies[i];
    // if ("GeeTestUser".equals(cookie.getName())) {
    // gtuser = cookie.getValue();
    // gtlog(String.format("GeeTestUser:%s", gtuser));
    // }
    // }
    // }

    final String host = BASE_URL;
    final String path = "/validate.php";
    final int port = 80;
    // String query = "seccode=" + seccode + "&sdk=" + this.sdkLang + "_"
    // + this.verName;

    final String query = format("seccode=%s&sdk=%s", seccode, (sdkLang + "_" + verName));

    String response = EMPTY;
    try {
      if (validate.length() <= 0) {
        return FAIL_RES;
      }

      if (!checkResultByPrivate(challenge, validate)) {
        return FAIL_RES;
      }

      response = postValidate(host, path, query, port);
    } catch (Exception ex) {
      if (logger.isErrorEnabled()) {
        logger.error("", ex);
      }
    }

    if (response.equals(md5Encode(seccode))) {
      return SUCCESS_RES;
    } else {
      return response;
    }
  }

  /**
   * the old api use before version code 8(not include)
   *
   * @param challenge
   *          challenge
   * @param validate
   *          validate
   * @param seccode
   *          seccode
   * @return is_valid
   */
  private boolean validate(String challenge, String validate, String seccode) {
    final String host = BASE_URL;
    final String path = "/validate.php";
    final int port = 80;
    if (validate.length() > 0 && checkResultByPrivate(challenge, validate)) {
      final String query = "seccode=" + seccode;
      String response = EMPTY;
      try {
        response = postValidate(host, path, query, port);
      } catch (Exception ex) {
        if (logger.isErrorEnabled()) {
          logger.error("", ex);
        }
      }

      if (response.equals(md5Encode(seccode))) {
        return true;
      }
    }

    return false;
  }

  private boolean checkResultByPrivate(String challenge, String validate) {
    return validate.equals(md5Encode(privateKey + "geetest" + challenge));
  }

  /**
   * fuck，貌似不是Post方式，后面重构时修改名字
   *
   * @param host
   *          host
   * @param path
   *          path
   * @param data
   *          data
   * @param port
   *          port
   * @return postValidate
   * @throws Exception
   *           Exception
   */
  private String postValidate(String host, String path, String data, int port) throws Exception {
    String response = "error";
    // data=fixEncoding(data);
    final InetAddress addr = getByName(host);
    try (final Socket socket = new Socket(addr, port);
        final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
            socket.getOutputStream(), UTF_8))) {
      wr.write("POST " + path + " HTTP/1.0\r\n");
      wr.write("Host: " + host + "\r\n");
      wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
      wr.write("Content-Length: " + data.length() + "\r\n");
      wr.write("\r\n"); // 以空行作为分割
      // 发送数据
      wr.write(data);
      wr.flush();
      try (final BufferedReader rd = new BufferedReader(new InputStreamReader(
          socket.getInputStream(), "UTF-8"))) {
        String line;
        while ((line = rd.readLine()) != null) {
          response = line;
        }
        return response;
      }
    }
  }

  /**
   * md5 加密
   *
   * @param plainText
   *          plainText
   * @return md5Encode
   */
  private String md5Encode(String plainText) {
    String re_md5 = EMPTY;
    try {
      final MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(plainText.getBytes());
      final byte b[] = md.digest();
      int i;
      final StringBuilder buf = new StringBuilder();
      for (int offset = 0, max = b.length; offset < max; offset++) {
        i = b[offset];
        if (i < 0) {
          i += 256;
        }
        if (i < 16) {
          buf.append("0");
        }
        buf.append(toHexString(i));
      }

      re_md5 = buf.toString();
    } catch (NoSuchAlgorithmException ex) {
      if (logger.isErrorEnabled()) {
        logger.error("", ex);
      }
    }

    return re_md5;
  }

  /**
   * 解析body
   *
   * @param request
   *          request
   * @return body
   */
  private static String extractRequestBody(HttpServletRequest request) {
    try {
      return IOUtils.toString(request.getInputStream(), UTF_8);
    } catch (IOException e) {
      return EMPTY;
    }
  }

}