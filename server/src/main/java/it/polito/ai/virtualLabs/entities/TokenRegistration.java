package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Data
public class TokenRegistration {
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        private String id;
        private String userId;
        private Timestamp expiryDate;

}
