package org.example.debriefrepository.config.UserContext;

public class UserContext {

    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setCurrentUserId(String userId) {
        currentUser.set(userId);
    }

    public static String getCurrentUserId() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}

