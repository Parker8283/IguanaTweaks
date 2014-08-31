package iguanaman.iguanatweaks.config;

import java.io.File;

public class IguanaConfigHandler {

    public IguanaConfigHandler(File configDir) {
        IguanaConfig.init(new File(configDir, "main.cfg"));
        IguanaWeightsConfig.init(new File(configDir, "weights.json"));
    }
}
