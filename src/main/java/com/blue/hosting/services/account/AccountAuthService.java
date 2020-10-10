package com.blue.hosting.services.account;

import com.blue.hosting.entity.AccountInfoTb;
import com.blue.hosting.entity.AccountInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service("accountAuthSerivce")
public class AccountAuthService implements UserDetailsService {
    @Autowired
    private AccountInfoRepo accountInfoRepo;

    private AccountInfoTb findById(String id) throws Exception{
        AccountInfoTb accountInfo = null;
        try {
            accountInfo = accountInfoRepo.findById(id).get();
        } catch (IllegalArgumentException except){
            throw (Exception) new Exception().initCause(except);
        } catch (NoSuchElementException except){
            throw (Exception) new Exception().initCause(except);
        } catch (NullPointerException except){
            throw (Exception) new Exception().initCause(except);
        }
        return accountInfo;
    }

    @Override
    public AccountInfoVO loadUserByUsername(String accountId) throws UsernameNotFoundException {
        eSecurityVal securityErrorMsg = null;
        AccountInfoVO accountInfoVO = null;
        try {
            AccountInfoTb accountInfo = findById(accountId);
            String email = accountInfo.getmEmail();
            String userName = accountInfo.getmUsername();
            String password = accountInfo.getmPassword();
            accountInfoVO = new AccountInfoVO(email, userName, password);
        } catch(Exception except){
            if(except.getCause() instanceof IllegalAccessException){
                //로그 메시지 포함
                throw new UsernameNotFoundException(accountId);
            }
            if(except.getCause() instanceof NoSuchElementException){
                //로그 메시지 포함
                throw new UsernameNotFoundException(accountId);
            }
            if(except.getCause() instanceof NullPointerException){
                //로그 메시지 포함
                throw new UsernameNotFoundException(accountId);
            }
        }

        return accountInfoVO;
    }
}
