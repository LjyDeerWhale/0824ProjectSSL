package com.deer.ljy.controller;

import com.deer.ljy.pojo.Role;
import com.deer.ljy.pojo.User;


import com.deer.ljy.pojo.base.BaseResult;
import com.deer.ljy.service.RoleService;
import com.deer.ljy.service.UserService;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    //跳转首页
    @RequiresUser
    @RequestMapping("/session.do")
    public BaseResult<User> login(HttpServletRequest request) {
        BaseResult<User> result = new BaseResult<>();
        String username = request.getRemoteUser();
        User userByUsername = userService.findUserByUsername(username);
        if (userByUsername != null) {
            result.setCode(0);
            result.setData(userByUsername);
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute("sessionUser", userByUsername);
        } else {
            result.setCode(1);
        }
        return result;
    }

    //管理员访问
    @RequiresUser
    @RequestMapping("/checking.do")
    public BaseResult<User> adminPage() {
        BaseResult<User> result = new BaseResult<>();
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("sessionUser");
        System.out.println(user);
        if (user.getRoleName().equals("管理员")) {
            result.setCode(0);
        } else {
            result.setCode(1);
        }
        return result;
    }

    //用户注册
    //1.查找对应的角色名称和推荐人名称
    @RequestMapping("/register.do")
    public BaseResult addUser(User user) {
        BaseResult result = new BaseResult<>();
        System.out.println(user.getRealName());

        User userById = userService.findUserById(user.getReferId());
        System.out.println(userById.getReferCode());
        Role rolebyId = roleService.findRolebyId(user.getRoleId());

        user.setReferCode(userById.getUsername());
        user.setRoleName(rolebyId.getRoleName());
        user.setIsStart(1);

        user.setCreateTime(new Date());

        user.setLastUpdateTime(new Date());
        user.setLastLoginTime(new Date());

        int i = userService.insertUser(user);
        if (i > 0) {
            result.setCode(0);
        } else {
            result.setCode(1);
        }
        return result;
    }

    //查找所有非注册用户的推荐人
    @RequestMapping("/findRefer.do")
    public BaseResult<List<User>> findUsers() {
        BaseResult<List<User>> result = new BaseResult<>();
        List<User> allUser = userService.findAllUser();
        if (allUser != null && !allUser.isEmpty()) {
            result.setData(allUser);
            result.setCode(0);
        } else {
            result.setCode(1);
        }
        return result;
    }

    //查找所有用户
    @RequestMapping("/findAll.do")
    public BaseResult<List<User>> findAllUsers(User user, int page, int limit) {
        System.out.println("user的值" + user);
        User attribute = (User) SecurityUtils.getSubject().getSession().getAttribute("sessionUser");
        user.setRoleName(attribute.getRoleName());
        user.setReferCode(attribute.getReferCode());
        BaseResult<List<User>> result = new BaseResult<>();
        List<User> users = userService.selectAll(user, page, limit);
        if (users == null || users.isEmpty()) {
            result.setCode(1);
        } else {
            int count = (int) PageInfo.of(users).getTotal();
            result.setCode(0);
            result.setData(users);
            result.setCount(count);
            result.setPage(page);
            result.setLimit(limit);
        }
        return result;
    }

    @RequestMapping("/updatePWD.do")
    public BaseResult updatePassword(String old_password, String new_password, Integer globe) {
        BaseResult result = new BaseResult();
        System.out.println("旧密码" + old_password);
        System.out.println("新密码" + new_password);
        System.out.println("等级"+globe);
        Session session = SecurityUtils.getSubject().getSession();
        User user = (User) session.getAttribute("sessionUser");
        if (globe == 1) {
            if (!user.getPassword().equals(old_password)) {
                result.setCode(1);
                return result;
            }
        } else {
            if (!user.getPassword2().equals(old_password)) {
                result.setCode(1);
                return result;
            }
        }
        int i = userService.updatePWD(new_password, user.getId(), globe);
        if (i > 0) {
            result.setCode(0);
        } else {
            result.setCode(2);
        }
        return result;
    }
}
