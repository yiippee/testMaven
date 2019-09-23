package com.lzb.orikaTest;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class MainTest {
    public static void main(String[] args) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(User.class, UserA.class)
                .field("id", "idA")
                .field("name", "nameA")
                .byDefault()
                .register();

        User user = new User();
        user.setId(123L);
        user.setName("小明");
        user.setScore(99);

        MapperFacade mapper = mapperFactory.getMapperFacade();

        UserA userA = mapper.map(user, UserA.class);
        System.out.println(userA.toString());
    }
}
