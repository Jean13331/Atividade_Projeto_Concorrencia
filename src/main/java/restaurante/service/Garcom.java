package restaurante.service;

import restaurante.model.Pedido;
import restaurante.util.Logger;

public class Garcom implements Runnable {

    private final Balcao balcao;

    public Garcom(Balcao balcao) {
        this.balcao = balcao;
    }

    @Override
    public void run() {

        try {

            while (true) {

                // Espera o sino tocar
                balcao.getSino().waitOne();

                // Esvazia TODO o balcão
                while (true) {

                    Pedido pedido =
                            balcao.getFila().poll();

                    if (pedido == null) {
                        break;
                    }

                    pedido.setStatus(
                            Pedido.Status.ENTREGUE
                    );

                    Logger.log(
                            "Garçom entregou " +
                            pedido
                    );
                }

                if (
                        balcao.isFechado()
                        && balcao.getFila().isEmpty()
                ) {

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