package Model;

public class Servico {
    private String descricao;
    private String area;
    private String localizacao;
    private String status;

    public Servico(String descricao, String area, String localizacao) {
        this.descricao = descricao;
        this.area = area;
        this.localizacao = localizacao;
        this.status = "Disponível";
    }

    public String getDescricao() { return descricao; }
    public String getArea() { return area; }
    public String getLocalizacao() { return localizacao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {

        return String.format("[%s] %s - %s", area, descricao, localizacao);
    }
}
