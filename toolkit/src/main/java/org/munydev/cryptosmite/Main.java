package org.munydev.cryptosmite;

import org.apache.commons.compress.archivers.tar.*;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    public static class TarEntryWithData {
        public TarArchiveEntry tae;
        public byte[] data;
    }
    public static List<TarEntryWithData> tarArchiveEntries = new ArrayList<>();

    public static <T> boolean ArrayHas(T[] a, T v) {
        for (T elem : a) {
            if (elem.equals(v)) {
                return true;
            }
        }
        return false;
    }

    public static boolean showFileSelectorAndWriteData(Frame f) {
        FileDialog fd = new FileDialog(f);
        fd.setMode(FileDialog.SAVE);
        fd.setFile("restored.tar.gz");
        fd.setVisible(true);
        File[] a = fd.getFiles();
        File file = a[0];
        try {

            if (!file.createNewFile()){
                System.out.println("file exists");
            };
            GZIPOutputStream compressed = new GZIPOutputStream( new FileOutputStream(file));
            TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(compressed );
            for (TarEntryWithData entry : tarArchiveEntries) {
                System.out.println("Hello world");
                entry.tae.setSize(entry.data.length);
                tarArchiveOutputStream.putArchiveEntry(entry.tae);
                System.out.println(entry.data.length);
                tarArchiveOutputStream.write(entry.data);
                tarArchiveOutputStream.closeArchiveEntry();

            }

            compressed.finish();
    //            tarArchiveOutputStream.close();
        } catch (Exception e) {
            System.out.println("Write rror");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static File showFileSelectorAndGetPath(Frame f) {
        FileDialog fd= new FileDialog(f);
        fd.setMode(FileDialog.LOAD);
        fd.setVisible(true);
        return fd.getFiles()[0];
    }
    public static byte[] getZipEntryData(ZipInputStream zis, ZipEntry zent) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] a = new byte[2048];
        for (long i = 0;i < zent.getSize(); i += 2048) {
            long to = Math.min(zent.getSize(), i + 2048);
            int readSize = (int)(to - i);
            System.out.println(readSize);
            if (zis.read(a, 0, readSize) != readSize) {
                System.out.println("Read error!");
                return null;
            }
            System.out.println(Arrays.toString(a));
            byteArrayOutputStream.write(a, 0, readSize);

        }
        return byteArrayOutputStream.toByteArray();
    }
    public static class FrameHolder {
        private final Frame frame;

        public interface ButtonClickHandler {
            void click(Button b);
        }

        public FrameHolder(Frame f) {
            frame = f;
        }

        public void addButtonWithLabel(String label, ButtonClickHandler clickHandler) {
            Button b = new Button();
            b.setLabel(label);
            Font f = new Font("monospace", Font.PLAIN, 20);
            frame.setFont(f);
            frame.add(b);
            int w = b.getFontMetrics(frame.getFont()).stringWidth(label);
            int h = b.getFontMetrics(frame.getFont()).getHeight();
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    clickHandler.click((Button) e.getSource());
                }
            });
            b.setSize(w, h);
        }
    }
    public static void addTarEntry(ZipInputStream zis, ZipEntry zent, String name) throws IOException {
        TarArchiveEntry tae = new TarArchiveEntry(name);
        tae.setIds(0,0);
        tae.setModTime(zent.getLastModifiedTime());
        tae.setCreationTime(zent.getCreationTime());


        byte[] mem = getZipEntryData(zis, zent);
        TarEntryWithData ted = new TarEntryWithData();
        ted.data = mem;
        ted.tae = tae;
        tarArchiveEntries.add(ted);
    }
    public static void main(String[] args) {
        Frame f = new Frame("Cryptotools modification toolkit");
        f.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("s");

            }
        });
        f.setSize(640, 480);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

        });
        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.CENTER);
        f.setLayout(fl);
        FrameHolder fh = new FrameHolder(f);
        fh.addButtonWithLabel("Upload policy file", (Button b) -> {
            File file = showFileSelectorAndGetPath(f);
            f.setTitle("CryptoTools - ["+ file.getPath()+ "]");
            try {
                tarArchiveEntries.clear();
                ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
                ZipEntry zent;
                while ((zent = zis.getNextEntry()) != null) {
                    if (zent.getName().equals("owner.key")) {
                        addTarEntry(zis, zent, "./var/lib/devicesettings/owner.key");
                    }
                    if (zent.getName().startsWith("policy")) {
                        addTarEntry(zis, zent, "./var/lib/devicesettings/policy.0");
                    }
                    if (zent.getName().startsWith("Local State")) {
                        addTarEntry(zis, zent, "./home/chronos/Local State");
                    }
                }

            }
            catch (FileNotFoundException e) {
                System.out.println("Could not read file: " + file);
            } catch (IOException e) {
                System.out.println("Could not read zip file!");
            }
        });
        fh.addButtonWithLabel("Download restored.tar.gz", (Button b)->{
            if (!showFileSelectorAndWriteData(f)) {

            }
        });
        f.setVisible(true);
    }
}
