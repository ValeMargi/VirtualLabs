package it.polito.ai.virtualalbs.dtos;
import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String password;
    private  String role;
}
