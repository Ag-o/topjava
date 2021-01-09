package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Comparator<? super Meal> MEAL_DATE_TIME_COMPARATOR = Comparator.comparing(Meal::getDateTime).reversed();
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private InMemoryUserRepository userRepository = new InMemoryUserRepository();

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
        MealsUtil.meals.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (userIsExist(userId)) {
            repository.putIfAbsent(userId, new HashMap<>());
            Map<Integer, Meal> userMeals = repository.get(userId);
            if (meal.isNew()) {
                meal.setId(counter.incrementAndGet());
                userMeals.put(meal.getId(), meal);
                return meal;
            }
            // handle case: update, but not present in storage
            return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        }
        return null;
    }

    @Override
    public boolean delete(int id, int userId) {
        return userIsExist(userId) && repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        return userIsExist(userId) ? repository.get(userId).get(id) : null;
    }

    @Override
    public Collection<Meal> getAll(int userId) {

        Collection<Meal> meals = userRepository.get(userId) != null
                ? repository.get(userId).values()
                : Collections.EMPTY_LIST;
        return meals.stream().sorted(MEAL_DATE_TIME_COMPARATOR).collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getBetween(int userId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        List<Meal> result = getAll(userId).stream()
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDate(), startDate, endDate))
                .collect(Collectors.toList());
        return result.isEmpty() ? Collections.EMPTY_LIST : result;
    }

    private boolean userIsExist(int userId) {
        return userRepository.get(userId) != null;
    }
}

