package com.example.famcare;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class MedicineFragment extends Fragment {

    private ProgressBar progressBar;
    private ListView medicineList;
    private EditText nameInput, doseInput, reasonInput;
    private Button addButton, editButton, deleteButton;

    private MedicineAdapter adapter;
    private ArrayList<Medicine> medicines = new ArrayList<>();
    private int selectedIndex = -1;

    private FirebaseFirestore db;
    private String userId;
    private ListenerRegistration medicineListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_medicine_fragment, container, false);

        progressBar = v.findViewById(R.id.progressBar);
        medicineList = v.findViewById(R.id.medicineList);
        nameInput = v.findViewById(R.id.nameInput);
        doseInput = v.findViewById(R.id.doseInput);
        reasonInput = v.findViewById(R.id.reasonInput);
        addButton = v.findViewById(R.id.addButton);
        editButton = v.findViewById(R.id.editButton);
        deleteButton = v.findViewById(R.id.deleteButton);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new MedicineAdapter(getContext(), medicines);
        medicineList.setAdapter(adapter);

        medicineList.setOnItemClickListener((parent, view1, pos, id) -> {
            selectedIndex = pos;
            Medicine m = medicines.get(pos);
            nameInput.setText(m.name);
            doseInput.setText(m.dose);
            reasonInput.setText(m.reason);
        });

        addButton.setOnClickListener(v1 -> addMedicine());
        editButton.setOnClickListener(v1 -> editMedicine());
        deleteButton.setOnClickListener(v1 -> confirmDelete());

        startMedicineListener();

        return v;
    }

    private void startMedicineListener() {
        progressBar.setVisibility(View.VISIBLE);
        medicineListener = db.collection("users").document(userId).collection("medicines")
                .addSnapshotListener((snapshots, error) -> {
                    progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(getContext(), "Error loading medicines.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    medicines.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Medicine m = doc.toObject(Medicine.class);
                            medicines.add(m);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (medicineListener != null) medicineListener.remove();
    }

    private void addMedicine() {
        String name = nameInput.getText().toString().trim();
        String dose = doseInput.getText().toString().trim();
        String reason = reasonInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Enter medicine name!", Toast.LENGTH_SHORT).show();
            return;
        }
        String docId = db.collection("users").document(userId)
                .collection("medicines").document().getId();
        Medicine m = new Medicine(docId, name, dose, reason);

        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(userId)
                .collection("medicines").document(docId).set(m)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Medicine added!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    selectedIndex = -1;
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to add medicine.", Toast.LENGTH_SHORT).show();
                });
    }

    private void editMedicine() {
        if (selectedIndex < 0) {
            Toast.makeText(getContext(), "Select a medicine to edit!", Toast.LENGTH_SHORT).show();
            return;
        }
        Medicine med = medicines.get(selectedIndex);
        String name = nameInput.getText().toString().trim();
        String dose = doseInput.getText().toString().trim();
        String reason = reasonInput.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(userId)
                .collection("medicines").document(med.id)
                .set(new Medicine(med.id, name, dose, reason))
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Medicine updated!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    selectedIndex = -1;
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to update medicine.", Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmDelete() {
        if (selectedIndex < 0) {
            Toast.makeText(getContext(), "Select a medicine to delete!", Toast.LENGTH_SHORT).show();
            return;
        }
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Delete medicine?")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> deleteMedicine())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteMedicine() {
        Medicine med = medicines.get(selectedIndex);
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(userId)
                .collection("medicines").document(med.id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Medicine deleted!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    selectedIndex = -1;
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to delete medicine.", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputs() {
        nameInput.setText("");
        doseInput.setText("");
        reasonInput.setText("");
    }
}
