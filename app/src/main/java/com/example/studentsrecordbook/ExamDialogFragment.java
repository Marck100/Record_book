package com.example.studentsrecordbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ExamDialogFragment extends DialogFragment {

    DBHandler dbHandler;

    UpdateListener listener;

    public ExamDialogFragment(UpdateListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        assert arguments != null;
        User user = (User) arguments.getSerializable("user");
        Exam exam = (Exam) arguments.getSerializable("exam");
        assert exam != null;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);

        builder.setTitle(exam.getName());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        String dateLiteral = format.format(exam.getDate().getTime());
        builder.setMessage(dateLiteral);
        builder.setNeutralButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("CLICK", "CLICK");
            }
        });
        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHandler = new DBHandler(getContext());
                int result = dbHandler.deleteExam(exam);

                if (result == 0) {
                    Toast.makeText(getContext(), "Exam has not been deleted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Exam delete successfully.", Toast.LENGTH_SHORT).show();
                    listener.callback(getView());
                }
            }
        });


        return builder.create();
    }

}
