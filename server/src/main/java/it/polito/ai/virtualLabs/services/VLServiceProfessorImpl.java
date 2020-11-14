package it.polito.ai.virtualLabs.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.Reader;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Transactional
@PreAuthorize("hasAuthority('professor')")
public class VLServiceProfessorImpl implements VLServiceProfessor {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ProfessorRepository professorRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    it.polito.ai.virtualLabs.repositories.VMRepository VMRepository;
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
    VLService vlService;
    @Autowired
    VLServiceProfessor vlServiceProfessor;

    /**
     * Metodo per ritornare una lista di mappe con le informazioni degli iscritti iscritti
     * al corso con nome pari a courseName e con i relativi team se presenti
     */
      public List<Map<String, Object>> getEnrolledStudentsAllInfo(String courseName){
        List<Map<String, Object>> l = new ArrayList<>();

        List<String> students = vlService.getEnrolledStudents(courseName).stream().map(s->s.getId()).collect(Collectors.toList());
        for(Student s: studentRepository.findAllById(students)){
            Map<String, Object> map = new HashMap<>();
            Optional<Team> ot = s.getTeams().stream().filter(t->t.getCourse().getName().equals(courseName)).findFirst();
            map.put("student", modelMapper.map(s, StudentDTO.class));
            if(ot.isPresent()){
                map.put("teamName", ot.get().getName());
            }else{
                map.put("teamName", "/");
            }
            l.add(map);
        }
        return l;
    }

    /**
     * Metodo per aggiungere uno studente al corso
     */
    @Override
    public boolean addStudentToCourse(String studentId, String courseName) {
        Optional<Student> student = studentRepository.findById(studentId);
        Optional<Course> course = courseRepository.findById(courseName);

        if(!course.isPresent() ){
            throw new CourseNotFoundException();
        }else if(!course.get().isEnabled()) {
            throw new CourseDisabledException();
        }else if( ! student.isPresent()){
            throw new StudentNotFoundException();
        }else if(vlService.getProfessorsForCourse(courseName).stream()
                .noneMatch(p ->p.getId()
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
                if(a.getExpiration().compareTo(Timestamp.from(Instant.now()).toString())<0){
                    h.setPermanent(true);
                    h.setGrade("0");
                }else {
                    h.setPermanent(false);
                    h.setGrade("-1");
                }
                h.setTimestamp("/");
                h.setStudent(s);
                homeworkRepository.saveAndFlush(h);
            }
            return true;
        }
    }

    /**
     * Metodo per aggiungere una lista di studenti al corso
     */
    @Override
    public List<Boolean> enrollAll(List<String> studentsIds, String courseName){
        return  studentsIds.stream().map( s -> vlServiceProfessor.addStudentToCourse(s, courseName)).collect(Collectors.toList());
    }

    /**
     * Metodo per eliminare una lista di studenti da un corso
     */
    @Override
    public List<StudentDTO> deleteStudentsFromCourse(List<String> studentsIds, String courseName){
        Optional<Course> course = courseRepository.findById(courseName);
        if(!course.isPresent() ){
            throw new CourseNotFoundException();
        }else if(!course.get().isEnabled()){
            throw new CourseDisabledException();
        }else if(vlService.getProfessorsForCourse(courseName).stream()
                .noneMatch(p ->p.getId()
                        .equals(SecurityContextHolder.getContext().getAuthentication().getName())))
            throw new PermissionDeniedException();
        else{
            List<Student> ret = new ArrayList<>();
            for(String s : studentsIds){
                Optional<Student> so = studentRepository.findById(s);
                if( ! so.isPresent()){
                    throw new StudentNotFoundException();
                }
                Student student = so.get();

                if (course.get().removeStudent(student)) {
                    Optional<Team> ot = student.getTeams().stream().filter(t -> t.getCourse().getName().equals(courseName)).findAny();
                    if (ot.isPresent()) {
                        // rimuovo VM, homework, corrections, membro dal team
                        Team team = ot.get();
                        if (team.getStatus().equals("active")) {
                            List<VM> VMstudent = student.getStudentsVM().stream()
                                    .filter(v -> v.getCourse().getName().equals(courseName))
                                    .collect(Collectors.toList());
                            if (team.getMembers().size() == 1) {
                                //lo studente è l'unico membro rimasto nel team -> cancello VM, photoVM e team
                                for (VM vm : VMstudent) {
                                    photoVMRepository.delete(vm.getPhotoVM());
                                    course.get().removeVM(vm);
                                    team.removeVM(vm);
                                    VMRepository.delete(vm);
                                }
                                course.get().removeTeam(team);
                                student.removeTeamForStudent(team);
                                teamRepository.delete(team);
                            } else {
                                List<VM> vmOwner = VMstudent.stream().filter(vm -> vm.getOwnersVM().contains(student)).collect(Collectors.toList());
                                vmOwner.forEach(vm -> {
                                    vm.getMembersVM().forEach(vm::addStudentToOwnerList);
                                    vm.removeStudentToOwnerList(student);
                                    vm.removeStudentToMemberList(student);
                                });
                                student.removeTeamForStudent(team);
                            }
                            List<Homework> homeworkStudent = student.getHomeworks().stream().filter(h -> h.getAssignment().getCourseAssignment().equals(course.get())).collect(Collectors.toList());
                            for (Homework h : homeworkStudent) {
                                h.getCorrections().forEach(c -> photoCorrectionRepository.delete(c));
                                h.getVersions().forEach(v -> photoVersionHMRepository.delete(v));
                            }
                            homeworkRepository.deleteAll(homeworkStudent);
                        } else {
                            // rimuovo token delle proposal
                            List<Team> teamsStudent = teamRepository.findAllById(tokenRepository.findAllByStudent(student).stream().map(Token::getTeamId).collect(Collectors.toList()));
                            for (Team t : teamsStudent) {
                                tokenRepository.deleteFromTokenByTeamId(t.getId());
                                vlService.evictTeam(t.getId());
                            }
                        }
                    }else{
                        List<Homework> homeworkStudent = student.getHomeworks().stream().filter(h -> h.getAssignment().getCourseAssignment().equals(course.get())).collect(Collectors.toList());
                        homeworkRepository.deleteAll(homeworkStudent);
                    }
                    ret.add(student);
                }
            }
            return ret.stream().map(st -> modelMapper.map(st, StudentDTO.class)).collect(Collectors.toList());
        }
    }

    /**
     * Metodo per aggiungere gli studenti ad un corso tramite file CSV
     */
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
        }catch(CourseNotFoundException | CourseDisabledException | StudentNotFoundException e){
            throw e;
        } catch (RuntimeException exception){
            throw  new FormatFileNotValidException();
        }
    }


    /**
     * Metodo per aggiungere un corso al sistema
     */
    @Override
    public boolean addCourse(CourseDTO course, List<String> professorsId, PhotoModelVM photoModelVM) {
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
                    if( course.getMin()>course.getMax()) throw new CardinalityNotAccetableException();
                    if( course.getRunningInstances()>course.getTotInstances() || course.getMaxVcpu()<=0 ||
                        course.getDiskSpace()<=0 || course.getRam()<=0 ||
                        course.getTotInstances()<=0 || course.getRunningInstances()<=0)
                        throw new ResourcesVMNotRespectedException();
                    professors.forEach(c::setProfessor);
                    if (c.getProfessors().stream().anyMatch(pr -> pr.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
                        //Controllo per verificare che il professore setta il modello per il corso con courseId per la prima volta
                        if (c.getPhotoModelVM() == null) {
                            List<Team> teams = teamRepository.findAllById(c.getTeams().stream().map(Team::getId).collect(Collectors.toList()));
                            c.setPhotoModelVM(photoModelVM);
                            c.setMaxVcpu(course.getMaxVcpu());
                            c.setDiskSpace(course.getDiskSpace());
                            c.setRam(course.getRam());
                            c.setTotInstances(course.getTotInstances());
                            c.setRunningInstances(course.getRunningInstances());

                            teams.forEach(t-> {
                                t.setDiskSpaceLeft(course.getDiskSpace());
                                t.setRamLeft(course.getRam());
                                t.setMaxVcpuLeft(course.getMaxVcpu());
                                t.setTotInstancesLeft(course.getTotInstances());
                                t.setRunningInstancesLeft(course.getRunningInstances());
                            });
                            photoModelVMRepository.save(photoModelVM);
                            return true;
                        } else throw new ModelVMAlreadytPresentException();
                    }else throw new PermissionDeniedException();
                }
            }else throw new ProfessorNotFoundException();
        }
        return false;
    }

    /**
     * Metodo per aggiungere un professore come titolare del corso con nome pari a courseName
     */
    @Override
    public List<ProfessorDTO> addProfessorsToCourse(String courseName, List<String> professorsId) {
        Optional<Course> oc= courseRepository.findById(courseName);
        if ( !oc.isPresent())  {
            throw new CourseNotFoundException();
        }
        Course c = oc.get();
        if(!c.isEnabled()) throw new CourseDisabledException();
        List<Professor> professors = professorRepository.findAllById(professorsId);
        if(professors.size()!=professorsId.size())
            throw new ProfessorNotFoundException();
        else{
            if(vlService.getProfessorsForCourse(courseName).stream()
                    .noneMatch(pf ->pf.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))){
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

    /**
     * Metodo per abilitare il corso
     */
    @Override
    public void enableCourse(String courseName) {
        try{
            Course c = courseRepository.getOne(courseName);
            if(vlService.getProfessorsForCourse(c.getName()).stream()
                    .noneMatch(pf ->pf.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName())))
                throw new PermissionDeniedException();
            if(!c.isEnabled())
                c.setEnabled(true);
            else
                throw  new CourseAlreadyEnabledException();
        }catch(EntityNotFoundException e){
            throw new CourseNotFoundException();
        }
    }

    /**
     * Metodo per disabilitare il corso
     */
    @Override
    public void disableCourse(String courseName) {
        try{
            Course c = courseRepository.getOne(courseName);
            if(vlService.getProfessorsForCourse(c.getName()).stream()
                    .noneMatch(pf ->pf.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName())))
                throw new PermissionDeniedException();
            if(c.isEnabled())
                c.setEnabled(false);
            else
                throw new CourseAlreadyEnabledException();
        }catch(EntityNotFoundException e){
            throw new CourseNotFoundException();
        }
    }

    /**
     * Metodo per ritornare la lista di DTO dei corsi di cui il professore con matricola professorId è titolare
     */
    @Override
    public List<CourseDTO> getCoursesForProfessor(String professorId){
        try{
            Professor p = professorRepository.getOne(professorId);
            return p.getCourses().stream().map(c->modelMapper.map(c, CourseDTO.class)).collect(Collectors.toList());
        }catch (EntityNotFoundException e){
            throw new ProfessorNotFoundException();
        }
    }

    /**
     * Metodo per rimuovere il corso
     */
    @Override
    public boolean removeCourse(String courseId) {
        Optional<Course> oc= courseRepository.findById(courseId);
        if (oc.isPresent())  {
            Course c = oc.get();
            String idProfessor= (SecurityContextHolder.getContext().getAuthentication().getName());
            if(vlService.getProfessorsForCourse(c.getName()).stream()
                    .noneMatch(pf ->pf.getId().equals(idProfessor)))
                throw new PermissionDeniedException();
            Optional<Professor> op = professorRepository.findById(idProfessor);
            if(op.isPresent()){
                List<Student> students = c.getStudents();
                List<Team> teams = c.getTeams();

                for(int j=teams.size()-1; j>=0; j--) {
                    Team tmpTeam = teams.get(j);
                    // rimuovo VM, homework, corrections, membro dal team
                    if (tmpTeam.getStatus().equals("active")) {
                        List<VM> VMcourse = c.getVms();

                        for ( int i=VMcourse.size()-1; i>=0; i--) {
                            VM tmp = VMcourse.get(i);
                            photoVMRepository.delete(tmp.getPhotoVM());
                            c.removeVM(tmp);
                            tmpTeam.removeVM(tmp);
                            students.forEach(s-> {
                                tmp.removeStudentToOwnerList(s);
                                tmp.removeStudentToMemberList(s);
                            });
                            VMRepository.delete(tmp);
                        }
                        c.removeTeam(tmpTeam);
                        students.forEach(s-> s.removeTeamForStudent(tmpTeam));
                        teamRepository.delete(tmpTeam);

                        List<Assignment> assignmentsCourse = c.getAssignments();
                        for(Assignment assignment: assignmentsCourse){ //verificare se rimuove homework per assignment
                            photoAssignmentRepository.delete(assignment.getPhotoAssignment());
                            assignment.getHomeworks().forEach(h->{
                                h.getCorrections().forEach(cor -> photoCorrectionRepository.delete(cor));
                                h.getVersions().forEach(v -> photoVersionHMRepository.delete(v));
                                h.removeStudentFromHomework(h.getStudent());
                            });
                            homeworkRepository.deleteAll(assignment.getHomeworks());
                        }
                        assignmentRepository.deleteAll(assignmentsCourse);
                    }else{
                        // rimuovo token delle proposal
                        List<Team> teamsCourse = teamRepository.findAllById(tokenRepository.findAllByCourseId(c.getName()).stream()
                                .map(Token::getTeamId)
                                .collect(Collectors.toList()));
                        for (Team team : teamsCourse) {
                            tokenRepository.deleteFromTokenByTeamId(team.getId());
                            vlService.evictTeam(team.getId());
                        }
                    }

                }

                List<Assignment> assignmentsCourse = c.getAssignments();
                for(Assignment assignment: assignmentsCourse) {
                    homeworkRepository.deleteAll(assignment.getHomeworks());
                }
                assignmentRepository.deleteAll(assignmentsCourse);

                if( c.getPhotoModelVM()!=null)
                    photoModelVMRepository.delete(c.getPhotoModelVM());

                for(int i=students.size()-1; i>=0; i--){// s: stu){
                    c.removeStudent(students.get(i));
                    //log.severe("stu:"); debug
                }
                List<Professor> professors= c.getProfessors();
                for(int i=professors.size()-1; i>=0; i--){// s: stu){
                    c.removeProfessor(professors.get(i));
                    //log.severe("stu:"); ug
                }
                courseRepository.delete(c);
                courseRepository.flush();
                return true;
            }else throw new ProfessorNotFoundException();
        }else throw new CourseNotFoundException();
    }

    /**
     * Metodo per modificare i parametri di un corso come min, max e acronimo
     */
    @Override
    public boolean modifyCourse(CourseDTO course) {
        Optional<Course> oc=courseRepository.findById(course.getName());
        if ( oc.isPresent())  {
            Course c = oc.get();
            if( !c.isEnabled()) throw new CourseDisabledException();
            String idProfessor= (SecurityContextHolder.getContext().getAuthentication().getName());
            if(vlService.getProfessorsForCourse(c.getName()).stream()
                    .noneMatch(pf ->pf.getId().equals(idProfessor)))
                throw new PermissionDeniedException();
            c.setAcronym(course.getAcronym());
            if(course.getMax() >= course.getMin()) {
                c.setMax(course.getMax());
                c.setMin(course.getMin());
            }else throw  new CardinalityNotAccetableException();
            c.setEnabled(course.isEnabled());
            courseRepository.saveAndFlush(c);
            return true;
        }else throw  new CourseNotFoundException();
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
    /*
    @Override
    public CourseDTO addModelVM(CourseDTO courseDTO, String courseId, PhotoModelVM photoModelVM) {
        Optional<Course> oc = courseRepository.findById(courseId);
        if( oc.isPresent()) {
            Course c = oc.get();
            if (c.getProfessors().stream().anyMatch(p -> p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
                //Controllo per verificare che il professore setta il modello per il corso con courseId per la prima volta
                if (c.getPhotoModelVM() == null) {
                    List<Team> teams = teamRepository.findAllById(c.getTeams().stream().map(Team::getId).collect(Collectors.toList()));
                    c.setPhotoModelVM(photoModelVM);
                    c.setMaxVcpu(courseDTO.getMaxVcpu());
                    c.setDiskSpace(courseDTO.getDiskSpace());
                    c.setRam(courseDTO.getRam());
                    c.setTotInstances(courseDTO.getTotInstances());
                    c.setRunningInstances(courseDTO.getRunningInstances());

                    teams.forEach(t-> {
                        t.setDiskSpaceLeft(courseDTO.getDiskSpace());
                        t.setRamLeft(courseDTO.getRam());
                        t.setMaxVcpuLeft(courseDTO.getMaxVcpu());
                        t.setTotInstancesLeft(courseDTO.getTotInstances());
                        t.setRunningInstancesLeft(courseDTO.getRunningInstances());
                    });
                    photoModelVMRepository.save(photoModelVM);
                    return modelMapper.map(c, CourseDTO.class);
                } else throw new ModelVMAlreadytPresentException();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();

    }
*/

    /**
     * metodo per modificare le risorse associate al modello di VM di un dato corso
     */
    @Override
    public CourseDTO updateModelVM(CourseDTO courseDTO, String courseName ) {
        Optional<Course> oc = courseRepository.findById(courseName);
        if( oc.isPresent()) {
            Course c = oc.get();
            if (c.getProfessors().stream().anyMatch(p -> p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
                if(!c.isEnabled()) throw new CourseDisabledException();
                //Controllo per verificare che il professore setta il modello per il corso con courseId per la prima volta
                if (c.getPhotoModelVM() != null) {
                    List<Team> teams = teamRepository.findAllById(c.getTeams().stream().map(Team::getId).collect(Collectors.toList()));
                    if( courseDTO.getRunningInstances()>courseDTO.getTotInstances() || courseDTO.getMaxVcpu()<=0 ||
                        courseDTO.getDiskSpace()<=0 || courseDTO.getRam()<=0 ||
                        courseDTO.getTotInstances()<=0 || courseDTO.getRunningInstances()<=0)
                        throw new ResourcesVMNotRespectedException();
                    int diskSpaceDecrease = c.getDiskSpace()-courseDTO.getDiskSpace();
                    int vcpuDecrease = c.getMaxVcpu()-courseDTO.getMaxVcpu();
                    int ramDecrease = c.getRam() - courseDTO.getRam();
                    int runningInstancesDecrease = c.getRunningInstances() - courseDTO.getRunningInstances();
                    int totalInstancesDecrease = c.getTotInstances() - courseDTO.getTotInstances();
                    for(Team t:teams){
                        if(     (diskSpaceDecrease>0 && t.getDiskSpaceLeft()<diskSpaceDecrease) ||
                                (vcpuDecrease>0 && t.getMaxVcpuLeft()<vcpuDecrease) ||
                                (ramDecrease>0 && t.getRamLeft()<ramDecrease) ||
                                (runningInstancesDecrease>0 && t.getRunningInstancesLeft()<runningInstancesDecrease) ||
                                (totalInstancesDecrease>0 && t.getTotInstancesLeft()<totalInstancesDecrease))
                            throw new ResourcesVMNotRespectedException();
                    }
                    c.setMaxVcpu(courseDTO.getMaxVcpu());
                    c.setDiskSpace(courseDTO.getDiskSpace());
                    c.setRam(courseDTO.getRam());
                    c.setTotInstances(courseDTO.getTotInstances());
                    c.setRunningInstances(courseDTO.getRunningInstances());

                    teams.forEach(t-> {
                        t.setDiskSpaceLeft(t.getDiskSpaceLeft()-diskSpaceDecrease);
                        t.setRamLeft(t.getRamLeft()-ramDecrease);
                        t.setMaxVcpuLeft(t.getMaxVcpuLeft()-vcpuDecrease);
                        t.setTotInstancesLeft(t.getTotInstancesLeft()-totalInstancesDecrease);
                        t.setRunningInstancesLeft(t.getRunningInstancesLeft()-runningInstancesDecrease);
                    });
                    return modelMapper.map(c, CourseDTO.class);
                } else throw new ModelVMNotSettedException();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
    }


    /**
     * Metodo per ritornare le risorse di VM massime utilizzate in un corso
     */
    @Override
    public Map<String, Object> getMaxResources(String courseId){
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Professor> op = professorRepository.findById(professor);
        if(op.isPresent()){
            Optional<Course> oc = courseRepository.findById(courseId);
            if(!oc.isPresent())
                throw new CourseNotFoundException();
            Course course = oc.get();
            if(!course.getProfessors().contains(op.get()))
                throw new PermissionDeniedException();
            List<Team> teams = course.getTeams();
            int minVcpuTmp = course.getMaxVcpu();
            int minDiskSpaceTmp = course.getDiskSpace();
            int minRamTmp = course.getRam();
            int minRunning = course.getRunningInstances();
            int minTotal = course.getTotInstances();
            for(Team t:teams){
                if( t.getMaxVcpuLeft() < minVcpuTmp )
                    minVcpuTmp = t.getMaxVcpuLeft();
                if( t.getDiskSpaceLeft() < minDiskSpaceTmp)
                    minDiskSpaceTmp = t.getDiskSpaceLeft();
                if( t.getRamLeft() < minRamTmp)
                    minRamTmp = t.getRamLeft();
                if( t.getRunningInstancesLeft() < minRunning)
                    minRunning = t.getRunningInstancesLeft();
                if( t.getTotInstancesLeft() < minTotal)
                    minTotal = t.getTotInstancesLeft();
            }
            Map<String, Object> resources = new HashMap<>();
            resources.put("vcpu", (course.getMaxVcpu()-minVcpuTmp));
            resources.put("diskSpace", (course.getDiskSpace()-minDiskSpaceTmp));
            resources.put("ram", (course.getRam()-minRamTmp));
            resources.put("running", (course.getRunningInstances()-minRunning));
            resources.put("total", (course.getTotInstances()-minTotal));
            return resources;
        }else throw new ProfessorNotFoundException();

    }

    /**Metodo per ritornare la lista di DTO degli studenti che sono owner della VM con id pari a VMid
     */
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


    /**
     * Metodo per ritornare la lista di DTO delle VM di un dato corso
     */
    @Override
    public List<VMDTO> allVMforCourse(String courseId) { //CourseId preso dal pathVariable
        Optional<Course> oc= courseRepository.findById(courseId);
        if( oc.isPresent()){
            Course c= oc.get();
            if(c.getProfessors().stream().map(Professor::getId).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())){
                List<VM> vmsList= c.getVms();
                return vmsList.stream().map( v -> modelMapper.map(v, VMDTO.class)).collect(Collectors.toList());
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
    }


    /**
     * Metodo per ritornare l'immagine di una VM
     */
    @Override
    public PhotoVMDTO getVMforProfessor(String courseId, Long VMid) {
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Professor p = professorRepository.getOne(professor);
        Optional<Course> oc = p.getCourses().stream().filter(c-> c.getName().equals(courseId)).findFirst();
        if(!oc.isPresent()) throw new CourseNotFoundException();
        if(!oc.get().isEnabled()) throw new CourseDisabledException();
        if(!oc.get().getTeams().isEmpty()){
            Optional<VM> ovm= VMRepository.findById(VMid);
            if(ovm.isPresent()){
                VM vm = ovm.get();
                if(vm.getStatus().equals("off")) throw new VMnotEnabledException();
                Optional<Team> teamOptional = oc.get().getTeams().stream().filter(t->t.getVms().contains(vm)).findFirst();
                if(teamOptional.isPresent()){
                    PhotoVMDTO photoVMDTO = modelMapper.map(vm.getPhotoVM(), PhotoVMDTO.class);
                    photoVMDTO.setPicByte(vlService.decompressZLib(photoVMDTO.getPicByte()));
                    StringTokenizer st = new StringTokenizer(photoVMDTO.getTimestamp(), ".");
                    photoVMDTO.setTimestamp(st.nextToken());
                    return photoVMDTO;
                }else throw new PermissionDeniedException();
            }else throw new VMNotFoundException();
        }else throw new TeamNotFoundException();
    }

    /**
     * Metodo per ritornare le risorser di una VM per un dato team
     */
    @Override
    public Map<String, Object> getResourcesVM(Long teamId){
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Professor p = professorRepository.getOne(professor);
        Optional<Team> ot = teamRepository.findById(teamId);
        if(!ot.isPresent()) throw new TeamNotFoundException();
        Team t = ot.get();
        Course course = t.getCourse();
        if(!course.getProfessors().contains(p)) throw new PermissionDeniedException();
        Map<String, Object> resources = new HashMap<>();
        resources.put("vcpu", (course.getMaxVcpu()-t.getMaxVcpuLeft())+"/"+course.getMaxVcpu());
        resources.put("diskSpace", (course.getDiskSpace()-t.getDiskSpaceLeft())+"/"+course.getDiskSpace());
        resources.put("ram", (course.getRam()-t.getRamLeft())+"/"+course.getRam());
        resources.put("running", (course.getRunningInstances()-t.getRunningInstancesLeft())+"/"+course.getRunningInstances());
        resources.put("total", (course.getTotInstances()-t.getTotInstancesLeft())+"/"+course.getTotInstances());
        return resources;
    }

    /**
     * Metodo per aggiungere una consegna al corso
     */
    @Override
    public AssignmentDTO addAssignment( AssignmentDTO assignmentDTO,PhotoAssignmentDTO photoAssignmentDTO,  String courseId) { //CourseId preso dal pathVariable
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Course> oc = courseRepository.findById(courseId);
        if(oc.isPresent()){
            Course c = oc.get();
            if(c.getProfessors().stream().anyMatch(p->p.getId().equals(professor))){
                if(!c.isEnabled()) throw new CourseDisabledException();
                if(c.getAssignments().stream().noneMatch(a->a.getAssignmentName().equals(assignmentDTO.getAssignmentName()))) {
                    Assignment assignment= modelMapper.map(assignmentDTO, Assignment.class);
                    PhotoAssignment photoAssignment = modelMapper.map(photoAssignmentDTO, PhotoAssignment.class);
                    assignment.setCourseAssignment(c);
                    assignment.setPhotoAssignment(photoAssignment);
                    assignment.setAlreadyExpired(false);
                    for(Student s: c.getStudents()){
                        Homework h = new Homework();
                        h.setStatus("NULL");
                        h.setTimestamp("/");
                        h.setGrade("-1");
                        h.setAssignment(assignment);
                        h.setStudentForHomework(s);
                        h.setPermanent(false);
                        homeworkRepository.save(h);
                    }
                    assignmentRepository.save(assignment);
                    photoAssignmentRepository.save(photoAssignment);
                    AssignmentDTO assignmentDTO1 = modelMapper.map(assignment, AssignmentDTO.class);
                    StringTokenizer releaseDateST = new StringTokenizer(assignmentDTO1.getReleaseDate(), ".");
                    assignmentDTO1.setReleaseDate(releaseDateST.nextToken());
                    StringTokenizer expirationST = new StringTokenizer(assignmentDTO1.getExpiration(), ".");
                    assignmentDTO1.setExpiration(expirationST.nextToken());
                    return assignmentDTO1;
                }else throw new AssignmentAlreadyExistException();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
    }

    /**
     * Metodo per ritornare la lista di DTO delle consegne presenti in un dato corso
     */
    @Override
    public List<AssignmentDTO> allAssignment(  String courseId) { //CourseId preso dal pathVariable
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Professor> op = professorRepository.findById(professor);
        if(op.isPresent()){
            Professor p= op.get();
            Optional<Course> c = p.getCourses().stream().filter(co->co.getName().equals(courseId)).findFirst();
            if(c.isPresent()){
                List<AssignmentDTO>  assignmentDTOList= c.get().getAssignments().stream()
                        .map( a -> modelMapper.map(a, AssignmentDTO.class))
                        .collect(Collectors.toList());
                assignmentDTOList.stream().forEach(a-> {
                    StringTokenizer expirationST = new StringTokenizer(a.getExpiration(), ".");
                    StringTokenizer releaseDateST= new StringTokenizer(a.getReleaseDate(), ".");
                    a.setExpiration(expirationST.nextToken());
                    a.setReleaseDate(releaseDateST.nextToken());
                });
                return assignmentDTOList;
            }throw new PermissionDeniedException();
        }else throw new ProfessorNotFoundException();
    }


    /**
     * Metodo per ritornare l'immagine associata ad una data consegna
     */
    @Override
    public PhotoAssignmentDTO getAssignmentProfessor(Long assignmentId ) {
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
                    paDTO.setPicByte(vlService.decompressZLib(paDTO.getPicByte()));
                    StringTokenizer st = new StringTokenizer(paDTO.getTimestamp(), ".");
                    paDTO.setTimestamp(st.nextToken());
                    return paDTO;
                } else throw new PermissionDeniedException();
            } else throw new AssignmentNotFoundException();
        }else throw new ProfessorNotFoundException();
    }


    /**
     * Metodo per ritornare il DTO di una data consegn
     */
    @Override
    public AssignmentDTO getAssignmentDTOProfessor(Long assignmentId ) {
        String professor =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Professor> op = professorRepository.findById(professor);
        if(op.isPresent()){
            Professor p =op.get();
            Optional<Assignment> oa=assignmentRepository.findById(assignmentId);
            if(oa.isPresent()){
                Assignment a = oa.get();
                if (a.getCourseAssignment().getProfessors().contains(p)) {
                    return modelMapper.map(a, AssignmentDTO.class);
                } else throw new PermissionDeniedException();
            } else throw new AssignmentNotFoundException();
        }else throw new ProfessorNotFoundException();
    }

    /**
     * Metodo per ritornare una lista di mappe contenenti tutti gli elaborati per un data consegna per il corso con nome pari a courseName
     */
    @Override
    public List<Map<String, Object>> allHomework(String courseName, Long assignmentId){
        Optional<Course> oc= courseRepository.findById(courseName);
        if(oc.isPresent()){
            Course c = oc.get();
            if(c.getProfessors().stream().anyMatch(p->p.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
                Optional<Assignment> assignment = c.getAssignments().stream().filter(a->a.getId().equals(assignmentId)).findFirst();
                if(assignment.isPresent()) {
                    List<Homework> homeworkList = assignment.get().getHomeworks();
                    List<Map<String,Object>> ret = new ArrayList<>();
                    for ( Homework h : homeworkList ) {
                        Map<String,Object> m = new HashMap<>();
                        HomeworkDTO hdto = modelMapper.map(h,HomeworkDTO.class);
                        if(h.getStudent()==null)
                            throw new StudentNotFoundException();
                        StudentDTO sdto = modelMapper.map(h.getStudent(),StudentDTO.class);
                        StringTokenizer st = new StringTokenizer(hdto.getTimestamp(), ".");
                        hdto.setTimestamp(st.nextToken());
                        m.put("Homework", hdto);
                        m.put("Student", sdto);
                        ret.add(m);
                    }
                    return ret;
                }else throw new AssignmentNotFoundException();
            }else throw new PermissionDeniedException();
        }else throw new CourseNotFoundException();
    }

    /**
     *Metodo per ritornare una lista di mappe con l'id, timestamp e nome dell'immagine associata alla versione per un dato elaborato
     */
    @Override
    public  List<Map<String, Object>> getVersionsHWForProfessor( Long homeworkId){
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
                    StringTokenizer st = new StringTokenizer(v.getTimestamp(), ".");
                    m.put("timestamp", st.nextToken());
                    m.put("nameFile", v.getNameFile());
                    l.add(m);
                }
                return l;
            }else throw  new PermissionDeniedException();
        }else throw new HomeworkNotFoundException();
    }

    /**
     * Metodo per caricare l'immagine di una correzione e assegnare un voto all'elaborato
     */
    @Override
    public Map<String, Object> uploadCorrection(Long homeworkId, Long versionHMid,
                                                PhotoCorrectionDTO photoCorrectionDTO,Boolean permanent, String grade) {
        Optional<Homework> oh = homeworkRepository.findById(homeworkId);
        String professorAuth = SecurityContextHolder.getContext().getAuthentication().getName();
        if(oh.isPresent()){
            Homework h= oh.get();
            if( h.getAssignment().getCourseAssignment().getProfessors().stream()
                    .anyMatch(p->p.getId().equals(professorAuth))){
                if(!h.getAssignment().getCourseAssignment().isEnabled()) throw new CourseDisabledException();
                if( photoVersionHMRepository.findById(versionHMid).isPresent()){
                    PhotoCorrection photoCorrection = modelMapper.map(photoCorrectionDTO, PhotoCorrection.class);
                    photoCorrection.setIdVersionHomework(versionHMid);
                    photoCorrection.setIdProfessor(professorAuth);
                    h.setPhotoCorrection(photoCorrection);
                    h.setStatus("RIVISTO");
                    h.setPermanent(permanent);
                    if(permanent) {
                        if( photoVersionHMRepository.findLastByHomeworkId(homeworkId).getId()!=versionHMid)
                            throw new NewVersionHMisPresentException();
                        if(grade==null || Integer.parseInt(grade) < 0 || Integer.parseInt(grade) > 30)
                            throw new GradeNotValidException();

                        h.setGrade(grade);
                    }
                    h.setTimestamp(photoCorrection.getTimestamp());
                    photoCorrectionRepository.saveAndFlush(photoCorrection);

                    Map<String, Object> m = new HashMap<>();
                    m.put("id", photoCorrection.getId());
                    StringTokenizer st = new StringTokenizer(photoCorrection.getTimestamp(), ".");
                    m.put("timestamp", st.nextToken());
                    m.put("nameFile", photoCorrection.getNameFile());
                    m.put("versionId",photoCorrection.getIdVersionHomework());

                    return m;
                }else throw new HomeworkVersionIdNotFoundException();
            }else throw  new PermissionDeniedException();
        }throw new HomeworkNotFoundException();
    }

    /**
     * Metodo per ritornare una lista di mappe contenenti le informaizoni associate alle correzioni di un dato elaborato
     */
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
                    StringTokenizer st = new StringTokenizer(c.getTimestamp(), ".");
                    m.put("timestamp", st.nextToken());
                    m.put("nameFile", c.getNameFile());
                    m.put("versionId",c.getIdVersionHomework());
                    l.add(m);
                }
                return l;
            }else throw  new PermissionDeniedException();
        }else throw new HomeworkNotFoundException();
    }

}
