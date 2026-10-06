package restaurante.service;

import restaurante.util.Logger;

public class Gerente implements Runnable {

    private final int tempoFuncionamentoSegundos;
    private volatile boolean restauranteAberto = true;

    public Gerente(int tempoFuncionamentoSegundos) {
        this.tempoFuncionamentoSegundos = tempoFuncionamentoSegundos;
    }

    @Override
    public void run() {

        try {

            Logger.log(
                    "Gerente abriu o restaurante por " +
                    tempoFuncionamentoSegundos +
                    " segundos."
            );

            Thread.sleep(tempoFuncionamentoSegundos * 1000L);

            restauranteAberto = false;

            Logger.log("================================");
            Logger.log("GERENTE: horário encerrado!");
            Logger.log("GERENTE: novos pedidos serão interrompidos.");
            Logger.log("================================");

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            restauranteAberto = false;

            Logger.log("Gerente foi interrompido.");
        }
    }

    public boolean isRestauranteAberto() {
        return restauranteAberto;
    }

    public void fecharRestaurante() {
        restauranteAberto = false;
    }
}