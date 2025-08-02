package com.example.famcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 101;
    private static final int TAKE_PHOTO = 102;

    private ImageView profilePic;
    private TextView userName, userEmail, userAge;
    private Button btnAuthor, btnVersion, btnInstructions, btnEditDetails;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        profilePic = v.findViewById(R.id.profilePic);
        userName = v.findViewById(R.id.userName);
        userEmail = v.findViewById(R.id.userEmail);
        userAge = v.findViewById(R.id.userAge);
        btnAuthor = v.findViewById(R.id.btnAuthor);
        btnVersion = v.findViewById(R.id.btnVersion);
        btnInstructions = v.findViewById(R.id.btnInstructions);
        btnEditDetails = v.findViewById(R.id.btnEditDetails);

        prefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        // Load saved photo
        loadProfilePhoto();

        // Tap to change photo
        profilePic.setOnClickListener(v1 -> showPhotoOptions());

        // Get Firebase user and local details
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userName.setText(getUserName(user));
        userEmail.setText(user != null && user.getEmail() != null ? user.getEmail() : "");
        int age = prefs.getInt("age", 0);
        userAge.setText(age > 0 ? "Age: " + age : "Age: -");

        btnAuthor.setOnClickListener(v1 -> showDialog("Author", "This app was developed by PAPA."));
        btnVersion.setOnClickListener(v1 -> showDialog("Version", "Version 1.0.0"));
        btnInstructions.setOnClickListener(v1 -> showDialog("Instructions", "1. Add medicines\n2. Edit or delete as needed\n3. Use navigation to access all features.\n\nFor more help, contact support."));
        btnEditDetails.setOnClickListener(v1 -> showEditDetailsDialog());

        return v;
    }

    private void loadProfilePhoto() {
        String uriStr = prefs.getString("profile_photo_uri", null);
        if (uriStr != null) {
            Glide.with(this).load(Uri.parse(uriStr)).placeholder(R.drawable.ic_profile_avatar).into(profilePic);
        } else {
            profilePic.setImageResource(R.drawable.ic_profile_avatar);
        }
    }

    private void showPhotoOptions() {
        String[] options = {"Choose from Gallery", "Take Photo"};
        new AlertDialog.Builder(getContext())
                .setTitle("Update Profile Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickIntent, PICK_IMAGE);
                    } else {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, TAKE_PHOTO);
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != getActivity().RESULT_OK) return;

        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profilePic);
            prefs.edit().putString("profile_photo_uri", imageUri.toString()).apply();
            Toast.makeText(getContext(), "Profile photo updated!", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == TAKE_PHOTO && data != null && data.getExtras() != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            // Save to cache directory and get URI
            try {
                File file = new File(getActivity().getCacheDir(), "profile_photo.jpg");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                Uri photoUri = Uri.fromFile(file);
                Glide.with(this).load(photoUri).into(profilePic);
                prefs.edit().putString("profile_photo_uri", photoUri.toString()).apply();
                Toast.makeText(getContext(), "Profile photo updated!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to save photo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getUserName(FirebaseUser user) {
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            return user.getDisplayName();
        }
        return prefs.getString("name", "User");
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showEditDetailsDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_details, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editAge = dialogView.findViewById(R.id.editAge);

        editName.setText(userName.getText().toString().trim());
        int age = prefs.getInt("age", 0);
        editAge.setText(age > 0 ? String.valueOf(age) : "");

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Details")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = editName.getText().toString().trim();
                    String newAgeStr = editAge.getText().toString().trim();
                    int newAge = 0;
                    try { newAge = Integer.parseInt(newAgeStr); } catch (Exception ignored) {}

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("name", newName);
                    if (newAge > 0) editor.putInt("age", newAge);
                    editor.apply();

                    userName.setText(newName);
                    userAge.setText(newAge > 0 ? "Age: " + newAge : "Age: -");
                    Toast.makeText(getContext(), "Details updated!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
