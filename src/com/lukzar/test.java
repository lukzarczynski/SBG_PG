package com.lukzar;

import com.lukzar.config.Templates;
import com.lukzar.exceptions.IntersectsException;
import com.lukzar.model.Piece;
import com.lukzar.services.PieceGenerator;
//import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lukasz on 08.10.17.
 */
public class test {

    //@Test
    public void test() throws IntersectsException {

        Piece piece = null;

        while (piece == null) {
            try {
                piece = PieceGenerator.generate();
            } catch (Exception e) {
                piece = null;
            }
        }

        String s = piece.toSvg();
        piece.convertToAsymmetric();

        String s2 = piece.toSvg();

        final File file = new File("out/test.html");

        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(String.format(Templates.getListTemplate(),
                    s + " \n" + s2)
                    .getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
