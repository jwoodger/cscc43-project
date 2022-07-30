package cscc43.mybnb.menus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

public final class MenuUtils {
  private static Scanner scanner = new Scanner(System.in);

  public static int menu(String title, String... options) {
    int result = -1;
    boolean success = false;

    while (!success) {
      System.out.println(title);
      int i = 1;
      for (String opt : options) {
        System.out.printf("(%d) %s\n", i, opt);
        i++;
      }

      try {
        result = scanner.nextInt();
        if (result < 1 || result > options.length) {
          System.out.printf("Please choose a number between 1 and %d\n", options.length);
        } else {
          success = true;
        }
      } catch (InputMismatchException e) {
        System.out.println("Invalid input.");
        scanner.next();
      }
    }
    return result;
  }

  public static String askString(String prompt) {
    System.out.printf("%s: ", prompt);
    return scanner.next();
  }

  public static LocalDate askDate(String prompt) {
    LocalDate result = null;

    while (result == null) {
      System.out.printf("%s (YYYY-MM-DD): ", prompt);
      String str = scanner.next();
      try {
        result = LocalDate.parse(str);
      } catch (DateTimeParseException e) {
        System.out.println("Invalid format.");
        result = null;
      }
    }

    return result;
  }
}
