package com.example.studentsrecordbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ExamAdapter extends ArrayAdapter<Exam> {

    Boolean showBin;
    ExamAdapterListener listener;

    public ExamAdapter(@NonNull Context context, ArrayList<Exam> exams, Boolean showBin, ExamAdapterListener listener) {
        super(context, 0, exams);

        this.showBin = showBin;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Exam exam = getItem(position);
        assert exam != null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mark_cell, parent, false);
        }

        Calendar examDate = exam.getDate();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        String dateLiteral = format.format(examDate.getTime());

        int examMark = exam.getMark();
        String examLiteral = examMark == 31 ? "30L" : String.valueOf(examMark);

        String cfuLiteral = exam.getCfu() + " CFU";

        String finalLiteral = dateLiteral + " - " + cfuLiteral;

        TextView nameLabel = convertView.findViewById(R.id.examNameLabel);
        TextView dateLabel = convertView.findViewById(R.id.examDateLabel);
        TextView markLabel = convertView.findViewById(R.id.examMarkLabel);
        ImageButton imageButton = convertView.findViewById(R.id.imageButton);

        imageButton.setVisibility(showBin ? View.VISIBLE : View.GONE);
        imageButton.setOnClickListener(listener -> this.listener.examDeletionRequested(position));

        nameLabel.setText(exam.getName());
        dateLabel.setText(finalLiteral);
        markLabel.setText(examLiteral);

        return convertView;
    }


}
