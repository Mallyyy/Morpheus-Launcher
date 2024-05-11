package launcher.main;

import club.minnced.discord.rpc.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RichPresense {

  private static final String CLIENT_ID = "1081717915725865140";
  private static DiscordRPC lib;
  private static DiscordRichPresence presence;

  public static void initiate() {
    lib = DiscordRPC.INSTANCE;
    DiscordEventHandlers handlers = new DiscordEventHandlers();
    lib.Discord_Initialize(CLIENT_ID, handlers, true, "");
    presence = new DiscordRichPresence();
    presence.startTimestamp = System.currentTimeMillis() / 1000;
    presence.largeImageKey = "newlogo";
    presence.details = "Just Opened Morpheus Launcher";
    presence.state = "Releasing Soon!";
    presence.joinSecret ="MTI4NzM0OjFpMmhuZToxMjMxMjM= ";
    updatePresence();
    new Thread(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        lib.Discord_RunCallbacks();
        try {
          Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
      }
    }, "RPC-Callback-Handler").start();
  }

  public static void initiateExit() {
    lib = DiscordRPC.INSTANCE;
    DiscordEventHandlers handlers = new DiscordEventHandlers();
    lib.Discord_Initialize(CLIENT_ID, handlers, true, "");
    presence = new DiscordRichPresence();
    presence.startTimestamp = System.currentTimeMillis() / 1000;
    presence.largeImageKey = "newlogo";
    presence.details = "Exiting Launcher";
    presence.state = "Releasing Soon!";
    presence.joinSecret ="MTI4NzM0OjFpMmhuZToxMjMxMjM=";
    updatePresence();
    new Thread(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        lib.Discord_RunCallbacks();
        try {
          Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
      }
    }, "RPC-Callback-Handler").start();
  }

  public boolean presenceIsNull() {
    return presence == null;
  }

  public void updateDetails(String details) {
    presence.details = details;
    updatePresence();
  }

  public void updateState(String state) {
    presence.state = state;
    updatePresence();
  }

  public void updateSmallImageKey(String key) {
    presence.smallImageKey = key;
    updatePresence();
  }

  private static void updatePresence() {
    lib.Discord_UpdatePresence(presence);
  }
}
