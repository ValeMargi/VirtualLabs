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
import org.springframework.expression.spel.ast.Assign;
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
import java.sql.Time;
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

    /*SERVICE student*/
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

    @PreAuthorize("hasAuthority('professor')")  //hasROLE????
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
        else {
            Course c = course.get();
            Student s = student.get();
            for( Assignment a: c.getAssignments()){
                Homework h = new Homework();
                h.setAssignment(a);
                h.setStatus("NULL");
                h.setStudent(s);
                homeworkRepository.saveAndFlush(h);
            }
            return c.addStudent(s);
        }
    }


    /*Metodo per eliminare student da un corso deleteStudentFromCourse*/
    @PreAuthorize("hasAuthority('professor')")  //hasROLE????
    @Override
    public boolean deleteStudentFromCourse(String studentId, String courseName) {
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
            return courseRepository.getOne(courseName).removeStudent(studentRepository.getOne(studentId));
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


    /*
    DOVREBBE ESSERE INUTILE, RICHIESTO NEL LAB MA NON NEL PROGETTO
    @Override
    public List<Boolean> addAll(List<StudentDTO> student){
        return  student.stream().map(s -> authenticationService.addStudent(s).isPresent()).collect(Collectors.toList());
    }
    */

    @Override
    public List<Boolean> enrollAll(List<String> studentsIds, String courseName){
        return  studentsIds.stream().map( s -> addStudentToCourse(s, courseName)).collect(Collectors.toList());
    }

    /*@PreAuthorize("hasAuthority('professor')") serviva per lab, add and roll
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

     */


    @PreAuthorize("hasAuthority('professor')")
    @Override
    public  List<Boolean> EnrollAllFromCSV(Reader r, String courseName){
        try {
            CsvToBean<String> csvToBean = new CsvToBeanBuilder(r)
                    .withType(StudentDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<String> studentsIds = csvToBean.parse();
           return enrollAll(studentsIds,  courseName);
        }catch (RuntimeException exception){
            throw  new FormatFileNotValidException();
        }
    }


    /*SERVICE CORSO*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public boolean addCourse(CourseDTO course) {
        if ( !courseRepository.findById(course.getName()).isPresent())  {
            Course c = modelMapper.map( course, Course.class);
            String idProfessor= (SecurityContextHolder.getContext().getAuthentication().getName());
            Optional<Professor> op = professorRepository.findById(idProfessor);
            if(op.isPresent()){
                Professor p=op.get();
                c.setProfessor(p);
                courseRepository.saveAndFlush(c);
                return true;
            }else throw new ProfessorNotFoundException();
        }
        return false;
    }

    @PreAuthorize("hasAuthority('professor')")
    @Override
    public ProfessorDTO addProfessorToCourse(String courseId, String professorId) {
        Optional<Course> oc= courseRepository.findById(courseId);
        if ( !oc.isPresent())  {
           throw new CourseNotFoundException();
        }
        Course c = oc.get();
        Optional<Professor> op =professorRepository.findById(professorId);
        if(!op.isPresent()){
            throw new ProfessorNotFoundException();
        }else if(getProfessorsForCourse(courseId).stream()
                                                  .noneMatch(pf ->pf.getId()
                                                  .equals(SecurityContextHolder.getContext().getAuthentication().getName()))){
            throw new PermissionDeniedException();
        }else{
            Professor p = op.get();
            if( !c.getProfessors().contains(p))
            {
                c.setProfessor(p);
                return modelMapper.map(p, ProfessorDTO.class);
            }else throw new ProfessorAlreadyPresentInCourse();

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
                p.removeCourse(c);
                courseRepository.delete(c);
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

        if( !memberIds.contains(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new PermissionDeniedException();

        if( !course.isPresent() || !course.get().isEnabled())
            throw  new CourseNotFoundException();

        List<String> enrolledStudents= getEnrolledStudents(courseId).stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList());

        if(course.get().getTeams().stream().anyMatch(t ->t.getName().equals(name)))
            throw new NameTeamIntoCourseAlreadyPresent();

        if ( !enrolledStudents.containsAll(memberIds))
            throw  new StudentNotEnrolledToCourseExcpetion();

        if( memberIds.stream()
                .map(s->studentRepository.getOne(s))
                .map(Student::getTeams)
                .filter( lt-> !lt.isEmpty())
                .map( lt-> lt.stream().map(Team::getCourse)
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
        if( course.get().getPhotoModelVM()!=null){
            Course c =course.get();
            team.setDiskSpaceLeft(c.getDiskSpace());
            team.setMaxVpcuLeft(c.getMaxVcpu());
            team.setRamLeft(c.getRam());
        }
        teamRepository.save(team);
        memberIds.stream().forEach(s-> team.addStudentIntoTeam(studentRepository.getOne(s)));

        notificationService.notifyTeam(modelMapper.map(team, TeamDTO.class), memberIds);

        return  modelMapper.map(team, TeamDTO.class);
    }

    @PreAuthorize("hasAuthority('student')")
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
        /*Per controllare scadenza elaborati*/
        for(Assignment a: assignmentRepository.findAll()){
            if(a.getExpiration().compareTo(now)<=0){
                a.getHomeworks().stream().map(h-> this.updateStatusHomework(h.getId(),"CONSEGNATO"));
                assignmentRepository.saveAndFlush(a);
            }
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
    public boolean addModelVM(CourseDTO courseDTO, String courseId, PhotoModelVM photoModelVM) {
        Optional<Course> oc = courseRepository.findById(courseId);
        if( oc.isPresent()) {
            Course c = oc.get();
            if (c.getProfessors().stream().anyMatch(p -> p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
                //Controllo per verificare che il professore setta il modello per il corso con courseId per la prima volta
                if (c.getPhotoModelVM() != null) {
                    c.setPhotoModelVM(photoModelVM);
                    c.setMaxVcpu(courseDTO.getMaxVcpu());
                    c.setDiskSpace(courseDTO.getDiskSpace());
                    c.setRam(courseDTO.getRam());
                    c.getTeams().stream().map(t-> {
                        t.setDiskSpaceLeft(c.getDiskSpace());
                        t.setRamLeft(c.getRam());
                        t.setMaxVpcuLeft(c.getMaxVcpu());
                        return t;
                    });
                    photoModelVMRepository.saveAndFlush(photoModelVM);
                    return true;

                } else throw new ModelVMAlreadytPresent();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();

        /*if ( !modelVMRepository.findById(modelVMdto.getId()).isPresent())  {
            ModelVM modelVM = modelMapper.map(modelVMdto, ModelVM.class);
           Course c = courseRepository.getOne(courseId);
           if(c==null)
               throw new CourseNotFoundException();
            modelVM.setScreenshot(image);
            modelVM.setCourse(c);
            modelVMRepository.saveAndFlush(modelVM);
            imageRepository.saveAndFlush(image);
            return true;
        }else throw new ModelVMAlreadytPresent();*/
    }



    /*Ogni gruppo può avere più VM e ogni VM ha l'identificativo del team e tutti i membri
    * del team possono accederci ma solo chi ha creato la VM è owner*/
    /**
     *
     * @param vmdto: contiene tutte le caratteristiche della VM compilate nel form per la creazione di una VM per gruppo
     * @param courseId: identificativo del corso
     * @return
     */
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean addVM(VMDTO vmdto, String courseId, PhotoVMDTO photoVMDTO) {
        if ( !VMRepository.findById(vmdto.getId()).isPresent())  {
            String studentAuth= SecurityContextHolder.getContext().getAuthentication().getName();
            if( getStudentsInTeams(courseId).stream().anyMatch(s-> s.getId().equals(studentAuth))){
                Course c = courseRepository.getOne(courseId);
                if(c==null || !c.isEnabled())
                    throw new CourseNotFoundException();
                if (c.getPhotoModelVM() == null)
                    throw  new ModelVMNotSetted();

                Student s = studentRepository.getOne(studentAuth);
                Team t = s.getTeams().stream().filter(te->te.getCourse().equals(c)).findFirst().get();

                if( vmdto.getDiskSpace() < t.getDiskSpaceLeft() && vmdto.getNumVcpu()< t.getMaxVpcuLeft()
                    && vmdto.getRam() < t.getRamLeft()){
                    VM vm = modelMapper.map(vmdto, VM.class);
                    vm.setCourse(c);
                    PhotoVM photoVM = modelMapper.map(photoVMDTO, PhotoVM.class);
                    vm.setPhotoVM(photoVM);
                    vm.getOwnersVM().add(s);
                    vm.setMembersVM(t.getMembers());
                    vm.setTeam(t);
                    t.setDiskSpaceLeft(t.getDiskSpaceLeft()-vmdto.getDiskSpace());
                    t.setMaxVpcuLeft(t.getMaxVpcuLeft()-vmdto.getNumVcpu());
                    t.setRamLeft(t.getRamLeft()-vmdto.getRam());
                    VMRepository.saveAndFlush(vm);
                    photoVMRepository.saveAndFlush(photoVM);
                   return true;
               }else throw new ResourcesVMNotRespected();
            }else throw new TeamNotFoundException();
        }else throw  new VMduplicated();
    }


    /*Metodo per rendere determinati membri del team owner di una data VM*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean addOwner(String VMid, String courseId, List<String> studentsId) { //CourseId preso dal pathVariable
       Optional<VM> ovm= VMRepository.findById(VMid);
        if (ovm.isPresent() ) {
            VM vm =ovm.get();
            Course c = courseRepository.getOne(courseId);
            if (c == null || !c.isEnabled())
                throw new CourseNotFoundException();
            if(!vm.getCourse().equals(c))
                throw new PermissionDeniedException();
                String studentAuth = SecurityContextHolder.getContext().getAuthentication().getName();
                if (vm.getOwnersVM().stream().anyMatch(s->s.getId().equals(studentAuth))) //lo student autenticato è già owner della VM, può quindi aggiungere altri owner
                {
                    if(vm.getMembersVM().containsAll(studentsId)) //tutti lgi studenti fanno parte del team
                    {
                     if( studentsId.stream().map(s-> vm.addStudentToOwnerList(studentRepository.getOne(s))).anyMatch(r->r.equals(false)))
                        throw new RuntimeException("One or more students are already owner");
                     else{
                         VMRepository.saveAndFlush(vm);
                         return true;
                     }
                    }else throw new StudentNotFoundException();
                }else throw new PermissionDeniedException();
        }else throw new VMNotFound();
    }

     /*Metodo per attivare VM, controllo se l'utente autenticato è un owner della VM*/
        @PreAuthorize("hasAuthority('student')")
        @Override
        public boolean activateVM(String VMid ){ //CourseId preso dal pathVariable
            Optional<VM> vm = VMRepository.findById(VMid);
            if (vm.isPresent()) {
                if (vm.get().getOwnersVM().contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                    vm.get().setStatus("enable");
                    VMRepository.save(vm.get());
                } else throw new PermissionDeniedException();
            } else throw new VMNotFound();
            return true;
        }

    /*Metodo per spegnere VM, controllo se l'utente autenticato è un owner della VM*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean disableVM(String VMid ){ //CourseId preso dal pathVariable
        Optional<VM> vm = VMRepository.findById(VMid);
        if (vm.isPresent()) {
            if (vm.get().getOwnersVM().contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                vm.get().setStatus("disable");
                VMRepository.save(vm.get());
            } else throw new PermissionDeniedException();
        } else throw new VMNotFound();
        return true;
    }
    /*Metodo per cancellare VM, controllo se l'utente autenticato è un owner della VM*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean removeVM(String VMid){ //CourseId preso dal pathVariable
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm= ovm.get();
            if (vm.getOwnersVM().contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                vm.getTeam().setRamLeft(vm.getRam()+vm.getTeam().getRamLeft());
                vm.getTeam().setMaxVpcuLeft(vm.getNumVcpu()+vm.getTeam().getMaxVpcuLeft());
                vm.getTeam().setDiskSpaceLeft(vm.getDiskSpace()+vm.getTeam().getDiskSpaceLeft());
                vm.getCourse().removeVM(vm);
                vm.getTeam().removeVM(vm);
                VMRepository.delete(vm);
            } else throw new PermissionDeniedException();
        } else throw new VMNotFound();
        return true;
    }

    /*Visualizzare VM accessibili allo student in tab corso*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public List<VMDTO> allVMforStudent( String courseId) { //CourseId preso dal pathVariable
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Student s = studentRepository.getOne(student);
        List<Team> teams = s.getTeams().stream().filter(c->c.getCourse().equals(courseId)).collect(Collectors.toList());
        if(!teams.isEmpty()){
         //   List<PhotoCorrection> correctionsList = h.getCorrections();
        //    correctionsList.stream().forEach(p->p.setPicByte(decompressZLib(p.getPicByte())));
            List<VM> vmsList = teams.get(0).getVms();
            //vmsList.stream().forEach(v->v.setPicByte(decompressZLib(v.getPicByte())));
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
            if(c.getProfessors().contains(SecurityContextHolder.getContext().getAuthentication().getName())){
               List<VM> vmsList= c.getVms();
              //  vmsList.stream().forEach(v->v.setPicByte(decompressZLib(v.getPicByte())));
                return vmsList.stream().map( v -> modelMapper.map(v, VMDTO.class)).collect(Collectors.toList());
               // return c.getVms();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
    }

    /*Visualizzare VM con un certo VMid  allo student in tab corso*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public PhotoVMDTO getVMforStudent( String courseId, String VMid) {
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Student s = studentRepository.getOne(student);
        List<Team> teams = s.getTeams().stream().filter(c->c.getCourse().equals(courseId)).collect(Collectors.toList());
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
           }else throw new VMNotFound();
        }else throw new TeamNotFoundException();
    }

    /*student owner modifia risorse associate a VM se è spenta e se non superano i limiti imposti dal gruppo*/


    /*Per vedere chi è owner*/
    @PreAuthorize("hasAuthority('student')")
    @Override
    public boolean isOwner(  String VMid) { //CourseId preso dal pathVariable
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if(os.isPresent()){
            Student s=os.get();
            Optional<VM> ovm= VMRepository.findById(VMid);
            if(ovm.isPresent()){
                VM vm= ovm.get();
                if(vm.getOwnersVM().contains(s))
                    return true;
                else throw new PermissionDeniedException();
            }else throw new VMNotFound();
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
                if( !c.getAssignments().stream().anyMatch(a->a.getId().equals(assignmentDTO.getId())) &&
                    !c.getAssignments().stream().anyMatch(a->a.getNameAssignment().equals(assignmentDTO.getAssignmentName()))) {
                    Assignment assignment= modelMapper.map(assignmentDTO, Assignment.class);
                    PhotoAssignment photoAssignment = modelMapper.map(photoAssignmentDTO, PhotoAssignment.class);
                    assignment.setCourseAssignment(c);
                    assignment.setPhotoAssignment(photoAssignment);
                    for(Student s: c.getStudents()){
                        Homework h = new Homework();
                        h.setStatus("NULL");
                        h.setAssignment(assignment);
                        h.setStudentForHomework(s);
                        homeworkRepository.saveAndFlush(h);
                    }
                    assignmentRepository.saveAndFlush(assignment);
                    photoAssignmentRepository.saveAndFlush(photoAssignment);
                }else throw new AssignmentAlreadyExist();
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
            }throw new CourseNotFoundException();
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
                        PhotoAssignment pa = a.getPhotoAssignment();
                        Optional<PhotoAssignment> photoAssignment = photoAssignmentRepository.findById(pa.getId());
                        if (photoAssignment.isPresent()) {
                            pa.setPicByte(decompressZLib(pa.getPicByte()));
                            return modelMapper.map(pa, PhotoAssignmentDTO.class);
                        } else throw new PhotoAssignmentNotFound();
                    } else throw new PermissionDeniedException();
                } else throw new AssignmentNotFound();
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
                } else throw new AssignmentNotFound();
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
                    } else throw new HomeworkIsPermanent();
                } else throw new PermissionDeniedException();
            } else throw new HomeworkNotFound();
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
                }else throw new HomeworkNotFound();
        }else throw new PermissionDeniedException();
    }


    @PreAuthorize("hasAuthority('professor')")
    @Override
    public  List<Homework> allHomework(String courseName, String assignmentId){ //AGGIUNTA 28/07
        Optional<Course> oc= courseRepository.findById(courseName);
        if(oc.isPresent()){
            Course c = oc.get();
            if(c.getProfessors().stream().anyMatch(p->p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
               Assignment assignment = c.getAssignments().stream().filter(a->a.getId().equals(assignmentId)).findFirst().get();
                if(assignment!=null ) {
                    return assignment.getHomeworks();
                }else throw new AssignmentNotFound();
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
        }else throw new HomeworkNotFound();
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
        }else throw new AssignmentNotFound();
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
        }else throw new PhotoVersionHMNotFound();

    }



    /*Metodo per consegnare correzione*/
    @PreAuthorize("hasAuthority('professor')")
    @Override
    public boolean uploadCorrection(Long homeworkId, Long versionHMid, PhotoCorrectionDTO photoCorrectionDTO,Boolean permanent) {
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
                    photoCorrectionRepository.saveAndFlush(photoCorrection);
                    return true;
                }else throw new HomeworkVersionIdNotFound();

            }else throw  new PermissionDeniedException();
        }throw new HomeworkNotFound();
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
                    l.add(m);
                }
                return l;
               // correctionsList.stream().forEach(p->p.setPicByte(decompressZLib(p.getPicByte())));
             //   return correctionsList.stream().map( c -> modelMapper.map(c, PhotoCorrectionDTO.class)).collect(Collectors.toList());

            }else throw  new PermissionDeniedException();
        }else throw new HomeworkNotFound();
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
                    l.add(m);
                }
                return l;
               // correctionsList.stream().forEach(p->p.setPicByte(decompressZLib(p.getPicByte())));
               // return correctionsList.stream().map( c -> modelMapper.map(c, PhotoCorrectionDTO.class)).collect(Collectors.toList());
            }else throw new PermissionDeniedException();
        }else throw new AssignmentNotFound();
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
        }else throw new PhotoCorrectionNotFound();

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
