package es.upm.etsiinf.gib.pmd_proyecto.groupdetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseRepository {

    private static final Map<Integer, ArrayList<Expense>> groupExpenses = new HashMap<>();

    public static ArrayList<Expense> getExpensesForGroup(int groupIndex) {
        // If we already have a list for this group, reuse it
        if (groupExpenses.containsKey(groupIndex)) {
            return groupExpenses.get(groupIndex);
        }

        // Otherwise create initial data for this group
        ArrayList<Expense> list = new ArrayList<>();

        switch (groupIndex) {
            case 0:
                list.add(new Expense("💶", "Game bar", "Baptiste", 25.00));
                list.add(new Expense("🍹", "Soft and sangria", "Erell", 6.00));
                list.add(new Expense("🍛", "Food", "Arthur", 19.95));
                break;

            case 1:
                list.add(new Expense("🎳", "Bowling", "Filip", 60.00));
                list.add(new Expense("🍺", "Drinks", "Aaron", 18.50));
                break;

            case 2:
                list.add(new Expense("🏖️", "Beach bar", "Sofia", 30.00));
                list.add(new Expense("🏖️", "Sagrada Familia", "Emma", 49.50));
                break;

            case 3:
                list.add(new Expense("🏎️", "Car breakdown", "Pedro", 430.00));
                list.add(new Expense("⛽️", "Gasoline", "Gael", 63.45));
                list.add(new Expense("🎢", "Amusement park", "Isaac", 120.00));
                break;

            default:
                // empty group
                break;
        }

        groupExpenses.put(groupIndex, list);
        return list;
    }
}

