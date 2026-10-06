package restaurante.service;

import restaurante.model.Pedido;
import restaurante.util.Logger;

public class Garcom implements Runnable {

    private final Balcao balcao;
    private final Caixa caixa;

    public Garcom(Balcao balcao, Caixa caixa) {
        this.balcao = balcao;
        this.caixa = caixa;
    }

    @Override
    public void run() {

        try {

            while (true) {

                balcao.getSino().waitOne();

                while (true) {

                    Pedido pedido =
                            balcao.getFila().poll();

                    if (pedido == null) {
                        break;
                    }

                    pedido.setStatus(Pedido.Status.ENTREGUE);

                    Logger.log(
                            "Garcom entregou " +
                            pedido
                    );

                    caixa.registrarVenda(pedido);
                }

                if (balcao.isFechado()
                        && balcao.getFila().isEmpty()) {

                    Logger.log(
                            "Garcom encerrou o trabalho."
                    );

                    break;
                }
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            Logger.log(
                    "Garcom foi interrompido."
            );
        }
    }
}