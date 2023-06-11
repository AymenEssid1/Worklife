package tn.esprit.springfever.Services.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import tn.esprit.springfever.Repositories.*;
import tn.esprit.springfever.Security.jwt.JwtUtils;
import tn.esprit.springfever.Services.Interface.IServiceUser;

import tn.esprit.springfever.configuration.MailConfiguration;
import tn.esprit.springfever.configuration.RequestUtils;
import tn.esprit.springfever.dto.UserDTO;
import tn.esprit.springfever.entities.*;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Slf4j
public class UserIMP implements IServiceUser {

    @Autowired
    UserRepo userrepo;

    @Autowired
    RoleRepo roleRepo;



    @Autowired
    private RequestUtils requestUtils;
    @Autowired
    JwtUtils jwtUtils;


    @Autowired
    private BanRepository banRepository;

    @Autowired
    private MailConfiguration mailConfiguration;



    @Override
    public User addUserAndAssignRole(User user, RoleType rolename) {
        Role role = roleRepo.findByRolename(rolename);


        user.getRoles().add(role);
        return userrepo.save(user);
    }




    @Override
    public User addUser(User user) {
        return userrepo.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User p = userrepo.findById(Long.valueOf(id)).orElse(null);
        if (p != null) {
            user.setUserid(p.getUserid());
            ;
            userrepo.save(user);
        }
        return p;
    }

    @Override
    public String deleteUser(Long user) {
        User p = userrepo.findById(Long.valueOf(user)).orElse(null);
        if (p != null) {
            userrepo.delete(p);
            return "User was successfully deleted !";
        }
        return "Not Found ! ";

    }

    @Override
    public List<User> getAllUsers() {
        return userrepo.findAll();
    }

    @Override
    public User getSingleUser(Long id) {
        return userrepo.findById(id).orElse(null);
    }









    @Transactional
    public void saveAll(List<User> users) {
        userrepo.saveAll(users);
    }

    public List<User> readUsersFromExcelFile(InputStream is) throws IOException {
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0); // assuming the user data is in the first sheet
        List<User> users = new ArrayList<>();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // skip the header row
                continue;
            }

            User user = new User();
            user.setUsername(row.getCell(0).getStringCellValue());
            user.setEmail(row.getCell(1).getStringCellValue());
            user.setFirstname(row.getCell(2).getStringCellValue());
            user.setLastname(row.getCell(3).getStringCellValue());
            user.setCin((int) row.getCell(4).getNumericCellValue());
            user.setDob(row.getCell(5).getDateCellValue());
            user.setPhoneNumber("21354227");
            user.setPassword(passwordEncoder.encode(row.getCell(6).getStringCellValue()));

            LocalDateTime currentDateTime = LocalDateTime.now();
            user.setCreationDate(currentDateTime);

            user.setEtatUser("disponible");
            Role role = roleRepo.findByRolename(RoleType.Employee);

            if (row.getCell(7) == null) {
                user.getRoles().add(role);

            } else {
                List<String> roles = Arrays.asList(row.getCell(7).getStringCellValue().split("\\s*,\\s*"));
                roles.forEach(rolee -> {
                    for (RoleType r : RoleType.values()) {

                        if (r.name().equals(rolee)) {
                            Role rol = roleRepo.findByRolename(r);
                            user.getRoles().add(rol);
                            break;
                        }
                    }
                });
            }

            users.add(user);
        }

        workbook.close();
        return users;
    }

    @Override
    public void timeoutuser(User user) throws GeoIp2Exception, IOException {

        Ban ban = new Ban();
        ban.setLastFailedLoginAttempt(LocalDateTime.now());
        ban.setExpiryTime(LocalDateTime.now().plusMinutes(100));
        ban.setUser(user);
        user.setBan(ban);
        banRepository.save(ban);
        userrepo.save(user);

        System.out.println("******************************" );

        String emailBody = "s Your Account is temporarily locked. Please try again later.";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("ACCOUNT SUSPENDED");
        message.setText(emailBody);
        message.setTo(user.getEmail());
        mailConfiguration.sendEmail(message);


    }


    @Override
    public String checkBan(User user) {


        if (user != null && user.getBan() != null && user.getBan().getExpiryTime() != null &&
                LocalDateTime.now().isBefore(user.getBan().getExpiryTime())) {
            // User is banned, return error response
            Duration remainingTime = Duration.between(LocalDateTime.now(), user.getBan().getExpiryTime());
            String timeLeft = String.format("%d minutes, %d seconds", remainingTime.toMinutes(), remainingTime.getSeconds() % 60);
            return timeLeft;

        }
        return "a";
    }

    @Override
    public User getUserDetailsFromToken(String token)  {
            String username =  jwtUtils.getUserNameFromJwtToken(token) ;
            return  userrepo.findByUsername(username).get();
    }


















}