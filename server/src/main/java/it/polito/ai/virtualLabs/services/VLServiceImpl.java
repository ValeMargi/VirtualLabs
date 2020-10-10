package it.polito.ai.virtualLabs.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.repositories.*;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.*;

import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Configuration
@EnableScheduling
@Service
@Transactional
@Log(topic="service")
public class VLServiceImpl implements VLService{

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
    NotificationService notificationService;
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

    /*To check if the tokens contained in the repository have expired,
     if so, they are removed from the repository and the corresponding
     Team in the repository Team is removed.*/
    @Scheduled(initialDelay = 1000, fixedRate = 20000)
    public void run(){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        passwordResetTokenRepository.deleteAllExpiredSince(now);

        for(Token token: tokenRepository.findAll() )
        {
            if( token.getExpiryDate().compareTo(now)<0){
                tokenRepository.deleteFromTokenDBexpired(token.getTeamId());
                if( teamRepository.findById(token.getTeamId()).isPresent())
                    evictTeam(token.getTeamId());
            }
        }
        /*Per controllare scadenza elaborati*/
        for(Assignment a: assignmentRepository.findAll()){
            if(a.getExpiration().compareTo(now.toString())<=0){
                a.getHomeworks().stream().forEach(h-> h.setPermanent(true));
                assignmentRepository.saveAndFlush(a);
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
        }catch(EntityNotFoundException ente){
            throw  new CourseNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('professor')")  //hasROLE????
    @Override
    public boolean addStudentToCourse(String studentId, String courseName) {
        Optional<Student> student = studentRepository.findById(studentId);
        Optional<Course> course = courseRepository.findById(courseName);


        if( ! student.isPresent()){
            throw new StudentNotFoundException();
        }else if(!course.isPresent() ){
            throw new CourseNotFoundException();
        }else if(!course.get().isEnabled()) {
            throw new CourseDisabledException();
        }else if(!getProfessorsForCourse(courseName).stream()
                                                    .anyMatch(p ->p.getId()
                                                    .equals(SecurityContextHolder.getContext().getAuthentication().getName())))
            throw new PermissionDeniedException();
        else {
            Course c = course.get();
            Student s = student.get();
            if(!c.addStudent(s))
                return false;
            for( Assignment a: c.getAssignments()){
                Homework h = new Homework();
                h.setAssignment(a);
                h.setStatus("NULL");
                h.setStudent(s);
                homeworkRepository.saveAndFlush(h);
            }
            return true;
        }
    }


    @PreAuthorize("hasAuthority('professor')")
    @Override
    public List<StudentDTO> deleteStudentsFromCourse(List<String> studentsIds, String courseName){
        Optional<Course> course = courseRepository.findById(courseName);
        if(!course.isPresent() ){
            throw new CourseNotFoundException();
        }else if(!course.get().isEnabled()){
            throw new CourseDisabledException();
        }else if(!getProfessorsForCourse(courseName).stream()
                .anyMatch(p ->p.getId()
                        .equals(SecurityContextHolder.getContext().getAuthentication().getName())))
            throw new PermissionDeniedException();
        else{
            List<Student> ret = new ArrayList<>();
            for(String s : studentsIds){
                Optional<Student> student = studentRepository.findById(s);
                if( ! student.isPresent()){
                    throw new StudentNotFoundException();
                }

                if (course.get().removeStudent(student.get()))
                    ret.add(student.get());
            }

            return ret.stream().map(student -> modelMapper.map(student, StudentDTO.class)).collect(Collectors.toList());
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
        }catch(EntityNotFoundException ente){
            throw  new CourseNotFoundException();
        }
    }

    @Override
    public List<Boolean> enrollAll(List<String> studentsIds, String courseName){
        return  studentsIds.stream().map( s -> addStudentToCourse(s, courseName)).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('professor')")
    @Override
    public  List<StudentDTO> EnrollAllFromCSV(Reader r, String courseName){
        try {
            CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder(r)
                    .withType(StudentDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<StudentDTO> students = csvToBean.parse();
            List<StudentDTO> addedStudents = new ArrayList<>();
            for (StudentDTO st:students){
                if(addStudentToCourse(st.getId(),courseName))
                    addedStudents.add(st);
            }
           return addedStudents;
        }catch (RuntimeException exception){
            throw  new FormatFileNotValidException();
        }
    }


    /*SERVICE CORSO*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public boolean addCourse(CourseDTO course, List<String> professorsId) {
        if ( !courseRepository.findById(course.getName()).isPresent())  {
            Course c = modelMapper.map( course, Course.class);
            String idProfessor= (SecurityContextHolder.getContext().getAuthentication().getName());
            Optional<Professor> op = professorRepository.findById(idProfessor);
            if(op.isPresent()){
                Professor p=op.get();
                c.setProfessor(p);
                List<Professor> professors = professorRepository.findAllById(professorsId);
                if(professors.size()!=professorsId.size())
                    throw new ProfessorNotFoundException();
                else{
                    professors.stream().forEach(pr-> c.setProfessor(pr));
                    courseRepository.saveAndFlush(c);
                    return true;
                }

            }else throw new ProfessorNotFoundException();
        }
        return false;
    }

    @PreAuthorize("hasAuthority('professor')")
    @Override
    public List<ProfessorDTO> addProfessorsToCourse(String courseId, List<String> professorsId) {
        Optional<Course> oc= courseRepository.findById(courseId);
        if ( !oc.isPresent())  {
           throw new CourseNotFoundException();
        }
        Course c = oc.get();
        List<Professor> professors = professorRepository.findAllById(professorsId);
        if(professors.size()!=professorsId.size())
            throw new ProfessorNotFoundException();
        else{
            if(getProfessorsForCourse(courseId).stream()
                    .noneMatch(pf ->pf.getId()
                            .equals(SecurityContextHolder.getContext().getAuthentication().getName()))){
                throw new PermissionDeniedException();
            }else{
                for(Professor professor: professors){
                    if( !c.getProfessors().contains(professor))
                        c.setProfessor(professor);
                    else throw new ProfessorAlreadyPresentInCourseException();
                }

                return professors.stream().map(pro-> modelMapper.map(pro, ProfessorDTO.class)).collect(Collectors.toList());

        }



    }
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

    @PreAuthorize("hasAuthority('professor')")
    @Override
    public void enableCourse(String courseName) {
        try{
            Course c = courseRepository.getOne(courseName);
            if(getProfessorsForCourse(c.getName()).stream()
                                                   .noneMatch(pf ->pf.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName())))
                throw new PermissionDeniedException();
            if(!c.isEnabled())
                c.setEnabled(true);
            else
                throw  new CourseAlreadyEnabledException();
        }catch(EntityNotFoundException enfe){
            throw new CourseNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('professor')")
    @Override
    public void disableCourse(String courseName) {
        try{
            Course c = courseRepository.getOne(courseName);
            if(getProfessorsForCourse(c.getName()).stream()
                    .noneMatch(pf ->pf.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName())))
                throw new PermissionDeniedException();
            if(c.isEnabled())
                c.setEnabled(false);
            else
                throw new CourseAlreadyEnabledException();
        }catch(EntityNotFoundException enfe){
            throw new CourseNotFoundException();
        }
    }


    @PreAuthorize("hasAuthority('student')")
    @Override
    public List<CourseDTO> getCoursesForStudent(String studentId){
        try{
            Student s = studentRepository.getOne(studentId);
            return s.getCourses().stream().map(c->modelMapper.map(c, CourseDTO.class)).collect(Collectors.toList());
        }catch (EntityNotFoundException enfe){
            throw new StudentNotFoundException();
        }
    }
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public List<CourseDTO> getCoursesForProfessor(String professorId){
        try{
            Professor p = professorRepository.getOne(professorId);
            return p.getCourses().stream().map(c->modelMapper.map(c, CourseDTO.class)).collect(Collectors.toList());
        }catch (EntityNotFoundException enfe){
            throw new ProfessorNotFoundException();
        }
    }

    /*Metodo per cancellare corso*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public boolean removeCourse(String courseId) {
        Optional<Course> oc= courseRepository.findById(courseId);
        if (oc.isPresent())  {
            Course c = oc.get();
            String idProfessor= (SecurityContextHolder.getContext().getAuthentication().getName());
            if(getProfessorsForCourse(c.getName()).stream()
                    .noneMatch(pf ->pf.getId().equals(idProfessor)))
                throw new PermissionDeniedException();
            Optional<Professor> op = professorRepository.findById(idProfessor);
            if(op.isPresent()){
                Professor p=op.get();
                List<Student> students = c.getStudents();

                for(int i=students.size()-1; i>=0; i--){// s: stu){
                   c.removeStudent(students.get(i));
                   //log.severe("stu:"); debug
                }
                List<Professor> professors= c.getProfessors();
                for(int i=professors.size()-1; i>=0; i--){// s: stu){
                    c.removeProfessor(professors.get(i));
                    //log.severe("stu:"); ug
                }
                //stu.stream().forEach(s-> c.removeStudent(s));
               // c.getProfessors().stream().forEach(pr -> c.removeProfessor(pr));
                //p.removeCourse(c);
                courseRepository.delete(c);
              //  courseRepository.save(c);
                courseRepository.flush();
                return true;
            }else throw new ProfessorNotFoundException();
        }else throw new CourseNotFoundException();

    }

    /*Metodo per modificare corso (modificare min,max,acronimo)*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public boolean modifyCourse(CourseDTO course) {
        Optional<Course> oc=courseRepository.findById(course.getName());
        if ( oc.isPresent())  {
            Course c = oc.get();
            String idProfessor= (SecurityContextHolder.getContext().getAuthentication().getName());
            if(getProfessorsForCourse(c.getName()).stream()
                    .noneMatch(pf ->pf.getId().equals(idProfessor)))
                throw new PermissionDeniedException();
            c.setAcronym(course.getAcronym());
            if(course.getMax() > course.getMin()) {
                c.setMax(course.getMax());
                c.setMin(course.getMin());
            }else throw  new CardinalityNotAccetableException();
            c.setEnabled(course.isEnabled());
            courseRepository.saveAndFlush(c);
            return true;
        }else throw  new CourseNotFoundException();
    }


    /*---> SERVICE GRUPPO*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public List<TeamDTO> getTeamsForStudent(String studentId){
        try {
            Student s = studentRepository.getOne(studentId);
            return s.getTeams().stream().map(t -> modelMapper.map(t, TeamDTO.class)).collect(Collectors.toList());
        }catch(EntityNotFoundException enfe){
            throw new StudentNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('student')")
    @Override
    public TeamDTO getTeamForStudent(String courseId, String studentId) {
        try {
            Optional<Course> oc = courseRepository.findById(courseId);
            if (!oc.isPresent())
                throw new CourseNotFoundException();
            Optional<Student> os = studentRepository.findById(studentId);
            if (!os.isPresent())
                throw new StudentNotFoundException();
            List<TeamDTO> list = oc.get().getTeams().stream()
                                        .filter(team -> team.getMembers().contains(os.get()))
                                        .map(team -> modelMapper.map(team, TeamDTO.class)).collect(Collectors.toList());

            if (list.size() != 1) {
                return null;
            }
            else {
                return list.get(0);
            }
        }catch(EntityNotFoundException enfe){
            throw new StudentNotFoundException();
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


    @PreAuthorize("hasAuthority('student')")
    @Override
    public  TeamDTO proposeTeam(String courseId, String name, List<String> memberIds){
        Optional<Course> course = courseRepository.findById(courseId);
        String creatorStudent = SecurityContextHolder.getContext().getAuthentication().getName();
        if( !memberIds.contains(creatorStudent))
            throw new PermissionDeniedException();

        if( !course.isPresent())
            throw  new CourseNotFoundException();
        if(!course.get().isEnabled())
            throw new CourseDisabledException();

        List<String> enrolledStudents= getEnrolledStudents(courseId).stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList());

        if(course.get().getTeams().stream().anyMatch(t ->t.getName().equals(name)))
            throw new NameTeamIntoCourseAlreadyPresentException();

        if ( !enrolledStudents.containsAll(memberIds))
            throw  new StudentNotEnrolledToCourseException();

        if( memberIds.stream()
                .map(s->studentRepository.getOne(s))
                .map(Student::getTeams)
                .filter( lt-> !lt.isEmpty())
                .map( lt-> lt.stream().map(Team::getCourse)
                        .anyMatch(c-> c.getName().equals(courseId))).collect(Collectors.toList()).contains(true))
            throw  new StudentAlreadyInTeamException();

        if ( memberIds.size()<getCourse(courseId).get().getMin() || memberIds.size()> getCourse(courseId).get().getMax())
            throw  new CardinalityNotAccetableException();

        if( memberIds.stream().distinct().count() != memberIds.size())
            throw  new StudentDuplicateException();

        Team team = new Team();
        team.setName(name);
        team.setCourse(course.get());
        team.setStatus(0);
        team.setCreatorId(creatorStudent);

        if( course.get().getPhotoModelVM()!=null){
            Course c =course.get();
            team.setDiskSpaceLeft(c.getDiskSpace());
            team.setMaxVpcuLeft(c.getMaxVcpu());
            team.setRamLeft(c.getRam());
            team.setRunningInstances(c.getRunningInstances());
            team.setTotInstances(c.getRunningInstances());
        }
        teamRepository.save(team);
        memberIds.stream().forEach(s-> team.addStudentIntoTeam(studentRepository.getOne(s)));

        notificationService.notifyTeam(modelMapper.map(team, TeamDTO.class), memberIds, creatorStudent,  courseId);

        return  modelMapper.map(team, TeamDTO.class);
    }
    /*Metodo per ottenere le proposte di Team*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public   List<Map<String, Object>> getProposals(String courseId) {
        String student = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if(!os.isPresent()) throw new StudentNotFoundException(); //PermissionDenied
        Student s = os.get();
        if(s.getTokens().stream().filter(t-> t.getCourseId().equals(courseId)).anyMatch(t->  t.getStatus().equals(true)))
            throw new StudentWaitingTeamCreationException();

        List<Long> teamList = s.getTokens().stream()
                .filter(t-> t.getCourseId().equals(courseId)).map(t-> t.getTeamId()).collect(Collectors.toList());

        List<Map<String, Object>> l = new ArrayList<>();
        for( Long teamId : teamList) {
            Team team= teamRepository.getOne(teamId);
            Student stu = studentRepository.getOne(team.getCreatorId());
            Map<String, Object> m = new HashMap<>();
            m.put("teamName", team.getName());
            m.put("creator", stu.getFirstName()+" "+stu.getName()+" "+ stu.getId());
            String currentToken = stu.getTokens().stream().filter(t-> t.getTeamId().equals(teamId)).findFirst().get().getId();
            m.put("tokenId", tokenRepository.getOne(currentToken));
            Map<String, Object> m2= new HashMap<>();
            for(Token token: tokenRepository.findAllByTeamId(teamId).stream()
                    .filter(t->!t.getStudent().equals(s)).collect(Collectors.toList())){
                m2.put("student", token.getStudent().getFirstName()+" "+token.getStudent().getName()+" "+token.getStudent().getId());
                m2.put("status", token.getStatus());
            }
            m.put("students", m2);
            l.add(m);

        }
        return l;
    }


    @PreAuthorize("hasAuthority('student') || hasAuthority('professor')")
    @Override
    public List<TeamDTO> getTeamForCourse(String courseName){
        try{
            Course course =courseRepository.getOne(courseName);
            return  course.getTeams().stream().map(t-> modelMapper.map(t, TeamDTO.class)).collect(Collectors.toList());
        }catch(EntityNotFoundException enfe){
            throw new CourseNotFoundException();
        }

    }

    @PreAuthorize("hasAuthority('student')")
    @Override
    public List<StudentDTO> getStudentsInTeams(String courseName){
        if( courseRepository.findById(courseName).isPresent())
            return courseRepository.getStudentsTeams(courseName).stream()
                    .map(s->modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException();
    }

    @PreAuthorize("hasAuthority('student')")
    @Override
    public List<StudentDTO>  getAvailableStudents(String courseName){
        if( courseRepository.findById(courseName).isPresent())
            return courseRepository.getStudentsNotInTeams(courseName).stream()
                    .map(s->modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException();
    }

    @Override
    public  void activateTeam(Long id){
        try{
            if(teamRepository.existsById(id)){
                Team t = teamRepository.findById(id).get();
                t.setStatus(1);
                t.getMembers().stream().forEach(s-> notificationService.sendMessage(s.getEmail(),"Notification: team created","Team creation success!"));

            }
        }catch(EntityNotFoundException enf){
            throw  new TeamNotFoundException();
        }
    }

    @Override
    public void evictTeam(Long id){
        try{
            Team t = teamRepository.getOne(id);
            t.getMembers().stream().forEach(s-> notificationService.sendMessage(s.getEmail(),"Notification: team not created","A student has rejected the proposal. Team creation stopped!"));
            teamRepository.delete(t);
        }catch(EntityNotFoundException enf){
            throw  new TeamNotFoundException();
        }
    }




    /*SERVICE MODELLO VM*/
    /*professor può caricare solo un modello per corso e può modificare i parametri per ogni gruppo*/

    /**
     *
     * @param courseDTO: contiene le informazioni del modello VM creato dal professor per il corso con courseId indicato
     * @param courseId: nome del corso identificato
     * @param photoModelVM: screenshot del modello VM creato
     * @return
     */
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public CourseDTO addModelVM(CourseDTO courseDTO, String courseId, PhotoModelVM photoModelVM) {
        Optional<Course> oc = courseRepository.findById(courseId);
        if( oc.isPresent()) {
            Course c = oc.get();
            if (c.getProfessors().stream().anyMatch(p -> p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
                //Controllo per verificare che il professore setta il modello per il corso con courseId per la prima volta
                if (c.getPhotoModelVM() == null) {
                    List<Team> teams = teamRepository.findAllById(c.getTeams().stream().map(t->t.getId()).collect(Collectors.toList()));
                    c.setPhotoModelVM(photoModelVM);
                    c.setMaxVcpu(courseDTO.getMaxVcpu());
                    c.setDiskSpace(courseDTO.getDiskSpace());
                    c.setRam(courseDTO.getRam());
                    c.setTotInstances(courseDTO.getTotInstances());
                    c.setRunningInstances(courseDTO.getRunningInstances());

                    teams.stream().forEach(t-> {
                        t.setDiskSpaceLeft(courseDTO.getDiskSpace());
                        t.setRamLeft(courseDTO.getRam());
                        t.setMaxVpcuLeft(courseDTO.getMaxVcpu());
                        t.setTotInstances(courseDTO.getTotInstances());
                        t.setRunningInstances(courseDTO.getRunningInstances());
                    });
                    photoModelVMRepository.save(photoModelVM);
                    return modelMapper.map(c, CourseDTO.class);

                } else throw new ModelVMAlreadytPresentException();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();

    }

    /*Metodo per modificare risorse vm da parte docente*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public CourseDTO updateModelVM(CourseDTO courseDTO, String courseName ) {
        Optional<Course> oc = courseRepository.findById(courseName);
        if( oc.isPresent()) {
            Course c = oc.get();
            if (c.getProfessors().stream().anyMatch(p -> p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
                //Controllo per verificare che il professore setta il modello per il corso con courseId per la prima volta
                if (c.getPhotoModelVM() != null) {
                    List<Team> teams = teamRepository.findAllById(c.getTeams().stream().map(t->t.getId()).collect(Collectors.toList()));
                    int diskSpaceDecrease = c.getDiskSpace()-courseDTO.getDiskSpace();
                    int vcpuDecrease = c.getMaxVcpu()-courseDTO.getMaxVcpu();
                    int ramDecrease = c.getRam() - courseDTO.getRam();
                    int runningInstancesDecrease = c.getRunningInstances() - courseDTO.getRunningInstances();
                    int totalInstancesDecrease = c.getTotInstances() - courseDTO.getTotInstances();
                    for(Team t:teams){
                        if(     (diskSpaceDecrease>0 && t.getDiskSpaceLeft()<diskSpaceDecrease) ||
                                (vcpuDecrease>0 && t.getMaxVpcuLeft()<vcpuDecrease) ||
                                (ramDecrease>0 && t.getRamLeft()<ramDecrease) ||
                                (runningInstancesDecrease>0 && t.getRunningInstances()<runningInstancesDecrease) ||
                                (totalInstancesDecrease>0 && t.getTotInstances()<totalInstancesDecrease))
                            throw new ResourcesVMNotRespectedException();
                    }
                    c.setMaxVcpu(courseDTO.getMaxVcpu());
                    c.setDiskSpace(courseDTO.getDiskSpace());
                    c.setRam(courseDTO.getRam());
                    c.setTotInstances(courseDTO.getTotInstances());
                    c.setRunningInstances(courseDTO.getRunningInstances());

                    teams.stream().forEach(t-> {
                        t.setDiskSpaceLeft(courseDTO.getDiskSpace()-diskSpaceDecrease);
                        t.setRamLeft(t.getRamLeft()-ramDecrease);
                        t.setMaxVpcuLeft(t.getMaxVpcuLeft()-vcpuDecrease);
                        t.setTotInstances(t.getTotInstances()-totalInstancesDecrease);
                        t.setRunningInstances(t.getRunningInstances()-runningInstancesDecrease);
                    });
                    return modelMapper.map(c, CourseDTO.class);

                } else throw new ModelVMNotSettedException();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
    }



    /*Ogni gruppo può avere più VM e ogni VM ha l'identificativo del team e tutti i membri
    * del team possono accederci ma solo chi ha creato la VM è owner*/
    /**
     *Controllo l'esistenza di una VM se è già presente una VM con lo stesso nome per lo stesso team all'interno del corso
     * @param vmdto: contiene tutte le caratteristiche della VM compilate nel form per la creazione di una VM per gruppo
     * @param courseId: identificativo del corso
     * @return
     */
    @PreAuthorize("hasAuthority('student')")
    @Override
    public VMDTO addVM(VMDTO vmdto, String courseId, PhotoVMDTO photoVMDTO) {

            String studentAuth= SecurityContextHolder.getContext().getAuthentication().getName();
            if( getStudentsInTeams(courseId).stream().anyMatch(s-> s.getId().equals(studentAuth))){
                Course c = courseRepository.getOne(courseId);
                if(c==null )
                    throw new CourseNotFoundException();
                if( !c.isEnabled())
                    throw new CourseDisabledException();
                if (c.getPhotoModelVM() == null)
                    throw  new ModelVMNotSettedException();

                Student s = studentRepository.getOne(studentAuth);
                Team t = teamRepository.getOne(s.getTeams().stream().filter(te->te.getCourse().equals(c)).findFirst().get().getId());
                if(t.getVms().stream().anyMatch(v->v.getNameVM().equals(vmdto.getNameVM())))
                    throw new VMduplicatedException();
                if( vmdto.getDiskSpace() <= t.getDiskSpaceLeft() && vmdto.getNumVcpu()<= t.getMaxVpcuLeft()
                    && vmdto.getRam() <= t.getRamLeft() && t.getTotInstances()>0){
                    VM vm = modelMapper.map(vmdto, VM.class);
                    vm.setCourse(c);
                    PhotoVM photoVM = modelMapper.map(photoVMDTO, PhotoVM.class);
                    vm.setPhotoVM(photoVM);
                    vm.addStudentToOwnerList(s); //add owner
                  //  c.setTotInstances(c.getTotInstances()-1);
                    t.getMembers().stream().forEach(stu-> vm.addStudentToMemberList(stu));
                    vm.setTeam(t);
                    t.setDiskSpaceLeft(t.getDiskSpaceLeft()-vmdto.getDiskSpace());
                    t.setMaxVpcuLeft(t.getMaxVpcuLeft()-vmdto.getNumVcpu());
                    t.setRamLeft(t.getRamLeft()-vmdto.getRam());
                    t.setTotInstances(t.getTotInstances()-1);
                    VMRepository.save(vm);
                    photoVMRepository.save(photoVM);
                   return modelMapper.map(vm, VMDTO.class);
               }else throw new ResourcesVMNotRespectedException();
            }else throw new PermissionDeniedException();
    }


    /*Metodo per rendere determinati membri del team owner di una data VM*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean addOwner(Long VMid, String courseId, List<String> studentsId) { //CourseId preso dal pathVariable
       Optional<VM> ovm= VMRepository.findById(VMid);
        if (ovm.isPresent() ) {
            VM vm =ovm.get();
            Course c = courseRepository.getOne(courseId);
            if (c == null )
                throw new CourseNotFoundException();
            if(!c.isEnabled())
                throw new CourseDisabledException();
            if(!vm.getCourse().equals(c))
                throw new PermissionDeniedException();
                String studentAuth = SecurityContextHolder.getContext().getAuthentication().getName();
                if (vm.getOwnersVM().stream().anyMatch(s->s.getId().equals(studentAuth))) //lo student autenticato è già owner della VM, può quindi aggiungere altri owner
                {
                    if(vm.getMembersVM().stream().map(st->st.getId()).collect(Collectors.toList()).containsAll(studentsId)) //tutti lgi studenti fanno parte del team
                    {
                     if( studentsId.stream().map(s-> vm.addStudentToOwnerList(studentRepository.getOne(s))).anyMatch(r->r.equals(false)))
                        throw new RuntimeException("One or more students are already owner");
                     else{
                         VMRepository.saveAndFlush(vm);
                         return true;
                     }
                    }else throw new StudentNotFoundException();
                }else throw new PermissionDeniedException();
        }else throw new VMNotFoundException();
    }

    @PreAuthorize("hasAuthority('professor')")
    @Override
    public List<StudentDTO> getOwnersForProfessor(Long VMid) {
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Professor> op = professorRepository.findById(professor);
        if(op.isPresent()){
            Optional<VM> ovm= VMRepository.findById(VMid);
            if(ovm.isPresent()){
                VM vm= ovm.get();
                if(vm.getCourse().getProfessors().contains(op.get())) {
                    return vm.getOwnersVM().stream().map(st -> modelMapper.map(st, StudentDTO.class)).collect(Collectors.toList());
                }else throw new PermissionDeniedException();
            }else throw new VMNotFoundException();
        }else throw new ProfessorNotFoundException();
    }

    @PreAuthorize("hasAuthority('student')")
    @Override
    public List<StudentDTO> getOwnersForStudent(Long VMid) {
        String student = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if (os.isPresent()) {
            Optional<VM> ovm= VMRepository.findById(VMid);
            if (ovm.isPresent()) {
                VM vm= ovm.get();
                if(vm.getTeam().getMembers().contains(os.get())) {
                    return vm.getOwnersVM().stream().map(st -> modelMapper.map(st, StudentDTO.class)).collect(Collectors.toList());
                }else throw new PermissionDeniedException();
           }else throw new VMNotFoundException();
        }else throw new StudentNotFoundException();
    }

    /*Metodo per attivare VM, controllo se l'utente autenticato è un owner della VM*/
        @PreAuthorize("hasAuthority('student')")
        @Override
        public boolean activateVM(Long VMid ){ //CourseId preso dal pathVariable
            Optional<VM> ovm = VMRepository.findById(VMid);
            if (ovm.isPresent()) {
                VM vm = ovm.get();
                if (vm.getOwnersVM().stream().map(s->s.getId()).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                    Team t = teamRepository.getOne(vm.getTeam().getId());
                    if(t.getRunningInstances()>0) { //se sono disponibili ancora delle VM da runnare
                        Course c = courseRepository.getOne(t.getCourse().getName());
                        if( c.isEnabled()){
                            t.setRunningInstances(t.getRunningInstances() - 1);
                            vm.setStatus("on");
                        }else throw new CourseDisabledException();
                    }else throw new ResourcesVMNotRespectedException();
                } else throw new PermissionDeniedException();
            } else throw new VMNotFoundException();
            return true;
        }

    /*Metodo per utilizzare VM, controllo se l'utente autenticato è un membro della VM*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean useVM(Long VMid, String timestamp, PhotoVMDTO photoVMDTO ){ //CourseId preso dal pathVariable
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm = ovm.get();
            if (vm.getMembersVM().stream().map(s->s.getId()).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                if(vm.getStatus().equals("on")){
                    PhotoVM p = photoVMRepository.getOne(vm.getPhotoVM().getId());
                    p.setNameFile(photoVMDTO.getNameFile());
                    p.setType(photoVMDTO.getType());
                    p.setPicByte(compressZLib(photoVMDTO.getPicByte()));
                    vm.setTimestamp(timestamp);
                    return true;
                }else throw new VMnotEnabledException();
            } else throw new PermissionDeniedException();
        } else throw new VMNotFoundException();

    }

    /*Metodo per spegnere VM, controllo se l'utente autenticato è un owner della VM*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean disableVM(Long VMid ){ //CourseId preso dal pathVariable
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm = ovm.get();
            if (vm.getOwnersVM().stream().map(s->s.getId()).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                Team t = teamRepository.getOne(vm.getTeam().getId());
                Course c = courseRepository.getOne(t.getCourse().getName());
                t.setRunningInstances(t.getRunningInstances()+1);
                vm.setStatus("off");
                //VMRepository.save(vm);
            } else throw new PermissionDeniedException();
        } else throw new VMNotFoundException();
        return true;
    }
    /*Metodo per cancellare VM, controllo se l'utente autenticato è un owner della VM*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean removeVM(Long VMid){
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm= ovm.get();
            if (vm.getOwnersVM().stream().map(s->s.getId()).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                Team t = teamRepository.getOne(vm.getTeam().getId());
                Course c = courseRepository.getOne(t.getCourse().getName());
                if( vm.getStatus().equals("on")) {
                    t.setRunningInstances(t.getRunningInstances()+1);
                }
                t.setTotInstances(t.getTotInstances()+1);
                t.setRamLeft(vm.getRam()+t.getRamLeft());
                t.setMaxVpcuLeft(vm.getNumVcpu()+t.getMaxVpcuLeft());
                t.setDiskSpaceLeft(vm.getDiskSpace()+t.getDiskSpaceLeft());
                photoVMRepository.delete(vm.getPhotoVM());
                VMRepository.delete(vm);
            } else throw new PermissionDeniedException();
        } else throw new VMNotFoundException();
        return true;
    }

    /*Metodo per modificare risorse VM owner*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public VMDTO updateVMresources(Long VMid,VMDTO vmdto) {
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm = ovm.get();
            if (vm.getOwnersVM().stream().map(s -> s.getId()).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                Team t = teamRepository.getOne(vm.getTeam().getId());
                if(t.getVms().stream().anyMatch(v->v.getNameVM().equals(vmdto.getNameVM())))
                    throw new VMduplicatedException();
               if(!vm.getStatus().equals("off") ) throw new VMnotOffException();
                if (
                        vmdto.getDiskSpace() <= (t.getDiskSpaceLeft()+vm.getDiskSpace()) &&
                        vmdto.getNumVcpu() <= (t.getMaxVpcuLeft()+vm.getNumVcpu()) &&
                        vmdto.getRam() <= (t.getRamLeft()+vm.getRam())) {
                    t.setDiskSpaceLeft(t.getDiskSpaceLeft() + vm.getDiskSpace() - vmdto.getDiskSpace());
                    t.setRamLeft(t.getRamLeft() + vm.getRam() - vmdto.getRam());
                    t.setMaxVpcuLeft(t.getMaxVpcuLeft() + vm.getNumVcpu() - vmdto.getNumVcpu());
                    vm.setNameVM(vmdto.getNameVM());
                    vm.setDiskSpace(vmdto.getDiskSpace());
                    vm.setRam(vmdto.getRam());
                    vm.setNumVcpu(vmdto.getNumVcpu());
                    return modelMapper.map(vm, VMDTO.class);
                } else throw new ResourcesVMNotRespectedException();
            } else throw new PermissionDeniedException();
        }else throw new VMNotFoundException();
    }

    /*Visualizzare VM accessibili allo student in tab corso*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public List<VMDTO> allVMforStudent( String courseId) { //CourseId preso dal pathVariable
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Student s = studentRepository.getOne(student);
        List<Team> teams = s.getTeams().stream().filter(c->c.getCourse().getName().equals(courseId)).collect(Collectors.toList());
        if(!teams.isEmpty()){
            List<VM> vmsList = teams.get(0).getVms();
            return  vmsList.stream().map(v->modelMapper.map(v, VMDTO.class)).collect(Collectors.toList());
        }else throw new TeamNotFoundException();

    }

    /*Visualizzare VM accessibili al professor in tab corso*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public List<VMDTO> allVMforCourse( String courseId) { //CourseId preso dal pathVariable
        Optional<Course> oc= courseRepository.findById(courseId);
        if( oc.isPresent()){
            Course c= oc.get();
            if(c.getProfessors().stream().map(p->p.getId()).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())){
               List<VM> vmsList= c.getVms();
                return vmsList.stream().map( v -> modelMapper.map(v, VMDTO.class)).collect(Collectors.toList());
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
    }
    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public  List<VMDTO> getAllVMTeam(  Long teamId){
        Optional<Team> ot = teamRepository.findById(teamId);
        if( !ot.isPresent())
            throw new TeamNotFoundException();
        Team t = ot.get();
        if( t.getStatus()==0)
            throw new TeamNotEnabledException();
        return t.getVms().stream().map(te-> modelMapper.map(te, VMDTO.class)).collect(Collectors.toList());


    }


    /*Visualizzare VM con un certo VMid  allo student in tab corso*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public PhotoVMDTO getVMforStudent( String courseId, Long VMid) {
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Student s = studentRepository.getOne(student);
        List<Team> teams = s.getTeams().stream().filter(c->c.getCourse().getName().equals(courseId)).collect(Collectors.toList());
        if(!teams.isEmpty()){
           Optional<VM> ovm= VMRepository.findById(VMid);
           if(ovm.isPresent()){
               VM vm = ovm.get();
               List<VM> listVMs= teams.get(0).getVms();
               if(listVMs.contains(vm)){
                   PhotoVMDTO photoVMDTO = modelMapper.map(vm.getPhotoVM(), PhotoVMDTO.class);
                   photoVMDTO.setPicByte(decompressZLib(photoVMDTO.getPicByte()));
                   return photoVMDTO;
               }else throw new PermissionDeniedException();
           }else throw new VMNotFoundException();
        }else throw new TeamNotFoundException();
    }

    /*student owner modifia risorse associate a VM se è spenta e se non superano i limiti imposti dal gruppo*/

    /*Per vedere chi è owner*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean isOwner(  Long VMid) { //CourseId preso dal pathVariable
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if(os.isPresent()){
            Student s=os.get();
            Optional<VM> ovm= VMRepository.findById(VMid);
            if(ovm.isPresent()){
                VM vm= ovm.get();
                if(vm.getOwnersVM().stream().map(st->st.getId()).collect(Collectors.toList()).contains(s.getId()))
                    return true;
                else throw new PermissionDeniedException();
            }else throw new VMNotFoundException();
        }else throw new StudentNotFoundException();
    }



    /*SERVICE CONSEGNA*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public boolean addAssignment( AssignmentDTO assignmentDTO,PhotoAssignmentDTO photoAssignmentDTO,  String courseId) { //CourseId preso dal pathVariable
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Course> oc = courseRepository.findById(courseId);
        if(oc.isPresent()){
            Course c = oc.get();
            if(c.getProfessors().stream().anyMatch(p->p.getId().equals(professor))){
                if( !c.getAssignments().stream().anyMatch(a->a.getNameAssignment().equals(assignmentDTO.getAssignmentName()))) {
                    Assignment assignment= modelMapper.map(assignmentDTO, Assignment.class);
                    PhotoAssignment photoAssignment = modelMapper.map(photoAssignmentDTO, PhotoAssignment.class);
                    assignment.setCourseAssignment(c);
                    assignment.setPhotoAssignment(photoAssignment);
                    for(Student s: c.getStudents()){
                        Homework h = new Homework();
                        h.setStatus("NULL");
                        h.setAssignment(assignment);
                        h.setStudentForHomework(s);
                        h.setPermanent(false);
                        homeworkRepository.save(h);
                    }
                    assignmentRepository.save(assignment);
                    photoAssignmentRepository.save(photoAssignment);
                }else throw new AssignmentAlreadyExistException();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
        return true;
    }

    /*Metodo per ritornare le consegne di un dato corso*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public List<AssignmentDTO> allAssignment(  String courseId) { //CourseId preso dal pathVariable
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Professor> op = professorRepository.findById(professor);
        if(op.isPresent()){
            Professor p= op.get();
            Optional<Course> c = p.getCourses().stream().filter(co->co.getName().equals(courseId)).findFirst();
            if(c.isPresent()){
                return c.get().getAssignments().stream().map( a -> modelMapper.map(a, AssignmentDTO.class)).collect(Collectors.toList());
            }throw new PermissionDeniedException();
        }else throw new ProfessorNotFoundException();
    }



    /*Metodo per ritornare le consegne di un dato corso*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public List<AssignmentDTO> allAssignmentStudent(  String courseId) { //CourseId preso dal pathVariable
        String student = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if (os.isPresent()) {
            Student s = os.get();
            Optional<Course> c = s.getCourses().stream().filter(co->co.getName().equals(courseId)).findFirst();
            if(c.isPresent()){
             //   List<Assignment> assignmentsList = c.get().getAssignments();
              //  assignmentsList.stream().forEach(a->a.setPicByte(decompressZLib(a.getPicByte())));
                return c.get().getAssignments().stream().map( a -> modelMapper.map(a, AssignmentDTO.class)).collect(Collectors.toList());
            }throw new CourseNotFoundException();
        }else throw new StudentNotFoundException();
    }
    /*Metodo per ritornare la consegna di un dato corso*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public PhotoAssignmentDTO getAssignmentStudent( Long assignmentId ) { //CourseId preso dal pathVariable
        String student = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if (os.isPresent()) {
            Student s = os.get();
            Optional<Assignment> oa = assignmentRepository.findById(assignmentId);
                if (oa.isPresent()) {
                    Assignment a = oa.get();
                    if (a.getCourseAssignment().getStudents().contains(s)) {
                        Homework homework = homeworkRepository.findById(a.getHomeworks().stream()
                                .filter(h->h.getStudent().getId().equals(s.getId())).findFirst()
                                .get().getId()).get();
                        if(homework.getStatus().equals("NULL")){
                            homework.setStatus("LETTO");
                        }
                        PhotoAssignment pa = a.getPhotoAssignment();
                        Optional<PhotoAssignment> photoAssignment = photoAssignmentRepository.findById(pa.getId());
                        if (photoAssignment.isPresent()) {
                            pa.setPicByte(decompressZLib(pa.getPicByte()));
                            return modelMapper.map(pa, PhotoAssignmentDTO.class);
                        } else throw new PhotoAssignmentNotFoundException();
                    } else throw new PermissionDeniedException();
                } else throw new AssignmentNotFoundException();
        }else throw new StudentNotFoundException();
    }

    /*Metodo per ritornare la consegna di un dato corso*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public PhotoAssignmentDTO getAssignmentProfessor(Long assignmentId ) { //CourseId preso dal pathVariable
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Professor> op = professorRepository.findById(professor);
        if(op.isPresent()){
                Professor p =op.get();
                Optional<Assignment> oa=assignmentRepository.findById(assignmentId);
                if(oa.isPresent()){
                    Assignment a = oa.get();
                    if (a.getCourseAssignment().getProfessors().contains(p)) {
                        PhotoAssignment pa = a.getPhotoAssignment();
                        PhotoAssignmentDTO paDTO = modelMapper.map(pa, PhotoAssignmentDTO.class);
                        paDTO.setPicByte(decompressZLib(paDTO.getPicByte()));
                        return paDTO;
                    } else throw new PermissionDeniedException();
                } else throw new AssignmentNotFoundException();
        }else throw new ProfessorNotFoundException();
    }


    /*SERVICE ELABORATI*/
    @PreAuthorize("hasAuthority('student')")
    @Override //uploadHomework
    public boolean uploadVersionHomework (Long homeworkId, PhotoVersionHomeworkDTO photoVersionHomeworkDTO) { //CourseId preso dal pathVariable
        String student = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if (os.isPresent()) {
            Student s = os.get();
            Optional<Homework> oh = homeworkRepository.findById(homeworkId);
            if (oh.isPresent()) {
                Homework h = oh.get();
                if (h.getStudent().getId().equals(student)) {
                    if (h.getPermanent().equals(false)) {
                        h.setStatus("CONSEGNATO");
                        PhotoVersionHomework photoVersionHomework = modelMapper.map(photoVersionHomeworkDTO, PhotoVersionHomework.class);
                        h.setPhotoVersionHomework(photoVersionHomework);
                        homeworkRepository.saveAndFlush(h);
                        photoVersionHMRepository.saveAndFlush(photoVersionHomework);
                        return true;
                    } else throw new HomeworkIsPermanentException();
                } else throw new PermissionDeniedException();
            } else throw new HomeworkNotFoundException();
        } else throw new PermissionDeniedException();
    }

    //@PreAuthorize("hasAuthority('student')")
    @Override
    public boolean updateStatusHomework( Long homeworkId, String status) {
        Boolean isAuthenticated =SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        if(isAuthenticated){
                Optional<Homework> oh= homeworkRepository.findById(homeworkId);
                if( oh.isPresent()){
                    Homework h = oh.get();
                    h.setStatus(status);
                    homeworkRepository.saveAndFlush(h);
                    return true;
                }else throw new HomeworkNotFoundException();
        }else throw new PermissionDeniedException();
    }


    @PreAuthorize("hasAuthority('professor')")
    @Override
    public  List<HomeworkDTO> allHomework(String courseName, Long assignmentId){ //AGGIUNTA 28/07
        Optional<Course> oc= courseRepository.findById(courseName);
        if(oc.isPresent()){
            Course c = oc.get();
            if(c.getProfessors().stream().anyMatch(p->p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
               Assignment assignment = c.getAssignments().stream().filter(a->a.getId().equals(assignmentId)).findFirst().get();
                if(assignment!=null ) {
                    return assignment.getHomeworks().stream().map(h->modelMapper.map(h,HomeworkDTO.class)).collect(Collectors.toList());
                }else throw new AssignmentNotFoundException();
            }else throw new PermissionDeniedException();


        }else throw new CourseNotFoundException();

    }

    /**
     *
     * @param homeworkId
     * @return si ritorna una lista di Map<String,Object> dove vengono inseriti id e timestamp e nome dell'immagine associata alla versione
     * di tutte le versioni di Homerwork per un certo corso
     * (evitando così di inviare anche tutte le immagini)
     */
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public  List<Map<String, Object>> getVersionsHMForProfessor( Long homeworkId){
        Optional<Homework> oh = homeworkRepository.findById(homeworkId);
        if(oh.isPresent()){
            Homework h = oh.get();
            if( h.getAssignment().getCourseAssignment().getProfessors().stream()
                    .anyMatch(p->p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))){
                List<PhotoVersionHomework> versions = h.getVersions();
                List<Map<String, Object>> l = new ArrayList<>();
                for( PhotoVersionHomework v: versions){
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", v.getId());
                    m.put("timestamp", v.getTimestamp());
                    m.put("nameFile", v.getNameFile());
                    l.add(m);
                }

                return l;
            }else throw  new PermissionDeniedException();
        }else throw new HomeworkNotFoundException();
    }



    @PreAuthorize("hasAuthority('student')")
    @Override
    public  HomeworkDTO getHomework(Long assignmentId){
        Optional<Assignment> oa = assignmentRepository.findById(assignmentId);
        if(oa.isPresent()){
            Assignment a = oa.get();
            String studentAuth =SecurityContextHolder.getContext().getAuthentication().getName();
            if(a.getCourseAssignment().getStudents().stream().anyMatch(s->s.getId().equals(studentAuth))){
                Homework homework = a.getHomeworks().stream().filter(h->h.getStudent().getId().equals(studentAuth)).
                        findFirst().get();
                return modelMapper.map(homework, HomeworkDTO.class);
            }else throw new PermissionDeniedException();
        }else throw new AssignmentNotFoundException();
    }

    @PreAuthorize("hasAuthority('student')")
    @Override
    public  List<Map<String, Object>> getVersionsHMForStudent(Long assignmentId){
        Optional<Assignment> oa = assignmentRepository.findById(assignmentId);
        if(oa.isPresent()){
            Assignment a = oa.get();
            String studentAuth =SecurityContextHolder.getContext().getAuthentication().getName();
            if(a.getCourseAssignment().getStudents().stream().anyMatch(s->s.getId().equals(studentAuth))){
                 List<PhotoVersionHomework> versions = a.getHomeworks().stream().filter(h->h.getStudent().getId().equals(studentAuth)).
                        findFirst().get().getVersions();
                // versionsList.stream().forEach(p->p.setPicByte(decompressZLib(p.getPicByte())));
                List<Map<String, Object>> l = new ArrayList<>();
                for( PhotoVersionHomework v: versions) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", v.getId());
                    m.put("timestamp", v.getTimestamp());
                    m.put("nameFile", v.getNameFile());
                    l.add(m);
                }
                return l;
            }else throw new PermissionDeniedException();
        }else throw new AssignmentNotFoundException();
    }

    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public  PhotoVersionHomeworkDTO getVersionHM(Long versionId){

        String auth =  SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<PhotoVersionHomework> op = photoVersionHMRepository.findById(versionId);
        if (op.isPresent())
        {
            PhotoVersionHomework p =op.get();
            Homework h = p.getHomework();
            if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("professor"))) {
                if (!h.getAssignment().getCourseAssignment().getProfessors().stream().anyMatch(pr -> pr.getId().equals(auth)))
                    throw new PermissionDeniedException();
            }else {
                if (!h.getStudent().getId().equals(auth))
                    throw new PermissionDeniedException();
            }
            return modelMapper.map(op.get(), PhotoVersionHomeworkDTO.class);
        }else throw new PhotoVersionHMNotFoundException();

    }



    /*Metodo per consegnare correzione*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public boolean uploadCorrection(Long homeworkId, Long versionHMid,
                                    PhotoCorrectionDTO photoCorrectionDTO,Boolean permanent, String grade) {
        Optional<Homework> oh = homeworkRepository.findById(homeworkId);
        String professorAuth = SecurityContextHolder.getContext().getAuthentication().getName();
        if(oh.isPresent()){
            Homework h= oh.get();
            if( h.getAssignment().getCourseAssignment().getProfessors().stream()
                    .anyMatch(p->p.getId().equals(professorAuth))){
                if( photoVersionHMRepository.findById(versionHMid).isPresent()){
                    PhotoCorrection photoCorrection = modelMapper.map(photoCorrectionDTO, PhotoCorrection.class);
                    photoCorrection.setIdVersionHomework(versionHMid);
                    photoCorrection.setIdProfessor(professorAuth);
                    h.setPhotoCorrection(photoCorrection);
                    h.setStatus("RIVISTO");
                    h.setPermanent(permanent);
                    if(permanent) {
                        if(grade.equals(null)) throw new GradeNotValidException();
                        h.setGrade(grade);
                    }
                    photoCorrectionRepository.saveAndFlush(photoCorrection);
                    return true;
                }else throw new HomeworkVersionIdNotFoundException();

            }else throw  new PermissionDeniedException();
        }throw new HomeworkNotFoundException();
    }

    @PreAuthorize("hasAuthority('professor')")
    @Override
    public  List<Map<String, Object>>  getCorrectionsForProfessor( Long homeworkId){
        Optional<Homework> oh = homeworkRepository.findById(homeworkId);
        if(oh.isPresent()){
            Homework h = oh.get();
            if( h.getAssignment().getCourseAssignment().getProfessors().stream()
                    .anyMatch(p->p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))){
                List<PhotoCorrection> corrections = h.getCorrections();
                List<Map<String, Object>> l = new ArrayList<>();
                for( PhotoCorrection c: corrections) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", c.getId());
                    m.put("timestamp", c.getTimestamp());
                    m.put("nameFile", c.getNameFile());
                    m.put("versionId",c.getIdVersionHomework());
                    l.add(m);
                }
                return l;
               // correctionsList.stream().forEach(p->p.setPicByte(decompressZLib(p.getPicByte())));
             //   return correctionsList.stream().map( c -> modelMapper.map(c, PhotoCorrectionDTO.class)).collect(Collectors.toList());

            }else throw  new PermissionDeniedException();
        }else throw new HomeworkNotFoundException();
    }

    @PreAuthorize("hasAuthority('student')")
    @Override
    public List<Map<String, Object>> getCorrectionsForStudent(Long assignmentId){
        Optional<Assignment> oa = assignmentRepository.findById(assignmentId);
        if(oa.isPresent()){
            Assignment a = oa.get();
            String studentAuth =SecurityContextHolder.getContext().getAuthentication().getName();
            if(a.getCourseAssignment().getStudents().stream().anyMatch(s->s.getId().equals(studentAuth))){
                List<PhotoCorrection> corrections =  a.getHomeworks().stream().filter(h->h.getStudent().getId().equals(studentAuth)).findFirst().get().getCorrections();
                List<Map<String, Object>> l = new ArrayList<>();
                for( PhotoCorrection c: corrections) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", c.getId());
                    m.put("timestamp", c.getTimestamp());
                    m.put("nameFile", c.getNameFile());
                    m.put("versionId",c.getIdVersionHomework());
                    l.add(m);
                }
                return l;
               // correctionsList.stream().forEach(p->p.setPicByte(decompressZLib(p.getPicByte())));
               // return correctionsList.stream().map( c -> modelMapper.map(c, PhotoCorrectionDTO.class)).collect(Collectors.toList());
            }else throw new PermissionDeniedException();
        }else throw new AssignmentNotFoundException();
    }

    @PreAuthorize("hasAuthority('professor') || hasAuthority('student')")
    @Override
    public  PhotoCorrectionDTO getCorrectionHM(Long correctionId){

        String auth =  SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<PhotoCorrection> op = photoCorrectionRepository.findById(correctionId);
        if (op.isPresent())
        {
            PhotoCorrection p =op.get();
            Homework h = p.getHomework();
            if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("professor"))) {
                if (!h.getAssignment().getCourseAssignment().getProfessors().stream().anyMatch(pr -> pr.getId().equals(auth)))
                    throw new PermissionDeniedException();
            }else {
                if (!h.getStudent().getId().equals(auth))
                    throw new PermissionDeniedException();
            }
            return modelMapper.map(op.get(), PhotoCorrectionDTO.class);
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











}
