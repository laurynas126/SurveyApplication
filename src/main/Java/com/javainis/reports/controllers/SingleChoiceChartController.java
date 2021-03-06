package com.javainis.reports.controllers;

import com.javainis.reports.api.SingleChoiceQuestionReport;
import com.javainis.reports.mybatis.model.*;
import lombok.Getter;
import org.apache.deltaspike.core.api.future.Futureable;
import org.primefaces.model.chart.PieChartModel;

import javax.ejb.AsyncResult;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;
@Named
@Dependent
@Alternative
public class SingleChoiceChartController implements SingleChoiceQuestionReport, Serializable {

    @Getter
    private SingleChoiceQuestion singleChoiceQuestion;
    @Getter
    private List<SingleChoiceAnswer> singleChoiceAnswers;

    @Getter
    private PieChartModel model;

    public void init() {
        createPieModel();
    }

    private void createPieModel() {
        model = new PieChartModel();

        for(Choice q: singleChoiceQuestion.getChoices())
        {
            int count = 0;
            for(SingleChoiceAnswer a: singleChoiceAnswers){
                if(a.getChoiceId().equals(q.getId()) ){
                    count++;
                }
            }
            model.set(q.getText(), count);
        }

        model.setLegendPosition("s");
        model.setShowDataLabels(true);
        model.setShadow(true);
    }

    @Override
    public String getTemplateName() {
        return "single-choice-show.xhtml";
    }

    @Override
    public void setQuestion(Question question) {
        if(question instanceof SingleChoiceQuestion) {
            singleChoiceQuestion = (SingleChoiceQuestion) question;
            singleChoiceAnswers = (List<SingleChoiceAnswer>) (List<?>) singleChoiceQuestion.getAnswers();
        }
    }

    @Override
    @Futureable
    public Future<Void> generateReportAsync() {
        init();
        return new AsyncResult<>(null);
    }
}
