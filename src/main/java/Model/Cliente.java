package Model;

public class Cliente extends User {
    private String cpf;
    private String endereco;
    private String telefone;


    public Cliente(String name, String email, String password, String cpf) {
        super(name, email, password);
        this.cpf = cpf;
        this.endereco = "";
        this.telefone = "";
        setFotoPerfilPath("default_profile.png");
    }


    public Cliente(String name, String email, String password, String cpf, String endereco, String telefone) {
        super(name, email, password);
        this.cpf = cpf;
        this.endereco = endereco;
        this.telefone = telefone;
        setFotoPerfilPath("default_profile.png");
    }

    // Getters
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }
    public String getTelefone() { return telefone; }

    // Setters
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}