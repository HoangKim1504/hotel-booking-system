package com.hotelbooking.service;

import com.hotelbooking.model.Permission;
import com.hotelbooking.model.Role;
import com.hotelbooking.repository.PermissionRepository;
import com.hotelbooking.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissionLoader {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * @return tập {@code permission.code} (vd. USER_VIEW) — thứ tự ổn định để debug
     */
    public Set<String> loadPermissionCodes(List<String> roleIds) {
        Set<String> codes = new LinkedHashSet<>();
        if (roleIds == null || roleIds.isEmpty()) {
            return codes;
        }

        // Load roles theo id
        List<Role> roles = roleRepository.findByIdIn(roleIds);
        Set<String> permissionIds = new LinkedHashSet<>();
        for (Role role : roles) {
            if (role.getPermissionIds() != null) {
                permissionIds.addAll(role.getPermissionIds());
            }
        }
        if (permissionIds.isEmpty()) {
            return codes;
        }

        // Load permissions → lấy code
        for (Permission p : permissionRepository.findByIdIn(permissionIds)) {
            codes.add(p.getCode());
        }
        return codes;
    }

    /**
     * Tên role (ADMIN, EDITOR, …) để trả /me — không dùng để @PreAuthorize.
     */
    public List<String> loadRoleCodes(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return roleRepository.findByIdIn(roleIds).stream()
                .map(Role::getCode)
                .toList();
    }

}
