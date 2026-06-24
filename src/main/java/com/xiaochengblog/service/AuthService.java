package com.xiaochengblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaochengblog.dto.AuthResponse;
import com.xiaochengblog.dto.LoginRequest;
import com.xiaochengblog.dto.RegisterRequest;
import com.xiaochengblog.dto.UserDTO;
import com.xiaochengblog.exception.BusinessException;
import com.xiaochengblog.mapper.UserMapper;
import com.xiaochengblog.model.User;
import com.xiaochengblog.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public AuthResponse login(LoginRequest request) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("email", request.getEmail());
        User user = userMapper.selectOne(qw);
        if (user == null) {
            throw new BusinessException(401, "邮箱或密码不正确");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "邮箱或密码不正确");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken, toUserDTO(user));
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("email", request.getEmail());
        if (userMapper.selectCount(qw) > 0) {
            throw new BusinessException(400, "该邮箱已被注册");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken, toUserDTO(user));
    }

    public UserDTO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return toUserDTO(user);
    }

    @Transactional
    public UserDTO updateProfile(Long userId, Map<String, Object> updates) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (updates.containsKey("username")) user.setUsername((String) updates.get("username"));
        if (updates.containsKey("email")) user.setEmail((String) updates.get("email"));
        if (updates.containsKey("bio")) user.setBio((String) updates.get("bio"));
        if (updates.containsKey("avatar")) user.setAvatar((String) updates.get("avatar"));
        if (updates.containsKey("link")) user.setLink((String) updates.get("link"));
        if (updates.containsKey("socialLinks")) {
            try {
                user.setSocialLinks(objectMapper.writeValueAsString(updates.get("socialLinks")));
            } catch (Exception ignored) {}
        }

        userMapper.updateById(user);
        return toUserDTO(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BusinessException(401, "刷新令牌无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        String newToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        return new AuthResponse(newToken, newRefreshToken, toUserDTO(user));
    }

    public UserDTO toUserDTO(User user) {
        Map<String, String> socialLinks = new LinkedHashMap<>();
        String linksJson = user.getSocialLinks();
        if (linksJson != null && !linksJson.isBlank()) {
            try {
                socialLinks = objectMapper.readValue(linksJson,
                        new TypeReference<Map<String, String>>() {});
            } catch (Exception ignored) {}
        }

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getBio(),
                user.getLink(),
                socialLinks,
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
