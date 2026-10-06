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
    private final Gerente gerente;
    private final ControlePedidos controlePedidos;

    private final Random random = new Random();

    public Atendente(
            int id,
            BlockingQueue<Pedido> fila,
            List<Prato> cardapio,
            int quantidadePedidos,
            AtomicInteger contadorPedidos,
            Gerente gerente,
            ControlePedidos controlePedidos) {

        this.id = id;
        this.fila = fila;
        this.cardapio = cardapio;
        this.quantidadePedidos = quantidadePedidos;
        this.contadorPedidos = contadorPedidos;
        this.gerente = gerente;
        this.controlePedidos = controlePedidos;
    }

    @Override
    public void run() {

        for (int i = 0; i < quantidadePedidos; i++) {

            // Verifica se o restaurante ainda está aberto
            if (!gerente.isRestauranteAberto()) {

                Logger.log(
                        "Atendente " + id +
                        " parou porque o restaurante fechou."
                );

                break;
            }

            try {

                // Intervalo aleatório entre os pedidos
                Thread.sleep(random.nextInt(500) + 200);

                // Verifica novamente após o intervalo
                if (!gerente.isRestauranteAberto()) {

                    Logger.log(
                            "Atendente " + id +
                            " não criou novo pedido porque o restaurante fechou."
                    );

                    break;
                }

                // Escolhe um prato aleatoriamente
                Prato prato =
                        cardapio.get(
                                random.nextInt(cardapio.size())
                        );

                // Gera número único para o pedido
                int numeroPedido =
                        contadorPedidos.incrementAndGet();

                Pedido pedido =
                        new Pedido(
                                numeroPedido,
                                prato
                        );

                // Registra o pedido para o relatório final
                controlePedidos.registrar(pedido);

                Logger.log(
                        "Atendente " + id +
                        " criou " + pedido
                );

                /*
                 * put() bloqueia enquanto a fila estiver cheia.
                 * Isso demonstra o comportamento da fila limitada.
                 */
                fila.put(pedido);

                Logger.log(
                        "Atendente " + id +
                        " colocou " + pedido +
                        " na fila (" +
                        fila.size() +
                        "/10)"
                );

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                Logger.log(
                        "Atendente " + id +
                        " foi interrompido."
                );

                break;
            }
        }

        Logger.log(
                "Atendente " + id +
                " encerrou."
        );
    }
}