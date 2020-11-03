package it.polito.ai.virtualLabs;

import it.polito.ai.virtualLabs.dtos.PhotoCorrectionDTO;
import it.polito.ai.virtualLabs.dtos.PhotoVersionHomeworkDTO;
import it.polito.ai.virtualLabs.entities.PhotoCorrection;
import it.polito.ai.virtualLabs.entities.PhotoVersionHomework;
import it.polito.ai.virtualLabs.repositories.HomeworkRepository;
import it.polito.ai.virtualLabs.repositories.PhotoAssignmentRepository;
import it.polito.ai.virtualLabs.repositories.PhotoCorrectionRepository;
import it.polito.ai.virtualLabs.repositories.PhotoVersionHMRepository;
import it.polito.ai.virtualLabs.services.VLService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class VirtualLabsApplication {

    @Bean
    ModelMapper modelMapper(){ return new ModelMapper();   }

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200");
            }
        };
    }
    public static void main(String[] args) {
        SpringApplication.run(VirtualLabsApplication.class, args);
    }

}
