package AC.Utils.PluginUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Messages {

    /**
     * Prints a startup message to the server console.
     * Provides context to administrators and users about the plugin's activation.
     */
    public void startUpComments() {
        System.out.println("=================================");
        System.out.println("Welcome to CLARA - Cheat Limiting Adaptive Response Algorithm");
        System.out.println("Powered by advanced AI to ensure fair and enjoyable gameplay!");
        System.out.println("CLARA is now actively monitoring cheating attempts...");
        System.out.println("=================================");
    }

    /**
     * Prints a shutdown message to the server console.
     * Informs administrators and users that the plugin is shutting down safely.
     */
    public void shutdownComments() {
        System.out.println("=================================");
        System.out.println("CLARA is shutting down...");
        System.out.println("Thank you for using Cheat Limiting Adaptive Response Algorithm.");
        System.out.println("Monitoring has been disabled. See you next time!");
        System.out.println("=================================");
    }

}
