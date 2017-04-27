package com.javainis.data_export_import.data_converters;

import com.javainis.data_export_import.interfaces.DataExporter;
import com.javainis.survey.entities.*;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import java.io.*;
import java.util.List;

@ApplicationScoped
public class XLSXDataExporter implements DataExporter{

    private void ChoiceQuestion(XSSFRow row, List<Choice> choices)
    {
        int cell = 3;
        for(Choice c: choices)
        {
            row.createCell(cell++).setCellValue(c.getText());
        }
    }

    private void IntervalQuestion(XSSFRow row, int min, int max)
    {
        row.createCell(3).setCellValue(min);
        row.createCell(4).setCellValue(max);
    }

    @Override
    public void exportSurvey(Survey survey, File destination)
    {
        System.out.println("Exporting survey");
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet surveySheet = wb.createSheet("Survey");
        XSSFRow statusRow = surveySheet.createRow(0);
        statusRow.createCell(0).setCellValue("$questionNumber");
        statusRow.createCell(1).setCellValue("$question");
        statusRow.createCell(2).setCellValue("$questionType");
        statusRow.createCell(3).setCellValue("$optionsList");
        List<Question> surveyQuestions = survey.getQuestions();
        for(int i = 0; i < surveyQuestions.size(); i++)
        {
            Question question = surveyQuestions.get(i);
            XSSFRow row = surveySheet.createRow(i+1);
            row.createCell(0).setCellValue(i+1);
            row.createCell(1).setCellValue(question.getText());
            if (question instanceof MultipleChoiceQuestion)
            {
                row.createCell(2).setCellValue("MULTIPLECHOICE");
                ChoiceQuestion(row, ((MultipleChoiceQuestion) question).getChoices());
            }
            else if (question instanceof SingleChoiceQuestion)
            {
                row.createCell(2).setCellValue("CHECKBOX");
                ChoiceQuestion(row, ((SingleChoiceQuestion) question).getChoices());
            }
            else if (question instanceof IntervalQuestion)
            {
                row.createCell(2).setCellValue("SCALE");
                IntervalQuestion iq = (IntervalQuestion) question;
                IntervalQuestion(row, iq.getMinValue(), iq.getMaxValue());
            }
            else{
                row.createCell(2).setCellValue("TEXT");
            }
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(destination);
            wb.write(fileOut);
            fileOut.close();
            System.out.println("Export finished");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void exportAnswers(List<Answer> answers, File destination)
    {

    }

}