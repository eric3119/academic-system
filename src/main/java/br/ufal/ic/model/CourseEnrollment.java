package br.ufal.ic.model;

import javax.persistence.*;

@Entity
public class CourseEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Course course;
    @ManyToOne
    private Student student;
    private boolean active;
}
