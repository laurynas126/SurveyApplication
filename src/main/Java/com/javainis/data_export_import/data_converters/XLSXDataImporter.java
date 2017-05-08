package com.javainis.data_export_import.data_converters;

import com.javainis.data_export_import.interfaces.DataImporter;
import com.javainis.survey.entities.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class XLSXDataImporter implements DataImporter{

    private File file;

    @Override
    public Survey importSurvey(File selectedFile) {
        Survey survey = new Survey();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(selectedFile));
            XSSFSheet sheet = workbook.getSheet("Survey");
            if (workbook.getSheet("Survey") == null) return null;
            Row headerRow = sheet.getRow(0);
            List<String> headerColumn = new ArrayList<>();
            for (Cell cell : headerRow){
                if (cell.getCellTypeEnum() == CellType.STRING){
                    headerColumn.add(cell.getStringCellValue());
                }
            }

            List<Question> questions = new ArrayList<>();

            for (Row row : sheet){
                if (row.getRowNum() == 0) continue; // header avoid reading

                String questionType = row.getCell(3).getStringCellValue();
                String questionName = row.getCell(2).getStringCellValue();
                String questionMandatory = row.getCell(1).getStringCellValue();
                double questionNumber = row.getCell(0).getNumericCellValue(); //id set?


                switch (questionType){
                    case "TEXT":
                        FreeTextQuestion freeTextQuestion = new FreeTextQuestion();
                        if (questionMandatory.equals("YES")) freeTextQuestion.setRequired(true);
                        else freeTextQuestion.setRequired(false);

                        freeTextQuestion.setText(questionName);
                        freeTextQuestion.setSurvey(survey);
                        questions.add(freeTextQuestion);
                        System.out.println("TEXT");
                        break;
                    case "CHECKBOX":
                        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
                        if (questionMandatory.equals("YES")) multipleChoiceQuestion.setRequired(true);
                        else multipleChoiceQuestion.setRequired(false);

                        List<Choice> choiceListMulti = new ArrayList<>();
                        for (Cell cell : row){
                            if(cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1 || cell.getColumnIndex() ==  2 || cell.getColumnIndex() == 3) continue;
                            Choice choice = new Choice();

                            if (cell.getCellTypeEnum() == CellType.STRING){
                                String option = cell.getStringCellValue();
                                choice.setText(option);
                            }
                            else if (cell.getCellTypeEnum() == CellType.NUMERIC){
                                double optionNumber = cell.getNumericCellValue();
                                choice.setText(String.valueOf(optionNumber));
                            } else if (cell.getCellTypeEnum() == CellType.BLANK){
                                break;
                            } else {
                                break;
                            }
                            choice.setQuestion(multipleChoiceQuestion);
                            choiceListMulti.add(choice);
                        }
                        multipleChoiceQuestion.setChoices(choiceListMulti);
                        multipleChoiceQuestion.setText(questionName);
                        multipleChoiceQuestion.setSurvey(survey);
                        questions.add(multipleChoiceQuestion);
                        System.out.println("CHECKBOX");
                        break;
                    case "MULTIPLECHOICE":
                        SingleChoiceQuestion singleChoiceQuestion = new SingleChoiceQuestion();
                        if (questionMandatory.equals("YES")) singleChoiceQuestion.setRequired(true);
                        else singleChoiceQuestion.setRequired(false);

                        List<Choice> choiceListSingle = new ArrayList<>();
                        for (Cell cell : row){
                            if(cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1 || cell.getColumnIndex() ==  2 || cell.getColumnIndex() == 3) continue;
                            Choice choice = new Choice();
                            if (cell.getCellTypeEnum() == CellType.STRING){
                                String option = cell.getStringCellValue();
                                choice.setText(option);
                            }
                            else if (cell.getCellTypeEnum() == CellType.NUMERIC){
                                double optionNumber = cell.getNumericCellValue();
                                choice.setText(String.valueOf(optionNumber));
                            } else if (cell.getCellTypeEnum() == CellType.BLANK){
                                break;
                            } else {
                                break;
                            }
                            choice.setQuestion(singleChoiceQuestion);
                            choiceListSingle.add(choice);
                        }
                        singleChoiceQuestion.setChoices(choiceListSingle);
                        singleChoiceQuestion.setText(questionName);
                        singleChoiceQuestion.setSurvey(survey);
                        questions.add(singleChoiceQuestion);
                        System.out.println("MULTIPLECHOICE");
                        break;
                    case "SCALE":
                        IntervalQuestion intervalQuestion = new IntervalQuestion();
                        if (questionMandatory.equals("YES")) intervalQuestion.setRequired(true);
                        else intervalQuestion.setRequired(false);

                        //try ?
                        double intervalMin = row.getCell(4).getNumericCellValue();
                        double intervalMax = row.getCell(5).getNumericCellValue();

                        intervalQuestion.setMin((int) intervalMin);
                        intervalQuestion.setMax((int) intervalMax);

                        intervalQuestion.setText(questionName);
                        intervalQuestion.setSurvey(survey);
                        questions.add(intervalQuestion);

                        System.out.println("SCALE");

                        break;
                }
            }
            survey.setQuestions(questions);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return survey;
    }

    @Override
    public List<SurveyResult> importAnswers(File selectedFile, Survey survey) {
        List<SurveyResult> surveyResultList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(selectedFile));
            XSSFSheet sheet = workbook.getSheet("Answer");
            if (workbook.getSheet("Answer") == null) return null;
            Row headerRow = sheet.getRow(0);
            List<String> headerColumn = new ArrayList<>();
            for (Cell cell : headerRow) {
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    headerColumn.add(cell.getStringCellValue());
                }
            }
            List<Answer> answerList = new ArrayList<>();
            SurveyResult surveyResult = new SurveyResult();

            surveyResult.setSurvey(survey);
            double oldAnswerID = -1;

            for (Row row : sheet) {
                System.out.println(row.getRowNum());
                if (row.getRowNum() == 0) continue; // header avoid reading

                double questionNumber = 0;
                if (row.getCell(1).getCellTypeEnum() == CellType.STRING){
                    questionNumber = Double.parseDouble(row.getCell(1).getStringCellValue());
                } else if (row.getCell(1).getCellTypeEnum() == CellType.NUMERIC){
                    questionNumber = row.getCell(1).getNumericCellValue();
                }
                double answerID = row.getCell(0).getNumericCellValue();
                if (answerID != oldAnswerID){ // esme pakeisti survey id
                    if (oldAnswerID != -1){ //pats pirmas su -1, jo nesaugome
                        surveyResult.setAnswers(answerList);
                        surveyResult.setSurvey(survey);
                        surveyResultList.add(surveyResult);
                    }
                    surveyResult.setAnswers(null);
                    surveyResult = new SurveyResult(); //naujas survey result kitam answer id
                    surveyResult.setSurvey(survey);
                    oldAnswerID = answerID;
                }

                Question question = survey.getQuestions().get((int)questionNumber - 1);

                if (question instanceof FreeTextQuestion){
                    TextAnswer textAnswer = new TextAnswer();
                    if (row.getCell(2).getCellTypeEnum() == CellType.STRING) {
                        textAnswer.setText(row.getCell(2).getStringCellValue());
                    } else if (row.getCell(2).getCellTypeEnum() == CellType.NUMERIC) {
                        textAnswer.setText(String.valueOf(row.getCell(2).getNumericCellValue()));
                    } else if (row.getCell(2).getCellTypeEnum() == CellType.BLANK) {
                        textAnswer.setText(""); // ar null, jei buvo neprivalomas?
                    }
                    textAnswer.setQuestion(question);
                    textAnswer.setResult(surveyResult);
                    answerList.add(textAnswer);
                } else if (question instanceof MultipleChoiceQuestion){
                    MultipleChoiceAnswer multipleChoiceAnswer = new MultipleChoiceAnswer();
                    List<Choice> choices = new ArrayList<>();
                    for (Cell cell : row) {
                        if (cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1) continue;
                        if (row.getCell(cell.getColumnIndex()).getCellTypeEnum() == CellType.NUMERIC) {
                            double number = row.getCell(cell.getColumnIndex()).getNumericCellValue();
                            Choice choice = ((MultipleChoiceQuestion) question).getChoices().get((int)number - 1);
                            choices.add(choice);
                        } else if (row.getCell(cell.getColumnIndex()).getCellTypeEnum() == CellType.BLANK) {
                            break;
                        }
                    }
                    multipleChoiceAnswer.setChoices(choices);
                    multipleChoiceAnswer.setQuestion(question);
                    multipleChoiceAnswer.setResult(surveyResult);
                    answerList.add(multipleChoiceAnswer);
                } else if (question instanceof SingleChoiceQuestion){
                    SingleChoiceAnswer singleChoiceAnswer = new SingleChoiceAnswer();
                    if (row.getCell(2).getCellTypeEnum() == CellType.NUMERIC) {
                        double number = row.getCell(2).getNumericCellValue();
                        singleChoiceAnswer.setChoice(((SingleChoiceQuestion) question).getChoices().get((int)number - 1));
                    } else if (row.getCell(2).getCellTypeEnum() == CellType.BLANK) {
                        singleChoiceAnswer.setChoice(null); //tikrai null?
                    }
                    singleChoiceAnswer.setQuestion(question);
                    singleChoiceAnswer.setResult(surveyResult);
                    answerList.add(singleChoiceAnswer);
                } else if (question instanceof IntervalQuestion){
                    NumberAnswer numberAnswer = new NumberAnswer();
                    if (row.getCell(2).getCellTypeEnum() == CellType.NUMERIC) {
                        double number = row.getCell(2).getNumericCellValue();
                        numberAnswer.setNumber((int)number);
                    } else if (row.getCell(2).getCellTypeEnum() == CellType.BLANK) {
                        numberAnswer.setNumber(null); //
                    }
                    numberAnswer.setQuestion(question);
                    numberAnswer.setResult(surveyResult);
                    answerList.add(numberAnswer);
                }

            }
            surveyResult.setAnswers(answerList);
            surveyResult.setSurvey(survey);
            surveyResultList.add(surveyResult);
            survey.setSurveyResults(surveyResultList);

        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //jei klausimas buvo privalomas pravaliduoti?
        return surveyResultList;
    }
}
