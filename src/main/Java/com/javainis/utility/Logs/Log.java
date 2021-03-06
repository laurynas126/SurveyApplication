package com.javainis.utility.Logs;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "logs")
@Getter
@Setter
public class Log
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long LogID;

    @Size(max = 70)
    @Column(name = "user_name")
    private String userName;

    @Size(max = 50)
    @Column(name = "user_email")
    private String userEmail;

    @Size(max = 10)
    @Column(name = "rights")
    private String rights;

    @Column(name = "time")
    private Timestamp time;

    @Size(max = 100)
    @Column(name = "class_name")
    private String class_name;

    @Size(max = 100)
    @Column(name = "method_name")
    private String method_name;
}
