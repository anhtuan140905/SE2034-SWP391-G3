package vn.edu.fpt.service;

import vn.edu.fpt.model.User;

public interface AuthenticatedUser {
    Long getUserId();
    String getEmail();
    User getUser();
}
