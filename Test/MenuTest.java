import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.*;

public class MenuTest {

    private Menu menu;

    @Before
    public void setUp(){
        File testSaveDirectory = new File("test_saved");
        if (!testSaveDirectory.exists()) {
            testSaveDirectory.mkdir();
    }
        menu = new Menu(testSaveDirectory);

        // Vyčistíme testovací složku před každým testem
        for (File file : testSaveDirectory.listFiles()) {
            file.delete();
        }
    }

    @Test
    public void testAddPrinter() {
        menu.addPrinter("Printer1");
        for (JButton button : menu.getButtons()) {
            assertEquals("Printer1", button.getText());
        }


    }

    @Test
    public void deleteSave() throws IOException {
        // Vytvoříme dočasnou složku pro ukládání souborů
        File testSaveDirectory = new File("test_saved");

        // Vytvoříme testovací soubor
        Path testFilePath = new File(testSaveDirectory, "Printer1.txt").toPath();
        Files.createFile(testFilePath);

        // Ověříme, že soubor existuje
        assertTrue(Files.exists(testFilePath));

        // Smažeme soubor
        menu.deleteSave("Printer1.txt");

        // Ověříme, že soubor byl smazán
        assertTrue(Files.notExists(testFilePath));
    }

}