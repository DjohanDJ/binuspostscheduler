package com.example.binuspostscheduler.authentications;

import com.example.binuspostscheduler.models.User;

public class UserSession {

    private static User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        UserSession.currentUser = currentUser;
    }

}
