package it.polito.ai.virtualLabs.dtos;
import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String password;
    private  String role;
    private Boolean activate;
}
