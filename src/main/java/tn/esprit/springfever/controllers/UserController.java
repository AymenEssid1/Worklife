package tn.esprit.springfever.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import tn.esprit.springfever.Repositories.FileSystemRepository;
import tn.esprit.springfever.Repositories.RoleRepo;
import tn.esprit.springfever.Repositories.UserRepo;

import tn.esprit.springfever.Security.jwt.JwtUtils;
import tn.esprit.springfever.Services.Interface.IClaimService;
import tn.esprit.springfever.configuration.SMS_service;
import tn.esprit.springfever.Services.Interface.IFileLocationService;
import tn.esprit.springfever.Services.Interface.IServiceUser;
import tn.esprit.springfever.dto.UserDTO;
import tn.esprit.springfever.entities.*;
import tn.esprit.springfever.tools.ResourceNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:4200")
@RestController

@RequestMapping(value = "/api/user" )
public class UserController {

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    FileSystemRepository fileSystemRepository;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    IServiceUser iServiceUser;


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    SMS_service sms_service;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    IClaimService iClaimService;


    @Autowired
    private UserDetailsService userDetailsService;


    @GetMapping("/getallusers")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/getby/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + userId));
        System.out.println(user.toString());
        return ResponseEntity.ok().body(user);
    }

    @PostMapping(value = "/ADD_USER", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> signUpV3(
                                           @RequestParam String username,
                                           @RequestParam String firstname,
                                           @RequestParam String lastname,
                                           @RequestParam String email,
                                           @RequestParam String phoneNumber,
                                           @RequestParam String cin,
                                           @RequestParam String dob,
                                           @RequestParam String password,
                                           @RequestParam RoleType roleType,
                                           HttpServletRequest request) throws Exception {
        if (request != null && request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            User authentificateduser = jwtUtils.getUserFromUserName(jwtUtils.getUserNameFromJwtToken(request.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length())));
            List<RoleType> roles=authentificateduser.getRoles().stream().map(Role::getRolename).collect(Collectors.toList());
            if (roles.contains(RoleType.Boss)) {

                String user = "{\"username\": \"" + username + "\",   \"email\": \"" + email + "\",   \"firstname\": \"" + firstname + "\",   \"lastname\": \"" + lastname + "\",   \"cin\": " + cin + ",   \"phoneNumber\": \"" + phoneNumber + "\",   \"dob\": \"" + dob + "\",   \"password\": \"" + password + "\" }";

                ObjectMapper objectMapper = new ObjectMapper();
                UserDTO userDTO = objectMapper.readValue(user, UserDTO.class);

                // Validate input attributes
                if (userDTO.getFirstname() == null || userDTO.getFirstname().matches(".*\\d.*")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Firstname");
                }
                if (userDTO.getLastname() == null || userDTO.getLastname().matches(".*\\d.*")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Lastname");
                }


                if (userDTO.getPhoneNumber() == null || !userDTO.getPhoneNumber().matches("\\d{8}")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Phone Number");
                }
                if (userDTO.getEmail() == null || !userDTO.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Email Address");
                }

                // Create User object
                User u = new User();
                u.setFirstname(userDTO.getFirstname());
                u.setCin(userDTO.getCin());
                u.setLastname(userDTO.getLastname());
                u.setDob(userDTO.getDob());
                u.setEmail(userDTO.getEmail());
                u.setPassword(encoder.encode(userDTO.getPassword()));
                u.setUsername(userDTO.getUsername());
                LocalDateTime currentDateTime = LocalDateTime.now();
                u.setCreationDate(currentDateTime);
                u.setPhoneNumber(userDTO.getPhoneNumber());




                // Add user and assign role
                iServiceUser.addUserAndAssignRole(u, roleType);

                return ResponseEntity.status(HttpStatus.CREATED).body(u.toString());
            } else {
               return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN");
            }

        } else {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }
    }

    @GetMapping(value="/user")
    public ResponseEntity<?> getFromToken(HttpServletRequest request){
        if (request!=null&&request.getHeader(HttpHeaders.AUTHORIZATION)!=null){
            return ResponseEntity.ok().body(jwtUtils.getUserNameFromJwtToken(request.getHeader(HttpHeaders.AUTHORIZATION)));

        }
        else {return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"Message\": \"Login or sign up to post!\"}");}

    }
    @PutMapping(value = "/UPDATE_USER/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> updateUser(@RequestBody MultipartFile image, @PathVariable long id, @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String firstname,
                                           @RequestParam(required = false) String lastname,
                                           @RequestParam(required = false) String email,
                                           @RequestParam(required = false) String phoneNumber,
                                           @RequestParam(required = false) String dob,
                                           @RequestParam(required = false) String password,
                                           @RequestParam RoleType roleType) throws Exception {


        String user = "{\"username\": \"" + username + "\",   \"email\": \"" + email + "\",   \"firstname\": \"" + firstname + "\",   \"lastname\": \"" + lastname + "\",   \"phoneNumber\": \"" + phoneNumber + "\",   \"dob\": \"" + dob + "\",   \"password\": \"" + password + "\" }";

        User oguser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + id));

        System.out.println(oguser);

        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDTO = objectMapper.readValue(user, UserDTO.class);

        if (firstname != null) {
            oguser.setFirstname(userDTO.getFirstname());
        }

        if (lastname != null) {
            oguser.setLastname(userDTO.getLastname());
        }

        if (dob != null) {
            oguser.setDob(userDTO.getDob());
        }

        if (password != null) {
            oguser.setPassword(encoder.encode(userDTO.getPassword()));
        }

        if (username != null) {
            oguser.setUsername(userDTO.getUsername());
        }

        if (phoneNumber != null) {
            oguser.setPhoneNumber(userDTO.getPhoneNumber());
        }

        if (email != null) {
            oguser.setEmail(userDTO.getEmail());
        }

        System.out.println("aaaaaaaaaaggghhhh!!!!");


        iServiceUser.addUserAndAssignRole(oguser, roleType);

        return ResponseEntity.ok(oguser);
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + userId));

        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }


    @PostMapping(value = "/users/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadUsersFile(@RequestParam("file") MultipartFile file) throws IOException {
        List<User> users = iServiceUser.readUsersFromExcelFile(file.getInputStream());
        iServiceUser.saveAll(users);
        return ResponseEntity.ok("Users uploaded successfully.");
    }






    @GetMapping("/FILTER_USERS")
    public List<User> getUsersByRoleAndYear(@RequestParam(value = "role", required = false) RoleType roleType,
                                            @RequestParam(value = "year", required = false) Integer year) {
        List<User> users = new ArrayList<>();
        if (roleType != null && year != null) {
            LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);
            users = userRepository.findByRolesRolenameAndCreationDateBetween(roleType, startOfYear, endOfYear);
        } else if (roleType != null) {
            users = userRepository.findByRolesRolename(roleType);
        } else if (year != null) {
            LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);
            users = userRepository.findByCreationDateBetween(startOfYear, endOfYear);
        } else {
            users = userRepository.findAll();
        }
        return users;
    }






    /////////////////////////////////////////////////////////////////////////////////////////////
    private Map<String, String> getRequestHeaders() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));

        }
        System.out.println(request.getHeader(HttpHeaders.AUTHORIZATION));
        return headers;
    }


    @PostMapping(value = "AddClaim/" )
    public ResponseEntity<?> AddClaim( @RequestBody Reclamation reclamation,@RequestHeader("AUTHORIZATION") String header){
        String token = header.substring(7);
        User user =  iServiceUser.getUserDetailsFromToken(token);
        iClaimService.AddClaim(reclamation,user);
        return new ResponseEntity<>( HttpStatus.OK);

    }
     @PutMapping("treatClaim/{id}")
    public Reclamation treatClaim(@PathVariable Integer id, @RequestParam String decision) {
        return iClaimService.treatClaim(id, decision);
    }

    @GetMapping(value = "/getClaimsByUser" , produces = "application/json" )
    @ResponseBody
    public List<Reclamation> getClaimsByUser( @RequestHeader("AUTHORIZATION") String header)  {
        String token = header.substring(7);
        User user =  iServiceUser.getUserDetailsFromToken(token);
        return  iClaimService.getClaimsByUser(user.getUserid());
    }
}
