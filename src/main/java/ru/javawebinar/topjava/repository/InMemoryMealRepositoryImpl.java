package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.javawebinar.topjava.util.MealsUtil.MEALS;

public class InMemoryMealRepositoryImpl implements MealRepository {
    private static final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private static AtomicInteger counter = new AtomicInteger(0);

    {
        MEALS.forEach(this::create);
    }

    @Override
    public Meal create(Meal meal) {
        if (meal != null) {
            if (meal.isNew()) {
                meal.setId(counter.incrementAndGet());
                repository.putIfAbsent(meal.getId(), meal);
            }
        }
        return null;
    }

    @Override
    public void update(Meal meal) {
        if (meal != null) {
            Integer id = meal.getId();
            if (checkId(id)) {
                repository.put(id, meal);
            }
        }
    }

    @Override
    public Meal get(Integer id) {
        return repository.getOrDefault(id, null);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public void delete(Integer id) {
        if (checkId(id)) {
            repository.remove(id);
        }
    }

    private boolean checkId(Integer id) {
        return repository.containsKey(id) && id != null;
    }
}
