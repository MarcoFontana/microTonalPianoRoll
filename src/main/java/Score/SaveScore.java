package Score;

import javax.swing.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class SaveScore {

    public static void save(Score scoreToSave, String fileName) {

        XMLEncoder xmlEncoder = null;

        try{
            xmlEncoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fileName + ".xml")));
        }catch(FileNotFoundException fileNotFound){
            System.out.println("ERROR: While Saving the File");
            JOptionPane.showMessageDialog(null, "Error while saving this file");
        }

        assert xmlEncoder != null;
        xmlEncoder.writeObject(scoreToSave);
        xmlEncoder.close();
        JOptionPane.showMessageDialog(null, "File saved");

    }

    public static Score load(String fileName) {

        XMLDecoder scoreDecoder = null;

        try {
            scoreDecoder=new XMLDecoder(new BufferedInputStream(new FileInputStream(fileName)));
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File "+ fileName + " not found");
        }

        try{
            assert scoreDecoder != null;
        }
        catch (IllegalArgumentException e) {
            System.out.println(fileName + " is not a valid Piano roll file");
            return null;
        }

        Score score;
        try{
            score = (Score) scoreDecoder.readObject();
            return score;
        }
        catch (ArrayIndexOutOfBoundsException | ClassCastException e) {
            System.out.println(fileName + " is not a valid Piano roll file");
        }

        return null;

    }

}
