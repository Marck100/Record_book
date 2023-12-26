package com.example.studentsrecordbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBHandler extends SQLiteOpenHelper {
    private static final String dbName = "librettodb";
    private static final int dbVersion = 4;
    // User
    private static final String userTableName = "users";
    private static final String userMatricolaCol = "matricola";
    private static final String userFullnameCol = "fullname";
    private static final String userBirthdayCol = "birthday";
    private static final String userPasswordCol = "password";

    // Exam
    private static final String examTableName = "exams";
    private static final String examUserCol = "user";
    private static final String examNameCol = "name";
    private static final String examCreditsCol = "credits";
    private static final String examDateCol = "date";
    private static final String examMarkCol = "mark";

    public DBHandler(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String userQuery = "CREATE TABLE " + userTableName + "("
                + userMatricolaCol + " TEXT PRIMARY KEY,"
                + userFullnameCol + " TEXT,"
                + userBirthdayCol + " TEXT,"
                + userPasswordCol + " TEXT)";

        String examQuery = "CREATE TABLE " + examTableName + "("
                + examNameCol + " TEXT,"
                + examUserCol + " TEXT,"
                + examCreditsCol + " INT,"
                + examDateCol + " TEXT,"
                + examMarkCol + " INT,"
                + "PRIMARY KEY(" + examNameCol + ", " + examUserCol + ")"
                + ")";

        db.execSQL(userQuery);
        db.execSQL(examQuery);
    }

    public void addNewUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(userMatricolaCol, user.getMatricola());
        values.put(userFullnameCol, user.getFullName());
        values.put(userBirthdayCol, user.getBirthdayLiteral());
        values.put(userPasswordCol, user.getPassword());

        db.insert(userTableName, null, values);
        db.close();
    }

    public Optional<User> loadUser(String matricola, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                userMatricolaCol,
                userFullnameCol,
                userBirthdayCol,
                userPasswordCol
        };

        String selection = userMatricolaCol + " = ? AND " + userPasswordCol + " = ?";
        String[] selectionArgs = {matricola, password};

        Cursor cursor = db.query(
                userTableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Optional<User> user = Optional.empty();

        if (cursor.moveToNext()) {
            String newMatricola = cursor.getString(cursor.getColumnIndexOrThrow(userMatricolaCol));
            String newFullname = cursor.getString(cursor.getColumnIndexOrThrow(userFullnameCol));
            String newPassword = cursor.getString(cursor.getColumnIndexOrThrow(userPasswordCol));
            String newBirthday = cursor.getString(cursor.getColumnIndexOrThrow(userBirthdayCol));

            try {
                user = Optional.of(new User(newFullname, newMatricola, newPassword, newBirthday));
            } catch (ParseException ignored) {
            }

        }

        cursor.close();
        db.close();

        return user;
    }

    public void addNewExam(Exam exam) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(examUserCol, exam.getUser());
        values.put(examNameCol, exam.getName());
        values.put(examMarkCol, exam.getMark());
        values.put(examCreditsCol, exam.getCfu());
        values.put(examDateCol, exam.getDateLiteral());

        db.insert(examTableName, null, values);
        db.close();
    }

    public Optional<Exam> loadExam(String user, String name) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                examMarkCol,
                examDateCol,
                examCreditsCol,
                examNameCol,
                examUserCol
        };

        String selection = examUserCol + " = ? AND " + examNameCol + " = ?";
        String[] selectionArgs = {user, name};

        Cursor cursor = db.query(
                examTableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Optional<Exam> exam = Optional.empty();

        if (cursor.moveToNext()) {
            String newExamUser = cursor.getString(cursor.getColumnIndexOrThrow(examUserCol));
            String newExamName = cursor.getString(cursor.getColumnIndexOrThrow(examNameCol));
            int newExamCredits = cursor.getInt(cursor.getColumnIndexOrThrow(examCreditsCol));
            int newExamMark = cursor.getInt(cursor.getColumnIndexOrThrow(examMarkCol));
            String newDate = cursor.getString(cursor.getColumnIndexOrThrow(examDateCol));

            try {
                exam = Optional.of(new Exam(newExamUser, newExamName, newExamMark, newExamCredits, newDate));
            } catch (ParseException ignored) {
            }

        }

        cursor.close();
        db.close();

        return exam;
    }

    public List<Exam> loadUserExams(String user) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                examMarkCol,
                examDateCol,
                examCreditsCol,
                examNameCol,
                examUserCol
        };

        String selection = examUserCol + " = ?";
        String[] selectionArgs = {user};
        String sortOrder = examDateCol + " DESC";

        Cursor cursor = db.query(
                examTableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        List<Exam> exams = new ArrayList<>();

        while (cursor.moveToNext()) {
            String newExamUser = cursor.getString(cursor.getColumnIndexOrThrow(examUserCol));
            String newExamName = cursor.getString(cursor.getColumnIndexOrThrow(examNameCol));
            int newExamCredits = cursor.getInt(cursor.getColumnIndexOrThrow(examCreditsCol));
            int newExamMark = cursor.getInt(cursor.getColumnIndexOrThrow(examMarkCol));
            String newDate = cursor.getString(cursor.getColumnIndexOrThrow(examDateCol));

            try {
                Exam exam = new Exam(newExamUser, newExamName, newExamMark, newExamCredits, newDate);
                exams.add(exam);
            } catch (ParseException ignored) {
            }

        }

        cursor.close();
        db.close();

        return exams;
    }

    public int deleteExam(Exam exam) {
        SQLiteDatabase db = getWritableDatabase();


        String selection = examUserCol + " = ? AND " + examNameCol + " = ?";
        String[] selectionArgs = {exam.getUser(), exam.getName()};

        return db.delete(examTableName, selection, selectionArgs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + userTableName);
        db.execSQL("DROP TABLE IF EXISTS " + examTableName);
        onCreate(db);
    }

}
