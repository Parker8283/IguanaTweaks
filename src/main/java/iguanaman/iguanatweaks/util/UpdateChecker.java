package iguanaman.iguanatweaks.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;

import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import iguanaman.iguanatweaks.IguanaTweaks;
import iguanaman.iguanatweaks.data.ModInfo;

public class UpdateChecker {

    public static UpdateChecker instance = new UpdateChecker();

    public enum Result {
        UNINITIALIZED(0),
        CURRENT(1),
        OUTDATED(2),
        ERRORED(3),
        DISABLED(4),
        DEV_VERSION(5);

        private int code;

        private Result (int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private static final String VERSION_JSON_URL = "https://raw.githubusercontent.com/Parker8283/IguanaTweaks/master/version.json";

    public static Result runUpdateCheck() {
        String modver = ModInfo.VERSION;
        String mcver = MinecraftForge.MC_VERSION;

        if(modver.equals("@MOD_VERSION@")) {
            return Result.DEV_VERSION;
        } else if(!ModInfo.IS_RELEASE) {
            return Result.DISABLED;
        }

        JsonReader reader = null;

        try {
            URL remoteJsonFile = new URL(VERSION_JSON_URL);
            URLConnection con = remoteJsonFile.openConnection();
            con.setConnectTimeout(1000);
            con.setReadTimeout(1000);
            InputStream is = con.getInputStream();
            reader = new JsonReader(new InputStreamReader(is, "UTF-8"));

            reader.beginArray();
            while(reader.hasNext()) {
                reader.beginObject();
                while(reader.hasNext()) {
                    String name = reader.nextName();
                    if(name.equalsIgnoreCase("minecraft")) {
                        if(reader.nextString().equals(mcver)) {
                            name = reader.nextName();
                            if(name.equalsIgnoreCase("version")) {
                                String readver = reader.nextString();
                                if(!readver.equals(modver)) {
                                    return Result.OUTDATED;
                                } else {
                                    return Result.CURRENT;
                                }
                            } else {
                                reader.skipValue();
                            }
                        } else {
                            continue;
                        }
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            }
            reader.endArray();
        } catch(Exception e) {
            IguanaTweaks.log.error("There was an error in the version checker");
            e.printStackTrace();
        } finally {
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch(IOException e) {
                IguanaTweaks.log.error("There was an error in closing the JSON reader in the update checker");
                e.printStackTrace();
            }
        }
        return Result.ERRORED;
    }

    public class UpdaterEventHook {
        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if(IguanaTweaks.instance.result != Result.CURRENT) {
                if(IguanaTweaks.instance.result == Result.DEV_VERSION) {
                    IguanaTweaks.log.info("You are running a dev copy of the mod. Update Checker Disabled.");
                } else if(IguanaTweaks.instance.result == Result.DISABLED) {
                    IguanaTweaks.log.info("The update checker has been disabled because you're running a snapshot.");
                } else if(IguanaTweaks.instance.result == Result.OUTDATED) {
                    event.player.addChatMessage(new ChatComponentTranslation("msg.updater.outdated"));
                } else {
                    IguanaTweaks.log.error("There was an error in checking for updates.");
                }
            } else {
                IguanaTweaks.log.info("Your copy of Iguana Tweaks is up to date.");
            }
        }
    }
}
