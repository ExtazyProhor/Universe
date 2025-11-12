package ru.prohor.universe.padawan.scripts.money;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class MoneyCalculator {
    public static void main(String[] args) {
        try {
            calculate(List.of());
        } catch (RuntimeException e) {
            System.out.println("Impossible exception");
            throw e;
        }
    }

    private static final Comparator<PersonWithBalance> COMPARATOR = Comparator.comparingInt(PersonWithBalance::balance);

    private static void calculate(List<Operation> operations) {
        Map<Person, Integer> counter = new HashMap<>();
        Set<Person> possiblePersons = new HashSet<>();
        operations.forEach(operation -> {
            possiblePersons.add(operation.paying());
            possiblePersons.addAll(operation.debtors());
        });

        for (Person person : possiblePersons)
            counter.put(person, 0);
        operations.forEach(operation -> {
            counter.computeIfPresent(operation.paying(), (p, old) -> old + operation.value());
            int valuePerDebtor = operation.value() / operation.debtors().size();
            operation.debtors().forEach(debtor -> counter.computeIfPresent(debtor, (p, old) -> old - valuePerDebtor));
        });
        assert counter.values().stream().mapToInt(i -> i).sum() == 0;

        PriorityQueue<PersonWithBalance> plus = new PriorityQueue<>(COMPARATOR);
        PriorityQueue<PersonWithBalance> minus = new PriorityQueue<>(COMPARATOR);

        counter.forEach((person, balance) -> {
            if (balance == 0)
                System.out.println("Нулевой баланс у " + person);
            else if (balance > 0)
                plus.add(new PersonWithBalance(person, balance));
            else
                minus.add(new PersonWithBalance(person, -balance));
        });

        Iterator<PersonWithBalance> plusIterator = plus.iterator();
        while (plusIterator.hasNext()) {
            PersonWithBalance person = plusIterator.next();

            Iterator<PersonWithBalance> minusIterator = minus.iterator();
            while (minusIterator.hasNext()) {
                PersonWithBalance personFromMinus = minusIterator.next();
                if (personFromMinus.balance() == person.balance()) {
                    System.out.println(
                            personFromMinus.person() + " должен заплатить " + person.balance()
                                    + " человеку_ " + person.person()
                    );
                    plusIterator.remove();
                    minusIterator.remove();
                    break;
                }
            }
        }

        while (!plus.isEmpty() && !minus.isEmpty()) {
            PersonWithBalance plusPerson = plus.poll();
            PersonWithBalance minusPerson = minus.poll();
            assert plusPerson != null && minusPerson != null;
            if (plusPerson.balance() == minusPerson.balance()) {
                System.out.println(
                        minusPerson.person() + " должен заплатить " + plusPerson.balance()
                                + " человеку " + plusPerson.person()
                );
                continue;
            }
            if (plusPerson.balance() < minusPerson.balance()) {
                minusPerson = new PersonWithBalance(minusPerson.person(), minusPerson.balance() - plusPerson.balance());
                minus.add(minusPerson);
                System.out.println(
                        minusPerson.person() + " должен заплатить " + plusPerson.balance()
                                + " человеку " + plusPerson.person()
                );
            } else {
                plusPerson = new PersonWithBalance(plusPerson.person(), plusPerson.balance() - minusPerson.balance());
                plus.add(plusPerson);
                System.out.println(
                        minusPerson.person() + " должен заплатить " + minusPerson.balance()
                                + " человеку " + plusPerson.person()
                );
            }
        }

        assert plus.isEmpty() && minus.isEmpty();
    }
}
