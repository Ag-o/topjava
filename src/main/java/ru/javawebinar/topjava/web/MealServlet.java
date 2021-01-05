package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.InMemoryMealRepositoryImpl;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.CALORIES_PER_DAY;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String LIST_MEALS = "/meals.jsp";
    private static final String INSERT_OR_EDIT = "/meal.jsp";
    private static MealRepository repository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        repository = new InMemoryMealRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward = "";
        int id;
        String action = request.getParameter("action");
        switch (action == null ? " " : action) {
            case "delete":
                id = Integer.parseInt(request.getParameter("id"));
                log.debug("delete meal with id = {}", id);
                repository.delete(id);
                forward = LIST_MEALS;
                log.debug("redirect to meals");
                request.setAttribute("meals",
                        MealsUtil.filteredByStreams(repository.getAll(),
                                LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
                break;
            case "edit":
                id = Integer.parseInt(request.getParameter("id"));
                log.debug("edit meal with id = {}", id);
                Meal meal = repository.get(id);
                forward = INSERT_OR_EDIT;
                log.debug("redirect to meal");
                request.setAttribute("meal", meal);
                break;
            case "insert":
                log.debug("insert meal");
                forward = INSERT_OR_EDIT;
                log.debug("redirect to meal");
                request.setAttribute("meal", new Meal(LocalDateTime.now(), "description", 200));
                break;
            default:
                forward = LIST_MEALS;
                log.debug("redirect to meals");
                request.setAttribute("meals",
                        MealsUtil.filteredByStreams(repository.getAll(),
                                LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
                break;
        }
        request.getRequestDispatcher(forward).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal();
        meal.setCalories(Integer.parseInt(request.getParameter("calories")));
        meal.setDescription(request.getParameter("description"));
        meal.setDateTime(LocalDateTime.parse(request.getParameter("dateTime")));
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            repository.create(meal);
        } else {
            meal.setId(Integer.parseInt(id));
            repository.update(meal);
        }
        request.setAttribute("meals", MealsUtil.filteredByStreams(repository.getAll(),
                LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
        request.getRequestDispatcher(LIST_MEALS).forward(request, response);
    }
}
