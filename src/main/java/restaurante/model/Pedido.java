package restaurante.model;

public class Pedido {

    public enum Status {
        PENDENTE,
        RECUSADO,
        PRONTO,
        ENTREGUE
    }

    private final int id;
    private final Prato prato;

    private volatile Status status;

    private final boolean sinalEncerramento;

    public Pedido(int id, Prato prato) {
        this.id = id;
        this.prato = prato;
        this.status = Status.PENDENTE;
        this.sinalEncerramento = false;
    }

    private Pedido() {
        this.id = -1;
        this.prato = null;
        this.status = Status.PENDENTE;
        this.sinalEncerramento = true;
    }

    public static Pedido sinalEncerramento() {
        return new Pedido();
    }

    public int getId() {
        return id;
    }

    public Prato getPrato() {
        return prato;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isSinalEncerramento() {
        return sinalEncerramento;
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + prato.getNome();
    }
}