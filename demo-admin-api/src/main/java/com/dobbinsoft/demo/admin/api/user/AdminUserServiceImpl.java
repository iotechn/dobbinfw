package com.dobbinsoft.demo.admin.api.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dobbinsoft.demo.data.domain.UserDO;
import com.dobbinsoft.demo.data.exception.ExceptionDefinition;
import com.dobbinsoft.demo.data.mapper.UserMapper;
import com.dobbinsoft.fw.core.exception.AdminServiceException;
import com.dobbinsoft.fw.core.exception.ServiceException;
import com.dobbinsoft.fw.support.model.Page;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kbq
 * Date: 2019-07-11
 * Time: 下午7:57
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(UserDO user, Long adminId) throws ServiceException {
        if (user == null){
            throw new AdminServiceException(ExceptionDefinition.USER_INFORMATION_MISSING);
        }
        if(user.getPhone() == null){
            throw new AdminServiceException(ExceptionDefinition.USER_INFORMATION_MISSING);
        }
        if(userMapper.selectCount(new QueryWrapper<UserDO>().eq("phone",user.getPhone())) > 0){
            throw new AdminServiceException(ExceptionDefinition.USER_PHONE_ALREADY_EXIST);
        }
        Date now = new Date();
        user.setId(null);
        user.setPassword(Md5Crypt.md5Crypt(user.getPassword().getBytes(), "$1$" + user.getPhone().substring(0,7)));
        user.setGmtCreate(now);
        user.setGmtUpdate(now);
        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id, Long adminId) throws ServiceException {
        return userMapper.delete(new QueryWrapper<UserDO>()
                .eq("id", id)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(UserDO user, Long adminId) throws ServiceException {
        if (user == null || user.getId() == null){
            throw new AdminServiceException(ExceptionDefinition.USER_INFORMATION_MISSING);
        }
        if(user.getPhone() == null){
            throw new AdminServiceException(ExceptionDefinition.USER_INFORMATION_MISSING);
        }
        if(userMapper.selectCount(new QueryWrapper<UserDO>().eq("phone",user.getPhone()).notIn("id",user.getId())) > 0){
            throw new AdminServiceException(ExceptionDefinition.USER_PHONE_ALREADY_EXIST);
        }
        Date now = new Date();
        user.setGmtUpdate(now);

        if(user.getPassword() != null) {
            user.setPassword(Md5Crypt.md5Crypt(user.getPassword().getBytes(), "$1$" + user.getPhone().substring(0, 7)));
        }
        return userMapper.updateById(user) > 0;
    }

    @Override
    public Boolean editStatus(Long userId, Integer status, Long adminId) throws ServiceException {
        if(userId == null || status == null || (status != 0 && status != 1)){
            throw new AdminServiceException(ExceptionDefinition.USER_INFORMATION_MISSING);
        }
        UserDO userDO = new UserDO();
        userDO.setId(userId);
        userDO.setStatus(status);
        userDO.setGmtUpdate(new Date());
        if(userMapper.updateById(userDO) > 0){
            return true;
        }
        return false;
    }

    @Override
    public Page<UserDO> list(Long id, String nickname, Integer level, Integer gender, Integer status, Integer pageNo, Integer limit, Long adminId) throws ServiceException {
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        if (id != null) {
            wrapper.eq("id", id);
        }
        if (level != null) {
            wrapper.eq("level", level);
        }
        if (gender != null) {
            wrapper.eq("gender", gender);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (!StringUtils.isEmpty(nickname)) {
            wrapper.like("nickname", nickname);
        }
        return userMapper.selectPage(Page.div(pageNo, limit, UserDO.class), wrapper);
    }
    
}
