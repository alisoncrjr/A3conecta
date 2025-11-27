package dao;

public class UserRecord {
    private final String id;
    private final String name;
    private final String email;
    private final String role;
    private final String passwordHash;
    private final String fotoPerfilPath;
    private final String categoria;
    private final String descricaoServico;
    private final String cnpj;
    private final String cpf;
    private final String endereco;
    private final String telefone;

    public UserRecord(String id, String name, String email, String role, String passwordHash, String fotoPerfilPath,
                      String categoria, String descricaoServico, String cnpj, String cpf, String endereco, String telefone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.fotoPerfilPath = fotoPerfilPath;
        this.categoria = categoria;
        this.descricaoServico = descricaoServico;
        this.cnpj = cnpj;
        this.cpf = cpf;
        this.endereco = endereco;
        this.telefone = telefone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getFotoPerfilPath() { return fotoPerfilPath; }
    public String getPasswordHash() { return passwordHash; }
    public String getCategoria() { return categoria; }
    public String getDescricaoServico() { return descricaoServico; }
    public String getCnpj() { return cnpj; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }
    public String getTelefone() { return telefone; }
}
