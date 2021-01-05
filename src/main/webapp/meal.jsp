<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="f" uri="http://example.com/functions" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Meal</title>
</head>
<body>
<h1>${meal.id == null ? "Add meal":"Edit meal"}</h1>
<form method="POST" action='meals' name="frmAddMeal">
    <input type="hidden" name="id" value="${meal.id}"/>
    DateTime : <input
        type="datetime-local" name="dateTime"
        value="${meal.dateTime}"/>
    <br/>
    Description: <input
        type="text" name="description"
        value="${meal.description}"/>
    <br/>
    Calories : <input
        type="number" name="calories"
        value="${meal.calories}"/>
    <br/>
    <input type="submit" value="${meal.id == null ? "Add":"Edit"}"/>
    <input onclick="window.history.back()" type="submit" value="Cancel"/>
</form>
</body>
</html>