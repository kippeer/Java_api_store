package com.store.service;

import com.store.entity.Order;
import com.store.entity.User;
import com.store.exceptions.UserNotFoundException;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public void checkOrderAccess(Order order) {
        checkPermission(order, "access");
    }

    public void checkOrderUpdatePermission(Order order) {
        checkPermission(order, "update");
    }

    private void checkPermission(Order order, String action) {
        User currentUser = getCurrentUser();
        if (!hasPermission(order, currentUser)) {
            throw new AccessDeniedException("You don't have permission to " + action + " this order");
        }
    }

    private boolean hasPermission(Order order, User user) {
        return isAdmin(user) || isOrderOwner(order, user);
    }

    private boolean isAdmin(User user) {
        // Se possível, use enum de roles em vez de verificar diretamente com string
        return user.getRoles().contains("ADMIN");
    }

    private boolean isOrderOwner(Order order, User user) {
        return order.getUser().getId().equals(user.getId());
    }
}
