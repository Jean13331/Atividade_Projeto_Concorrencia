package restaurante.service;

import java.util.concurrent.ConcurrentLinkedQueue;

import restaurante.model.Pedido;
import restaurante.util.AutoResetEvent;
import restaurante.util.Logger;

public class Balcao {

    private final ConcurrentLinkedQueue<Pedido> fila =
            new ConcurrentLinkedQueue<>();

    private final AutoResetEvent sino =
            new AutoResetEvent();

    private volatile boolean fechado = false;

    public void adicionar(Pedido pedido) {

        fila.offer(pedido);

        Logger.log(
                pedido +
                " ficou pronto e entrou no balcao."
        );

        // Acorda o garçom
        sino.set();
    }

    public ConcurrentLinkedQueue<Pedido> getFila() {
        return fila;
    }

    public AutoResetEvent getSino() {
        return sino;
    }

    public void fechar() {

        fechado = true;

        // Acorda o garçom para ele verificar
        sino.set();
    }

    public boolean isFechado() {
        return fechado;
    }
}