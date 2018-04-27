package it.polito.mad.koko.kokolab3.profile;



import java.io.Serializable;

public class Profile implements Serializable {

    private static final String TAG = "Profile";

    private String  name,
                    email,
                    phone,
                    location,
                    bio,imgUrl, latLng;

    public Profile() {
    }


    public Profile(String name, String email, String phone, String location, String bio, String imgUrl, String latLng) {
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.location=location;
        this.bio=bio;
        this.imgUrl=imgUrl;
        this.latLng = latLng;
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

    public String getPosition() {return latLng;}

    public String getBio() {
        return bio;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPosition(String latLng) {this.latLng=latLng;}

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", bio='" + bio + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", position='" + latLng + '\'' +
                '}';
    }
}
