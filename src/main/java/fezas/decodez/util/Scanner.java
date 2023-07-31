package fezas.decodez.util;

import fezas.decodez.MainApplication;
import fezas.decodez.entity.Setting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.docx4j.Docx4J;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.CTCompat;
import org.docx4j.wml.Document;
import org.docx4j.wml.ObjectFactory;

import java.io.*;
import java.nio.channels.Channel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class Scanner extends Thread {
    private static final Logger logger = LogManager.getLogger();
    private MainApplication mainApplication = MainApplication.getInstance();
    private String timeOperation() {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        Date date = Date.from(instant);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM HH:mm:ss");
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    private ArrayList<String> readerFileToArrayList(File file) throws IOException {
        ArrayList<String> fileToArrayList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("Cp866")));
        String s;
        while ((s = reader.readLine()) != null) {
            fileToArrayList.add(s);
        }
        reader.close();
        return fileToArrayList;
    }


    private void createDoc(String urlFileOut, ArrayList<String> strings)  {
        WordprocessingMLPackage wordMLPackage = null;
        try {
            wordMLPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
            for (String s : strings) {
                mdp.addParagraphOfText(s);
            }
            DocumentSettingsPart dsp = mdp.getDocumentSettingsPart(true);
            CTCompat compat = Context.getWmlObjectFactory().createCTCompat();
            dsp.getContents().setCompat(compat);
            compat.setCompatSetting("compatibilityMode", "http://schemas.microsoft.com/office/word", "15");
            File file = new File(urlFileOut);
            if (!file.exists()) {
                Docx4J.save(wordMLPackage, file, Docx4J.FLAG_SAVE_ZIP_FILE);
                logger.info("УСПЕШНАЯ ПЕРЕКОДИРОВКА: " + urlFileOut);
            } else logger.info("Файл: " + urlFileOut + " уже существует!");
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
    }

    public void processFilesFromFolder() {
        File folder = new File(mainApplication.readSettings().getInputDirectory());
        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries)
        {
            if (entry.isDirectory())
            {
                processFilesFromFolder();
                continue;
            }
            // иначе вам попался файл, обрабатывайте его!
            try {
                readerFileToArrayList(entry);
                String filenameWithoutEx = entry.getName().replaceAll("\\.\\w+$", "");
                String urlFileOut = mainApplication.readSettings().getOutputDirectory() + "/" + filenameWithoutEx + ".docx";
                System.out.println(urlFileOut);
                createDoc(urlFileOut, readerFileToArrayList(entry));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run(){
        logger.info("Сканер запущен " + timeOperation());
        try {
            while (!this.isInterrupted()) {
                processFilesFromFolder();
                if (MainApplication.getInstance().readSettings().isAutoscan()) {
                    sleep(MainApplication.getInstance().readSettings().getTimeDecode() * 1000);
                } else interrupt();
            }
        } catch (InterruptedException e) {
            logger.info("Сканер остановлен " + timeOperation());
        }
    }
}
