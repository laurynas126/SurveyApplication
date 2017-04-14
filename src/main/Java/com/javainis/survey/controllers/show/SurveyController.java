package com.javainis.survey.controllers.show;

import com.javainis.survey.dao.SurveyDAO;
import com.javainis.survey.dao.SurveyResultDAO;
import com.javainis.survey.entities.*;
import lombok.Getter;
import lombok.Setter;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class SurveyController implements Serializable{

    @Inject
    private SurveyDAO surveyDAO;

    @Inject
    private SurveyResultDAO surveyResultDAO;

    @Setter
    @Getter
    private String surveyUrl;

    @Getter
    private Survey survey;

    @Getter
    private Map<Question, Answer> answers = new HashMap<>();

    public void init(){
        // Check if parameter exists
        if(surveyUrl == null){
            return;
        }

        // Find survey
        try{
            survey = surveyDAO.findByUrl(surveyUrl);
        }catch (NoResultException ex){
            return;
        }

        //Init answer objects
        for(Question question : survey.getQuestions()){
            if(question.getClass().getSimpleName().equals("FreeTextQuestion")){
                Answer answer = new TextAnswer();
                answer.setQuestion(question);
                answers.put(question, answer);
            }else if(question.getClass().getSimpleName().equals("IntervalQuestion")){
                Answer answer = new NumberAnswer();
                answer.setQuestion(question);
                answers.put(question, answer);
            }else if(question.getClass().getSimpleName().equals("SingleChoiceQuestion")){

                Answer answer = new SingleChoiceAnswer();
                answer.setQuestion(question);
                answers.put(question, answer);
            }else if(question.getClass().getSimpleName().equals("MultipleChoiceQuestion")){
                Answer answer = new MultipleChoiceAnswer();
                answer.setQuestion(question);
                answers.put(question, answer);
            }
        }
    }

    @Transactional
    public void submitAnswers(){
        // Create SurveyResult object
        SurveyResult result = new SurveyResult();
        result.setSurvey(survey);

        // Validation?

        List<Answer> answerList = new ArrayList<>(answers.values());
        for(Answer answer : answerList){
            answer.setResult(result);
        }
        result.setAnswers(answerList);

        // Save answers to DB
        surveyResultDAO.create(result);
    }
}
