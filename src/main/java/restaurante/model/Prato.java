package restaurante.model;

public class Prato {

    private final String nome;
    private final double preco;
    private final int tempoPreparo;

    public Prato(String nome, double preco, int tempoPreparo) {
        this.nome = nome;
        this.preco = preco;
        this.tempoPreparo = tempoPreparo;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getTempoPreparo() {
        return tempoPreparo;
    }

    @Override
    public String toString() {
        return nome;
    }
}