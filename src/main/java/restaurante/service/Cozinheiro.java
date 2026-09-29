package restaurante.service;

import restaurante.model.Pedido;
import restaurante.util.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Cozinheiro implements Runnable {

    private final int id;
    private final BlockingQueue<Pedido> fila;

    public Cozinheiro(
            int id,
            BlockingQueue<Pedido> fila
    ) {
        this.id = id;
        this.fila = fila;
    }

    @Override
    public void run() {

        try {

            while (true) {

                Pedido pedido = fila.poll(
                        2,
                        TimeUnit.SECONDS
                );

                if (pedido == null) {

                    Logger.log(
                            "Cozinheiro " + id +
                            " nao encontrou mais pedidos e encerrou."
                    );

                    break;
                }

                Logger.log(
                        "Cozinheiro " + id +
                        " pegou " + pedido
                );

                int tempo =
                        pedido.getPrato().getTempoPreparo();

                Logger.log(
                        "Cozinheiro " + id +
                        " preparando " +
                        pedido +
                        " (" +
                        tempo +
                        " ms)"
                );

                Thread.sleep(tempo);

                Logger.log(
                        "Cozinheiro " + id +
                        " terminou " +
                        pedido
                );
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            Logger.log(
                    "Cozinheiro " + id +
                    " foi interrompido."
            );
        }
    }
}