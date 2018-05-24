package it.polito.mad.koko.kokolab3.profile;



import java.io.Serializable;

public class Profile implements Serializable {

    private static final String TAG = "Profile";

    private String  name,
                    email,
                    phone,
                    location,
                    bio,
                    image,
                    position,
                    tokenMessage;

    /**
     * True upon completing the registration.
     */
    private boolean registrationCompleted;

    public Profile() {
    }

    public Profile(String name, String email, String phone, String location, String bio, String imgUrl, String position, String tokenMessage) {
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.location=location;
        this.bio=bio;
        this.image =imgUrl;
        this.position = position;
        this.tokenMessage=tokenMessage;
        this.registrationCompleted = false;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }

    public String getPosition() {
        return position;
    }

    public String getTokenMessage() {
        return tokenMessage;
    }

    public boolean isRegistrationCompleted() {
        return registrationCompleted;
    }

    public void setName(String name) {
        this.name = name;

        checkCompletedRegistration();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLocation(String location) {
        this.location = location;

        checkCompletedRegistration();
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setTokenMessage(String tokenMessage) {
        this.tokenMessage = tokenMessage;
    }

    /**
     * It checks whether this user has completed
     * the registration.
     * This is done by checking that all minimum fields
     * have been properly set.
     */
    private void checkCompletedRegistration() {
        // 'name' field must be set
        if(name == null || name.isEmpty() || name.compareTo("") == 0) {
            registrationCompleted = false;
            return;
        }

        // 'position field must be set
        if(position == null || position.isEmpty() || position.compareTo("") == 0) {
            registrationCompleted = false;
            return;
        }

        registrationCompleted = true;
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
                '}';
    }


}
