package tn.esprit.springfever.configuration;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SMS_service {



    public void sendSmsvalide(String s,String message) {
        final String ACCOUNT_SID = "ACcf915aecd1748ab4ce97eae023b72903";
        final String AUTH_TOKEN = "fb02c735173dc8e9333fffece452e6d4";
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message msg = Message.creator(new PhoneNumber("+216"+s),new PhoneNumber("+13157954012"),(message)).create();

    }
}
