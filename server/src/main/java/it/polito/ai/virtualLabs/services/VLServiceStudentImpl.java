package it.polito.ai.virtualLabs.services;

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
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@PreAuthorize("hasAuthority('student')")
public class VLServiceStudentImpl implements VLServiceStudent{

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    ModelMapper modelMapper;
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
    VLService vlService;


    /**
     * Metodo per ritornare la lista di DTO dei corsi a cui lo studente con matricola pari a studentId è iscritto
     */
    @Override
    public List<CourseDTO> getCoursesForStudent(String studentId){
        try{
            Student s = studentRepository.getOne(studentId);
            return s.getCourses().stream().map(c->modelMapper.map(c, CourseDTO.class)).collect(Collectors.toList());
        }catch (EntityNotFoundException e){
            throw new StudentNotFoundException();
        }
    }

    /**
     * Metodo per ritornare la lista di DTO dei team di cui uno studente è membro
     */
    @Override
    public List<TeamDTO> getTeamsForStudent(String studentId){
        try {
            Student s = studentRepository.getOne(studentId);
            return s.getTeams().stream().filter(t-> t.getStatus().equals("active")).map(t -> modelMapper.map(t, TeamDTO.class)).collect(Collectors.toList());
        }catch(EntityNotFoundException e){
            throw new StudentNotFoundException();
        }
    }

    /**
     * Metodo per ritornare il team al quale lo studente è iscritto per un determinato corso
     */
    @Override
    public TeamDTO getTeamForStudent(String courseId, String studentId) {
        try {
            Optional<Course> oc = courseRepository.findById(courseId);
            if (!oc.isPresent())
                throw new CourseNotFoundException();
            Optional<Student> os = studentRepository.findById(studentId);
            if (!os.isPresent())
                throw new StudentNotFoundException();
            if(!os.get().getCourses().contains(oc.get()))
                throw new StudentNotEnrolledToCourseException();
            List<TeamDTO> list = oc.get().getTeams().stream()
                    .filter(team -> team.getMembers().contains(os.get())  && team.getStatus().equals("active"))
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

    /**
     * Metodo utillizzato per creare una proposta di team per un determinato corso, controllando che tutti i vincoli siano rispettati
     * e chiamando il metodo per generare le email di invito agli studenti ricevuti tramite una lista di ID
     */
    @Override
    public Map<String,Object> proposeTeam(String courseId, String name, List<String> memberIds, Timestamp timeout){
        Optional<Course> course = courseRepository.findById(courseId);
        String creatorStudent = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(creatorStudent);
        if(!os.isPresent()) throw new StudentNotFoundException();
        Student creator = os.get();
        if( memberIds.contains(creatorStudent))
            throw new PermissionDeniedException();
        if( !course.isPresent())
            throw  new CourseNotFoundException();
        if(!course.get().isEnabled())
            throw new CourseDisabledException();
        if(course.get().getTeams().stream().anyMatch(t ->t.getName().equals(name)))
            throw new NameTeamIntoCourseAlreadyPresentException();

        List<String> enrolledStudents= vlService.getEnrolledStudents(courseId).stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList());
        if ( !enrolledStudents.containsAll(memberIds) || !enrolledStudents.contains(creatorStudent))
            throw  new StudentNotEnrolledToCourseException();

        if( memberIds.stream()
                .map(s->studentRepository.getOne(s))
                .map(Student::getTeams)
                .filter( lt-> !lt.isEmpty())
                .map( lt-> lt.stream().filter(l-> l.getStatus().equals("active")).map(Team::getCourse)
                        .anyMatch(c-> c.getName().equals(courseId))).collect(Collectors.toList()).contains(true))
            throw  new StudentAlreadyInTeamException();

        if ( memberIds.size()+1<vlService.getCourse(courseId).get().getMin()
                || memberIds.size()+1> vlService.getCourse(courseId).get().getMax())
            throw  new CardinalityNotAcceptableException();

        if( memberIds.stream().distinct().count() != memberIds.size())
            throw  new StudentDuplicateException();

        Team team = new Team();
        team.setName(name);
        team.setCourse(course.get());
        team.setStatus("pending");
        team.setCreatorId(creatorStudent);

        if( course.get().getPhotoModelVM()!=null){
            Course c =course.get();
            team.setDiskSpaceLeft(c.getDiskSpace());
            team.setMaxVcpuLeft(c.getMaxVcpu());
            team.setRamLeft(c.getRam());
            team.setRunningInstancesLeft(c.getRunningInstances());
            team.setTotInstancesLeft(c.getTotInstances());
        }
        team.addStudentIntoTeam(studentRepository.getOne(creatorStudent));
        teamRepository.save(team);
        if(memberIds.isEmpty())
            team.setStatus("active");
        else{
            memberIds.forEach(s-> team.addStudentIntoTeam(studentRepository.getOne(s)));
            vlService.notifyTeam(modelMapper.map(team, TeamDTO.class), memberIds, creatorStudent,  courseId, timeout);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("teamName", team.getName());
        map.put("creator", creator.getName()+" "+ creator.getFirstName()+" "+"("+creator.getId()+")");
        map.put("status", "accepted");
        List<Map<String, Object>> l2 = new ArrayList<>();
        for(Token token: tokenRepository.findAllByTeamId(team.getId()).stream()
                .filter(t->!t.getStudent().equals(creator)).collect(Collectors.toList())){
            Map<String, Object> m2= new HashMap<>();
            m2.put("student", token.getStudent().getName()+" "+token.getStudent().getFirstName()+" "+"("+token.getStudent().getId()+")");
            m2.put("status", token.getStatus());
            l2.add(m2);
        }
        map.put("students", l2);
        return map;
    }

    /**
     * Metodo per ritornare la lista di proposte di team per un determinato corso per l'utente autenticato
     */
    @Override
    public   List<Map<String, Object>> getProposals(String courseId) {
        String student = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if(!os.isPresent()) throw new StudentNotFoundException();
        Student s = os.get();
        if(s.getTeams().stream().anyMatch(t-> t.getCourse().getName().equals(courseId) && t.getStatus().equals("active")))
            throw new StudentAlreadyInTeamException();
        List<Long> teamList = s.getTokens().stream()
                .filter(t-> t.getCourseId().equals(courseId)).map(Token::getTeamId).collect(Collectors.toList());
        List<Map<String, Object>> l = new ArrayList<>();
        for( Long teamId : teamList) {
            Optional<Team> oteam= teamRepository.findById(teamId);
            if(!oteam.isPresent()) throw new TeamNotFoundException();
            Team team = oteam.get();
            Optional<Student> ostu = studentRepository.findById(team.getCreatorId());
            if(!ostu.isPresent()) throw new StudentNotFoundException();
            Student stu= ostu.get();
            Map<String, Object> m = new HashMap<>();
            m.put("teamName", team.getName());
            m.put("creator", stu.getName()+" "+ stu.getFirstName()+" "+"("+stu.getId()+")");
            m.put("teamStatus", team.getStatus());
            Optional<Token> otoken = s.getTokens().stream().filter(t-> t.getTeamId().equals(teamId)).findFirst();
            if(!otoken.isPresent()) throw new TokenNotFoundException();
            Token tokenStudent = otoken.get();
            String currentToken = tokenStudent.getId();
            m.put("tokenId", currentToken);
            m.put("status", tokenStudent.getStatus());
            m.put("scadenza", tokenStudent.getExpiryDate());
            List<Map<String, Object>> l2 = new ArrayList<>();
            for(Token token: tokenRepository.findAllByTeamId(teamId).stream()
                    .filter(t->!t.getStudent().equals(s) && !t.getStudent().equals(stu)).collect(Collectors.toList())){
                Map<String, Object> m2= new HashMap<>();
                m2.put("student", token.getStudent().getName()+" "+token.getStudent().getFirstName()+" "+"("+token.getStudent().getId()+")");
                m2.put("status", token.getStatus());
                l2.add(m2);
            }
            m.put("students", l2);
            l.add(m);
        }
        return l;
    }

    /**
     * Metodo per ritornare la lista di DTO degli studenti che appartengono ad un team per un determinato corso
     */
    @Override
    public List<StudentDTO> getStudentsInTeams(String courseName){
        if( courseRepository.findById(courseName).isPresent())
            return courseRepository.getStudentsTeams(courseName).stream()
                    .map(s->modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException();
    }

    /**
     * Metodo per ritornare la lista di DTO degli studenti che non appartengono ad un team per un determinato corso
     */
    @Override
    public List<StudentDTO>  getAvailableStudents(String courseName){
        if( courseRepository.findById(courseName).isPresent())
            return courseRepository.getStudentsNotInTeams(courseName).stream()
                    .filter(s->!s.getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
                    .map(s->modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException();
    }


    /**
     * Metodo per aggiungere una VM (parametri e foto) controllando che i vincoli siano rispettati e decrementando le risorse disponibili per il team
     */
    @Override
    public VMDTO addVM(VMDTO vmdto, String courseId, String timestamp) {
        String studentAuth= SecurityContextHolder.getContext().getAuthentication().getName();
        if( getStudentsInTeams(courseId).stream().anyMatch(s-> s.getId().equals(studentAuth))){
            Optional<Course> oc = courseRepository.findById(courseId);
            if(!oc.isPresent())
                throw new CourseNotFoundException();
            Course c = oc.get();
            if( !c.isEnabled())
                throw new CourseDisabledException();
            if (c.getPhotoModelVM() == null)
                throw  new ModelVMNotSettedException();
            Student s = studentRepository.getOne(studentAuth);
            Team t = teamRepository.getOne(s.getTeams().stream().filter(te->te.getCourse().equals(c)).findFirst().get().getId());
            if(t.getVms().stream().anyMatch(v->v.getNameVM().equals(vmdto.getNameVM())))
                throw new VMduplicatedException();
            if( vmdto.getDiskSpace() <= t.getDiskSpaceLeft() && vmdto.getNumVcpu()<= t.getMaxVcpuLeft()
                    && vmdto.getRam() <= t.getRamLeft() && t.getTotInstancesLeft()>0){
                if( vmdto.getDiskSpace()<=0 || vmdto.getNumVcpu()<=0 || vmdto.getRam()<=0)
                    throw new InvalidInputVMresources();
                VM vm = modelMapper.map(vmdto, VM.class);
                vm.setCourse(c);
                PhotoModelVM photoModelVM = c.getPhotoModelVM();
                PhotoVM photoVM = new PhotoVM();
                photoVM.setNameFile(photoModelVM.getNameFile());
                photoVM.setType(photoModelVM.getType());
                photoVM.setPicByte(photoModelVM.getPicByte());
                photoVM.setTimestamp(timestamp);
                vm.setPhotoVM(photoVM);
                vm.addStudentToOwnerList(s);
                t.getMembers().forEach(vm::addStudentToMemberList);
                vm.setTeam(t);
                t.setDiskSpaceLeft(t.getDiskSpaceLeft()-vmdto.getDiskSpace());
                t.setMaxVcpuLeft(t.getMaxVcpuLeft()-vmdto.getNumVcpu());
                t.setRamLeft(t.getRamLeft()-vmdto.getRam());
                t.setTotInstancesLeft(t.getTotInstancesLeft()-1);
                VMRepository.save(vm);
                photoVMRepository.save(photoVM);
                return modelMapper.map(vm, VMDTO.class);
            }else throw new ResourcesVMNotRespectedException();
        }else throw new PermissionDeniedException();
    }


    /**
     * Metodo per aggiungere agli owner di una VM gli studenti dei quali viene passato l'ID in una lista
     */
    @Override
    public boolean addOwner(Long VMid, String courseId, List<String> studentsId) {
        Optional<VM> ovm= VMRepository.findById(VMid);
        if (ovm.isPresent() ) {
            VM vm =ovm.get();
            Optional<Course> oc = courseRepository.findById(courseId);
            if (!oc.isPresent() )
                throw new CourseNotFoundException();
            Course c=oc.get();
            if(!c.isEnabled())
                throw new CourseDisabledException();
            if(!vm.getCourse().equals(c))
                throw new PermissionDeniedException();
            String studentAuth = SecurityContextHolder.getContext().getAuthentication().getName();
            if (vm.getOwnersVM().stream().anyMatch(s->s.getId().equals(studentAuth))){
                if(vm.getMembersVM().stream().map(Student::getId).collect(Collectors.toList()).containsAll(studentsId)){
                    if( studentsId.stream().map(s-> vm.addStudentToOwnerList(studentRepository.getOne(s))).anyMatch(r->r.equals(false)))
                        throw new StudentAlreadyOwnerException();
                    else{
                        VMRepository.saveAndFlush(vm);
                        return true;
                    }
                }else throw new StudentNotFoundException();
            }else throw new PermissionDeniedException();
        }else throw new VMNotFoundException();
    }

    /**
     * Metodo che ritorna la lista di DTO degli studenti owner di una determinata VM
     */
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

    /**
     * Metodo per accendere una VM
     */
    @Override
    public boolean activateVM(Long VMid ){
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm = ovm.get();
            if (vm.getOwnersVM().stream().map(Student::getId).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                Team t = teamRepository.getOne(vm.getTeam().getId());
                if(t.getRunningInstancesLeft()>0) { //controllo se è ancora possibile avviare delle VM
                    Course c = courseRepository.getOne(t.getCourse().getName());
                    if( c.isEnabled()){
                        t.setRunningInstancesLeft(t.getRunningInstancesLeft() - 1);
                        vm.setStatus("on");
                    }else throw new CourseDisabledException();
                }else throw new ResourcesVMNotRespectedException();
            } else throw new PermissionDeniedException();
        } else throw new VMNotFoundException();
        return true;
    }

    /**
     * Metodo per usare una VM, ovvero caricare una versione aggiornata della foto della VM
     */
    @Override
    public boolean useVM(Long VMid, String timestamp, PhotoVMDTO photoVMDTO ){
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm = ovm.get();
            if(!vm.getCourse().isEnabled()) throw new CourseDisabledException();
            if (vm.getMembersVM().stream().map(Student::getId).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                if(vm.getStatus().equals("on")){
                    PhotoVM p = photoVMRepository.getOne(vm.getPhotoVM().getId());
                    p.setNameFile(photoVMDTO.getNameFile());
                    p.setType(photoVMDTO.getType());
                    p.setPicByte(photoVMDTO.getPicByte());
                    p.setTimestamp(timestamp);
                    return true;
                }else throw new VMnotEnabledException();
            } else throw new PermissionDeniedException();
        } else throw new VMNotFoundException();

    }

    /**
     * Metodo per spegnere una VM
     */
    @Override
    public boolean disableVM(Long VMid ){
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm = ovm.get();
            if (vm.getOwnersVM().stream().map(Student::getId).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                Team t = teamRepository.getOne(vm.getTeam().getId());
                t.setRunningInstancesLeft(t.getRunningInstancesLeft()+1);
                vm.setStatus("off");
            } else throw new PermissionDeniedException();
        } else throw new VMNotFoundException();
        return true;
    }

    /**
     * Metodo per rimuovere una VM, rendendo nuovamente disponibili le risorse al team
     */
    @Override
    public boolean removeVM(Long VMid){
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm= ovm.get();
            if(!vm.getCourse().isEnabled()) throw new CourseDisabledException();
            if (vm.getOwnersVM().stream().map(Student::getId).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                Team t = teamRepository.getOne(vm.getTeam().getId());
                Course c = courseRepository.getOne(t.getCourse().getName());
                if( vm.getStatus().equals("on")) {
                    t.setRunningInstancesLeft(t.getRunningInstancesLeft()+1);
                }
                t.setTotInstancesLeft(t.getTotInstancesLeft()+1);
                t.setRamLeft(vm.getRam()+t.getRamLeft());
                t.setMaxVcpuLeft(vm.getNumVcpu()+t.getMaxVcpuLeft());
                t.setDiskSpaceLeft(vm.getDiskSpace()+t.getDiskSpaceLeft());
                t.removeVM(vm);
                c.removeVM(vm);
                List<Student> students = t.getMembers();
                for(Student s:students){
                    s.removeMemberToVM(vm);
                    s.removeOwnerToVM(vm);
                }
                photoVMRepository.delete(vm.getPhotoVM());
                VMRepository.delete(vm);
            } else throw new PermissionDeniedException();
        } else throw new VMNotFoundException();
        return true;
    }

    /**
     * Metodo per aggiornare le risorse utilizzate da una VM, propagando le modifiche al team
     */
    @Override
    public VMDTO updateVMresources(Long VMid,VMDTO vmdto) {
        Optional<VM> ovm = VMRepository.findById(VMid);
        if (ovm.isPresent()) {
            VM vm = ovm.get();
            if (vm.getOwnersVM().stream().map(Student::getId).collect(Collectors.toList()).contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                Team t = teamRepository.getOne(vm.getTeam().getId());
                if(t.getVms().stream().anyMatch(v->v.getNameVM().equals(vmdto.getNameVM()) && !v.getId().equals(VMid)))
                    throw new VMduplicatedException();
                if(!t.getCourse().isEnabled()) throw new CourseDisabledException();
                if(!vm.getStatus().equals("off") ) throw new VMnotOffException();
                if (
                        vmdto.getDiskSpace() <= (t.getDiskSpaceLeft()+vm.getDiskSpace()) &&
                                vmdto.getNumVcpu() <= (t.getMaxVcpuLeft()+vm.getNumVcpu()) &&
                                vmdto.getRam() <= (t.getRamLeft()+vm.getRam())) {
                    t.setDiskSpaceLeft(t.getDiskSpaceLeft() + vm.getDiskSpace() - vmdto.getDiskSpace());
                    t.setRamLeft(t.getRamLeft() + vm.getRam() - vmdto.getRam());
                    t.setMaxVcpuLeft(t.getMaxVcpuLeft() + vm.getNumVcpu() - vmdto.getNumVcpu());
                    vm.setNameVM(vmdto.getNameVM());
                    vm.setDiskSpace(vmdto.getDiskSpace());
                    vm.setRam(vmdto.getRam());
                    vm.setNumVcpu(vmdto.getNumVcpu());
                    return modelMapper.map(vm, VMDTO.class);
                } else throw new ResourcesVMNotRespectedException();
            } else throw new PermissionDeniedException();
        }else throw new VMNotFoundException();
    }

    /**
     * Metodo per ritornare una lista di DTO delle VM collegate al team a cui appartiene uno studente per un determinato corso
     */
    @Override
    public List<VMDTO> allVMforStudent( String courseId) {
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Student s = studentRepository.getOne(student);
        List<Team> teams = s.getTeams().stream().filter(c->c.getCourse().getName().equals(courseId)).collect(Collectors.toList());
        if(!teams.isEmpty()){
            List<VM> vmsList = teams.get(0).getVms();
            return  vmsList.stream().map(v->modelMapper.map(v, VMDTO.class)).collect(Collectors.toList());
        }else throw new TeamNotFoundException();

    }

    /**
     * Metodo per ritornare la foto di una determinata VM
     */
    @Override
    public PhotoVMDTO getVMforStudent( String courseId, Long VMid) {
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Student s = studentRepository.getOne(student);
        List<Team> teams = s.getTeams().stream().filter(c->c.getCourse().getName().equals(courseId)).collect(Collectors.toList());
        if(!teams.isEmpty()){
            if( !teams.get(0).getCourse().isEnabled()) throw new CourseDisabledException();
            Optional<VM> ovm= VMRepository.findById(VMid);
            if(ovm.isPresent()){
                VM vm = ovm.get();
                List<VM> listVMs= teams.get(0).getVms();
                if(listVMs.contains(vm)){
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
     * Metodo per verificare se l'utente autenticato è owner di una determinata VM
     */
    @Override
    public boolean isOwner(Long VMid) {
        String student =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if(os.isPresent()){
            Student s=os.get();
            Optional<VM> ovm= VMRepository.findById(VMid);
            if(ovm.isPresent()){
                VM vm= ovm.get();
                if(vm.getOwnersVM().stream().map(Student::getId).collect(Collectors.toList()).contains(s.getId()))
                    return true;
                else throw new PermissionDeniedException();
            }else throw new VMNotFoundException();
        }else throw new StudentNotFoundException();
    }

    /**
     * Metodo per ritornare una lista di mappe contenenti le consegne dello studente autenticato per un determinato corso, con il voto e lo stato della consegna
     */
    @Override
    public List<Map<String, Object>> allAssignmentStudent( String courseId){
        String student = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if (os.isPresent()) {
            Student s = os.get();
            Optional<Course> c = s.getCourses().stream().filter(co->co.getName().equals(courseId)).findFirst();
            if(c.isPresent()){
                List<Assignment>  assignmentList= c.get().getAssignments();
                List<Map<String, Object>> list = new ArrayList<>();
                for(Assignment  a:assignmentList){
                    Map<String, Object> map = new HashMap<>();
                    AssignmentDTO assignmentDTO = modelMapper.map(a, AssignmentDTO.class);
                    StringTokenizer stReleaseDate = new StringTokenizer(assignmentDTO.getReleaseDate(), ".");
                    StringTokenizer stExpiration = new StringTokenizer(assignmentDTO.getExpiration(), ".");
                    assignmentDTO.setReleaseDate(stReleaseDate.nextToken());
                    assignmentDTO.setExpiration(stExpiration.nextToken());
                    map.put("assignment", assignmentDTO);
                    map.put("grade", s.getHomeworks().stream().filter(h-> h.getAssignment().equals(a)).findFirst().get().getGrade());
                    map.put("status", s.getHomeworks().stream().filter(h-> h.getAssignment().equals(a)).findFirst().get().getStatus());
                    list.add(map);
                }
                return list;
            }throw new StudentNotEnrolledToCourseException();
        }else throw new StudentNotFoundException();
    }

    /**
     * Metodo ritornare l'immagine di una determinata consegna, aggiornando lo stato se era a NULL, impostando anche il nuovo timestamp di ultima modifica dello stato
     */
    @Override
    public PhotoAssignmentDTO getAssignmentStudent( Long assignmentId ) {
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
                    if(homework.getStatus().equals("NULL") && !homework.getPermanent() && a.getCourseAssignment().isEnabled()){
                        homework.setStatus("LETTO");
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        homework.setTimestamp(timestamp.toString());
                    }
                    PhotoAssignment pa = a.getPhotoAssignment();
                    Optional<PhotoAssignment> photoAssignment = photoAssignmentRepository.findById(pa.getId());
                    if (photoAssignment.isPresent()) {
                        PhotoAssignmentDTO paDTO = modelMapper.map(pa, PhotoAssignmentDTO.class);
                        paDTO.setPicByte(vlService.decompressZLib(paDTO.getPicByte()));
                        StringTokenizer st = new StringTokenizer(paDTO.getTimestamp(), ".");
                        paDTO.setTimestamp(st.nextToken());
                        return paDTO;
                    } else throw new PhotoAssignmentNotFoundException();
                } else throw new PermissionDeniedException();
            } else throw new AssignmentNotFoundException();
        }else throw new StudentNotFoundException();
    }

    /**
     * Metodo per caricare una versione di elaborato per un determinato elaborato
     */
    @Override
    public PhotoVersionHomeworkDTO uploadVersionHomework (Long homeworkId, PhotoVersionHomeworkDTO photoVersionHomeworkDTO) {
        String student = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Student> os = studentRepository.findById(student);
        if (os.isPresent()) {
            Student s = os.get();
            Optional<Homework> oh = homeworkRepository.findById(homeworkId);
            if (oh.isPresent()) {
                Homework h = oh.get();
                if(!h.getAssignment().getCourseAssignment().isEnabled())
                    throw new CourseDisabledException();
                if (h.getStudent().getId().equals(student)) {
                    if (h.getPermanent().equals(false)) {
                        Optional<Team> teamStudent = s.getTeams().stream().filter(t-> t.getCourse().equals(h.getAssignment().getCourseAssignment())).findAny();
                        if(teamStudent.isPresent()){
                            if(teamStudent.get().getVms().size()>0){
                                h.setStatus("CONSEGNATO");
                                PhotoVersionHomework photoVersionHomework = modelMapper.map(photoVersionHomeworkDTO, PhotoVersionHomework.class);
                                h.setPhotoVersionHomework(photoVersionHomework);
                                h.setTimestamp(photoVersionHomework.getTimestamp());
                                homeworkRepository.saveAndFlush(h);
                                photoVersionHMRepository.saveAndFlush(photoVersionHomework);
                                PhotoVersionHomeworkDTO photoVersionHomeworkDTO1 = modelMapper.map(photoVersionHomework, PhotoVersionHomeworkDTO.class);
                                photoVersionHomeworkDTO1.setPicByte(vlService.decompressZLib(photoVersionHomeworkDTO1.getPicByte()));
                                StringTokenizer st = new StringTokenizer(photoVersionHomeworkDTO1.getTimestamp(), ".");
                                photoVersionHomeworkDTO1.setTimestamp(st.nextToken());
                                return photoVersionHomeworkDTO1;
                            }else throw new VMNotFoundException();
                        }else throw new TeamNotFoundException();
                    } else throw new HomeworkIsPermanentException();
                } else throw new PermissionDeniedException();
            } else throw new HomeworkNotFoundException();
        } else throw new PermissionDeniedException();
    }

    /**
     * Metodo per ritornare il DTO dell'elaborato collegato all'utente autenticato per una data consegna
     */
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

    /**
     * Metodo per ritornare una lista di mappe contenenti l'id, il timestamp e il nome dell'immagine delle versioni elaborati collegate
     * all'elaborato dello studente autenticato per una determinata consegna
     */
    @Override
    public  List<Map<String, Object>> getVersionsHWForStudent(Long assignmentId){
        Optional<Assignment> oa = assignmentRepository.findById(assignmentId);
        if(oa.isPresent()){
            Assignment a = oa.get();
            String studentAuth =SecurityContextHolder.getContext().getAuthentication().getName();
            if(a.getCourseAssignment().getStudents().stream().anyMatch(s->s.getId().equals(studentAuth))){
                List<PhotoVersionHomework> versions = a.getHomeworks().stream().filter(h->h.getStudent().getId().equals(studentAuth)).
                        findFirst().get().getVersions();
                List<Map<String, Object>> l = new ArrayList<>();
                for( PhotoVersionHomework v: versions) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", v.getId());
                    StringTokenizer st = new StringTokenizer(v.getTimestamp(), ".");
                    m.put("timestamp", st.nextToken());
                    m.put("nameFile", v.getNameFile());
                    l.add(m);
                }
                return l;
            }else throw new PermissionDeniedException();
        }else throw new AssignmentNotFoundException();
    }

    /**
     * Metodo per ritornare una lista di mappe contenenti l'id, il timestamp e il nome dell'immagine delle correzioni elaborati collegate
     * all'elaborato (del quale l'ID è inserito nella mappa) dello studente autenticato per una determinata consegna
     */
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
                    StringTokenizer st = new StringTokenizer(c.getTimestamp(), ".");
                    m.put("timestamp", st.nextToken());
                    m.put("nameFile", c.getNameFile());
                    m.put("versionId",c.getIdVersionHomework());
                    l.add(m);
                }
                return l;
            }else throw new PermissionDeniedException();
        }else throw new AssignmentNotFoundException();
    }

}
