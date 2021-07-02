package com.dobbinarchetype.demo.data.exception;

import com.dobbinsoft.fw.core.exception.ServiceExceptionDefinition;

/**
 * ClassName: ExceptionDefinition
 * Description: 异常常量定义
 * 1. 所有已知异常，均以ServiceException异常抛出
 * 2. 异常定义不区分 ADMIN 与 APP 均定义在此文件中
 * 3. 每个模块1000个异常码
 *
 *    例如用户： 11000 —— 11999
 *      管理员： 50000 —— 50999
 *
 * @author: e-weichaozheng
 * @date: 2021-03-18
 */
public class ExceptionDefinition {

    public static final ServiceExceptionDefinition APP_UNKNOWN_EXCEPTION =
            new ServiceExceptionDefinition(10000, "系统未知异常");

    public static final ServiceExceptionDefinition PARAM_CHECK_FAILED =
            new ServiceExceptionDefinition(10002, "参数校验失败");

    public static final ServiceExceptionDefinition SYSTEM_BUSY =
            new ServiceExceptionDefinition(10007, "系统繁忙～");

    public static final ServiceExceptionDefinition USER_UNKNOWN_EXCEPTION =
            new ServiceExceptionDefinition(11000, "用户系统未知异常");

    public static final ServiceExceptionDefinition USER_SEND_VERIFY_FAILED =
            new ServiceExceptionDefinition(11001, "发送验证码失败");

    public static final ServiceExceptionDefinition USER_VERIFY_CODE_NOT_EXIST =
            new ServiceExceptionDefinition(11002, "验证码未发送或已过期");

    public static final ServiceExceptionDefinition USER_VERIFY_CODE_NOT_CORRECT =
            new ServiceExceptionDefinition(11003, "验证码不正确");

    public static final ServiceExceptionDefinition USER_PHONE_HAS_EXISTED =
            new ServiceExceptionDefinition(11004, "手机已经被注册");

    public static final ServiceExceptionDefinition USER_PHONE_NOT_EXIST =
            new ServiceExceptionDefinition(11005, "手机尚未绑定账号");

    public static final ServiceExceptionDefinition USER_PHONE_OR_PASSWORD_NOT_CORRECT =
            new ServiceExceptionDefinition(11006, "手机号或密码错误!");

    public static final ServiceExceptionDefinition USER_THIRD_PART_LOGIN_FAILED =
            new ServiceExceptionDefinition(11007, "用户第三方登录失败");

    public static final ServiceExceptionDefinition USER_THIRD_UNEXPECT_RESPONSE =
            new ServiceExceptionDefinition(11008, "第三方登录期望之外的错误");

    public static final ServiceExceptionDefinition USER_THIRD_PART_NOT_SUPPORT =
            new ServiceExceptionDefinition(11009, "未知的第三方登录平台");

    public static final ServiceExceptionDefinition USER_INFORMATION_MISSING =
            new ServiceExceptionDefinition(11010, "用户信息缺失，不能添加");

    public static final ServiceExceptionDefinition USER_PHONE_ALREADY_EXIST =
            new ServiceExceptionDefinition(11011, "用户电话已经存在，不能添加");

    public static final ServiceExceptionDefinition USER_CAN_NOT_ACTIVE =
            new ServiceExceptionDefinition(11012, "用户处于冻结状态，请联系管理员");



    public static final ServiceExceptionDefinition ADMIN_UNKNOWN_EXCEPTION =
            new ServiceExceptionDefinition(50000, "管理员系统未知异常");

    public static final ServiceExceptionDefinition ADMIN_NOT_EXIST =
            new ServiceExceptionDefinition(50001, "管理员不存在");

    public static final ServiceExceptionDefinition ADMIN_PASSWORD_ERROR =
            new ServiceExceptionDefinition(50002, "密码错误");

    public static final ServiceExceptionDefinition ADMIN_NOT_BIND_WECHAT =
            new ServiceExceptionDefinition(50003, "管理员尚未绑定微信");

    public static final ServiceExceptionDefinition ADMIN_APPLY_NOT_BELONGS_TO_YOU =
            new ServiceExceptionDefinition(50004, "用户申请表并不属于您");

    public static final ServiceExceptionDefinition ADMIN_APPLY_NOT_SUPPORT_ONE_KEY =
            new ServiceExceptionDefinition(50005, "未定义类型不支持一键发布");

    public static final ServiceExceptionDefinition ADMIN_ROLE_IS_EMPTY =
            new ServiceExceptionDefinition(50006, "管理员角色为空！");

    public static final ServiceExceptionDefinition ADMIN_USER_NAME_REPEAT =
            new ServiceExceptionDefinition(50007, "管理员用户名重复");

    public static final ServiceExceptionDefinition ADMIN_VERIFYCODE_ERROR =
            new ServiceExceptionDefinition(50008, "登陆验证码错误");

    public static final ServiceExceptionDefinition ADMIN_USER_NOT_EXIST =
            new ServiceExceptionDefinition(50009, "请输入正确账号密码");

    public static final ServiceExceptionDefinition ADMIN_GUEST_NOT_NEED_VERIFY_CODE =
            new ServiceExceptionDefinition(50010, "游客用户无须验证码，请直接输入666666");

    public static final ServiceExceptionDefinition ADMIN_VERIFY_CODE_SEND_FAIL=
            new ServiceExceptionDefinition(50011, "登陆验证码发送失败");

    public static final ServiceExceptionDefinition ADMIN_GENERATOR_WORK_DIR_NOT_EXIST =
            new ServiceExceptionDefinition(50012, "工作路径不正确");

    public static final ServiceExceptionDefinition ADMIN_GENERATOR_FILE_ALREADY_EXIST =
            new ServiceExceptionDefinition(50013, "欲生成的文件已经存在");

    public static final ServiceExceptionDefinition ADMIN_GENERATOR_IO_EXCEPTION =
            new ServiceExceptionDefinition(50013, "代码生成网络异常");

    public static final ServiceExceptionDefinition ADMIN_GENERATOR_TEMPLATE_EXCEPTION =
            new ServiceExceptionDefinition(50013, "代码生成模板异常");

    public static final ServiceExceptionDefinition ADMIN_ROLE_UNION_ADMIN =
            new ServiceExceptionDefinition(50014, "角色关联仍有管理员关联");

}
