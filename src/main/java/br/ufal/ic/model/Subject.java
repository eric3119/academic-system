package br.ufal.ic.model;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@RequiredArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull private String name;
    @NonNull private String code;
    @NonNull private Integer credits;
    @NonNull private Integer min_credits; // TODO default 0
    //@NonNull
    private ArrayList<Subject> requirements;

//    @NonNull
    @ManyToOne
    private Department department;
//    @NonNull
    @OneToOne
    private Secretary secretary;

    public Long getId() {
        return id;
    }
}
