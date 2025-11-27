package Model;

import java.util.ArrayList;
import java.util.List;

public class Prestador extends User {
    private String cnpj;
    private String descricaoServico;
    private String endereco;
    private String telefone;
    private String categoria;
    private List<Avaliacao> avaliacoes;


    public Prestador(String name, String email, String password, String cnpj) {
        super(name, email, password);
        this.cnpj = cnpj;
        this.descricaoServico = "";
        this.endereco = "";
        this.telefone = "";
        this.categoria = "Outro";
        this.avaliacoes = new ArrayList<>();
        setFotoPerfilPath("default_profile.png");
    }


    public Prestador(String name, String email, String password, String cnpj, String descricaoServico, String endereco, String telefone, String categoria, String fotoPerfilPath) {
        super(name, email, password);
        this.cnpj = cnpj;
        this.descricaoServico = descricaoServico;
        this.endereco = endereco;
        this.telefone = telefone;
        this.categoria = categoria;
        this.avaliacoes = new ArrayList<>();
        setFotoPerfilPath(fotoPerfilPath);
    }

    // Getters
    public String getCnpj() { return cnpj; }
    public String getDescricaoServico() { return descricaoServico; }
    public String getEndereco() { return endereco; }
    public String getTelefone() { return telefone; }
    public String getCategoria() { return categoria; }
    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }

    public String getServico() { return descricaoServico; }

    // Setters
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public void setDescricaoServico(String descricaoServico) { this.descricaoServico = descricaoServico; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public void addAvaliacao(Avaliacao avaliacao) {
        this.avaliacoes.add(avaliacao);
    }
}