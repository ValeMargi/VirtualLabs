package it.polito.ai.virtualLabs;

import it.polito.ai.virtualLabs.dtos.PhotoCorrectionDTO;
import it.polito.ai.virtualLabs.dtos.PhotoVersionHomeworkDTO;
import it.polito.ai.virtualLabs.entities.PhotoAssignment;
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

@SpringBootApplication
public class VirtualLabsApplication {

    @Bean
    ModelMapper modelMapper(){ return new ModelMapper();   }

    @Bean
    CommandLineRunner runner(PhotoAssignmentRepository pa, PhotoVersionHMRepository pvh,
                             PhotoCorrectionRepository photoc, HomeworkRepository h, VLService service) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                PhotoCorrectionDTO pc = new PhotoCorrectionDTO();
                pc.setIdProfessor("p1");
                photoc.saveAndFlush(modelMapper().map(pc, PhotoCorrection.class));
                PhotoVersionHomeworkDTO ph = new PhotoVersionHomeworkDTO();
                pvh.saveAndFlush(modelMapper().map(ph, PhotoVersionHomework.class));
            }
        };

    }
    public static void main(String[] args) {
        SpringApplication.run(VirtualLabsApplication.class, args);
    }

}
