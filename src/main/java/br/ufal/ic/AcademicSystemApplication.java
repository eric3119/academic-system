package br.ufal.ic;

import br.ufal.ic.DAO.DepartmentDAO;
import br.ufal.ic.DAO.StudentDAO;
import br.ufal.ic.DAO.SubjectDAO;
import br.ufal.ic.model.*;
import br.ufal.ic.resources.DepartmentResource;
import br.ufal.ic.resources.StudentResource;
import br.ufal.ic.resources.SubjectResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AcademicSystemApplication extends Application<AcademicSystemConfiguration> {

    public static void main(String[] args) throws Exception {
        new AcademicSystemApplication().run(args);
    }

    @Override
    public String getName(){
        return "academic-system";
    }

    @Override
    public void initialize(Bootstrap<AcademicSystemConfiguration> bootstrap) {
        //super.initialize(bootstrap);
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(AcademicSystemConfiguration configuration,
                    Environment environment) throws Exception {

        final StudentDAO studentDAO = new StudentDAO(hibernateBundle.getSessionFactory());
        final SubjectDAO subjectDAO = new SubjectDAO(hibernateBundle.getSessionFactory());
        final DepartmentDAO departmentDAO = new DepartmentDAO(hibernateBundle.getSessionFactory());

        environment.jersey().register(new StudentResource(studentDAO));
        environment.jersey().register(new SubjectResource(subjectDAO));
        environment.jersey().register(new DepartmentResource(departmentDAO));
    }

    private final HibernateBundle<AcademicSystemConfiguration> hibernateBundle = new HibernateBundle<AcademicSystemConfiguration>(
            AcademicOffer.class,
            Course.class,
            CourseEnrollment.class,
            Department.class,
            Professor.class,
            Secretary.class,
            Student.class,
            Subject.class,
            SubjectEnrollment.class
    ) {
        public DataSourceFactory getDataSourceFactory(AcademicSystemConfiguration academicSystemConfiguration) {
            return academicSystemConfiguration.getDataSourceFactory();
        }
    };
}
