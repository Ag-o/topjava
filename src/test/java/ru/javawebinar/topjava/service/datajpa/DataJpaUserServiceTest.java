package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;

@ActiveProfiles("datajpa")
public class DataJpaUserServiceTest extends AbstractUserServiceTest {
    @Test
    public void getWithMeals() {
        User admin = service.get(ADMIN_ID);
        USER_MATCHER.assertMatch(admin, UserTestData.admin);
        MEAL_MATCHER.assertMatch(admin.getMeals(), adminMeal2, adminMeal1);
    }
}
