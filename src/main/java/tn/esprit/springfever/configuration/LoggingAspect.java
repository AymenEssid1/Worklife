package tn.esprit.springfever.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j

public class LoggingAspect {


    @Autowired
    private MailConfiguration mailConfiguration;

    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);





}