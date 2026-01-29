import java.util.Date;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private long phone_number;
    private String licence_no;
    private String licence_exp;

    // Default constructor
    public User() {
    }

    // Constructor without id (for new users before DB insertion)
    public User(String name, String email, String password, long phone_number, String licence_no, String licence_exp) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone_number = phone_number;
        this.licence_no = licence_no;
        this.licence_exp = licence_exp;

    }

    // Constructor with id (for users loaded from DB)
    public User(int id, String name, String email, String password, long phone_number) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone_number = phone_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getLicence_exp() {
        return licence_exp;
    }

    public void setLicence_exp(String licence_exp) {
        this.licence_exp = licence_exp;
    }

    public String getLicence_no() {
        return licence_no;
    }

    public void setLicence_no(String licence_no) {
        this.licence_no = licence_no;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', phone=" + phone_number + "}";
    }
}
