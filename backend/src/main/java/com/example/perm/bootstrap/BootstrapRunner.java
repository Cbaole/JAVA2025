package com.example.perm.bootstrap;

import com.example.perm.common.UserStatus;
import com.example.perm.entity.ModuleEntity;
import com.example.perm.entity.OptionEntity;
import com.example.perm.entity.RoleEntity;
import com.example.perm.entity.RoleModulePermEntity;
import com.example.perm.entity.UserEntity;
import com.example.perm.repo.ModuleRepository;
import com.example.perm.repo.OptionRepository;
import com.example.perm.repo.RoleModulePermRepository;
import com.example.perm.repo.RoleRepository;
import com.example.perm.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BootstrapRunner implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;
    private final ModuleRepository moduleRepository;
    private final RoleModulePermRepository roleModulePermRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapRunner(RoleRepository roleRepository,
                           UserRepository userRepository,
                           OptionRepository optionRepository,
                           ModuleRepository moduleRepository,
                           RoleModulePermRepository roleModulePermRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.optionRepository = optionRepository;
        this.moduleRepository = moduleRepository;
        this.roleModulePermRepository = roleModulePermRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        var adminRole = roleRepository.findByRoleName("ADMIN").orElseGet(() -> {
            var r = new RoleEntity();
            r.setRoleName("ADMIN");
            r.setRoleDesc("系统管理员");
            return roleRepository.save(r);
        });
        var userRole = roleRepository.findByRoleName("USER").orElseGet(() -> {
            var r = new RoleEntity();
            r.setRoleName("USER");
            r.setRoleDesc("普通用户");
            return roleRepository.save(r);
        });

        ensureOption("POST", "管理员", "ADMIN", 1);
        ensureOption("POST", "员工", "STAFF", 2);
        ensureOption("AREA", "一区", "A1", 1);
        ensureOption("AREA", "二区", "A2", 2);

        var root = ensureModule(null, "系统管理", "sys", 1, 1, "anticon-menu", "系统", "sys", "/admin");
        var mUser = ensureModule(root, "账号管理", "userMng", 2, 1001, "anticon-user", "系统", "au/user", "/admin/users");
        var mRole = ensureModule(root, "角色管理", "roleMng", 2, 1002, "anticon-team", "系统", "au/role", "/admin/roles");
        var mOpt = ensureModule(root, "选项管理", "optionMng", 2, 1003, "anticon-setting", "系统", "au/option", "/admin/options");
        var mMod = ensureModule(root, "模块管理", "moduleMng", 2, 1004, "anticon-appstore", "系统", "au/module", "/admin/modules");
        var mPerm = ensureModule(root, "权限配置", "permMng", 2, 1005, "anticon-safety", "系统", "au/perm", "/admin/perms");

        var admin = userRepository.findByUsername("admin").orElseGet(() -> {
            var u = new UserEntity();
            u.setName("系统管理员");
            u.setIdCard("110101199001010011");
            u.setPhone("13800000000");
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("123456"));
            u.setRole(adminRole);
            u.setStatus(UserStatus.ACTIVE);
            return userRepository.save(u);
        });
        if (admin.getRole() == null || !"ADMIN".equalsIgnoreCase(admin.getRole().getRoleName())) {
            admin.setRole(adminRole);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(admin);
        }

        var userPermModules = List.of(mUser, mRole, mOpt, mMod, mPerm);
        for (var mod : userPermModules) {
            ensureRolePerm(adminRole, mod, true, true, true, true);
        }
        for (var mod : userPermModules) {
            ensureRolePerm(userRole, mod, false, false, false, false);
        }
    }

    private void ensureOption(String groupKey, String title, String value, int orderNo) {
        var existing = optionRepository.findByGroupKeyOrderByOrderNoAsc(groupKey).stream()
                .filter(o -> value.equals(o.getValue()))
                .findFirst();
        if (existing.isPresent()) {
            return;
        }
        var o = new OptionEntity();
        o.setGroupKey(groupKey);
        o.setTitle(title);
        o.setValue(value);
        o.setOrderNo(orderNo);
        optionRepository.save(o);
    }

    private ModuleEntity ensureModule(ModuleEntity parent, String cnName, String enName, int level, int orderNo, String icon, String groupName, String permKey, String path) {
        return moduleRepository.findByPermKey(permKey).orElseGet(() -> {
            var m = new ModuleEntity();
            m.setCnName(cnName);
            m.setEnName(enName);
            m.setLevel(level);
            m.setOrderNo(orderNo);
            m.setIcon(icon);
            m.setGroupName(groupName);
            m.setPermKey(permKey);
            m.setPath(path);
            m.setParent(parent);
            m.setIsParent(true);
            m.setExpanded(true);
            return moduleRepository.save(m);
        });
    }

    private void ensureRolePerm(RoleEntity role, ModuleEntity module, boolean canRead, boolean canAdd, boolean canUpdate, boolean canSee) {
        var existing = roleModulePermRepository.findByRole_IdAndModule_Id(role.getId(), module.getId());
        if (existing.isPresent()) {
            return;
        }
        var p = new RoleModulePermEntity();
        p.setRole(role);
        p.setModule(module);
        p.setCanRead(canRead);
        p.setCanAdd(canAdd);
        p.setCanUpdate(canUpdate);
        p.setCanSee(canSee);
        roleModulePermRepository.save(p);
    }
}
