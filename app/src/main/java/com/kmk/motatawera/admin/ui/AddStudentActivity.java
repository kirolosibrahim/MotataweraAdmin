package com.kmk.motatawera.admin.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kmk.motatawera.admin.R;
import com.kmk.motatawera.admin.databinding.ActivityAddStudentBinding;
import com.kmk.motatawera.admin.model.StudentModel;
import com.kmk.motatawera.admin.util.CheckInternetConn;

import org.jetbrains.annotations.NotNull;

import static com.kmk.motatawera.admin.util.Hide_Keyboard.hideKeyboard;
import static com.kmk.motatawera.admin.util.ShowAlert.SHOW_ALERT;


public class AddStudentActivity extends AppCompatActivity {

    private ActivityAddStudentBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_student);

        binding.btnAddStudent.setOnClickListener(v -> {
            if (new CheckInternetConn(this).isConnection()) {
                validation();

            } else Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        });


    }

    private void cleardata() {
        binding.etStudentId.getText().clear();
        binding.etConfirmStudentId.getText().clear();
        binding.etStudentFullname.getText().clear();
        binding.etStudentId.requestFocus();
    }

    private void validation() {
        String uid = binding.etStudentId.getText().toString();
        String password = binding.etConfirmStudentId.getText().toString();
        String name = binding.etStudentFullname.getText().toString();

        if (!uid.equals(password)) {
            SHOW_ALERT(this, "Invalid Confirm ID");
            return;
        }

        if (name.isEmpty()) {
            SHOW_ALERT(this, "Please Enter Student Full Name");
            return;
        }

        if (uid.isEmpty()) {
            SHOW_ALERT(this, "Please Enter Student ID");
            return;
        }
        if (password.isEmpty()) {
            SHOW_ALERT(this, "Please Enter Student Confirm ID");
            return;
        }


        hideKeyboard(this);

        int gender = binding.spGender.getSelectedItemPosition() + 1;
        int branch = binding.spBranch.getSelectedItemPosition() + 1;
        int department = binding.spDepartment.getSelectedItemPosition() + 1;
        int grad = binding.spGrad.getSelectedItemPosition() + 1;

        CreateNewStudent(uid, password, name, gender, branch, department, grad);

    }


    private void CreateNewStudent(String uid, String password, String name, int Gender, int Branch, int Department, int Grad) {

        //run progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("wait");
        progressDialog.setCancelable(false);
        progressDialog.show();


        StudentModel studentModel = new StudentModel();
        studentModel.setId(uid);
        studentModel.setBranch(Branch);
        studentModel.setDepartment(Department);
        studentModel.setGrad(Grad);
        studentModel.setName(name);
        studentModel.setPhone("");
        studentModel.setPhoto("");
        studentModel.setEmail("");
        studentModel.setDisable(false);
        studentModel.setGender(Gender);
        studentModel.setPassword(password);


        db = FirebaseFirestore.getInstance();
        db.collection("student")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Toast.makeText(AddStudentActivity.this, "Student Code is Already Exist", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            db.collection("student").document(uid).set(studentModel).addOnCompleteListener(task1 -> {
                                Toast.makeText(AddStudentActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                cleardata();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            });

                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AddStudentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




    }
}