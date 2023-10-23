package by.devtools.order.util;

import by.devtools.domain.OrderDto;
import by.devtools.domain.ResultEvent;
import by.devtools.domain.Statuses;
import by.devtools.order.dto.LoginRequest;
import by.devtools.order.dto.OrderCreate;
import by.devtools.order.model.Order;
import by.devtools.order.model.User;

public class TestData {

    public static final int ID = 1;
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String HASHED_PASSWORD = "hashed_password";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE2OTc5ODg5MDgsImV4cCI6MTY5Nzk5MDcwOCwiaXNzIjoiZGV2dG9vbHMiLCJzdWIiOiJ1c2VybmFtZSJ9.fMc8EYXhAjha9O1crJ60JwjtxvGcS9dCSRaru1HlPETpyEcmXupyCpYCN4IYqTYSpqhzzB7y6a3_Z-NXhjfzRg";
    public static final int PRODUCT_COUNT = 5;
    public static final int PRODUCT_ID = 2;
    public static final int CUSTOMER_ID = 3;
    public static final double TOTAL_PRICE = 109.90;

    public static User getUser() {
        return new User(ID, USERNAME, HASHED_PASSWORD);
    }

    public static LoginRequest getLoginRequest() {
        return new LoginRequest(USERNAME, PASSWORD);
    }

    public static OrderCreate getOrderCreate() {
        return new OrderCreate(PRODUCT_COUNT, CUSTOMER_ID, PRODUCT_ID, TOTAL_PRICE);
    }

    public static Order getOrder() {
        return new Order(
                ID,
                CUSTOMER_ID,
                PRODUCT_COUNT,
                PRODUCT_ID,
                TOTAL_PRICE,
                null,
                null,
                null
        );
    }

    public static Order getDefaultOrder() {
        return new Order(
                ID,
                CUSTOMER_ID,
                PRODUCT_COUNT,
                PRODUCT_ID,
                TOTAL_PRICE,
                Statuses.IN_PROCESS,
                Statuses.IN_PROCESS,
                Statuses.IN_PROCESS
        );
    }

    public static OrderDto getOrderDto() {
        return new OrderDto(
                ID,
                CUSTOMER_ID,
                PRODUCT_COUNT,
                PRODUCT_ID,
                TOTAL_PRICE,
                Statuses.IN_PROCESS,
                Statuses.IN_PROCESS,
                Statuses.IN_PROCESS
        );
    }
}
