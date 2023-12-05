package machine;

import java.util.Scanner;
import java.lang.StringBuilder;

public class CoffeeMachine {
    public static final int INGREDIENT_COUNT = 3;
    public static final int SUPPLY_COUNT = 4;
    public static final int PRODUCT_COUNT = 4;
    public static int[] ESPRESSO_INGREDIENTS = { 250, 0, 16, 1 };
    public static int[] LATTE_INGREDIENTS = { 350, 75, 20, 1 };
    public final static int[] CAPPUCCINO_INGREDIENTS = { 200, 100, 12, 1 };
    public static Product[] products = new Product[PRODUCT_COUNT];
    private static String[] supplyNames = { "water", "milk", "coffee", "cups" };
    public static Scanner scanner;
    private static int[] supplies = new int[SUPPLY_COUNT];
    private static int[] needs = new int[INGREDIENT_COUNT];
    private static int money = 0;
    private static int cups = -1;

    public static void main(String[] args) {
        CoffeeMachine.products[0] = new Product(1, "Espresso", ESPRESSO_INGREDIENTS, 4);
        CoffeeMachine.products[1] = new Product(2, "Latte", LATTE_INGREDIENTS, 7);
        CoffeeMachine.products[2] = new Product(3, "Cappuccino", CAPPUCCINO_INGREDIENTS, 6);

        scanner = new Scanner(System.in);
        menu();

        /*
        Estimation estimation;

        ask();
        Calculator.calcNeeds(needs, cups);
        estimation = Calculator.calcCapacity(needs, supplies, cups);
        Calculator.printEstimation(estimation);
        */
    }

    private static void ask() {
        System.out.println(Messages.ASK_0);
        supplies[0] = scanner.nextInt();

        System.out.println(Messages.ASK_1);
        supplies[1] = scanner.nextInt();

        System.out.println(Messages.ASK_2);
        supplies[2] = scanner.nextInt();

        System.out.println(Messages.ASK_3);
        cups = scanner.nextInt();
    }

    private static synchronized void buy(Scanner scanner) {
        final String ERROR_TEMPLATE = "Sorry, not enough *PN*!\n";
        String command = "";
        int command2int = 0;

        int productId = 0;
        Product product = null;
        int[] doses = new int[CoffeeMachine.SUPPLY_COUNT];

        int count = 0;
        StringBuilder builder = new StringBuilder();
        String productName = "";

        System.out.printf("%s\n", Messages.BUY_0);
        command = scanner.next();

        if (command.equals("back")) {
            menu();
        }
        else {
            try {
                command2int = Integer.parseInt(command);
                product = products[--command2int];
                productName = product.getName();
                doses = product.getIngredients();
            }
            catch (NullPointerException | NumberFormatException e) {
                System.err.println("Unknown product");
                menu();
            }
        }

        for (int i = 0; i < supplies.length; i++) {
            if (doses[i] <= supplies[i]) {
                count += 1;
            }
            else {
                builder.append(ERROR_TEMPLATE.replace("*PN*", supplyNames[i]));
            }
        }
        System.err.printf(builder.toString());

        if (count == CoffeeMachine.SUPPLY_COUNT) {
            for (int j = 0; j < supplies.length; j++) {
                supplies[j] -= doses[j];
            }

            CoffeeMachine.setMoney(CoffeeMachine.getMoney() + product.getPrice());
            System.out.println("I have enough resources, making you coffee!");
        }

        menu();
    }

    private static void exit() {
        System.exit(0);
    }

    private static void fill() {
        int qty2add = 0;

        System.out.println(Messages.FILL_0);
        qty2add = scanner.nextInt();
        while (qty2add <= 0) {
            System.err.printf("%s\n", Messages.FILL_4);
            System.out.println(Messages.FILL_0);
            qty2add = scanner.nextInt();
        }
        supplies[0] += qty2add;

        System.out.println(Messages.FILL_1);
        qty2add = scanner.nextInt();
        while (qty2add <= 0) {
            System.err.printf("%s\n", Messages.FILL_4);
            System.out.println(Messages.FILL_1);
            qty2add = scanner.nextInt();
        }
        supplies[1] += qty2add;

        System.out.println(Messages.FILL_2);
        qty2add = scanner.nextInt();
        while (qty2add <= 0) {
            System.err.printf("%s\n", Messages.FILL_4);
            System.out.println(Messages.FILL_2);
            qty2add = scanner.nextInt();
        }
        supplies[2] += qty2add;

        System.out.println(Messages.FILL_3);
        qty2add = scanner.nextInt();
        while (qty2add <= 0) {
            System.err.printf("%s\n", Messages.FILL_4);
            System.out.println(Messages.FILL_3);
            qty2add = scanner.nextInt();
        }
        supplies[3] += qty2add;

        menu();
    }

    private static synchronized void menu() {
        String command = "";

        System.out.println(Messages.INIT);
        command = scanner.next();

        switch(command) {
            case Commands.BUY: buy(scanner);
                               break;
            case Commands.FILL: fill();
                                break;
            case Commands.TAKE: take();
                                break;
            case Commands.REMAINING: remaining();
                                     break;
            case Commands.EXIT: exit();
                                break;
            default: System.err.println(Messages.ERROR_0);
                     menu();
        }
    }

    private static synchronized void take() {
        int money = CoffeeMachine.getMoney();

        if (money == 0) {
            System.err.printf("%s\n", Messages.ERROR_DEFICIT);
        }
        else {
            Collector.increase(money);
            CoffeeMachine.setMoney(0);
            System.out.printf(Messages.TAKE, money);
        }

        menu();
    }

    private static void remaining() {
        int[] supplies = CoffeeMachine.getSupplies();
        int money = CoffeeMachine.getMoney();

        System.out.println(Messages.STATUS_0);
        System.out.println(supplies[0] + Messages.STATUS_1);
        System.out.println(supplies[1] + Messages.STATUS_2);
        System.out.println(supplies[2] + Messages.STATUS_3);
        System.out.println(supplies[3] + Messages.STATUS_4);
        System.out.printf(Messages.STATUS_5, money);

        menu();
    }

    public static void setSupplies(int quantity, int position) {
        if (position < 0 || position >= supplies.length) {
            throw new IllegalArgumentException("Illegal position");
        }

        CoffeeMachine.supplies[position] = quantity;
    }

    public static int[] getSupplies() {
        return CoffeeMachine.supplies;
    }

    public static void setMoney(int money) {
        CoffeeMachine.money = money;
    }

    public static int getMoney() {
        return CoffeeMachine.money;
    }
}

interface Commands {
    public final String BUY = "buy";
    public final String FILL = "fill";
    public final String TAKE = "take";
    public final String REMAINING = "remaining";
    public final String EXIT = "exit";
}

interface Doses {
    public static final int[] ESPRESSO = {250, 0, 16, 1};
    public static final int[] LATTE = {350, 75, 20, 1};
    public static final int[] CAPPUCCINO = {200, 100, 12, 1};
}

interface Ingredients {
    public final String[] CAPPUCINO = { "water", "milk", "coffee", "cups" };
    public final String[] ESPRESSO = { "water", "coffee", "cups" };
    public final String[] LATTE = { "water", "milk", "coffee", "cups" };
}

interface Messages {
    public static final String ASK_0 = "Write how many ml of water the coffee machine has:";
    public static final String ASK_1 = "Write how many ml of milk the coffee machine has:";
    public static final String ASK_2 = "Write how many grams of coffee beans the coffee machine has:";
    public static final String ASK_3 = "Write how many cups of coffee you will need:";
    public static final String BUY_0 = "What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ";
    public static final String DENY = "No, I can make only %d cup(s) of coffee";
    public static final String CONFIRM_FAIR = "Yes, I can make that amount of coffee";
    public static final String CONFIRM_EXCEED = "Yes, I can make that amount of coffee (and even %d more than that)";
    public static final String ERROR_0 = "Unknown command";
    public static final String ERROR_1 = "Unknown product";
    public static final String ERROR_DEFICIT = "Insufficient resources";
    public static final String FILL_0 = "Write how many ml of water you want to add:";
    public static final String FILL_1 = "Write how many ml of milk you want to add:";
    public static final String FILL_2 = "Write how many grams of coffee beans you want to add:";
    public static final String FILL_3 = "Write how many disposable cups you want to add:";
    public static final String FILL_4 = "Please, enter only POSITIVE numbers";
    public static final String INIT = "Write action (buy, fill, take, remaining, exit):";
    public static final String STATUS_0 = "\nThe coffee machine has:";
    public static final String STATUS_1 = " ml of water";
    public static final String STATUS_2 = " ml of milk";
    public static final String STATUS_3 = " g of coffee beans";
    public static final String STATUS_4 = " disposable cups";
    public static final String STATUS_5 = "$%s of money\n";
    public static final String TAKE = "I gave you $%s of money\n";
}

class Product {
    private int id;
    private String name;
    private int[] ingredients;
    private int price;

    public Product() {
    }

    public Product(int id, String name, int[] ingredients, int price) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
    }

    public void setId(int setId) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredients(int[] ingredients) {
        this.ingredients = ingredients;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int[] getIngredients() {
        return this.ingredients;
    }

    public int getPrice() {
        return this.price;
    }
};

class Calculator {
    private static final String[] GRADES = {"D", "F", "E"};
    private static final String[] MASKS = {"FFF", "EFF", "FEF", "FFE", "FEE", "EFE", "EEF", "EEE"};
    public static void calcNeeds(int[] needs, int cups) {
        /*
        int[] ipc = {Ingredients.WATER, Ingredients.MILK, Ingredients.COFFEE};

        for (int i = 0; i < ipc.length; i++) {
            int[] doses = CoffeeMachine.products[i].getDoses();

            ipc[i] = doses[i];
            needs[i] = cups * ipc[i];
        }
        */
    }

    private static int calcMinValue(int[] values) {
        int min = values[0];

        for (int i = 1; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }

        return min;
    }

    public static Estimation calcCapacity(int[] needs, int[] supplies, int cups) {
        Estimation estimation = new Estimation();

        /*
        int[] ipc = {Ingredients.WATER, Ingredients.MILK, Ingredients.COFFEE};
        int currentCupsPossible = 0;
        int currentCupsNeeded = 0;
        int currentDiff = 0;
        String mask = "";
        int [] diffs = new int[needs.length];
        int minDiff = 0;

        for (int i = 0; i < needs.length; i++) {
            currentCupsPossible = supplies[i] / ipc[i];
            currentCupsNeeded = needs[i] / ipc[i];
            currentDiff = currentCupsPossible - currentCupsNeeded;

            if (currentDiff < 0) {
                currentDiff = currentCupsPossible;
                mask += GRADES[0];
            }
            else if (currentDiff >= 0 && currentDiff < 1) {
                mask += GRADES[1];
            }
            else if (currentDiff >= 1) {
                mask += GRADES[2];
            }

            diffs[i] = currentDiff;
        }

        estimation.setMask(mask);
        minDiff = calcMinValue(diffs);
        estimation.setDiff(minDiff);
        */

        return estimation;
    }

    public static void printEstimation(Estimation estimation) {
        try {
            String mask = estimation.getMask();
            int diff = estimation.getDiff();
            boolean isMaskFair = mask.equals(
                    MASKS[0]) ||
                    mask.equals(MASKS[1]) ||
                    mask.equals(MASKS[2]) ||
                    mask.equals(MASKS[3]) ||
                    mask.equals(MASKS[4]) ||
                    mask.equals(MASKS[5]) ||
                    mask.equals(MASKS[6]);

            if (isMaskFair) {
                System.out.println(Messages.CONFIRM_FAIR);
            }
            else if (mask.equals(MASKS[7])) {
                System.out.printf(Messages.CONFIRM_EXCEED, diff);
            }
            else {
                System.out.printf(Messages.DENY, diff);
            }
        } catch(NullPointerException npe) {
            npe.printStackTrace(System.err);
        }
    }
}

class Collector {
    public static final int ERROR_NO_MONEY = 345;
    private static double vault;

    private static void setVault(int vault) {
        Collector.vault = vault;
    }

    private static double getVault() {
        return Collector.vault;
    }

    public static void increase(double amount) {
        Collector.vault += amount;
    }

    public static void decrease(double amount) {
        Collector.vault -= amount;
    }
}

class Estimation {
    private String mask;
    private int diff;

    public Estimation() {
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public String getMask() {
        return this.mask;
    }

    public int getDiff() {
        return this.diff;
    }
}

class DeficitException extends Exception {
    public DeficitException(String message) {
        super(message);
    }
}

