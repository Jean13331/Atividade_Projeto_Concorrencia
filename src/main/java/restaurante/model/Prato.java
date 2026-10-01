package restaurante.model;

import java.util.List;

public class Prato {

    private final String nome;
    private final double preco;
    private final int tempoPreparo;
    private final boolean usaForno;
    private final boolean usaUtensilios;
    private final List<String> ingredientes;

    public Prato(
            String nome,
            double preco,
            int tempoPreparo,
            boolean usaForno,
            boolean usaUtensilios,
            List<String> ingredientes
    ) {
        this.nome = nome;
        this.preco = preco;
        this.tempoPreparo = tempoPreparo;
        this.usaForno = usaForno;
        this.usaUtensilios = usaUtensilios;
        this.ingredientes = ingredientes;
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

    public boolean usaForno() {
        return usaForno;
    }

    public boolean usaUtensilios() {
        return usaUtensilios;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    @Override
    public String toString() {
        return nome;
    }
}