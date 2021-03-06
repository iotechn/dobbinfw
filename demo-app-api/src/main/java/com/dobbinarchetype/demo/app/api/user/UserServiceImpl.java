package com.dobbinarchetype.demo.app.api.user;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dobbinarchetype.demo.biz.service.user.UserBizService;
import com.dobbinarchetype.demo.data.constant.CacheConst;
import com.dobbinarchetype.demo.data.domain.UserDO;
import com.dobbinarchetype.demo.data.dto.AdminDTO;
import com.dobbinarchetype.demo.data.dto.UserDTO;
import com.dobbinarchetype.demo.data.enums.UserLoginType;
import com.dobbinarchetype.demo.data.exception.ExceptionDefinition;
import com.dobbinarchetype.demo.data.mapper.UserMapper;
import com.dobbinsoft.fw.core.Const;
import com.dobbinsoft.fw.core.exception.AppServiceException;
import com.dobbinsoft.fw.core.exception.CoreExceptionDefinition;
import com.dobbinsoft.fw.core.exception.ServiceException;
import com.dobbinsoft.fw.core.exception.ThirdPartServiceException;
import com.dobbinsoft.fw.core.util.GeneratorUtil;
import com.dobbinsoft.fw.support.component.CacheComponent;
import com.dobbinsoft.fw.support.properties.FwWxAppProperties;
import com.dobbinsoft.fw.support.service.BaseService;
import com.dobbinsoft.fw.support.sms.SMSClient;
import com.dobbinsoft.fw.support.sms.SMSResult;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.apache.commons.codec.digest.Md5Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rize on 2019/6/30.
 */
@Service
public class UserServiceImpl extends BaseService<UserDTO, AdminDTO> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SMSClient smsClient;

    @Autowired
    private CacheComponent cacheComponent;

    @Autowired
    private UserBizService userBizService;

    @Autowired
    private StringRedisTemplate userRedisTemplate;

    @Autowired
    private FwWxAppProperties fwWxAppProperties;

    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public String sendVerifyCode(String phone) throws ServiceException {
        String verifyCode = GeneratorUtil.genSixVerifyCode();
        SMSResult res = smsClient.sendRegisterVerify(phone, verifyCode);
        if (res.isSucc()) {
            cacheComponent.putRaw(CacheConst.USER_VERIFY_CODE_PREFIX + phone, verifyCode, 300);
            return "ok";
        } else {
            throw new AppServiceException(res.getMsg(), ExceptionDefinition.USER_SEND_VERIFY_FAILED.getCode());
        }

    }

    @Override
    @Transactional
    public String register(String phone, String password, String verifyCode, String ip) throws ServiceException {
        //1.???????????????
        checkVerifyCode(phone, verifyCode);
        //2.????????????????????????
        Integer count = userMapper.selectCount(
                new QueryWrapper<UserDO>()
                        .eq("phone", phone));
        if (count > 0) {
            throw new AppServiceException(ExceptionDefinition.USER_PHONE_HAS_EXISTED);
        }
        //3.???????????????????????????
        Date now = new Date();
        UserDO userDO = new UserDO();
        userDO.setPhone(phone);
        userDO.setPassword(Md5Crypt.md5Crypt(password.getBytes(), "$1$" + phone.substring(0, 7)));
        userDO.setLastLoginIp(ip);
        userDO.setGmtLastLogin(now);
        userDO.setGmtUpdate(now);
        userDO.setGmtCreate(now);
        userMapper.insert(userDO);
        //????????????DTO
        cacheComponent.del(CacheConst.USER_VERIFY_CODE_PREFIX + phone);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String bindPhone(String phone, String password, String verifyCode, Long userId) throws ServiceException {
        //1.???????????????
        checkVerifyCode(phone, verifyCode);
        //2.????????????????????????
        Integer count = userMapper.selectCount(
                new QueryWrapper<UserDO>()
                        .eq("phone", phone));
        if (count > 0) {
            throw new AppServiceException(ExceptionDefinition.USER_PHONE_HAS_EXISTED);
        }
        //3.???????????????????????????
        UserDO updateUserDO = new UserDO();
        updateUserDO.setId(userId);
        updateUserDO.setPhone(phone);
        updateUserDO.setGmtUpdate(new Date());
        if (userMapper.updateById(updateUserDO) > 0) {
            cacheComponent.del(CacheConst.USER_VERIFY_CODE_PREFIX + phone);
            return "ok";
        }
        throw new AppServiceException(ExceptionDefinition.USER_UNKNOWN_EXCEPTION);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resetPassword(String phone, String password, String verifyCode) throws ServiceException {
        //1.???????????????
        checkVerifyCode(phone, verifyCode);
        //2.????????????????????????
        List<UserDO> targetUserList = userMapper.selectList(
                new QueryWrapper<UserDO>()
                        .eq("phone", phone));
        if (CollectionUtils.isEmpty(targetUserList)) {
            throw new AppServiceException(ExceptionDefinition.USER_PHONE_NOT_EXIST);
        }
        Long id = targetUserList.get(0).getId();
        //3.???????????????????????????
        UserDO updateUserDO = new UserDO();
        updateUserDO.setId(id);
        updateUserDO.setPassword(Md5Crypt.md5Crypt(password.getBytes(), "$1$" + phone.substring(0, 7)));
        updateUserDO.setGmtUpdate(new Date());
        if (userMapper.updateById(updateUserDO) > 0) {
            cacheComponent.del(CacheConst.USER_VERIFY_CODE_PREFIX + phone);
            return "ok";
        }
        throw new AppServiceException(ExceptionDefinition.USER_UNKNOWN_EXCEPTION);
    }

    /**
     * ?????????????????????
     *
     * @param phone
     * @param verifyCode
     * @throws ServiceException
     */
    private void checkVerifyCode(String phone, String verifyCode) throws ServiceException {
        String raw = cacheComponent.getRaw(CacheConst.USER_VERIFY_CODE_PREFIX + phone);
        if (StringUtils.isEmpty(raw)) {
            throw new AppServiceException(ExceptionDefinition.USER_VERIFY_CODE_NOT_EXIST);
        }
        if (!raw.equals(verifyCode)) {
            throw new AppServiceException(ExceptionDefinition.USER_VERIFY_CODE_NOT_CORRECT);
        }
    }

    @Override
    @Transactional
    public UserDTO login(String phone, String password, Integer loginType, String raw, String ip) throws ServiceException {
        String cryptPassword = Md5Crypt.md5Crypt(password.getBytes(), "$1$" + phone.substring(0, 7));
        UserDTO userDTO = userMapper.login(phone, cryptPassword);
        if (userDTO == null) {
            throw new AppServiceException(ExceptionDefinition.USER_PHONE_OR_PASSWORD_NOT_CORRECT);
        }
        //??????????????????????????????
        if (userDTO.getStatus() == 0) {
            throw new AppServiceException(ExceptionDefinition.USER_CAN_NOT_ACTIVE);
        }
        if (!StringUtils.isEmpty(raw) && UserLoginType.contains(loginType)) {
            if (loginType == UserLoginType.MP_WEIXIN.getCode()) {
                try {
                    JSONObject thirdPartJsonObject = JSONObject.parseObject(raw);
                    String code = thirdPartJsonObject.getString("code");
                    String body = okHttpClient.newCall(new Request.Builder()
                            .url("https://api.weixin.qq.com/sns/jscode2session?appid=" + (UserLoginType.MP_WEIXIN.getCode() == loginType ? this.fwWxAppProperties.getMiniAppId() : this.fwWxAppProperties.getAppId()) +
                                    "&secret=" + (UserLoginType.MP_WEIXIN.getCode() == loginType ? this.fwWxAppProperties.getMiniAppSecret() : this.fwWxAppProperties.getAppSecret()) +
                                    "&grant_type=authorization_code&js_code=" + code).get().build()).execute().body().string();
                    JSONObject jsonObject = JSONObject.parseObject(body);
                    Integer errcode = jsonObject.getInteger("errcode");
                    if (errcode == null || errcode == 0) {
                        //??????????????????openId???????????????user???????????????????????????????????????
                        userDTO.setWxMpOpenId(jsonObject.getString("openid"));
                    }
                } catch (Exception e) {
                    logger.error("[?????????????????????] ??????", e);
                    throw new ThirdPartServiceException(CoreExceptionDefinition.THIRD_PART_SERVICE_EXCEPTION);
                }
            }
        }
        String accessToken = GeneratorUtil.genSessionId();
        //??????SESSION??????Redis????????????
        userRedisTemplate.opsForValue().set(Const.USER_REDIS_PREFIX + accessToken, JSONObject.toJSONString(userDTO));
        userDTO.setAccessToken(accessToken);
        UserDO userUpdateDO = new UserDO();
        userUpdateDO.setId(userDTO.getId());
        userUpdateDO.setGmtLastLogin(new Date());
        userUpdateDO.setLastLoginIp(ip);
        userMapper.updateById(userUpdateDO);
        return userDTO;
    }

    @Override
    public String logout(String accessToken, Long userId) throws ServiceException {
        userRedisTemplate.delete(accessToken);
        return "ok";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO thirdPartLogin(Integer loginType, String ip, String raw) throws ServiceException {
        try {
            if (UserLoginType.MP_WEIXIN.getCode() == loginType) {
                return wechatSafeLogin(UserLoginType.MP_WEIXIN.getCode(), ip, raw);
            } else if (UserLoginType.H5_WEIXIN.getCode() == loginType) {
                return wechatH5Login(ip, raw);
            } else if (UserLoginType.APP_WEIXIN.getCode() == loginType) {
                return wechatAppLogin(ip, raw);
            } else {
                throw new AppServiceException(ExceptionDefinition.USER_THIRD_PART_NOT_SUPPORT);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("[?????????????????????] ??????", e);
            throw new AppServiceException(ExceptionDefinition.USER_THIRD_PART_LOGIN_FAILED);
        }
    }

    @Override
    public String registerOneKeyLoginKey(String encryption, String ip) throws ServiceException {
        AES aes = SecureUtil.aes("MjZdfoLInbGG1va4vWvy5IrP8BOTIGFQe".getBytes());
        String json = new String(aes.decrypt(encryption), StandardCharsets.UTF_8);
        JSONObject jsonObject = JSONObject.parseObject(json);
        cacheComponent.putRaw(CacheConst.USER_ONE_KEY_LOGIN_TEMP_TOKEN + jsonObject.getString("tempToken"), jsonObject.getString("phone"), 90);
        return "ok";
    }

    @Override
    public UserDTO oneKeyLogin(String tempToken) throws ServiceException {
        String phone = cacheComponent.getRaw(CacheConst.USER_ONE_KEY_LOGIN_TEMP_TOKEN + tempToken);
        if (!StringUtils.isEmpty(phone)) {
            // ???????????????
            // TODO

        }
        throw new AppServiceException(ExceptionDefinition.USER_THIRD_PART_LOGIN_FAILED);
    }

    @Override
    public String syncUserInfo(String nickname, String avatarUrl, Integer gender, Long birthday, String accessToken, Long userId) throws ServiceException {
        UserDO updateUserDO = new UserDO();
        updateUserDO.setId(userId);
        updateUserDO.setNickname(nickname);
        updateUserDO.setAvatarUrl(avatarUrl);
        updateUserDO.setGender(gender);
        updateUserDO.setGmtUpdate(new Date());
        if (birthday != null)
            updateUserDO.setBirthday(new Date(birthday));
        if (userMapper.updateById(updateUserDO) > 0) {
            //??????SESSION??????
            UserDTO user = sessionUtil.getUser();
            if (!StringUtils.isEmpty(nickname)) {
                user.setNickname(nickname);
            }
            if (!StringUtils.isEmpty(avatarUrl)) {
                user.setAvatarUrl(avatarUrl);
            }
            if (birthday != null) {
                user.setBirthday(new Date(birthday));
            }
            if (gender != null) {
                user.setGender(gender);
            }
            userRedisTemplate.opsForValue().set(Const.USER_REDIS_PREFIX + accessToken, JSONObject.toJSONString(user));
            return "ok";
        }
        throw new AppServiceException(ExceptionDefinition.USER_UNKNOWN_EXCEPTION);
    }

    @Override
    public Object getH5Sign(String url) throws ServiceException {
        try {
            String wxH5AccessToken = userBizService.getWxH5AccessToken();
            //????????????????????????????????????????????????
            String wxH5Ticket = userBizService.getWxH5Ticket(wxH5AccessToken);
            String noncestr = GeneratorUtil.genUUId();
            long timestamp = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append("jsapi_ticket=");
            sb.append(wxH5Ticket);
            sb.append("&noncestr=");
            sb.append(noncestr);
            sb.append("&timestamp=");
            sb.append(timestamp);
            sb.append("&url=");
            sb.append(url);
            //??????
            String content = sb.toString();
            String signature = SecureUtil.sha256(content);
            Map<String, Object> obj = new HashMap<>();
            obj.put("noncestr", noncestr);
            obj.put("timestamp", timestamp);
            obj.put("sign", signature);
            return obj;
        } catch (Exception e) {
            logger.info("[??????H5??????] ??????", e);
            throw new AppServiceException(ExceptionDefinition.APP_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Boolean checkLogin(String accessToken) throws ServiceException {
        return userRedisTemplate.hasKey(Const.USER_REDIS_PREFIX + accessToken);
    }


    /*************************??????????????? START******************************/

    /**
     * ??????????????????????????????????????????APP????????????
     * @param loginType MP_WEIXIN(1, "WX???????????????"), APP_WEIXIN(2, "WX???????????????")
     * @param ip
     * @param raw
     * @return
     * @throws Exception
     */
    private UserDTO wechatSafeLogin(Integer loginType, String ip, String raw) throws Exception {
        //?????????????????????
        JSONObject thirdPartJsonObject = JSONObject.parseObject(raw);
        String code = thirdPartJsonObject.getString("code");
        String body = okHttpClient.newCall(new Request.Builder()
                .url("https://api.weixin.qq.com/sns/jscode2session?appid=" + (UserLoginType.MP_WEIXIN.getCode() == loginType ? this.fwWxAppProperties.getMiniAppId() : this.fwWxAppProperties.getAppId()) +
                        "&secret=" + (UserLoginType.MP_WEIXIN.getCode() == loginType ? this.fwWxAppProperties.getMiniAppSecret() : this.fwWxAppProperties.getAppSecret()) +
                        "&grant_type=authorization_code&js_code=" + code).get().build()).execute().body().string();
        JSONObject jsonObject = JSONObject.parseObject(body);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode == null || errcode == 0) {
            String miniOpenId = jsonObject.getString("openid");
            List<UserDO> userDOS = userMapper.selectList(new QueryWrapper<UserDO>().eq("open_id", miniOpenId).eq("login_type", loginType));
            UserDO userDO;
            if (CollectionUtils.isEmpty(userDOS)) {
                //????????????????????????????????????
                Date now = new Date();
                UserDO newUserDO = new UserDO();
                newUserDO.setWxMpOpenId(miniOpenId);
                newUserDO.setLastLoginIp(ip);
                newUserDO.setGmtLastLogin(now);
                newUserDO.setGmtUpdate(now);
                newUserDO.setGmtCreate(now);
                userMapper.insert(newUserDO);
                //?????????????????????????????????????????????????????????
                userDO = userMapper.selectById(newUserDO.getId());
            } else {
                userDO = userDOS.get(0);
                UserDO userUpdateDO = new UserDO();
                userUpdateDO.setId(userDO.getId());
                userUpdateDO.setGmtLastLogin(new Date());
                userUpdateDO.setLastLoginIp(ip);
                userMapper.updateById(userUpdateDO);
            }
            //??????????????????????????????
            if (userDO.getStatus() == 0) {
                throw new AppServiceException(ExceptionDefinition.USER_CAN_NOT_ACTIVE);
            }
            String accessToken = GeneratorUtil.genSessionId();
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(userDO, userDTO);
            userRedisTemplate.opsForValue().set(Const.USER_REDIS_PREFIX + accessToken, JSONObject.toJSONString(userDTO));
            userDTO.setAccessToken(accessToken);
            return userDTO;
        } else {
            logger.info("[????????????] ???????????? ???????????????" + body);
            throw new AppServiceException(ExceptionDefinition.USER_THIRD_UNEXPECT_RESPONSE);
        }

    }

    /**
     * ????????????????????????H5??????
     * @param ip
     * @param raw
     * @return
     * @throws Exception
     */
    private UserDTO wechatH5Login(String ip, String raw) throws Exception {
        //H5 ???????????????????????????
        String json = okHttpClient.newCall(
                new Request.Builder().url("https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                        + this.fwWxAppProperties.getH5AppId() + "&secret=" + this.fwWxAppProperties.getH5AppSecret() + "&code=" + raw + "&grant_type=authorization_code").build()).execute().body().string();
        JSONObject jsonObject = JSONObject.parseObject(json);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode == null || errcode == 0) {
            String openid = jsonObject.getString("openid");
            List<UserDO> userDOS = userMapper.selectList(new QueryWrapper<UserDO>().eq("open_id", openid).eq("login_type", UserLoginType.H5_WEIXIN.getCode()));
            if (!CollectionUtils.isEmpty(userDOS)) {
                //?????????????????????????????????????????????
                String accessToken = GeneratorUtil.genSessionId();
                UserDTO userDTO = new UserDTO();
                BeanUtils.copyProperties(userDOS.get(0), userDTO);
                userRedisTemplate.opsForValue().set(Const.USER_REDIS_PREFIX + accessToken, JSONObject.toJSONString(userDTO));
                userDTO.setAccessToken(accessToken);
                return userDTO;
            } else {
                String userAccessToken = jsonObject.getString("access_token");
                //????????????AccessToken??????????????????
                String userInfoJson = okHttpClient.newCall(
                        new Request.Builder().url("https://api.weixin.qq.com/sns/userinfo?access_token="
                                + userAccessToken + "&openid=" + openid + "&lang=zh_CN").build()).execute().body().string();
                JSONObject userInfoJsonObject = JSONObject.parseObject(userInfoJson);
                Date now = new Date();
                UserDO newUserDO = new UserDO();
                newUserDO.setNickname(userInfoJsonObject.getString("nickname"));
                newUserDO.setAvatarUrl(userInfoJsonObject.getString("headimgurl"));
                newUserDO.setGender(userInfoJsonObject.getInteger("sex"));
                newUserDO.setWxH5OpenId(openid);
                newUserDO.setLastLoginIp(ip);
                newUserDO.setGmtLastLogin(now);
                newUserDO.setGmtUpdate(now);
                newUserDO.setGmtCreate(now);
                userMapper.insert(newUserDO);
                //?????????????????????????????????????????????????????????
                UserDO userDO = userMapper.selectById(newUserDO.getId());
                String accessToken = GeneratorUtil.genSessionId();
                UserDTO userDTO = new UserDTO();
                BeanUtils.copyProperties(userDO, userDTO);
                userRedisTemplate.opsForValue().set(Const.USER_REDIS_PREFIX + accessToken, JSONObject.toJSONString(userDTO));
                userDTO.setAccessToken(accessToken);
                return userDTO;
            }
        } else {
            throw new AppServiceException(ExceptionDefinition.USER_THIRD_PART_LOGIN_FAILED);
        }
    }

    /**
     * ??????APP??????
     * @param ip
     * @param raw
     * @return
     * @throws Exception
     */
    private UserDTO wechatAppLogin(String ip, String raw) throws Exception {
        //UNI-APP ??? ??????APP?????? APPSecret??????????????????????????????????????????????????????????????????????????????????????????
        JSONObject jsonObject = JSONObject.parseObject(raw);
        JSONObject authResult = jsonObject.getJSONObject("authResult");
        String openid = authResult.getString("openid");
        List<UserDO> userDOS = userMapper.selectList(new QueryWrapper<UserDO>().eq("open_id", openid).eq("login_type", UserLoginType.APP_WEIXIN.getCode()));
        UserDO userDO;
        if (CollectionUtils.isEmpty(userDOS)) {
            //???????????????
            Date now = new Date();
            UserDO newUserDO = new UserDO();
            newUserDO.setWxAppOpenId(openid);
            newUserDO.setLastLoginIp(ip);
            newUserDO.setGmtLastLogin(now);
            newUserDO.setGmtUpdate(now);
            newUserDO.setGmtCreate(now);
            userMapper.insert(newUserDO);
            //?????????????????????????????????????????????????????????
            userDO = userMapper.selectById(newUserDO.getId());
        } else {
            userDO = userDOS.get(0);
            UserDO userUpdateDO = new UserDO();
            userUpdateDO.setId(userDO.getId());
            userUpdateDO.setGmtLastLogin(new Date());
            userUpdateDO.setLastLoginIp(ip);
            userMapper.updateById(userUpdateDO);
        }
        //??????????????????????????????
        if (userDO.getStatus() == 0) {
            throw new AppServiceException(ExceptionDefinition.USER_CAN_NOT_ACTIVE);
        }
        String accessToken = GeneratorUtil.genSessionId();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userDO, userDTO);
        userRedisTemplate.opsForValue().set(Const.USER_REDIS_PREFIX + accessToken, JSONObject.toJSONString(userDTO));
        userDTO.setAccessToken(accessToken);
        return userDTO;
    }

    /*************************??????????????? END******************************/

}
