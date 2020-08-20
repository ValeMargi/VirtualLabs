package it.polito.ai.virtualLabs.security;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import it.polito.ai.virtualLabs.entities.UserDAO;
import it.polito.ai.virtualLabs.exceptions.ImageSizeException;
import it.polito.ai.virtualLabs.services.VLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.server.ResponseStatusException;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    @Value("${jwt.secret}")
    private String secret;
    static final String CLAIM_KEY_ROLE="role";
    static final String CLAIM_KEY_FIRSTNAME = "firstname";
    static final String CLAIM_KEY_NAME = "name";
    static final String CLAIM_KEY_ID = "id";
  //  static final String CLAIM_KEY_PHOTO = "photo";

    static final String CLAIM_KEY_PHOTO_NAME = "photoName";
    static final String CLAIM_KEY_PHOTO_TYPE = "photoType";
    static final String CLAIM_KEY_PHOTO_BYTE = "photoPicByte";

    @Autowired
    VLService vlService;


    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    //retrieve expiration date from jwt token

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //check if the token has expired getAuthentication
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserDetails userDetails, UserDAO userDAO) {
        Map<String, Object> claims = new HashMap<>();
        try{
            if(userDAO.getRole().equals("professor")){
                claims.put(CLAIM_KEY_ROLE, userDAO.getRole());
                claims.put(CLAIM_KEY_FIRSTNAME, userDAO.getProfessor().getFirstName());
                claims.put(CLAIM_KEY_NAME, userDAO.getProfessor().getName());
                claims.put(CLAIM_KEY_ID, userDAO.getProfessor().getId());
               // claims.put(CLAIM_KEY_PHOTO, userDAO.getProfessor().getPhotoProfessor());
                claims.put(CLAIM_KEY_PHOTO_NAME, userDAO.getProfessor().getPhotoProfessor().getNameFile());
                claims.put(CLAIM_KEY_PHOTO_TYPE, userDAO.getProfessor().getPhotoProfessor().getType());
                claims.put(CLAIM_KEY_PHOTO_BYTE, vlService.decompressZLib(userDAO.getProfessor().getPhotoProfessor().getPicByte()));
            }else if(userDAO.getRole().equals("student")){
                claims.put(CLAIM_KEY_ROLE, userDAO.getRole());
                claims.put(CLAIM_KEY_FIRSTNAME, userDAO.getStudent().getFirstName());
                claims.put(CLAIM_KEY_NAME, userDAO.getStudent().getName());
                claims.put(CLAIM_KEY_ID, userDAO.getStudent().getId());
               // claims.put(CLAIM_KEY_PHOTO, userDAO.getStudent().getPhotoStudent());
                claims.put(CLAIM_KEY_PHOTO_NAME, userDAO.getStudent().getPhotoStudent().getNameFile());
                claims.put(CLAIM_KEY_PHOTO_TYPE, userDAO.getStudent().getPhotoStudent().getType());
                claims.put(CLAIM_KEY_PHOTO_BYTE, vlService.decompressZLib(userDAO.getStudent().getPhotoStudent().getPicByte()));
            }
        }catch(ImageSizeException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return doGenerateToken(claims, userDetails.getUsername());
    }

    //while creating the token -
//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
//2. Sign the JWT using the HS512 algorithm and secret key.
//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
//   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }
    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
