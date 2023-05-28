import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String country;
    private String bio;
    private String location;
    private String webAddress;
    private LocalDateTime birthDate;
    private LocalDateTime signUpDate;
    private ArrayList<User> followers;
    private ArrayList<User> followings;
    private ArrayList<Vote> votes;
    private ArrayList<Direct> directs;
    private byte[] avatar;
    private byte[] header;
    private ArrayList<Tweet> tweets;
    private ArrayList<String> blockedUsers;


    public  User(String username, String firstName, String lastName, String email, String phoneNumber, String password,
                     String country, LocalDateTime birthDate,LocalDateTime signUpDate){
        this.username=username;
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.phoneNumber=phoneNumber;
        this.password=password;
        this.country=country;
        this.birthDate=birthDate;
        this.signUpDate=signUpDate;
        followers=new ArrayList<>();
        followings=new ArrayList<>();
        votes=new ArrayList<>();
        directs=new ArrayList<>();
        tweets=new ArrayList<>();
        blockedUsers=new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public LocalDateTime getSignUpDate() {
        return signUpDate;
    }

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public ArrayList<User> getFollowings() {
        return followings;
    }

    public ArrayList<Direct> getDirects() {
        return directs;
    }

    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public ArrayList<Vote> getVotes() {
        return votes;
    }

    public ArrayList<String> getBlockedUsers() {
        return blockedUsers;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public byte[] getHeader() {
        return header;
    }


}
