package com.uber.uber.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Data
public class UserSchema {
    @Id
    private ObjectId id;
    private String name;
    private String email;
    private String password;
    private List<String> role;
    private String phone;
    private String city;
    private String profilePicUrl;
    private String drivingLicense;
    private String vehicleNumber;
    private String status;
    private double[] location; // Add this field
    private String geoHash; // Add this field

    public String getStringId() {
        return id != null ? id.toHexString() : null;
    }

    public void setStringId(String stringId) {
        if (stringId != null && ObjectId.isValid(stringId)) {
            this.id = new ObjectId(stringId);
        }
    }

    // Add these methods for easier access to latitude and longitude
    public double getLatitude() {
        return location != null && location.length > 1 ? location[1] : 0;
    }

    public double getLongitude() {
        return location != null && location.length > 0 ? location[0] : 0;
    }
}