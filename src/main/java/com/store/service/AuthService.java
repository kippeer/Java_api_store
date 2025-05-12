package com.store.service;

import com.store.dto.AuthRequest;
import com.store.dto.AuthResponse;
import com.store.dto.UserDTO;
import com.store.entity.User;
import com.store.entity.enums.UserRole;
import com.store.mapper.UserMapper;
import com.store.repository.UserRepository;
import com.store.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        // Check if username already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = userMapper.toEntity(userDTO);

        // Set default role if none provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<UserRole> roles = new HashSet<>();
            roles.add(UserRole.CLIENT); // ou o papel padrão desejado
            user.setRoles(roles);
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);

        // Return DTO without password
        return userMapper.toDTO(savedUser);
    }

    public AuthResponse authenticateUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        List<UserRole> roles = userDetails.getAuthorities().stream()
                .map(item -> UserRole.valueOf(item.getAuthority().replace("ROLE_", "")))  // Convertendo a autoridade para o enum UserRole
                .collect(Collectors.toList());

        return new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );
    }

    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toDTO(user);
    }
}