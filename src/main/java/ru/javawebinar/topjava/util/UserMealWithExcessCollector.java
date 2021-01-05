package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Мы реализуем интерфейс Collector, который типизируется тремя разными типами:
 * входной тип для коллектора (UserMeal в нашем случае),
 * тип контейнера для хранения промежуточных вычислений (HashMap<LocalDate, Integer> в нашем случае)
 * и выходной тип коллектора, который он возвращает (опять List<UserMealWithExcess>)
 */
public class UserMealWithExcessCollector implements Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>> {

    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int caloriesPerDay;
    private final Map<LocalDate, Integer> dateNumberCaloriesMap = new ConcurrentHashMap<>();

    public UserMealWithExcessCollector(LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.caloriesPerDay = caloriesPerDay;
    }

    /**
     * Supplier возвращает лямбда-выражение, создающее контейнер для хранения промежуточных выражений:
     */
    @Override
    public Supplier<List<UserMealWithExcess>> supplier() {
        return ArrayList::new;
    }

    /**
     * Accumulator добавляет очередное значение в контейнер промежуточных значений.
     * Если быть точным, то accumulator возвращает лямбда-выражение, которое обрабатывает очередное значение и сохраняет его
     */
    @Override
    public BiConsumer<List<UserMealWithExcess>, UserMeal> accumulator() {
        return (userMealWithExcesses, userMeal) -> {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                userMealWithExcesses.add(UserMealsUtil.buildUserMealWithExcess(userMeal, true));
            dateNumberCaloriesMap.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
        };
    }

    /**
     * Combiner возвращает лямбда-выражение, объединяющее два контейнера промежуточных значений в один.
     * Дело в том, что Stream API может создать несколько таких контейнеров, для параллельной обработки и в конце слить их в один общий контейнер.
     */
    @Override
    public BinaryOperator<List<UserMealWithExcess>> combiner() {
        return (left, right) -> {
            if (left.size() < right.size()) {
                right.addAll(left);
                return right;
            } else {
                left.addAll(right);
                return left;
            }
        };
    }

    /**
     * Finisher возвращает лямбда-выражение, которое производит финальное преобразование: обрабатывает содержимое контейнера промежуточных результатов
     * и приводит его к заданному выходному типу
     */
    @Override
    public Function<List<UserMealWithExcess>, List<UserMealWithExcess>> finisher() {
        return userMealWithExcesses -> {
            userMealWithExcesses.forEach(mealWithExcess ->
                    mealWithExcess.setExcess(dateNumberCaloriesMap.get(mealWithExcess.getDateTime().toLocalDate()) > caloriesPerDay));
            return userMealWithExcesses;
        };
    }

    /**
     * Декларирование свойств коллектора.
     */
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
    }

}
