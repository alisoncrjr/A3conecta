package Model;

import java.util.UUID;

public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected String fotoPerfilPath;

    public User(String name, String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.password = password;
        this.fotoPerfilPath = "default_profile.png";
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFotoPerfilPath() { return fotoPerfilPath; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFotoPerfilPath(String fotoPerfilPath) { this.fotoPerfilPath = fotoPerfilPath; }


    public void setId(String id) { this.id = id; }


    public boolean checkPassword(String password) {

        if (this.password != null && (this.password.startsWith("$2a$") || this.password.startsWith("$2b$") || this.password.startsWith("$2y$"))) {
            return Utils.PasswordUtil.verifyPassword(this.password, password);
        }

        return this.password != null && this.password.equals(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}