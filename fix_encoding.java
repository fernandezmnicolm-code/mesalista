
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class fix_encoding {
    public static void main(String[] args) throws IOException {
        String[] files = {"src/main/resources/templates/index.html", "src/main/resources/templates/restaurante-detalle.html"};
        for (String file : files) {
            Path path = Paths.get(file);
            if (!Files.exists(path)) continue;
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            content = content.replace("Ã³", "ó")
                             .replace("Ã­", "í")
                             .replace("Ã©", "é")
                             .replace("Ã¡", "á")
                             .replace("Ã±", "ñ")
                             .replace("Ãº", "ú")
                             .replace("Â¿", "¿")
                             .replace("Ã", "í"); // Note: single Ã is tricky, but "Ã­" usually is í
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        }
    }
}
