package org.example.service;

import org.example.VO.LoginVO;
import org.example.dto.LoginDTO;
import org.example.dto.RegisterDTO;
import org.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface UserService {
    public LoginVO login(LoginDTO loginDTO);
    public void register(RegisterDTO registerDTO);
}
