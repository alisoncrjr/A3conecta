package Model;

import java.time.LocalDateTime;

public class Avaliacao {
    private Cliente cliente;
    private Prestador prestador;
    private int pontuacao;
    private String comentario;
    private LocalDateTime data;

    public Avaliacao(Cliente cliente, Prestador prestador, int pontuacao, String comentario, LocalDateTime data) {
        this.cliente = cliente;
        this.prestador = prestador;
        this.pontuacao = pontuacao;
        this.comentario = comentario;
        this.data = data;
    }

    // Getters
    public Cliente getCliente() { return cliente; }
    public Prestador getPrestador() { return prestador; }
    public int getPontuacao() { return pontuacao; }
    public String getComentario() { return comentario; }
    public LocalDateTime getData() { return data; }


    public void setData(LocalDateTime data) { this.data = data; }
}