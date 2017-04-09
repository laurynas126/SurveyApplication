package com.javainis.survey.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity

@Getter
@Setter
@DiscriminatorValue("4")
@NamedQueries({
        @NamedQuery(name = "Choice.findAll", query = "SELECT c FROM IntervalQuestion c")
})
@Table(name = "interval_choice")
public class IntervalQuestion extends Question{

    private int min_value;
    private int max_value;
}