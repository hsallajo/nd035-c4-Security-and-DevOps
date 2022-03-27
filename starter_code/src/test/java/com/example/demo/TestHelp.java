package com.example.demo;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.ModifyCartRequest;

import java.math.BigDecimal;

public class TestHelp {

    /*user*/
    public static final String USERNAME = "jane";
    public static final int USER_ID = 1;
    public static final String VALID_PASSWORD = "qwertyui2";
    public static final String INVALID_PASSWORD_1 = "qwertyui";
    public static final String INVALID_PASSWORD_2 = "12345678";
    public static final String INVALID_PASSWORD_3 = "abc123";

    /*item*/
    public static final long ITEM_1_ID = 1L;
    public static final String ITEM_1_NAME = "test item";
    public static final double ITEM_1_PRICE = 2.0;
    public static final String ITEM_1_DESCRIPTION = "Perfect item for any use.";

    /*cart*/
    public static final int NUMBER_OF_ITEMS = 3;
    public static final int NUMBER_OF_ITEMS_TO_BE_REMOVED = 2;

    public static Item createItem1(){

        Item i = new Item();
        i.setId(ITEM_1_ID);
        i.setName(ITEM_1_NAME);
        i.setDescription(ITEM_1_DESCRIPTION);
        i.setPrice(BigDecimal.valueOf(ITEM_1_PRICE));
        return i;
    }

    public static ModifyCartRequest createModifyCartRequest(String username, long itemId, int quantity){

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setItemId(itemId);
        request.setQuantity(quantity);
        return request;
    }

    public static User createUser(){

        User user = new User();
        user.setUsername(TestHelp.USERNAME);
        user.setId(TestHelp.USER_ID);
        return user;
    }

    public static Item createItem(){

        Item i = new Item();
        i.setId(TestHelp.ITEM_1_ID);
        i.setName(TestHelp.ITEM_1_NAME);
        i.setDescription(TestHelp.ITEM_1_DESCRIPTION);
        i.setPrice(BigDecimal.valueOf(TestHelp.ITEM_1_PRICE));
        return i;
    }
}
