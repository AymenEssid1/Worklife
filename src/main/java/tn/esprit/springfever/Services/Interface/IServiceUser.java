package tn.esprit.springfever.Services.Interface;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import tn.esprit.springfever.dto.UserDTO;
import tn.esprit.springfever.entities.*;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public interface IServiceUser {


    public User addUserAndAssignRole(User user, RoleType rolename);



    public User addUser(User user);
    public User updateUser(Long id,User user);
    public String deleteUser(Long user);
    public List<User> getAllUsers();
    public User getSingleUser(Long id);


    public  void saveAll(List<User> users) ;

    public List<User> readUsersFromExcelFile(InputStream is) throws IOException;

    public void timeoutuser(User user) throws GeoIp2Exception, IOException;


    public String checkBan(User user);

    public User getUserDetailsFromToken(String token) ;







}
