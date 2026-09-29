package restaurante.model;

public class Pedido {

    private final int id;
    private final Prato prato;

    public Pedido(int id, Prato prato) {
        this.id = id;
        this.prato = prato;
    }

    public int getId() {
        return id;
    }

    public Prato getPrato() {
        return prato;
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + prato.getNome();
    }
}