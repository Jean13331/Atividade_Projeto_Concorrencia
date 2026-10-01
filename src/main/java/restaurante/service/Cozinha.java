package restaurante.service;

import java.util.concurrent.Semaphore;

import restaurante.model.Pedido;
import restaurante.util.Logger;

public class Cozinha {

    // No máximo 2 pratos no forno
    private final Semaphore forno =
            new Semaphore(2);

    // Recursos compartilhados
    private final Object tabua =
            new Object();

    private final Object faca =
            new Object();

    public void preparar(Pedido pedido)
            throws InterruptedException {

        if (pedido.getPrato().usaForno()) {

            prepararComForno(pedido);

        } else if (pedido.getPrato().usaUtensilios()) {

            prepararComUtensilios(pedido);

        } else {

            Thread.sleep(
                    pedido.getPrato().getTempoPreparo()
            );
        }
    }

    private void prepararComForno(Pedido pedido)
            throws InterruptedException {

        forno.acquire();

        try {

            int ocupados =
                    2 - forno.availablePermits();

            Logger.log(
                    "Cozinheiro " +
                    Thread.currentThread().getName() +
                    " colocou " +
                    pedido +
                    " no forno (" +
                    ocupados +
                    "/2)"
            );

            Thread.sleep(
                    pedido.getPrato().getTempoPreparo()
            );

        } finally {

            forno.release();

            int ocupados =
                    2 - forno.availablePermits();

            Logger.log(
                    "Cozinheiro " +
                    Thread.currentThread().getName() +
                    " liberou o forno (" +
                    ocupados +
                    "/2)"
            );
        }
    }

    private void prepararComUtensilios(Pedido pedido)
            throws InterruptedException {

        /*
         * IMPORTANTE:
         * Sempre pegamos primeiro a tábua
         * e depois a faca.
         *
         * Isso evita deadlock.
         */
        synchronized (tabua) {

            Logger.log(
                    "Cozinheiro " +
                    Thread.currentThread().getName() +
                    " pegou a tabua"
            );

            synchronized (faca) {

                Logger.log(
                        "Cozinheiro " +
                        Thread.currentThread().getName() +
                        " pegou a faca"
                );

                Thread.sleep(
                        pedido.getPrato().getTempoPreparo()
                );
            }
        }
    }
}