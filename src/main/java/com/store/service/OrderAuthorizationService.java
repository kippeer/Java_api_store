package com.store.service;

import com.store.entity.Order;
import com.store.entity.User;
import com.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderAuthorizationService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        // Reusing the JWT authentication that was set in SecurityContextHolder by JwtAuthenticationFilter
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void checkOrderAccess(Order order) {
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser) && !isOrderOwner(order, currentUser)) {
            throw new AccessDeniedException("You don't have permission to access this order");
        }
    }

    public void checkOrderUpdatePermission(Order order) {
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser) && !isOrderOwner(order, currentUser)) {
            throw new AccessDeniedException("You don't have permission to update this order");
        }
    }

    private boolean isAdmin(User user) {
        return user.getRoles().contains("ADMIN");
    }

    private boolean isOrderOwner(Order order, User user) {
        return order.getUser().getId().equals(user.getId());
    }
}