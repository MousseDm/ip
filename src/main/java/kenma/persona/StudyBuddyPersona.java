package kenma.persona;

public class StudyBuddyPersona implements Persona {
    @Override
    public String name() {
        return "Kenma (Study Buddy)";
    }

    @Override
    public String greeting() {
        return "Hey! Ready to grind? I’ll keep it short & helpful ✨";
    }

    @Override
    public String decorateBot(String msg) {
        return "📘 " + msg;
    }

    @Override
    public String decorateError(String msg) {
        return "⚠️ " + msg;
    }

    @Override
    public String css() {
        return "/view/themes/study.css";
    }

    @Override
    public String botAvatar() {
        return "/images/kenma_study.png";
    } // or return null if none
}
