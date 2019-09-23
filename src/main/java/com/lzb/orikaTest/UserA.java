package com.lzb.orikaTest;

import lombok.Data;

@Data
public class UserA {
    private Long idA;
    private String nameA;
    private String addr;

    @Override
    public String toString() {
        return "UserA{" +
                "id=" + idA +
                ", name='" + nameA + '\'' +
                '}';
    }
}
