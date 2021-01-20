package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.getNew;
import static ru.javawebinar.topjava.MealTestData.getUpdated;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.NOT_FOUND;
import static ru.javawebinar.topjava.UserTestData.*;

public class MealServiceTest extends AbstractServiceTest {

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(USER_MEAL_ID_1, USER_ID);
        assertMatch(meal, USER_MEAL_1);
    }

    @Test
    public void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getNotFoundWithOtherUserId() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID_1, ADMIN_ID));
    }

    @Test
    public void delete() {
        service.delete(USER_MEAL_ID_7, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID_7, USER_ID));
    }

    @Test
    public void deletedNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deleteNotFoundWithOtherUserId() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(USER_MEAL_ID_1, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> allBetweenInclusive = service.getBetweenInclusive(
                LocalDate.of(2020, Month.JANUARY, 29),
                LocalDate.of(2020, Month.JANUARY, 30),
                USER_ID);
        MealTestData.assertMatch(allBetweenInclusive, USER_MEAL_3, USER_MEAL_2, USER_MEAL_1);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(ADMIN_ID);
        MealTestData.assertMatch(all, ADMIN_MEALS);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        MealTestData.assertMatch(service.get(USER_MEAL_ID_1, USER_ID), updated);
    }

    @Test
    public void updateNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.update(
                new Meal(NOT_FOUND,LocalDateTime.now(),"NotFound",10), USER_ID));
    }

    @Test
    public void updateNotFoundWithOtherUserId() throws Exception {
        assertThrows(NotFoundException.class, () -> service.update(USER_MEAL_1, ADMIN_ID));
    }


    @Test
    public void create() {
        Meal newMeal = getNew();
        Meal created = service.create(newMeal, ADMIN_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, ADMIN_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() throws Exception {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null,
                        LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0),
                        "duplicateDateTime", 500), USER_ID));
    }
}