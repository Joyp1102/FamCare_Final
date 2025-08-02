package com.example.famcare;

public class Medicine {
    public String id;
    public String name, dose, reason;

    public Medicine() {} // Needed for Firestore

    public Medicine(String id, String name, String dose, String reason) {
        this.id = id;
        this.name = name;
        this.dose = dose;
        this.reason = reason;
    }
}
