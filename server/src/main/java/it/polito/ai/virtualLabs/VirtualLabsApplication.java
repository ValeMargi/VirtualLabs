package it.polito.ai.virtualLabs;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.repositories.*;
import it.polito.ai.virtualLabs.services.VLService;
import it.polito.ai.virtualLabs.services.VLServiceProfessor;
import it.polito.ai.virtualLabs.services.VLServiceStudent;
import lombok.var;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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



    @Bean
    CommandLineRunner runner(UserRepository userRepository, StudentRepository studentRepository, AvatarStudentRepository avatarStudentRepository,
                             ProfessorRepository professorRepository, AvatarProfessorRepository avatarProfessorRepository, CourseRepository courseRepository,
                             VLService vlService, VLServiceProfessor vlServiceProfessor, VLServiceStudent vlServiceStudent, PasswordEncoder bcryptEncoder, TeamRepository teamRepository){
        return  new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                if( studentRepository.findAll().isEmpty()) {
                    System.out.println("\nInserimento nuovo studente -----> s267543, Valentina, Margiotta\n");
                    StudentDTO studentDTO1 = new StudentDTO("s267543", "Valentina", "Margiotta", "s267543@studenti.polito.it");
                    var imgFile = new ClassPathResource("/avatar_woman.png");
                    byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    Student s1 = modelMapper().map(studentDTO1, Student.class);
                    studentRepository.saveAndFlush(s1);
                    AvatarStudentDTO avatarStudentDTO1 = new AvatarStudentDTO();
                    avatarStudentDTO1.setNameFile(imgFile.getFilename());
                    avatarStudentDTO1.setType("image/png");
                    avatarStudentDTO1.setPicByte(vlService.compressZLib(bytes));
                    AvatarStudent avatarStudent1 = modelMapper().map(avatarStudentDTO1, AvatarStudent.class);
                    s1.setPhotoStudent(avatarStudent1);
                    avatarStudentRepository.save(avatarStudent1);
                    studentRepository.saveAndFlush(s1);
                    UserDAO userDAO1 = new UserDAO();                                                                         //hashing MD5 password
                    userDAO1.setPassword(bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"));
                    userDAO1.setRole("student");
                    userDAO1.setId(s1.getId());
                    userDAO1.setActivate(true);
                    userDAO1.setStudent(s1);
                    userRepository.save(userDAO1);

                    System.out.println("\nInserimento nuovo studente -----> s267560, Salvatore, Russo\n");
                    StudentDTO studentDTO2 = new StudentDTO("s267560", "Salvatore", "Russo", "s267560@studenti.polito.it");
                    imgFile = new ClassPathResource("/avatar_male.png");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    Student s2 = modelMapper().map(studentDTO2, Student.class);
                    studentRepository.saveAndFlush(s2);
                    AvatarStudentDTO avatarStudentDTO2 = new AvatarStudentDTO();
                    avatarStudentDTO2.setNameFile(imgFile.getFilename());
                    avatarStudentDTO2.setType("image/png");
                    avatarStudentDTO2.setPicByte(vlService.compressZLib(bytes));
                    AvatarStudent avatarStudent2 = modelMapper().map(avatarStudentDTO2, AvatarStudent.class);
                    s2.setPhotoStudent(avatarStudent2);
                    avatarStudentRepository.save(avatarStudent2);
                    studentRepository.saveAndFlush(s2);
                    UserDAO userDAO2 = new UserDAO();
                    //hashing MD5 password
                    userDAO2.setPassword(bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"));
                    userDAO2.setRole("student");
                    userDAO2.setId(s2.getId());
                    userDAO2.setActivate(true);
                    userDAO2.setStudent(s2);
                    userRepository.save(userDAO2);

                    System.out.println("\nInserimento nuovo studente -----> s266556, Alexandro, Vassallo\n");
                    StudentDTO studentDTO3 = new StudentDTO("s266556", "Alexandro", "Vassallo", "s266556@studenti.polito.it");
                    imgFile = new ClassPathResource("/avatar_male2.png");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    Student s3 = modelMapper().map(studentDTO3, Student.class);
                    studentRepository.saveAndFlush(s3);
                    AvatarStudentDTO avatarStudentDTO3 = new AvatarStudentDTO();
                    avatarStudentDTO3.setNameFile(imgFile.getFilename());
                    avatarStudentDTO3.setType("image/png");
                    avatarStudentDTO3.setPicByte(vlService.compressZLib(bytes));
                    AvatarStudent avatarStudent3 = modelMapper().map(avatarStudentDTO3, AvatarStudent.class);
                    s3.setPhotoStudent(avatarStudent3);
                    avatarStudentRepository.save(avatarStudent3);
                    studentRepository.saveAndFlush(s3);
                    UserDAO userDAO3 = new UserDAO();
                    //hashing MD5 password
                    userDAO3.setPassword(bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"));
                    userDAO3.setRole("student");
                    userDAO3.setId(s3.getId());
                    userDAO3.setActivate(true);
                    userDAO3.setStudent(s3);
                    userRepository.save(userDAO3);

                    System.out.println("\nInserimento nuovo studente -----> s267782, Giacomo, Gorga\n");
                    StudentDTO studentDTO4 = new StudentDTO("s267782", "Giacomo", "Gorga", "s267782@studenti.polito.it");
                    imgFile = new ClassPathResource("/avatar_male3.png");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    Student s4 = modelMapper().map(studentDTO4, Student.class);
                    studentRepository.saveAndFlush(s4);
                    AvatarStudentDTO avatarStudentDTO4 = new AvatarStudentDTO();
                    avatarStudentDTO4.setNameFile(imgFile.getFilename());
                    avatarStudentDTO4.setType("image/png");
                    avatarStudentDTO4.setPicByte(vlService.compressZLib(bytes));
                    AvatarStudent avatarStudent4 = modelMapper().map(avatarStudentDTO4, AvatarStudent.class);
                    s4.setPhotoStudent(avatarStudent4);
                    avatarStudentRepository.save(avatarStudent4);
                    studentRepository.saveAndFlush(s4);
                    UserDAO userDAO4 = new UserDAO();
                    //hashing MD5 password
                    userDAO4.setPassword(bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"));
                    userDAO4.setRole("student");
                    userDAO4.setId(s4.getId());
                    userDAO4.setActivate(true);
                    userDAO4.setStudent(s4);
                    userRepository.save(userDAO4);

                    System.out.println("\nInserimento nuovo studente -----> s1, Mario, Rossi\n");
                    StudentDTO studentDTO5 = new StudentDTO("s1", "Mario", "Rossi", "s1@studenti.polito.it");
                    imgFile = new ClassPathResource("/avatar_male4.png");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    Student s5 = modelMapper().map(studentDTO5, Student.class);
                    studentRepository.saveAndFlush(s5);
                    AvatarStudentDTO avatarStudentDTO5 = new AvatarStudentDTO();
                    avatarStudentDTO5.setNameFile(imgFile.getFilename());
                    avatarStudentDTO5.setType("image/png");
                    avatarStudentDTO5.setPicByte(vlService.compressZLib(bytes));
                    AvatarStudent avatarStudent5 = modelMapper().map(avatarStudentDTO5, AvatarStudent.class);
                    s5.setPhotoStudent(avatarStudent5);
                    avatarStudentRepository.save(avatarStudent5);
                    studentRepository.saveAndFlush(s5);
                    UserDAO userDAO5 = new UserDAO();
                    //hashing MD5 password
                    userDAO5.setPassword(bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"));
                    userDAO5.setRole("student");
                    userDAO5.setId(s5.getId());
                    userDAO5.setActivate(true);
                    userDAO5.setStudent(s5);
                    userRepository.save(userDAO5);

                    System.out.println("\nInserimento nuovo studente -----> s2, Federico, Reno\n");
                    StudentDTO studentDTO6 = new StudentDTO("s2", "Federico", "Reno", "s2@studenti.polito.it");
                    imgFile = new ClassPathResource("/avatar_male4.png");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    Student s6 = modelMapper().map(studentDTO6, Student.class);
                    studentRepository.saveAndFlush(s6);
                    AvatarStudentDTO avatarStudentDTO6 = new AvatarStudentDTO();
                    avatarStudentDTO6.setNameFile(imgFile.getFilename());
                    avatarStudentDTO6.setType("image/png");
                    avatarStudentDTO6.setPicByte(vlService.compressZLib(bytes));
                    AvatarStudent avatarStudent6 = modelMapper().map(avatarStudentDTO6, AvatarStudent.class);
                    s6.setPhotoStudent(avatarStudent6);
                    avatarStudentRepository.save(avatarStudent6);
                    studentRepository.saveAndFlush(s6);
                    UserDAO userDAO6 = new UserDAO();
                    //hashing MD5 password
                    userDAO6.setPassword(bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"));
                    userDAO6.setRole("student");
                    userDAO6.setId(s6.getId());
                    userDAO6.setActivate(true);
                    userDAO6.setStudent(s6);
                    userRepository.save(userDAO6);

                    System.out.println("\nInserimento nuovo docente -----> d1, Antonio, Servetti\n");
                    ProfessorDTO professorDTO1 = new ProfessorDTO("d1", "Antonio", "Servetti", "d1@polito.it");
                    imgFile = new ClassPathResource("/avatar.jpg");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    Professor p1 = modelMapper().map(professorDTO1, Professor.class);
                    professorRepository.saveAndFlush(p1);
                    AvatarProfessorDTO avatarProfessorDTO1 = new AvatarProfessorDTO();
                    avatarProfessorDTO1.setNameFile(imgFile.getFilename());
                    avatarProfessorDTO1.setType("image/jpg");
                    avatarProfessorDTO1.setPicByte(vlService.compressZLib(bytes));
                    AvatarProfessor avatarProfessor1 = modelMapper().map(avatarProfessorDTO1, AvatarProfessor.class);
                    p1.setPhotoProfessor(avatarProfessor1);
                    avatarProfessorRepository.save(avatarProfessor1);
                    professorRepository.saveAndFlush(p1);
                    UserDAO userDAO7 = new UserDAO();
                    //hashing MD5 password
                    userDAO7.setPassword(bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"));
                    userDAO7.setRole("professor");
                    userDAO7.setId(p1.getId());
                    userDAO7.setActivate(true);
                    userDAO7.setProfessor(p1);
                    userRepository.save(userDAO7);

                    System.out.println("\nInserimento nuovo docente -----> d2, Giovanni, Malnati\n");
                    ProfessorDTO professorDTO2 = new ProfessorDTO("d2", "Giovanni", "Malnati", "d2@polito.it");
                    imgFile = new ClassPathResource("/avatar_male4.png");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    Professor p2 = modelMapper().map(professorDTO2, Professor.class);
                    professorRepository.saveAndFlush(p2);
                    AvatarProfessorDTO avatarProfessorDTO2 = new AvatarProfessorDTO();
                    avatarProfessorDTO2.setNameFile(imgFile.getFilename());
                    avatarProfessorDTO2.setType("image/png");
                    avatarProfessorDTO2.setPicByte(vlService.compressZLib(bytes));
                    AvatarProfessor avatarProfessor2 = modelMapper().map(avatarProfessorDTO2, AvatarProfessor.class);
                    p2.setPhotoProfessor(avatarProfessor2);
                    avatarProfessorRepository.save(avatarProfessor2);
                    professorRepository.saveAndFlush(p2);
                    UserDAO userDAO8 = new UserDAO();
                    //hashing MD5 password
                    userDAO8.setPassword(bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"));
                    userDAO8.setRole("professor");
                    userDAO8.setId(p2.getId());
                    userDAO8.setActivate(true);
                    userDAO8.setProfessor(p2);
                    userRepository.save(userDAO8);


                    System.out.println("\nInserimento nuovo corso -----> Applicazioni Internet, AI, 1, 5, true, 6, 50, 8, 9, 10 professorId=d2 \n");
                    imgFile = new ClassPathResource("/macos_model.jpg");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    Image image = new Image(imgFile.getFilename(), "image/jpg", vlService.compressZLib(bytes));
                    PhotoModelVM photoModelVM = new PhotoModelVM(image);
                    CourseDTO courseDTO = new CourseDTO();
                    courseDTO.setName("applicazioni-internet");
                    courseDTO.setAcronym("AI");
                    courseDTO.setMin(1);
                    courseDTO.setMax(5);
                    courseDTO.setEnabled(true);
                    courseDTO.setMaxVcpu(6);
                    courseDTO.setDiskSpace(50);
                    courseDTO.setRam(8);
                    courseDTO.setRunningInstances(9);
                    courseDTO.setTotInstances(10);

                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("professor"));
                    Authentication authentication = new UsernamePasswordAuthenticationToken("d1", bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"), authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    List<String> professorsId = new ArrayList<>();
                    professorsId.add("d2");
                    vlServiceProfessor.addCourse(courseDTO, professorsId, photoModelVM);

                    System.out.println("\nAggiunta studenti al corso -----> tutti gli studenti presenti nel sistema\n");
                    vlServiceProfessor.enrollAll(Arrays.asList(new String[]{"s267543", "s267560", "s266556", "s267782", "s1", "s2"}.clone()), courseDTO.getName());

                    System.out.println("\nInserimento nuova consegna -----> Consegna1 \n");
                    AssignmentDTO assignmentDTO1 = new AssignmentDTO();
                    assignmentDTO1.setAssignmentName("Consegna1");
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    assignmentDTO1.setReleaseDate(timestamp.toString());
                    assignmentDTO1.setExpiration(Timestamp.from(Instant.now().plus(60, ChronoUnit.DAYS)).toString());
                    imgFile = new ClassPathResource("/consegna.jpg");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    PhotoAssignmentDTO photoAssignmentDTO1 = new PhotoAssignmentDTO();
                    photoAssignmentDTO1.setNameFile(imgFile.getFilename());
                    photoAssignmentDTO1.setType("image/jpg");
                    photoAssignmentDTO1.setPicByte(vlService.compressZLib(bytes));
                    photoAssignmentDTO1.setTimestamp( timestamp.toString());
                    vlServiceProfessor.addAssignment(assignmentDTO1, photoAssignmentDTO1, courseDTO.getName());

                    System.out.println("\nInserimento nuova consegna -----> Consegna2 \n");
                    AssignmentDTO assignmentDTO2 = new AssignmentDTO();
                    assignmentDTO2.setAssignmentName("Consegna2");
                    assignmentDTO2.setReleaseDate(timestamp.toString());
                    assignmentDTO2.setExpiration(Timestamp.from(Instant.now().plus(60, ChronoUnit.DAYS)).toString());
                    imgFile = new ClassPathResource("/consegna2.jpg");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    PhotoAssignmentDTO photoAssignmentDTO2 = new PhotoAssignmentDTO();
                    photoAssignmentDTO2.setNameFile(imgFile.getFilename());
                    photoAssignmentDTO2.setType("image/jpg");
                    photoAssignmentDTO2.setPicByte(vlService.compressZLib(bytes));
                    photoAssignmentDTO2.setTimestamp( timestamp.toString());
                    vlServiceProfessor.addAssignment(assignmentDTO2, photoAssignmentDTO2, courseDTO.getName());
                    SecurityContextHolder.clearContext();


                    Student studentPropose = studentRepository.findById("s267543").get();
                    Course coursePropose = courseRepository.findById("applicazioni-internet").get();

                    Team team = new Team();
                    team.setName("Team1_AI");
                    team.setCourse(coursePropose);
                    team.setStatus("active");
                    team.setCreatorId("s267543");

                    team.setDiskSpaceLeft(50);
                    team.setMaxVcpuLeft(6);
                    team.setRamLeft(8);
                    team.setRunningInstancesLeft(9);
                    team.setTotInstancesLeft(10);
                    teamRepository.saveAndFlush(team);

                    Team t = teamRepository.findById(Long.valueOf(26)).get();
                    studentPropose.setTeamForStudent(t);
                    List<String> membersId = new ArrayList<>();
                    membersId.add("s267560");
                    membersId.add("s266556");
                    membersId.add("s267782");
                    for (String s : membersId) {
                        studentRepository.findById(s).get().setTeamForStudent(t);
                    }
                    teamRepository.saveAndFlush(t);


                    System.out.println("\nInserimento VM -----> VM1 e VM2 e owners \n");
                    authorities.add(new SimpleGrantedAuthority("student"));
                    authentication = new UsernamePasswordAuthenticationToken("s267543", bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"), authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    VMDTO vmdto1 = new VMDTO();
                    vmdto1.setDiskSpace(2);
                    vmdto1.setNumVcpu(1);
                    vmdto1.setNameVM("VM1");
                    vmdto1.setRam(1);
                    vmdto1.setStatus("off");
                    vlServiceStudent.addVM(vmdto1, courseDTO.getName(), timestamp.toString());
                    VMDTO vmdto2 = new VMDTO();
                    vmdto2.setDiskSpace(2);
                    vmdto2.setNumVcpu(1);
                    vmdto2.setNameVM("VM2");
                    vmdto2.setRam(1);
                    vmdto2.setStatus("off");
                    vlServiceStudent.addVM(vmdto2, courseDTO.getName(), timestamp.toString());

                    List<String> owners1 = Arrays.asList("s267560");
                    vlServiceStudent.addOwner(Long.valueOf(27), courseDTO.getName(), owners1);
                    List<String> owners2 = Arrays.asList("s266556");
                    vlServiceStudent.addOwner(Long.valueOf(29), courseDTO.getName(), owners2);

                    System.out.println("\nInserimento versione Homework -----> Version1 \n");
                    imgFile = new ClassPathResource("/course.png");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    PhotoVersionHomeworkDTO photoVersionHomeworkDTO = new PhotoVersionHomeworkDTO();
                    photoVersionHomeworkDTO.setNameFile(imgFile.getFilename());
                    photoVersionHomeworkDTO.setType("image/png");
                    photoVersionHomeworkDTO.setPicByte(vlService.compressZLib(bytes));
                    photoVersionHomeworkDTO.setTimestamp(timestamp.toString());
                    vlServiceStudent.uploadVersionHomework(Long.valueOf(10), photoVersionHomeworkDTO);
                    SecurityContextHolder.clearContext();

                    authorities.add(new SimpleGrantedAuthority("professor"));
                    authentication = new UsernamePasswordAuthenticationToken("d1", bcryptEncoder.encode("2e9ec317e197819358fbc43afca7d837"), authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    PhotoCorrectionDTO photoCorrectionDTO = new PhotoCorrectionDTO();
                    Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
                    imgFile = new ClassPathResource("/correction.jpg");
                    bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                    photoCorrectionDTO.setTimestamp(timestamp.toString());
                    photoCorrectionDTO.setNameFile(imgFile.getFilename());
                    photoCorrectionDTO.setType("image/jpg");
                    photoCorrectionDTO.setPicByte(vlService.compressZLib(bytes));
                    vlServiceProfessor.uploadCorrection(Long.valueOf(10), Long.valueOf(31), photoCorrectionDTO, false, null);
                    SecurityContextHolder.clearContext();
                }
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(VirtualLabsApplication.class, args);
    }
}
