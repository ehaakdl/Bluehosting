package com.blue.hosting.service.email;

import com.blue.hosting.entity.account.AccountInfoRepo;
import com.blue.hosting.entity.email.EmailStateDAO;
import com.blue.hosting.entity.email.EmailStateRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service("emailManagement")
public class EmailManagement extends MailTransfer {
    private int CODE_BOUNDRY = 9999;
    @Resource(name="accountInfoRepo")
    public void setmAccountInfoRepo(AccountInfoRepo mAccountInfoRepo) {
        this.mAccountInfoRepo = mAccountInfoRepo;
    }

    private AccountInfoRepo mAccountInfoRepo;

    private final char NO_AUTHENTICATED_FLAG = 'N';

    private final int EXPIRE_TIME = 1000 * 60;

    @Resource(name="emailStateRepo")
    public void setEmailStateRepo(EmailStateRepo emailStateRepo) {
        this.mEmailStateRepo = emailStateRepo;
    }

    private EmailStateRepo mEmailStateRepo;

    @Scheduled(fixedDelay = 1000 * 60)
    protected void cleaner(){
        List<EmailStateDAO> list = mEmailStateRepo.findAll();
        Date date = new Date();
        for (EmailStateDAO emailStateDAO : list) {
            if(date.after(new Date(emailStateDAO.getExpireTime())) == false){
                continue;
            }
            mEmailStateRepo.deleteById(emailStateDAO.getEmail());
        }
    }

    public boolean insert(String email){
        Random rand = new Random();
        Long expireTime = new Date().getTime() + EXPIRE_TIME;
        try {
            if(mAccountInfoRepo.existsByEmail(email)){
                throw new Exception();
            }
            if(mEmailStateRepo.existsById(email)){
                throw new Exception();
            }
            mEmailStateRepo.save(new EmailStateDAO(email, NO_AUTHENTICATED_FLAG, expireTime
                    , rand.nextInt(CODE_BOUNDRY)));
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean isCode(int code, String email){
        if(mEmailStateRepo.existsById(email) == false){
            return false;
        }
        try{
            Optional<EmailStateDAO> optional = mEmailStateRepo.findById(email);
        } catch (Exception e){
            return false;
        }

        return true;
    }
}
