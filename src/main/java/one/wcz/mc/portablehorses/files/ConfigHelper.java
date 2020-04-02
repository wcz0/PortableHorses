package one.wcz.mc.portablehorses.files;

import one.wcz.mc.portablehorses.Main;

public class ConfigHelper {

    public static void loadConfigs() {

        Main.getInstance().saveDefaultConfig();
        Main.getInstance().saveConfig();

        FileManager.getInstance().loadFiles();

    }
    

}
