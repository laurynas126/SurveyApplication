package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity

@Getter
@Setter
class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne
    private Question question;

    @Version
    @Column(name = "opt_lock_version")
    private Integer optLockVersion;
}
