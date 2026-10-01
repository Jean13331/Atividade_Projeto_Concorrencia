package restaurante.service;

import java.util.concurrent.BlockingQueue;

import restaurante.model.Pedido;
import restaurante.util.Logger;

public class Cozinheiro implements Runnable {

    private final int id;
    private final BlockingQueue<Pedido> fila;

    private final Estoque estoque;
    private final Cozinha cozinha;
    private final Balcao balcao;

    public Cozinheiro(
            int id,
            BlockingQueue<Pedido> fila,
            Estoque estoque,
            Cozinha cozinha,
            Balcao balcao
    ) {
        this.id = id;
        this.fila = fila;
        this.estoque = estoque;
        this.cozinha = cozinha;
        this.balcao = balcao;
    }

    @Override
    public void run() {

        try {

            while (true) {

                Pedido pedido = fila.take();

                // Sinal para encerrar o cozinheiro
                if (pedido.isSinalEncerramento()) {

                    Logger.log(
                            "Cozinheiro " +
                            id +
                            " encerrou."
                    );

                    break;
                }

                Logger.log(
                        "Cozinheiro " +
                        id +
                        " pegou " +
                        pedido
                );

                /*
                 * Reserva todos os ingredientes
                 * antes de começar o preparo.
                 */
                boolean reservado =
                        estoque.reservarTodos(
                                pedido
                                        .getPrato()
                                        .getIngredientes()
                        );

                if (!reservado) {

                    pedido.setStatus(
                            Pedido.Status.RECUSADO
                    );

                    Logger.log(
                            "Pedido recusado por falta " +
                            "de ingrediente: " +
                            pedido
                    );

                    continue;
                }

                Logger.log(
                        "Cozinheiro " +
                        id +
                        " reservou os ingredientes " +
                        "de " +
                        pedido
                );

                // Prepara usando forno ou utensílios
                cozinha.preparar(pedido);

                pedido.setStatus(
                        Pedido.Status.PRONTO
                );

                // Envia para o balcão
                balcao.adicionar(pedido);
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            Logger.log(
                    "Cozinheiro " +
                    id +
                    " foi interrompido."
            );
        }
    }
}