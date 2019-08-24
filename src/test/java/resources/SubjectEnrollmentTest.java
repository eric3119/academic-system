package resources;

import br.ufal.ic.DAO.GenericDAO;
import br.ufal.ic.model.*;
import br.ufal.ic.resources.SubjectEnrollmentResource;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class SubjectEnrollmentTest {

    private GenericDAO dao = mock(GenericDAO.class);
    private SubjectEnrollmentResource resource = new SubjectEnrollmentResource(dao);

    public ResourceExtension RULE = ResourceExtension.builder()
            .addResource(resource)
            .build();

    private Student student;
    private Secretary secretary;
    private Department department;
    private Subject subjectToEnroll;
    private Subject subjectRequired;
    private SubjectEnrollment subjectEnrollment;
    private List<Student> studentList;
    private List<Subject> subjectList;
    @BeforeEach
    @SneakyThrows
    public void setUp() {
        secretary = new Secretary(SecretaryType.PostGraduation);
        FieldUtils.writeField(secretary, "id", 5L, true);
        when(dao.get(Secretary.class, secretary.getId())).thenReturn(secretary);

        department = new Department("deptTest",secretary);
        FieldUtils.writeField(department, "id", 5L, true);
        when(dao.get(Department.class, department.getId())).thenReturn(department);

        student = new Student("eric", "c789123", department, secretary, 153);
        FieldUtils.writeField(student, "id", 12L, true);
        when(dao.get(Student.class, student.getId())).thenReturn(student);
        studentList = Collections.singletonList(student);
        when(dao.findAll(Student.class)).thenReturn(studentList);

        subjectRequired = new Subject("subjRequired","c159553", 123, student.getCredits(), new ArrayList<>(), department, secretary);
        FieldUtils.writeField(subjectRequired, "id", 54L, true);
        when(dao.get(Subject.class, subjectRequired.getId())).thenReturn(subjectRequired);

        subjectToEnroll = new Subject("subjTest","c159753", 123, student.getCredits(), Collections.singletonList(subjectRequired), department, secretary);
        FieldUtils.writeField(subjectToEnroll, "id", 55L, true);
        when(dao.get(Subject.class, subjectToEnroll.getId())).thenReturn(subjectToEnroll);

        subjectList = Arrays.asList(subjectRequired, subjectToEnroll);
        when(dao.findAll(Subject.class)).thenReturn(subjectList);

        subjectEnrollment = new SubjectEnrollment(subjectRequired, student);
        when(dao.findAll(SubjectEnrollment.class)).thenReturn(Collections.singletonList(subjectEnrollment));
    }
    @AfterEach
    public void tearDown(){
        reset(dao);
    }


    @Test
    public void testListStudents(){
        final List<Student> studentList = Collections.singletonList(student);
        when(dao.findAll(Student.class)).thenReturn(studentList);

        final List<Student> response = RULE.target("/enrollsubject")
                .request().get(new GenericType<List<Student>>() {
                });
        assertNotNull(response);
        assertEquals(1, response.size());
        assertTrue(response.containsAll(studentList));
    }

    @Test
    public void testListSubjects(){


        final List<Subject> response = RULE.target("/enrollsubject")
                .queryParam("id_student", student.getId())
                .request().get(new GenericType<List<Subject>>() {
                });
        assertNotNull(response);
        assertEquals(subjectList.size(), response.size());
        assertTrue(response.containsAll(subjectList));
    }

    @Test
    public void testEnrollStudent(){
        final SubjectEnrollment subjectEnrollment = new SubjectEnrollment(subjectToEnroll, student);
        when(dao.persist(SubjectEnrollment.class, subjectEnrollment)).thenReturn(subjectEnrollment);

        final SubjectEnrollment response = RULE.target("/enrollsubject")
                .queryParam("id_student", student.getId())
                .queryParam("id_subject", subjectToEnroll.getId())
                .request().get(SubjectEnrollment.class);

        assertNotNull(response);
        assertEquals(subjectEnrollment, response);
    }

    @Test
    void testEnrollStudentStatusCodes(){

        Response response = RULE.target("/enrollsubject").request().get();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = RULE.target("/enrollsubject")
                .queryParam("id_student", student.getId())
                .request()
                .get();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        SubjectEnrollment newEnrollment = new SubjectEnrollment(subjectToEnroll, student);
        when(dao.persist(SubjectEnrollment.class, newEnrollment))
        .thenReturn(newEnrollment);

        response = RULE.target("/enrollsubject")
                .queryParam("id_student", student.getId())
                .queryParam("id_subject", subjectToEnroll.getId())
                .request()
                .get();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testStudentNotFound(){
        Response response = RULE.target("/enrollsubject")
                .queryParam("id_student", -1)
                .request()
                .get();

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testSubjectNotFound(){
        Response response = RULE.target("/enrollsubject")
                .queryParam("id_student", student.getId())
                .queryParam("id_subject", -1)
                .request()
                .get();

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testStudentNotEnoughCredits(){

        Student s1 = new Student("eric", "c789123", department, secretary, 0);
        try {
            FieldUtils.writeField(s1, "id", 15L, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        when(dao.get(Student.class, s1.getId())).thenReturn(s1);

        Response response = RULE.target("/enrollsubject")
                .queryParam("id_student", s1.getId())
                .queryParam("id_subject", subjectToEnroll.getId())
                .request()
                .get();

        assertNotNull(response);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    void testStudentSubjectsNotSatisfied(){

        Student s1 = new Student("studentNoSubjects", "c789123", department, secretary, subjectToEnroll.getMin_credits());
        try {
            FieldUtils.writeField(s1, "id", 15L, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        when(dao.get(Student.class, s1.getId())).thenReturn(s1);

        Response response = RULE.target("/enrollsubject")
                .queryParam("id_student", s1.getId())
                .queryParam("id_subject", subjectToEnroll.getId())
                .request()
                .get();

        assertNotNull(response);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }
}
