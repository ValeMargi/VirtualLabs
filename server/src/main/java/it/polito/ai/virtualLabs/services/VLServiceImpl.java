package it.polito.ai.virtualLabs.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.repositories.*;
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
import java.io.Reader;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@Service
@Transactional
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
    ModelVMRepository modelVMRepository;
    @Autowired
    VMRepository VMRepository;

    /*SERVICE STUDENTE*/
    @Override
    public Optional<StudentDTO> getStudent(String studentId) {
        return studentRepository.findById(studentId)
                .map(s -> modelMapper.map(s, StudentDTO.class));
    }


    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map( s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

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

    @PreAuthorize("hasAuthority('docente')")  //hasROLE????
    @Override
    public boolean addStudentToCourse(String studentId, String courseName) {
        Optional<Student> student = studentRepository.findById(studentId);
        Optional<Course> course = courseRepository.findById(courseName);


        if( ! student.isPresent()){
            throw new StudentNotFoundException();
        }else if(!course.isPresent() || !course.get().isEnabled() ){
            throw new CourseNotFoundException();
        }else if(!getProfessorsForCourse(courseName).stream()
                                                    .anyMatch(p ->p.getId()
                                                    .equals(SecurityContextHolder.getContext().getAuthentication().getName())))
            throw new PermissionDeniedException();
        else
            return courseRepository.getOne(courseName).addStudent(studentRepository.getOne(studentId));
    }


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
    public List<Boolean> addAll(List<StudentDTO> student){
        return  student.stream().map(s -> authenticationService.addStudent(s).isPresent()).collect(Collectors.toList());
    }

    @Override
    public List<Boolean> enrollAll(List<String> studentsIds, String courseName){
        return  studentsIds.stream().map( s -> addStudentToCourse(s, courseName)).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('docente')")
    @Override
    public  List<Boolean> addAndEnroll(Reader r, String courseName){
        try {
            CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder(r)
                    .withType(StudentDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<StudentDTO> students = csvToBean.parse();
            addAll(students);
            return enrollAll(students.stream().map(s -> s.getId()).collect(Collectors.toList()),  courseName);
        }catch (RuntimeException exception){
            throw  new FormatFileNotValidException();
        }
    }

    /*SERVICE CORSO*/
    @PreAuthorize("hasAuthority('docente')")
    @Override
    public boolean addCourse(CourseDTO course) {
        if ( !courseRepository.findById(course.getName()).isPresent())  {
            Course c = modelMapper.map( course, Course.class);

            String idProfessor= (SecurityContextHolder.getContext().getAuthentication().getName());
            Professor p = professorRepository.findById(idProfessor).get();
            c.setProfessor(p);
            courseRepository.save(c);
            courseRepository.flush();
            return true;
        }
        return false;
    }

    @PreAuthorize("hasAuthority('docente')")
    @Override
    public boolean addProfessorToCourse(CourseDTO course, ProfessorDTO professor) {
        if ( !courseRepository.findById(course.getName()).isPresent())  {
           throw new CourseNotFoundException();
        }
        Optional<Professor> p =professorRepository.findById(professor.getId());
        if(!p.isPresent()){
            throw new ProfessorNotFoundException();
        }else if(!getProfessorsForCourse(course.getName()).stream()
                .anyMatch(pf ->pf.getId()
                        .equals(SecurityContextHolder.getContext().getAuthentication().getName()))){
            throw new PermissionDeniedException();
        }else{
            Course c = modelMapper.map( course, Course.class);
            c.setProfessor(p.get());
            return  true;
    }
    }


    @Override
    public Optional<CourseDTO> getCourse(String name) {
        return courseRepository.findById(name)
                .map(c -> modelMapper.map(c, CourseDTO.class));
    }


    @Override
    public List<CourseDTO> getAllCourses() {
        courseRepository.flush();
        return courseRepository.findAll()
                .stream()
                .map( p -> modelMapper.map(p, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('docente')")
    @Override
    public void enableCourse(String courseName) {
        try{
            Course c = courseRepository.getOne(courseName);
            if(!getProfessorsForCourse(c.getName()).stream()
                                                   .anyMatch(pf ->pf.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName())))
                throw new PermissionDeniedException();
            if(!c.isEnabled())
                c.setEnabled(true);
            else
                throw  new CourseAlreadyEnabledException();
        }catch(EntityNotFoundException enfe){
            throw new CourseNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('docente')")
    @Override
    public void disableCourse(String courseName) {
        try{
            Course c = courseRepository.getOne(courseName);
            if(!getProfessorsForCourse(c.getName()).stream()
                    .anyMatch(pf ->pf.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName())))
                throw new PermissionDeniedException();
            if(c.isEnabled())
                c.setEnabled(false);
            else
                throw new CourseAlreadyEnabledException();
        }catch(EntityNotFoundException enfe){
            throw new CourseNotFoundException();
        }
    }


    @PreAuthorize("hasAuthority('studente')")
    @Override
    public List<CourseDTO> getCourses(String studentId){
        try{
            Student s = studentRepository.getOne(studentId);
            return s.getCourses().stream().map(c->modelMapper.map(c, CourseDTO.class)).collect(Collectors.toList());

        }catch (EntityNotFoundException enfe){
            throw new StudentNotFoundException();
        }
    }

    /*---> SERVICE GRUPPO*/
    @PreAuthorize("hasAuthority('studente')")
    @Override
    public List<TeamDTO> getTeamsForStudent(String studentId){
        try {
            Student s = studentRepository.getOne(studentId);
            return s.getTeams().stream().map(t -> modelMapper.map(t, TeamDTO.class)).collect(Collectors.toList());
        }catch(EntityNotFoundException enfe){
            throw new StudentNotFoundException();
        }
    }

    /*@PreAuthorize("hasAuthority('studente')")
    @Override
    public List<Team> getTeamsForStudentBis(String studentId){
        try {
            Student s = studentRepository.getOne(studentId);
            return s.getTeams().stream().collect(Collectors.toList());
        }catch(EntityNotFoundException enfe){
            throw new StudentNotFoundException();
        }
    }*/

    @Override
    public List<StudentDTO> getMembers(Long TeamId) {
        try {
            Team t = teamRepository.getOne(TeamId);
            return t.getMembers().stream().map(s -> modelMapper.map(s, StudentDTO.class)).collect(Collectors.toList());
        }catch(EntityNotFoundException enfe){
            throw new TeamNotFoundException();
        }

    }

    @PreAuthorize("hasAuthority('studente')")
    @Override
    public  TeamDTO proposeTeam(String courseId, String name, List<String> memberIds){
        Optional<Course> course = courseRepository.findById(courseId);

        if( !memberIds.contains(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new PermissionDeniedException();

        if( !course.isPresent() || !course.get().isEnabled())
            throw  new CourseNotFoundException();

        List<String> enrolledStudents= getEnrolledStudents(courseId).stream()
                .map(s->s.getId())
                .collect(Collectors.toList());

        if(course.get().getTeams().stream().anyMatch(t ->t.getName().equals(name)))
            throw new NameTeamIntoCourseAlreadyPresent();

        if ( !enrolledStudents.containsAll(memberIds))
            throw  new StudentNotEnrolledToCourseExcpetion();

        if( memberIds.stream()
               /* .map( st-> getTeamsForStudentBis(st))
                .filter( c-> !c.isEmpty())
                .map( st-> st.stream()
                        .map(t-> t.getCourse())
                        .anyMatch(c-> c.getName().equals(courseId)))
                .collect(Collectors.toList()).contains(true))*/
                .map(s->studentRepository.getOne(s))
                .map( st-> st.getTeams())
                .filter( lt-> !lt.isEmpty())
                .map( lt-> lt.stream().map(t-> t.getCourse())
                        .anyMatch(c-> c.getName().equals(courseId))).collect(Collectors.toList()).contains(true))
            throw  new StudentAlreadyInTeamExcpetion();

        if ( memberIds.size()<getCourse(courseId).get().getMin() || memberIds.size()> getCourse(courseId).get().getMax())
            throw  new CardinalityNotAccetableException();

        if( memberIds.stream().distinct().count() != memberIds.size())
            throw  new StudentDuplicateException();

        Team team = new Team();
        team.setName(name);
        team.setCourse(course.get());
        team.setStatus(0);
        teamRepository.save(team);
        memberIds.stream().forEach(s-> team.addStudentIntoTeam(studentRepository.getOne(s)));

        notificationService.notifyTeam(modelMapper.map(team, TeamDTO.class), memberIds);

        return  modelMapper.map(team, TeamDTO.class);
    }

    @PreAuthorize("hasAuthority('studente')")
    @Override
    public List<TeamDTO> getTeamForCourse(String courseName){
        try{
            Course course =courseRepository.getOne(courseName);
            return  course.getTeams().stream().map(t-> modelMapper.map(t, TeamDTO.class)).collect(Collectors.toList());
        }catch(EntityNotFoundException enfe){
            throw new CourseNotFoundException();
        }

    }

    @PreAuthorize("hasAuthority('studente')")
    @Override
    public List<StudentDTO> getStudentsInTeams(String courseName){
        if( courseRepository.findById(courseName).isPresent())
            return courseRepository.getStudentsTeams(courseName).stream()
                    .map(s->modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException();
    }

    @PreAuthorize("hasAuthority('studente')")
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
                teamRepository.findById(id).get().setStatus(1);
            }
        }catch(EntityNotFoundException enf){
            throw  new TeamNotFoundException();
        }
    }

    @Override
    public void evictTeam(Long id){
        try{
            Team t = teamRepository.getOne(id);
            teamRepository.delete(t);
        }catch(EntityNotFoundException enf){
            throw  new TeamNotFoundException();
        }
    }

    /*To check if the tokens contained in the repository have expired,
     if so, they are removed from the repository and the corresponding
     Team in the repository Team is removed.*/
    @Scheduled(initialDelay = 1000, fixedRate = 20000)
    public void run(){
        Timestamp now = new Timestamp(System.currentTimeMillis());

        for(Token token: tokenRepository.findAll() )
        {
            if( token.getExpiryDate().compareTo(now)<0){
                tokenRepository.deleteFromTokenDBexpired(token.getTeamId());
                if( teamRepository.findById(token.getTeamId()).isPresent())
                    evictTeam(token.getTeamId());
            }
        }
    }


    /*SERVICE MODELLO VM*/
    @PreAuthorize("hasAuthority('docente')")
    @Override
    public boolean addModelVM(ModelVMDTO modelVMdto, String courseId) { //CourseId preso dal pathVariable
        if ( !modelVMRepository.findById(modelVMdto.getId()).isPresent())  {
            ModelVM modelVM = modelMapper.map(modelVMdto, ModelVM.class);
           Course c = courseRepository.getOne(courseId);
           if(c==null)
               throw new CourseNotFoundException();
            modelVM.setCourse(c);
            modelVMRepository.save(modelVM);
            modelVMRepository.flush();
            return true;
        }
        return false;
    }


   /*
   DA COMPLETARE
   @PreAuthorize("hasAuthority('studente')")
    @Override
    public boolean addVM(VMDTO vmdto, String courseId) { //CourseId preso dal pathVariable
        if ( !VMRepository.findById(vmdto.getId()).isPresent())  {
            VM vm = modelMapper.map(vmdto, VM.class);
            Course c = courseRepository.getOne(courseId);
            if(c==null || !c.isEnabled())
                throw new CourseNotFoundException();
            if( c.getModelVM()==null)
                throw new ModelVMNotSetted();
            vm.setModelVM(c.getModelVM());
            //setModelVM in VM implementazione DA FAREEE

            modelVM.setCourse(c);
            modelVMRepository.save(modelVM);
            modelVMRepository.flush();
            return true;
        }
        return false;
    }
*/










}
