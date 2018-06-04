package it.polito.mad.koko.kokolab3.profile;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * Properties that don't map to class fields are ignored when
 * serializing to a class annotated with this annotation.
 * https://firebase.google.com/docs/reference/android/com/google/firebase/database/IgnoreExtraProperties
 */
@IgnoreExtraProperties
public class Profile implements Serializable {

    private static final String TAG = "Profile";

    private String name,
            email,
            phone,
            location,
            bio,
            image,
            position,
            tokenMessage,
            totalStars,
            completedExchanges;

    public Profile() {
    }


    public Profile(String name,
                   String email,
                   String phone, String location, String bio, String imgUrl, String position, String tokenMessage, String totalStars, String completedExchanges) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.bio = bio;
        this.image = imgUrl;
        this.position = position;
        this.tokenMessage = tokenMessage;
        this.totalStars = totalStars;
        this.completedExchanges = completedExchanges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTokenMessage() {
        return tokenMessage;
    }

    public void setTokenMessage(String tokenMessage) {
        this.tokenMessage = tokenMessage;
    }

    public String getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(String totalStars) {
        this.totalStars = totalStars;
    }

    public String getCompletedExchanges() {
        return completedExchanges;
    }

    public void setCompletedExchanges(String completedExchanges) {
        this.completedExchanges = completedExchanges;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", bio='" + bio + '\'' +
                ", image='" + image + '\'' +
                ", position='" + position + '\'' +
                ", totalStars='" + totalStars + '\'' +
                ", completedExchanges='" + completedExchanges + '\'' +
                '}';
    }
}