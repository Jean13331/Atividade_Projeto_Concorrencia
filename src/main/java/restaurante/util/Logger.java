package restaurante.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static synchronized void log(String mensagem) {

        String horario =
                LocalTime.now().format(FORMATTER);

        System.out.println(
                "[" + horario + "] " + mensagem
        );
    }
}