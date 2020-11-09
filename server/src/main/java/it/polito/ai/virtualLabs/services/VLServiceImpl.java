package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.repositories.*;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.StringTokenizer;

@Configuration
@EnableScheduling
@Service
@Transactional
@Log(topic="service")
public class VLServiceImpl implements VLService{
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    ProfessorRepository professorRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    VMRepository VMRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    HomeworkRepository homeworkRepository;
    @Autowired
    PhotoAssignmentRepository photoAssignmentRepository;
    @Autowired
    PhotoModelVMRepository photoModelVMRepository;
    @Autowired
    PhotoVMRepository photoVMRepository;
    @Autowired
    PhotoVersionHMRepository photoVersionHMRepository;
    @Autowired
    PhotoCorrectionRepository photoCorrectionRepository;
    @Autowired
    TokenRegistrationRepository tokenRegistrationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    AvatarProfessorRepository avatarProfessorRepository;
    @Autowired
    AvatarStudentRepository avatarStudentRepository;
    @Autowired
    VLServiceProfessor vlServiceProfessor;

    /*To check if the tokens contained in the repository have expired,
     if so, they are removed from the repository and the corresponding
     Team in the repository Team is removed.*/
    @Transactional
    @Scheduled(initialDelay = 1000, fixedRate = 20000)
    public void run(){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        passwordResetTokenRepository.deleteAllExpiredSince(now);

        for(Token token: tokenRepository.findAll() )
        {
            if( token.getExpiryDate().compareTo(now)<0){
               Optional<Team> oteam = teamRepository.findById(token.getTeamId());
               if(!oteam.isPresent()) throw new TeamNotFoundException();
               oteam.get().setStatus("disabled");
               oteam.get().setDisabledTimestamp(now.toString());

              //  tokenRepository.deleteFromTokenByTeamId(token.getTeamId());
                //if( teamRepository.findById(token.getTeamId()).isPresent())
                  //  evictTeam(token.getTeamId());
            }
        }
        for(Team team: teamRepository.findAllByStatusEquals("disabled")){
            if( team.getDisabledTimestamp().compareTo(Timestamp.from(Instant.now().plus(5, ChronoUnit.MILLIS)).toString())<0){
                tokenRepository.deleteFromTokenByTeamId(team.getId());
                evictTeam(team.getId());
            }
        }

        /*Per controllare scadenza elaborati*/
        for(Assignment a: assignmentRepository.findAll()){
            if(a.getExpiration().compareTo(now.toString())<=0){
                assignmentExpiredSetPermanentHW(a);
            }
        }

        for(TokenRegistration tokenR: tokenRegistrationRepository.findAll() )
        {
            if( tokenR.getExpiryDate().compareTo(now)<0){
                if(!userRepository.findById(tokenR.getUserId()).get().getActivate()){
                    userRepository.deleteById(tokenR.getUserId());
                    if( studentRepository.existsById(tokenR.getUserId()))
                        studentRepository.deleteById(tokenR.getUserId());
                    else
                        professorRepository.deleteById(tokenR.getUserId());
                }
                tokenRegistrationRepository.delete(tokenR);
            }
        }

    }

    @Override
    public void assignmentExpiredSetPermanentHW(Assignment a){
        List<Homework> homeworks = a.getHomeworks();
        for(Homework h: homeworks){
            h.setPermanent(true);
            if(!photoVersionHMRepository.findAllByHomework(h).isEmpty()) {
                if (h.getVersions().isEmpty()) //TESTARE
                    h.setGrade("0");
            }
            homeworkRepository.save(h);
        }
        assignmentRepository.saveAndFlush(a);


    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map( s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfessorDTO> getAllProfessors() {
        return professorRepository.findAll()
                .stream()
                .map( p -> modelMapper.map(p, ProfessorDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CourseDTO> getCourse(String name) {
        return courseRepository.findById(name)
                .map(c -> modelMapper.map(c, CourseDTO.class));
    }


    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map( p -> modelMapper.map(p, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public boolean changeAvatar(AvatarProfessorDTO avatarProfessorDTO, AvatarStudentDTO avatarStudentDTO){
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        if(avatarProfessorDTO!=null){
            Optional<Professor> op= professorRepository.findById(auth);
            if(op.isPresent()){
                Professor p = op.get();
                if(p.getPhotoProfessor()!=null){
                    AvatarProfessor avatar = avatarProfessorRepository.findById(p.getPhotoProfessor().getId()).get();
                    avatar.setPicByte(avatarProfessorDTO.getPicByte());
                    avatar.setNameFile(avatarProfessorDTO.getNameFile());
                    avatar.setType(avatarProfessorDTO.getType());
                    return true;
                }else throw new AvatarNotPresentException();
            }else throw new ProfessorNotFoundException();
        }else if(avatarStudentDTO!=null){
            Optional<Student> os= studentRepository.findById(auth);
            if(os.isPresent()){
                Student s = os.get();
                if(s.getPhotoStudent()!=null){
                    AvatarStudent avatar = avatarStudentRepository.findById(s.getPhotoStudent().getId()).get();
                    avatar.setPicByte(avatarStudentDTO.getPicByte());
                    avatar.setNameFile(avatarStudentDTO.getNameFile());
                    avatar.setType(avatarStudentDTO.getType());
                    return true;
                }else throw new AvatarNotPresentException();
            }else throw new StudentNotFoundException();
        }else return false;
    }

    /*SERVICE student*/
    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public Map<String, Object> getStudent(String studentId) {
        Optional<Student> os= studentRepository.findById(studentId);
        if(!os.isPresent())
            throw new StudentNotFoundException();
        Student s = os.get();
        AvatarStudentDTO avatarStudentDTO = modelMapper.map(s.getPhotoStudent(), AvatarStudentDTO.class);
        avatarStudentDTO.setPicByte(decompressZLib(avatarStudentDTO.getPicByte()));
        Map<String, Object> profile = new HashMap<>();
        profile.put("student", modelMapper.map(os.get(), StudentDTO.class));
        profile.put("avatar", avatarStudentDTO);
        return profile;
    }

    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public Map<String, Object> getProfileStudent(){
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os= studentRepository.findById(auth);
        if(!os.isPresent())
            throw new StudentNotFoundException();
        Student s = os.get();
        AvatarStudentDTO avatarStudentDTO = modelMapper.map(s.getPhotoStudent(), AvatarStudentDTO.class);
        avatarStudentDTO.setPicByte(decompressZLib(avatarStudentDTO.getPicByte()));
        Map<String, Object> profile = new HashMap<>();
        profile.put("student", modelMapper.map(s, StudentDTO.class));
        profile.put("avatar", avatarStudentDTO);
        return profile;
    }



    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public  Map<String, Object> getProfessor(String professorId) {
        Optional<Professor> op= professorRepository.findById(professorId);
        if(!op.isPresent())
            throw new ProfessorNotFoundException();
        Professor p = op.get();
        AvatarProfessorDTO avatarProfessorDTO = modelMapper.map(p.getPhotoProfessor(), AvatarProfessorDTO.class);
        avatarProfessorDTO.setPicByte(decompressZLib(avatarProfessorDTO.getPicByte()));
        Map<String, Object> profile = new HashMap<>();
        profile.put("professor", modelMapper.map(p, ProfessorDTO.class));
        profile.put("avatar", avatarProfessorDTO);
        return profile;
    }

    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public Map<String, Object> getProfileProfessor(){
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Professor> op= professorRepository.findById(auth);
        if(!op.isPresent())
            throw new ProfessorNotFoundException();
        Professor p = op.get();
        AvatarProfessorDTO avatarProfessorDTO = modelMapper.map(p.getPhotoProfessor(), AvatarProfessorDTO.class);
        avatarProfessorDTO.setPicByte(decompressZLib(avatarProfessorDTO.getPicByte()));
        Map<String, Object> profile = new HashMap<>();
        profile.put("student", modelMapper.map(p, ProfessorDTO.class));
        profile.put("avatar", avatarProfessorDTO);
        return profile;
    }

    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        try {
            Course c = courseRepository.getOne(courseName);
            return c.getStudents()
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        }catch(EntityNotFoundException e){
            throw  new CourseNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('professor') || hasAnyAuthority('student')")
    @Override
    public List<ProfessorDTO> getProfessorsForCourse(String courseName) {
        try {
            Course c = courseRepository.getOne(courseName);
            return c.getProfessors()
                    .stream()
                    .map(p -> modelMapper.map(p, ProfessorDTO.class))
                    .collect(Collectors.toList());
        }catch(EntityNotFoundException e){
            throw  new CourseNotFoundException();
        }
    }

    @Override
    public List<StudentDTO> getMembers(Long TeamId) {
        try {
            Team t = teamRepository.getOne(TeamId);
            return t.getMembers().stream().map(s -> modelMapper.map(s, StudentDTO.class)).collect(Collectors.toList());
        }catch(EntityNotFoundException enfe){
            throw new TeamNotFoundException();
        }

    }

    @PreAuthorize("hasAuthority('student') || hasAuthority('professor')")
    @Override
    public List<TeamDTO> getTeamForCourse(String courseName){
        try{
            Course course =courseRepository.getOne(courseName);
            return  course.getTeams().stream().filter(t-> t.getStatus().equals("active")).map(t-> modelMapper.map(t, TeamDTO.class)).collect(Collectors.toList());
        }catch(EntityNotFoundException enfe){
            throw new CourseNotFoundException();
        }

    }

    @Override
    public  void activateTeam(Long id){
        try{
            if(teamRepository.existsById(id)){
                Team t = teamRepository.findById(id).get();
                if(!t.getCourse().isEnabled()) throw new CourseDisabledException();
                t.setStatus("active");
                for(Student s: t.getMembers()){
                    tokenRepository.findAllByStudent(s).stream().map(Token::getTeamId).forEach(tId -> {
                        tokenRepository.findAllByTeamId(tId).forEach(tk-> tokenRepository.delete(tk));
                        evictTeam(tId);
                    });
                }
                t.getMembers().forEach(s-> sendMessage(s.getEmail(),"Notification: Team "+t.getName()+ " created","Team creation success!"));
            }
        }catch(EntityNotFoundException e){
            throw  new TeamNotFoundException();
        }
    }

    @Override
    public void evictTeam(Long id){
        try{
            Team t = teamRepository.getOne(id);
            if(!t.getCourse().isEnabled()) throw new CourseDisabledException();
            t.getMembers().forEach(s-> sendMessage(s.getEmail(),"Notification: Team "+t.getName()+ " not created","A student has rejected the proposal. Team creation stopped!"));
            teamRepository.delete(t);
        }catch(EntityNotFoundException e){
            throw  new TeamNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public  List<VMDTO> getAllVMTeam(  Long teamId){
        Optional<Team> ot = teamRepository.findById(teamId);
        if( !ot.isPresent())
            throw new TeamNotFoundException();
        Team t = ot.get();
        if( !t.getStatus().equals("active"))
            throw new TeamNotEnabledException();
        return t.getVms().stream().map(te-> modelMapper.map(te, VMDTO.class)).collect(Collectors.toList());


    }

    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public  PhotoVersionHomeworkDTO getVersionHW(Long versionId){

        String auth =  SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<PhotoVersionHomework> op = photoVersionHMRepository.findById(versionId);
        if (op.isPresent()){
            PhotoVersionHomework p =op.get();
            Homework h = p.getHomework();
            if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("professor"))) {
                if (h.getAssignment().getCourseAssignment().getProfessors().stream().noneMatch(pr -> pr.getId().equals(auth)))
                    throw new PermissionDeniedException();
            }else {
                if (!h.getStudent().getId().equals(auth))
                    throw new PermissionDeniedException();
            }
            PhotoVersionHomeworkDTO photoVersionHomeworkDTO = modelMapper.map(op.get(), PhotoVersionHomeworkDTO.class);
            photoVersionHomeworkDTO.setPicByte(decompressZLib(photoVersionHomeworkDTO.getPicByte()));
            StringTokenizer st = new StringTokenizer(photoVersionHomeworkDTO.getTimestamp(), ".");
            photoVersionHomeworkDTO.setTimestamp(st.nextToken());
            return photoVersionHomeworkDTO;
        }else throw new PhotoVersionHMNotFoundException();

    }

    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public  PhotoCorrectionDTO getCorrectionHW(Long correctionId){

        String auth =  SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<PhotoCorrection> op = photoCorrectionRepository.findById(correctionId);
        if (op.isPresent())
        {
            PhotoCorrection p =op.get();
            Homework h = p.getHomework();
            if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("professor"))) {
                if (h.getAssignment().getCourseAssignment().getProfessors().stream().noneMatch(pr -> pr.getId().equals(auth)))
                    throw new PermissionDeniedException();
            }else {
                if (!h.getStudent().getId().equals(auth))
                    throw new PermissionDeniedException();
            }
            PhotoCorrectionDTO photoCorrectionDTO = modelMapper.map(p, PhotoCorrectionDTO.class);
            photoCorrectionDTO.setPicByte(decompressZLib(photoCorrectionDTO.getPicByte()));
            StringTokenizer st = new StringTokenizer(photoCorrectionDTO.getTimestamp(), ".");
            photoCorrectionDTO.setTimestamp(st.nextToken());
            return photoCorrectionDTO;
        }else throw new PhotoCorrectionNotFoundException();

    }


    // compress the image bytes before storing it in the database
    public  byte[] compressZLib(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        if(outputStream.toByteArray().length >= 16777215)
            throw new ImageSizeException();

        return outputStream.toByteArray();
    }

    // uncompress the image bytes before returning it to the angular application
    public  byte[] decompressZLib(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException ioe) {
        } catch (DataFormatException e) {
        }
        return outputStream.toByteArray();
    }

    @Override
    public boolean sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("testaivirtuallabs@gmail.com");
        message.setSubject(subject);
        message.setText(body);

        try {
            emailSender.send(message);
            return true;
        }
        catch (MailException me) {
            me.printStackTrace();
            return false;
        }
    }

    /**
     * Controllo se tutti gli studenti hanno accettato, elimina token dal repo e attivazione Team con invio email
     * @param token
     * @return
     */
    @Override
    public Integer confirm(String token) {
        Optional<Token> t = checkTokenValidity(token);
        if(t.isPresent()){
            if( tokenRepository.findAllByTeamId(t.get().getTeamId())
                    .stream().filter(to-> to.getStatus().equals("accepted")).count() == teamRepository.getOne(t.get().getTeamId()).getMembers().size()) {
                tokenRepository.findAllByTeamId(t.get().getTeamId()).forEach(tk-> tokenRepository.delete(tk));
                activateTeam(t.get().getTeamId());
                return 2;
            }else
                return 1;
        }else
            return 0;
    }

    @Override
    public Integer reject(String token) {
        Optional<Token> t = checkTokenValidity(token);
        if( t.isPresent()){
            //richiesta non scaduta
            Optional<Team> oteam = teamRepository.findById(t.get().getTeamId());
            if(!oteam.isPresent()) throw new TeamNotFoundException();
            t.get().setStatus("rejected");
            oteam.get().setStatus("disabled");
            Timestamp now = new Timestamp(System.currentTimeMillis());
            oteam.get().setDisabledTimestamp(now.toString());
            //tokenRepository.deleteFromTokenByTeamId(t.get().getTeamId()); //TESTare
            //VLService.evictTeam(t.get().getTeamId());
            return  1;
        }else
            return 0;
    }
    @Override
    public void notifyTeam(TeamDTO dto, List<String> memberIds, String creatorStudent, String courseId, Timestamp timeout) {
        if(timeout.before(Timestamp.from(Instant.now())))
            throw new TimeoutNotValidException();
        for (String memberId : memberIds) {
            Token t = new Token();
            t.setId(UUID.randomUUID().toString());
            t.setTeamId(dto.getId());
            t.setStatus("pending");
            t.setCourseId(courseId);
            t.setStudent(studentRepository.getOne(memberId));
            t.setExpiryDate(timeout);
            tokenRepository.saveAndFlush(t);
            sendMessage(memberId + "@studenti.polito.it",
                    "Join the Team",
                    "You have been added to the Team " + dto.getName() + "\n\n");
        }
        Token t = new Token();
        t.setId(UUID.randomUUID().toString());
        t.setTeamId(dto.getId());
        t.setStatus("accepted");
        t.setCourseId(courseId);
        t.setStudent(studentRepository.getOne(creatorStudent));
        t.setExpiryDate(timeout);
        tokenRepository.saveAndFlush(t);
    }
    @Override
    public Optional<Token> checkTokenValidity(String token){
        Optional<Token> t= tokenRepository.findById(token);
        if(t.isPresent()){
            Optional<Team> oteam = teamRepository.findById(t.get().getTeamId());
            if(!oteam.isPresent()) throw new TeamNotFoundException();
            if(oteam.get().getStatus().equals("disabled")) throw new TeamDisabledException();
            // se è ancora valido
            if( t.get().getExpiryDate().compareTo(Timestamp.from(Instant.now()))>0){
                t.get().setStatus("accepted");
                return t;
            }else{
                t.get().setStatus("rejected");
                oteam.get().setStatus("disabled");
                Timestamp now = new Timestamp(System.currentTimeMillis());
                oteam.get().setDisabledTimestamp(now.toString());
                //tokenRepository.deleteById(token);
                return Optional.empty();
            }
        }
        return t;
    }

}
