package com.blue.hosting.service.signup;

import com.blue.hosting.entity.account.AccountInfoDAO;
import com.blue.hosting.entity.account.AccountInfoRepo;
import com.blue.hosting.security.eRoleName;
import com.blue.hosting.entity.account.AccountInfoVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.NoSuchElementException;

@Service("accountSignUp")
public class AccountMangement {
    private AccountInfoRepo mAccountInfoRepo;

    @Resource(name = "accountInfoRepo")
    private void setAccountInfoRepo(AccountInfoRepo accountInfoRepo) {
        mAccountInfoRepo = accountInfoRepo;
    }

    public boolean findById(String id) {
        try {
            mAccountInfoRepo.findById(id).get();
        } catch (NoSuchElementException except) {
            return false;
        }
        return true;
    }

    public boolean create(AccountInfoVO accountInfoVO) {
        boolean bResult = findById(accountInfoVO.getId());
        if (bResult == true) {
            return false;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        eRoleName roleName = eRoleName.ROLE_USER;
        String id = accountInfoVO.getId();
        String passwd = accountInfoVO.getPasswd();
        String encodePasswd = passwordEncoder.encode(passwd);
        AccountInfoDAO accountInfoDAO = new AccountInfoDAO(roleName.getmRole(), encodePasswd, id);
        mAccountInfoRepo.saveAndFlush(accountInfoDAO);

        return true;
    }
}
