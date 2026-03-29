package org.csu.bj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Admin;
import org.csu.bj.persistence.AdminMapper;
import org.csu.bj.service.AdminService;
import org.csu.bj.utils.SaltMD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminMapper adminMapper;
    @Override
    public Admin login(String username, String password){
        if(username.length() == 0 || password.length() == 0){
            return null;
        }
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        Admin admin = adminMapper.selectOne(queryWrapper);
        boolean verifyPassword = SaltMD5Util.verifySaltPassword(password,admin.getPassword());
        if(!verifyPassword){
            return null;
        }
        return admin;
    }
    @Override
    public Result getAdmins(){
        List<Admin> adminList = adminMapper.getAdmins();
        return Result.success(adminList);
    }
    @Override
    public Result register(String username,String password){
        if(username.length()==0 || password.length()==0){
            return Result.failed("账号或密码不为空");
        }
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        Admin admin = adminMapper.selectOne(queryWrapper);
        if(admin!=null){
            return Result.failed("用户名重复!");
        }
        Admin admin1 = new Admin();
        admin1.setUsername(username);
        admin1.setAuthority(1);
        admin1.setPassword(SaltMD5Util.generateSaltPassword(password));
        int insertAns = adminMapper.insert(admin1);
        if(insertAns == 1){
            return Result.success("");
        } else {
            return Result.failed();
        }
    }
    @Override
    public Result updatePassword(Admin admin){
        try{
            admin.setPassword(SaltMD5Util.generateSaltPassword(admin.getPassword()));
            int updateAns = adminMapper.updateById(admin);
            if(updateAns==1){
                return Result.success("");
            } else {
                return Result.failed("修改失败!");
            }
        } catch (Exception e){
            e.printStackTrace();
            return Result.failed();
        }
    }
    @Override
    public Result updateAuthority(int authority, int id){
        try {
            int updateAns = adminMapper.updateAuthority(authority,id);
            if(updateAns == 1){
                return Result.success("");
            } else {
                return Result.failed("修改失败");
            }
        } catch (Exception e){
            e.printStackTrace();
            return Result.failed();
        }
    }
}
