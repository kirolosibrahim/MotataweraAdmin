package com.kmk.motatawera.admin.ui.fragment.users.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kmk.motatawera.admin.R;
import com.kmk.motatawera.admin.adapter.DoctorAdapter;
import com.kmk.motatawera.admin.databinding.FragmentDoctorBinding;
import com.kmk.motatawera.admin.model.DoctorModel;
import com.kmk.motatawera.admin.model.StudentModel;

import java.util.ArrayList;
import java.util.List;

public class DoctorFragment extends Fragment {

    private FragmentDoctorBinding binding;

    private FirebaseFirestore db;
    private DoctorAdapter adapter;
    private List<DoctorModel> list;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_doctor, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.recyclerDoctor;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();


        binding.progressbar.setVisibility(View.VISIBLE);

        binding.spinnerDoctorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null & list != null) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                    getUsers(position);
                }else {
                    getUsers(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void getUsers(int i ) {
        adapter = new DoctorAdapter(getActivity(), list);
       db.collection("doctor").addSnapshotListener((value, error) -> {
            if (error == null) {
                if (value == null) {
                    binding.progressbar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {

                    for (DocumentChange documentChange : value.getDocumentChanges()) {

                        switch (documentChange.getType()) {

                            case ADDED:
                                onDocumentAdded(documentChange,i);
                                break;
                            case MODIFIED:

                                onDocumentModified(documentChange);
                                break;
                            case REMOVED:
                                onDocumentRemoved(documentChange);
                                break;

                        }

                        binding.recyclerDoctor.setAdapter(adapter);
                        binding.progressbar.setVisibility(View.GONE);

                    }
                }
            } else {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                binding.progressbar.setVisibility(View.GONE);
            }
        });


    }

    private Query checkQuery(int i) {

        Query query;
        switch (i) {

            case 1:
                query = db.collection("doctor")
                        .whereEqualTo("approved", false)
                        .whereEqualTo("disable", false);
                break;
            case 2:
                query = db.collection("doctor")
                        .whereEqualTo("approved", true)
                        .whereEqualTo("disable", false);
                break;

            case 3:
                query = db.collection("doctor")
                        .whereEqualTo("approved", true)
                        .whereEqualTo("disable", true);
                break;

            case 0:
            default:
                query = db.collection("doctor");

        }
        return query;
    }
    private void onDocumentAdded(DocumentChange documentChange,int i) {
        DoctorModel model = documentChange.getDocument().toObject(DoctorModel.class);
        switch (i) {

            case 1:

                    if (!model.isDisable()&&!model.isApproved()){
                        list.add(model);
                        adapter.notifyItemInserted(list.size() - 1);
                        adapter.getItemCount();
                    }
                break;
            case 2:

                    if (!model.isDisable()&&model.isApproved()){
                        list.add(model);
                        adapter.notifyItemInserted(list.size() - 1);
                        adapter.getItemCount();
                    }

                break;

            case 3:
                    if (model.isDisable()&&model.isApproved()){
                        list.add(model);
                        adapter.notifyItemInserted(list.size() - 1);
                        adapter.getItemCount();
                    }

                break;

            case 0:
            default:
                        list.add(model);
                        adapter.notifyItemInserted(list.size() - 1);
                        adapter.getItemCount();

        }

    }

    private void onDocumentModified(DocumentChange documentChange) {
        try {
            DoctorModel model = documentChange.getDocument().toObject(DoctorModel.class);
            if (documentChange.getOldIndex() == documentChange.getNewIndex()) {
                // Item changed but remained in same position
                list.set(documentChange.getOldIndex(), model);
                adapter.notifyItemChanged(documentChange.getOldIndex());
            } else {
                // Item changed and changed position
                list.remove(documentChange.getOldIndex());
                list.add(documentChange.getNewIndex(), model);
                adapter.notifyItemRangeChanged(0, list.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onDocumentRemoved(DocumentChange documentChange) {
        try {
            list.remove(documentChange.getOldIndex());
            adapter.notifyItemRemoved(documentChange.getOldIndex());
            adapter.notifyItemRangeChanged(0, list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}