package kenma.persona;

public interface Persona {
    String name(); // Window title / header label

    String greeting(); // First message

    String decorateBot(String msg);// How bot replies are phrased

    String decorateError(String msg);// How errors are phrased

    String css(); // Extra stylesheet for this persona (classpath)

    String botAvatar(); // e.g. "/images/kenma_study.png" (nullable)
}
