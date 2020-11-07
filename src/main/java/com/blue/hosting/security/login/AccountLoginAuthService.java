package com.blue.hosting.security.login;

import com.blue.hosting.entity.account.AccountInfoDAO;
import com.blue.hosting.entity.account.AccountInfoRepo;
import com.blue.hosting.security.AccountInfoVO;
import com.blue.hosting.utils.eExceptionCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.*;

@Service("accountLoginAuthSerivce")
public class AccountLoginAuthService implements UserDetailsService {
    private AccountInfoRepo mAccountInfoRepo;

    @Resource(name="accountInfoRepo")
    private void setAccountInfoRepo(AccountInfoRepo accountInfoRepo){
        mAccountInfoRepo = accountInfoRepo;
    }

    private AccountInfoDAO findById(String id) throws Exception{
        AccountInfoDAO accountInfo = null;
        try {
            accountInfo = mAccountInfoRepo.findById(id).get();
        } catch (Exception except){
            throw except;
        }
        return accountInfo;
    }

    @Override
    public AccountInfoVO loadUserByUsername(String accountId) throws UsernameNotFoundException {
        eExceptionCode securityErrorMsg = null;
        AccountInfoVO accountInfoVO = null;
        try {
            AccountInfoDAO accountInfo = findById(accountId);
            String userName = accountInfo.getmUsername();
            String password = accountInfo.getmPassword();
            //List<? extends GrantedAuthority> roleAuth = new List();
            accountInfo.getmRoleAuth();
            accountInfoVO = new AccountInfoVO(userName, password);
        } catch(Exception except){
            throw new UsernameNotFoundException(except.getMessage());
        }

        return accountInfoVO;
    }
}
