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
    private final Gerente gerente;

    public Cozinheiro(
            int id,
            BlockingQueue<Pedido> fila,
            Estoque estoque,
            Cozinha cozinha,
            Balcao balcao,
            Gerente gerente) {

        this.id = id;
        this.fila = fila;
        this.estoque = estoque;
        this.cozinha = cozinha;
        this.balcao = balcao;
        this.gerente = gerente;
    }

    @Override
    public void run() {

        try {

            while (true) {

                /*
                 * Se o restaurante fechou e não existem mais pedidos,
                 * o cozinheiro pode encerrar.
                 */
                if (!gerente.isRestauranteAberto() && fila.isEmpty()) {
                    Logger.log(
                            "Cozinheiro " + id +
                            " encerrou após o fechamento."
                    );
                    break;
                }

                Pedido pedido = fila.poll();

                if (pedido == null) {
                    Thread.sleep(100);
                    continue;
                }

                if (pedido.isSinalEncerramento()) {
                    Logger.log(
                            "Cozinheiro " + id +
                            " recebeu sinal de encerramento."
                    );
                    break;
                }

                Logger.log(
                        "Cozinheiro " + id +
                        " pegou " + pedido
                );

                boolean reservado =
                        estoque.reservarTodos(
                                pedido.getPrato().getIngredientes()
                        );

                if (!reservado) {

                    pedido.setStatus(Pedido.Status.RECUSADO);

                    Logger.log(
                            "Pedido recusado por falta de ingrediente: " +
                            pedido
                    );

                    continue;
                }

                Logger.log(
                        "Cozinheiro " + id +
                        " reservou os ingredientes de " +
                        pedido
                );

                /*
                 * O cozinheiro termina o pedido mesmo que
                 * o restaurante seja fechado durante o preparo.
                 */
                cozinha.preparar(pedido);

                pedido.setStatus(Pedido.Status.PRONTO);

                balcao.adicionar(pedido);
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