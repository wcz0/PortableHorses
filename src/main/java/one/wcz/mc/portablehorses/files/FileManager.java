package one.wcz.mc.portablehorses.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import one.wcz.mc.portablehorses.Main;

import java.io.*;

public class FileManager {

    private static FileManager instance;
    private File messagesFile;
    public FileConfiguration messages;

    private FileManager() {
        instance = this;
    }

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }
    public void reloadConfig() {
    	Main.getInstance().reloadConfig();
    }

    // 加载/重载文件
    // Load/reload files
    public void loadFiles() {

        messagesFile = new File(Main.getInstance().getDataFolder(), "messages.yml");

        // 检测文件是否存在, 如果没有就创建
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            // 获取插件内的messages.yml的文件
            copy(Main.getInstance().getResource("messages.yml"), messagesFile);
        }

        messages = new YamlConfiguration();

        try {
            messages.load(messagesFile);
            saveFiles(FileName.MESSAGES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 保存文件
    // Save files
    private void saveFiles(FileName file) {
        try {

            switch (file) {

                case MESSAGES:
                    messages.save(messagesFile);
                    break;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 复制默认文件
    // Copy default files
    private void copy(InputStream in, File file) {

        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum FileName {
        MESSAGES
    }
}
