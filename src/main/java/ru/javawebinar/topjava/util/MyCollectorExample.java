package ru.javawebinar.topjava.util;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Мы реализуем интерфейс Collector, который типизируется тремя разными типами:
 * входной тип для коллектора (Integer в нашем случае),
 * тип контейнера для хранения промежуточных вычислений (TreeSet в нашем случае)
 * и выходной тип коллектора, который он возвращает (опять Integer).
 */

public class MyCollectorExample implements Collector<Integer, TreeSet<Integer>, Integer> {

    /**
     * Supplier возвращает лямбда-выражение, создающее контейнер для хранения промежуточных выражений:
     */
    @Override
    public Supplier<TreeSet<Integer>> supplier() {
        return TreeSet<Integer>::new;
    }

    /**
     * Accumulator добавляет очередное значение в контейнер промежуточных значений.
     * Если быть точным, то accumulator возвращает лямбда-выражение, которое обрабатывает очередное значение и сохраняет его
     */
    @Override
    public BiConsumer<TreeSet<Integer>, Integer> accumulator() {
        return TreeSet::add;
    }

    /**
     * Combiner возвращает лямбда-выражение, объединяющее два контейнера промежуточных значений в один.
     * Дело в том, что Stream API может создать несколько таких контейнеров, для параллельной обработки и в конце слить их в один общий контейнер.
     */
    @Override
    public BinaryOperator<TreeSet<Integer>> combiner() {
        return (l, r) -> {
            l.addAll(r);
            return l;
        };
    }

    /**
     * Finisher возвращает лямбда-выражение, которое производит финальное преобразование: обрабатывает содержимое контейнера промежуточных результатов
     * и приводит его к заданному выходному типу
     */
    @Override
    public Function<TreeSet<Integer>, Integer> finisher() {
        return s -> {
            long size = s.size();
            if (size % 2 == 0) {
                return new Double(s
                        .stream()
                        .skip(size % 2 + 2)
                        .limit(2)
                        .mapToInt(i -> i)
                        .average()
                        .getAsDouble())
                        .intValue();
            }
            return s
                    .stream()
                    .skip(size % 2 + 2)
                    .findFirst()
                    .get();
        };
    }

    /**
     * Декларирование свойств коллектора.
     */
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }

}
