package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {
    Meal create(Meal meal);

    void update(Meal meal);

    Meal get(Integer id);

    List<Meal> getAll();

    void delete(Integer id);
}
