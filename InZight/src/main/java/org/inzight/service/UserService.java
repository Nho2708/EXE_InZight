package org.inzight.service;

import org.inzight.dto.request.UserRegisterRequest;
import org.inzight.dto.response.UserResponse;

public interface UserService {

    UserResponse getUserById(Long id);
}