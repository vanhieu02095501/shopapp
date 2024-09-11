package com.project.shopapp.service;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exception.DataNotFindException;
import com.project.shopapp.models.User;

public interface IUserService {

    User createUser(UserDTO userDTO) throws Exception;
    String login (String phoneNumber,String password) throws Exception;


}
