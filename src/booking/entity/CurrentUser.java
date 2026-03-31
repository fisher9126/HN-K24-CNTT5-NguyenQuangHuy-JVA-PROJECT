package booking.entity;

public  class CurrentUser {
    private static User currentUser;
    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User currentUser) {
        CurrentUser.currentUser = currentUser;
    }
    public static void removeCurrentUser() {
        currentUser = null;
    }
}
