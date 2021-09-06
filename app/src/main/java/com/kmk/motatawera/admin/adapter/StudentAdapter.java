package com.kmk.motatawera.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kmk.motatawera.admin.R;
import com.kmk.motatawera.admin.databinding.FormatStudentBinding;
import com.kmk.motatawera.admin.model.StudentModel;
import com.kmk.motatawera.admin.util.CheckInternetConn;

import java.util.List;

import static com.kmk.motatawera.admin.util.ShowAlert.SHOW_ALERT;


public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<StudentModel> studentModelList;
    private Context context;
    private OnStudentListener onStudentListener;


    public StudentAdapter(List<StudentModel> studentModelList, Context context, OnStudentListener onStudentListener) {
        this.studentModelList = studentModelList;
        this.context = context;
        this.onStudentListener = onStudentListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FormatStudentBinding formatStudentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.format_student, parent, false);
        return new ViewHolder(formatStudentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentModel studentModel = studentModelList.get(position);


        if (studentModel.isDisable()) {
            holder.formatStudentBinding.studentItemDelete.setVisibility(View.GONE);
            holder.formatStudentBinding.txtDisableStudent.setText("unDisable");
        } else {
            holder.formatStudentBinding.txtDisableStudent.setText("Disable");
        }


        holder.formatStudentBinding.txtNameStudent.setText("Name" + "  " + ":" + "  " + studentModel.getName());
        holder.formatStudentBinding.txtIdStudent.setText("ID" + "  " + ":" + "  " + studentModel.getId());


        holder.formatStudentBinding.studentItemDelete.setOnClickListener(v -> {
            //delete student
            if (new CheckInternetConn(context).isConnection()) {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("هل تريد خذف هذا الحساب؟")
                        .setPositiveButton("لا", null)
                        .setNegativeButton("نعم", (dialog, which) -> DeleteStudent(studentModel.getId()))
                        .create().show();
            } else
                SHOW_ALERT(context, context.getString(R.string.noInternet));

        });

        holder.formatStudentBinding.studentItemDisable.setOnClickListener(v -> {
            //disable student
            if (new CheckInternetConn(context).isConnection()) {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("هل تريد ايقاف هذا الحساب؟")
                        .setPositiveButton("لا", null)
                        .setNegativeButton("نعم", (dialog, which) -> {
                            notifyDataSetChanged();
                            onStudentListener.onStudentDisableClick(position, studentModelList, context);
                        })
                        .create()
                        .show();
            } else
                SHOW_ALERT(context, context.getString(R.string.noInternet));

        });


    }

    @Override
    public int getItemCount() {
        return studentModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        FormatStudentBinding formatStudentBinding;

        public ViewHolder(@NonNull FormatStudentBinding formatStudentBinding) {
            super(formatStudentBinding.getRoot());
            this.formatStudentBinding = formatStudentBinding;
        }

    }

    private void DeleteStudent(String ID) {
        if (new CheckInternetConn(context).isConnection()) {
            FirebaseFirestore.getInstance().collection("student")
                    .document(ID)
                    .delete().addOnCompleteListener(task -> {

                Toast.makeText(context, "Student Record Deleted Successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "Please Check your internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    public interface OnStudentListener {
        void onStudentDisableClick(int position, List<StudentModel> studentModelList, Context context);
    }

}
