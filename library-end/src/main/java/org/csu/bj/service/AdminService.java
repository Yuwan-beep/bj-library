package org.csu.bj.service;

import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Admin;
import org.springframework.stereotype.Service;

public interface AdminService {
    Admin login(String username,String password);
    Result getAdmins();
    Result register(String username,String password);
    Result updatePassword(Admin admin);
    Result updateAuthority(int authority, int id);
}
