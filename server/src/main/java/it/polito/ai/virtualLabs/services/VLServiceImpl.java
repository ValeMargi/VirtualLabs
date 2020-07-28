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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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
    ModelVMRepository modelVMRepository;
    @Autowired
    VMRepository VMRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    HomeworkRepository homeworkRepository;

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


    /*Metodo per eliminare studente da un corso deleteStudentFromCourse*/
    @PreAuthorize("hasAuthority('docente')")  //hasROLE????
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

    /*@PreAuthorize("hasAuthority('docente')") serviva per lab, add and roll
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


    @PreAuthorize("hasAuthority('docente')")
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
    public boolean addProfessorToCourse(String courseId, ProfessorDTO professor) {
        Optional<Course> oc= courseRepository.findById(courseId);
        if ( !oc.isPresent())  {
           throw new CourseNotFoundException();
        }
        Course c = oc.get();
        Optional<Professor> p =professorRepository.findById(professor.getId());
        if(!p.isPresent()){
            throw new ProfessorNotFoundException();
        }else if(!getProfessorsForCourse(courseId).stream()
                .anyMatch(pf ->pf.getId()
                        .equals(SecurityContextHolder.getContext().getAuthentication().getName()))){
            throw new PermissionDeniedException();
        }else{
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
    public List<CourseDTO> getCoursesForStudent(String studentId){
        try{
            Student s = studentRepository.getOne(studentId);
            return s.getCourses().stream().map(c->modelMapper.map(c, CourseDTO.class)).collect(Collectors.toList());
        }catch (EntityNotFoundException enfe){
            throw new StudentNotFoundException();
        }
    }
    @PreAuthorize("hasAuthority('docente')")
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
    @PreAuthorize("hasAuthority('docente')")
    @Override
    public boolean removeCourse(String courseId) {
        Optional<Course> oc= courseRepository.findById(courseId);
        if (oc.isPresent())  {
            Course c = oc.get();
            String idProfessor= (SecurityContextHolder.getContext().getAuthentication().getName());
            if(!getProfessorsForCourse(c.getName()).stream()
                    .anyMatch(pf ->pf.getId().equals(idProfessor)))
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
    @PreAuthorize("hasAuthority('docente')")
    @Override
    public boolean modifyCourse(CourseDTO course) {
        Optional<Course> oc=courseRepository.findById(course.getName());
        if ( oc.isPresent())  {
            Course c = oc.get();
            String idProfessor= (SecurityContextHolder.getContext().getAuthentication().getName());
            if(!getProfessorsForCourse(c.getName()).stream()
                    .anyMatch(pf ->pf.getId().equals(idProfessor)))
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
        /*Per controllare scadenza elaborati*/
        for(Assignment a: assignmentRepository.findAll()){
            if(a.getExpiration().compareTo(now)<=0){
                a.getHomeworks().stream().map(h-> this.updateStatusHomework(h.getId(),"CONSEGNATO"));
                assignmentRepository.saveAndFlush(a);
            }
        }
    }


    /*SERVICE MODELLO VM*/
    @PreAuthorize("hasAuthority('docente')")
    @Override
    public boolean addModelVM(ModelVMDTO modelVMdto, String courseId, Image image) { //CourseId preso dal pathVariable
        if ( !modelVMRepository.findById(modelVMdto.getId()).isPresent())  {
            ModelVM modelVM = modelMapper.map(modelVMdto, ModelVM.class);
           Course c = courseRepository.getOne(courseId);
           if(c==null)
               throw new CourseNotFoundException();
            modelVM.setScreenshot(image);
            modelVM.setCourse(c);
            modelVMRepository.saveAndFlush(modelVM);
            imageRepository.saveAndFlush(image);
            return true;
        }else throw new ModelVMAlreadytPresent();
    }



    /*Ogni gruppo può avere più VM e ogni VM ha l'identificativo del team e tutti i membri
    * del team possono accederci ma solo chi ha creato la VM è owner*/

   //DA COMPLETARE
   /*Metodo per aggiungere VM, prima della creazione verificare
    che tali risorse siano disponibili altrimenti errore
    Controllare se lo studente fa parte di un Team prima di creare VM*/

    @PreAuthorize("hasAuthority('studente')")
    @Override
    public boolean addVM(VMDTO vmdto, String courseId, Image image) { //CourseId preso dal pathVariable
        if ( !VMRepository.findById(vmdto.getId()).isPresent())  {
            String studentAuth= SecurityContextHolder.getContext().getAuthentication().getName();
            if( getStudentsInTeams(courseId).stream().anyMatch(s-> s.getId().equals(studentAuth))){
                Course c = courseRepository.getOne(courseId);
                if(c==null || !c.isEnabled())
                    throw new CourseNotFoundException();
                ModelVM modelVM = c.getModelVM();
                if( modelVM==null)
                    throw new ModelVMNotSetted();
               if( vmdto.getDiskSpace() < modelVM.getDiskSpace() && vmdto.getNumVcpu()< modelVM.getMaxVcpu()
                    && vmdto.getRam()<modelVM.getRam()){
                   VM vm = modelMapper.map(vmdto, VM.class);
                   vm.setModelVM(c.getModelVM());
                   vm.setScreenshot(image);
                   Student s = studentRepository.getOne(studentAuth);
                   vm.getOwnersVM().add(s);
                   Team t = s.getTeams().stream().filter(te->te.getCourse().equals(c)).findFirst().get();
                   vm.setMembersVM(t.getMembers());
                   vm.setTeam(t);
                   modelVM.setCourse(c);
                   modelVMRepository.saveAndFlush(modelVM);
                   VMRepository.saveAndFlush(vm);
                   imageRepository.saveAndFlush(image);
                   return true;
               }else throw new ResourcesVMNotRespected();

            }else throw new TeamNotFoundException();
        }else throw  new VMduplicated();
    }


    /*Metodo per rendere determinati membri del team owner di una data VM*/
    @PreAuthorize("hasAuthority('studente')")
    @Override
    public boolean addOwner(String VMid, String courseId, List<String> studentsId) { //CourseId preso dal pathVariable
       Optional<VM> ovm= VMRepository.findById(VMid);
        if (ovm.isPresent() ) {
            VM vm =ovm.get();
            Course c = courseRepository.getOne(courseId);
            if (c == null || !c.isEnabled())
                throw new CourseNotFoundException();
            ModelVM modelVM = c.getModelVM();
            if (modelVM == null)
                throw new ModelVMNotSetted();
            if( vm.getModelVM().equals(modelVM)) {
                String studentAuth = SecurityContextHolder.getContext().getAuthentication().getName();
                if (getStudentsInTeams(courseId).stream().anyMatch(s -> s.getId().equals(studentAuth))) {
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
                }else throw new ModelVMNotSetted();
        }else throw new VMNotFound();
    }

     /*Metodo per attivare VM, controllo se l'utente autenticato è un owner della VM*/
        @PreAuthorize("hasAuthority('studente')")
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
    @PreAuthorize("hasAuthority('studente')")
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
    @PreAuthorize("hasAuthority('studente')")
    @Override
    public boolean removeVM(String VMid){ //CourseId preso dal pathVariable
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm= ovm.get();
            if (vm.getOwnersVM().contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
               vm.getModelVM().removeVM(vm);
               vm.getTeam().removeVM(vm);
               VMRepository.delete(vm);
               /*Aggiungere eventualemnte gestione risorse*/

            } else throw new PermissionDeniedException();
        } else throw new VMNotFound();
        return true;
    }
    /*Visualizzare VM accessibili allo studente in tab corso*/
    @PreAuthorize("hasAuthority('studente')")
    @Override
    public List<VM> allVMforStudent( String courseId) { //CourseId preso dal pathVariable
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Student s = studentRepository.getOne(student);
        List<Team> teams = s.getTeams().stream().filter(c->c.getCourse().equals(courseId)).collect(Collectors.toList());
        if(!teams.isEmpty()){
            return  teams.get(0).getVms();
        }else throw new TeamNotFoundException();

    }

    /*Visualizzare VM accessibili al docente in tab corso*/
    @PreAuthorize("hasAuthority('docente')")
    @Override
    public List<VM> allVMforCourse( String courseId) { //CourseId preso dal pathVariable
        Optional<Course> oc= courseRepository.findById(courseId);
        if( oc.isPresent()){
            Course c= oc.get();
            if(c.getProfessors().contains(SecurityContextHolder.getContext().getAuthentication().getName())){
                return c.getModelVM().getVms();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
    }


    /*Studente owner modifia risorse associate a VM se è spenta e se non superano i limiti imposti dal gruppo*/


    /*Per vedere chi è owner*/
    @PreAuthorize("hasAuthority('studente')")
    @Override
    public boolean isOwner(  String VMid) { //CourseId preso dal pathVariable
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if(os.isPresent()){
            Student s=os.get();
            Optional<VM> ovm= VMRepository.findById(VMid);
            if(ovm.isPresent()){
                VM vm= ovm.get();
                if(vm.getOwnersVM().contains(student))
                    return true;
                else throw new PermissionDeniedException();
            }else throw new VMNotFound();
        }else throw new StudentNotFoundException();
    }

    /*SERVICE CONSEGNA*/
    @PreAuthorize("hasAuthority('docente')")
    @Override
    public boolean addAssignment( AssignmentDTO assignmentDTO, Image image, String courseId) { //CourseId preso dal pathVariable
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Course> oc = courseRepository.findById(courseId);
        if(oc.isPresent()){
            Course c = oc.get();
            if(c.getProfessors().stream().anyMatch(p->p.getId().equals(professor))){
                if( !c.getAssignments().stream().anyMatch(a->a.getId().equals(assignmentDTO.getId()))) {
                    Assignment assignment= modelMapper.map(assignmentDTO, Assignment.class);
                    assignment.setCourseAssignment(c);
                    assignment.setImageAssignment(image);
                    for(Student s: c.getStudents()){
                        Homework h=new Homework();
                        h.setStatus("NULL");
                        h.setAssignment(assignment);
                        h.setStudentForHomework(s);
                        homeworkRepository.saveAndFlush(h);
                    }
                    assignmentRepository.saveAndFlush(assignment);
                    imageRepository.saveAndFlush(image);
                }else throw new AssignmentAlreadyExist();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
        return true;
    }

    /*Metodo per ritornare le consegne di un dato corso*/
    @PreAuthorize("hasAuthority('docente')")
    @Override
    public List<Assignment> allAssignment(  String courseId) { //CourseId preso dal pathVariable
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Professor> op = professorRepository.findById(professor);
        if(op.isPresent()){
            Professor p= op.get();
            Optional<Course> c = p.getCourses().stream().filter(co->co.getName().equals(courseId)).findFirst();
            if(c.isPresent()){
                return c.get().getAssignments();
            }throw new CourseNotFoundException();
        }else throw new ProfessorNotFoundException();
    }


    /*SERVICE ELABORATI*/
    @PreAuthorize("hasAuthority('studente')")
    @Override
    public boolean addHomework (String homeworkId, Image image) { //CourseId preso dal pathVariable
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
                        h.setImageHomework(image);
                        homeworkRepository.saveAndFlush(h);
                        imageRepository.saveAndFlush(image);
                        return true;
                    } else throw new HomeworkIsPermanent();
                } else throw new PermissionDeniedException();
            } else throw new HomeworkNotFound();
        } else throw new PermissionDeniedException();
    }

    //@PreAuthorize("hasAuthority('studente')")
    @Override
    public boolean updateStatusHomework( String homeworkId, String status) {
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

    /*Metodo per consegnare elaborato*/
    @PreAuthorize("hasAuthority('studente')")
    @Override
    public boolean uploadHomework( ImageDTO imageDTO,  String homeworkId, String courseId) {
        String student = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if (os.isPresent()) {
            Student s = os.get();
            Optional<Homework> oh = homeworkRepository.findById(homeworkId);
            if (oh.isPresent()) {
                Homework h = oh.get();
                if (h.getStudent().equals(student)) {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    if (h.getPermanent().equals(false) && h.getAssignment().getExpiration().compareTo(now) > 0) {
                        Image image = modelMapper.map(imageDTO, Image.class);
                        h.setStatus("CONSEGNATO");
                        h.setImageHomework(image);
                        //gestire immagini
                        //imageRep
                        homeworkRepository.saveAndFlush(h);
                        return true;
                    } else throw new ModificationDenied();
                } else throw new StudentNotFoundException();
            } else throw new HomeworkNotFound();
        }else throw new StudentNotFoundException();
    }

    @PreAuthorize("hasAuthority('docente')")
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

    @PreAuthorize("hasAuthority('docente')")
    @Override
    public boolean uploadCorrection( ImageDTO imageDTO,  String homeworkId, String courseId, Boolean permanent) {
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Professor> op = professorRepository.findById(professor);
        if(op.isPresent()){
            Professor p= op.get();
                Optional<Homework> oh= homeworkRepository.findById(homeworkId);
                if(oh.isPresent()){
                    Homework h= oh.get();
                    if(h.getAssignment().getCourseAssignment().getProfessors().contains(professor))
                    {
                        Image image = modelMapper.map(imageDTO, Image.class);
                        h.setStatus("RIVISTO");
                        h.setImageHomework(image);
                        h.setPermanent(permanent);
                        //gestire immagi
                        homeworkRepository.saveAndFlush(h);
                        return true;
                    }throw new PermissionDeniedException();
            }else throw new HomeworkNotFound();
        }else throw  new ProfessorNotFoundException();
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
