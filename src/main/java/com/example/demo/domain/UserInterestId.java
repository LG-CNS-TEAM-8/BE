package com.example.demo.domain;

import java.io.Serializable;
import java.util.Objects;

public class UserInterestId implements Serializable{
    private Long user;
    private Long interest;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInterestId)) return false;
        UserInterestId that = (UserInterestId) o;
        return Objects.equals(user, that.user)
            && Objects.equals(interest, that.interest);
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(user,interest);
    }

}
