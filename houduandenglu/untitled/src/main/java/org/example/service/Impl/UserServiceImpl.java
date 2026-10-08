package org.example.service.Impl;

import org.example.VO.LoginVO;
import org.example.VO.UserVO;
import org.example.common.ResultCode;
import org.example.dto.LoginDTO;
import org.example.entity.User;
import org.example.exception.BusinessException;
import org.example.mapper.UserMapper;
import org.example.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {


    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;


    @Override
    public LoginVO login(LoginDTO loginDTO){

        String username=loginDTO.getUsername();
        String password=loginDTO.getPassword();

        Optional<User> userOptional=userMapper.findByUsername(username);
        if(userOptional.isEmpty()){
            log.warn("登录失败，用户不存在: {}", username);
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        User user=userOptional.get();
        if(!password.equals(user.getPassword())){
            log.warn("登录失败，密码错误: {}", username);
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        // 4. 生成 token（真实项目应使用 JWT）
        String token = UUID.randomUUID().toString().replace("-", "");

        // 5. 组装返回给前端的用户信息（隐藏密码）
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setName(user.getName());

        // 6. 组装登录结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(userVO);

        log.info("用户登录成功: {} ({})", username, user.getName());
        return loginVO;
    }

}
