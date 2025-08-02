package com.example.famcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class HomeActivity extends AppCompatActivity {

    // Views
    private TextView greetingText, nextMedTime, nextMedName, nextMedDetails, tipText, appointmentText;
    private MaterialButton missedButton, emergencyButton;
    private FloatingActionButton fabScan;
    private BottomNavigationView bottomNav;
    private ImageButton menuButton;
    private ImageView appLogo;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // Bind views
        greetingText = findViewById(R.id.greetingText);
        nextMedTime = findViewById(R.id.nextMedTime);
        nextMedName = findViewById(R.id.nextMedName);
        nextMedDetails = findViewById(R.id.nextMedDetails);
        tipText = findViewById(R.id.tipText);
        appointmentText = findViewById(R.id.appointmentText);
        missedButton = findViewById(R.id.missedButton);
        emergencyButton = findViewById(R.id.emergencyButton);
        fabScan = findViewById(R.id.fabScan);
        bottomNav = findViewById(R.id.bottomNav);
        menuButton = findViewById(R.id.menuButton);
        appLogo = findViewById(R.id.appLogo);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // User info
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String name = prefs.getString("name", "User");
        greetingText.setText("Good Morning, " + name);

        // Drawer header
        android.view.View headerView = navigationView.getHeaderView(0);
        TextView drawerUserName = headerView.findViewById(R.id.drawer_user_name);
        drawerUserName.setText("Hello, " + name);

        // Button actions
        missedButton.setOnClickListener(v -> {
            Toast.makeText(this, "Missed dose details coming soon!", Toast.LENGTH_SHORT).show();
        });

        emergencyButton.setOnClickListener(v -> {
            String emergencyNumber = "+1234567890";
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + emergencyNumber));
            startActivity(callIntent);
        });

        fabScan.setOnClickListener(v -> {
            Toast.makeText(this, "Scan Medicine feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        menuButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.LEFT);
        });

        appLogo.setOnClickListener(v -> {
            Toast.makeText(this, "FamCare", Toast.LENGTH_SHORT).show();
        });

        // Bottom navigation
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already on home, do nothing
                return true;
            } else if (id == R.id.nav_guide) {
                Toast.makeText(HomeActivity.this, "Guide clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_meds) {
                Intent intent = new Intent(HomeActivity.this, MedicineActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_profile) {
                // Opens ProfileActivity
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Set default selected tab
        bottomNav.setSelectedItemId(R.id.nav_home);

        // Demo content for now:
        nextMedTime.setText("8:00 AM");
        nextMedName.setText("Acetaminophen");
        nextMedDetails.setText("1 Pill • Before Food");
        tipText.setText("💡 Tip: Take meds with water unless told otherwise.");
        appointmentText.setText("📅 Appointment: Tomorrow at 11:00 AM with Dr. Patel");

        // Drawer navigation logic
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
                Toast.makeText(HomeActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_settings) {
                Toast.makeText(HomeActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();
                getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        });
    }
}
