package restaurante.service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import restaurante.model.Pedido;
import restaurante.model.Prato;
import restaurante.util.Logger;

public class Atendente implements Runnable {

    private final int id;
    private final BlockingQueue<Pedido> fila;
    private final List<Prato> cardapio;
    private final int quantidadePedidos;
    private final AtomicInteger contadorPedidos;

    private final Random random = new Random();

    public Atendente(
            int id,
            BlockingQueue<Pedido> fila,
            List<Prato> cardapio,
            int quantidadePedidos,
            AtomicInteger contadorPedidos
    ) {
        this.id = id;
        this.fila = fila;
        this.cardapio = cardapio;
        this.quantidadePedidos = quantidadePedidos;
        this.contadorPedidos = contadorPedidos;
    }

    @Override
    public void run() {

        for (int i = 0; i < quantidadePedidos; i++) {

            try {

                Thread.sleep(
                        random.nextInt(500) + 200
                );

                Prato prato =
                        cardapio.get(
                                random.nextInt(
                                        cardapio.size()
                                )
                        );

                int numeroPedido =
                        contadorPedidos.incrementAndGet();

                Pedido pedido =
                        new Pedido(
                                numeroPedido,
                                prato
                        );

                Logger.log(
                        "Atendente " +
                        id +
                        " criou " +
                        pedido
                );

                fila.put(pedido);

                Logger.log(
                        "Atendente " +
                        id +
                        " colocou " +
                        pedido +
                        " na fila (" +
                        fila.size() +
                        "/10)"
                );

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                Logger.log(
                        "Atendente " +
                        id +
                        " foi interrompido."
                );

                break;
            }
        }

        Logger.log(
                "Atendente " +
                id +
                " terminou de gerar pedidos."
        );
    }
}