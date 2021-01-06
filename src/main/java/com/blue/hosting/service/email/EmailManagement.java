package com.blue.hosting.service.email;

import com.blue.hosting.entity.email.EmailStateDAO;
import com.blue.hosting.entity.email.EmailStateRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service("emailManagement")
public class EmailManagement extends MailTransfer {
    private final int EXPIRE_TIME = 1000 * 60;
    @Resource(name="emailStateRepo")
    public void setEmailStateRepo(EmailStateRepo emailStateRepo) {
        this.mEmailStateRepo = emailStateRepo;
    }

    private EmailStateRepo mEmailStateRepo;

    @Scheduled(fixedDelay = 1000 * 10)
    private void cleaner(){
        List<EmailStateDAO> list = mEmailStateRepo.findAll();
        Date date = new Date();
        for (EmailStateDAO emailStateDAO : list) {
            if(date.after(new Date(emailStateDAO.getmExpireTime())) == false){
                continue;
            }
            mEmailStateRepo.deleteById(emailStateDAO.getmEmail());
        }
    }

    public boolean insert(String email){
        Random rand = new Random();
        Long expireTime = new Date().getTime() + EXPIRE_TIME;
        try {
            if(mEmailStateRepo.existsById(email)){
                throw new Exception();
            }
            mEmailStateRepo.save(new EmailStateDAO(email, "N", expireTime
                    , rand.nextInt(9999)));
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
