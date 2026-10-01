package restaurante.util;

public class AutoResetEvent {

    private boolean sinalizado = false;

    public synchronized void set() {

        if (!sinalizado) {
            sinalizado = true;
            notify();
        }
    }

    public synchronized void waitOne()
            throws InterruptedException {

        while (!sinalizado) {
            wait();
        }

        sinalizado = false;
    }
}