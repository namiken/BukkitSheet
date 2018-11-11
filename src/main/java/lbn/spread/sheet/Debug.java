package lbn.spread.sheet;

import java.util.Arrays;

import lbn.spread.api.LbnSpreadSheet;

public class Debug {
  public static void main(String[] args) throws Exception {
    LbnSpreadSheet instance = LbnSpreadSheet.getInstance("TheLowSheet", "mob");
    String[][] allData = instance.getAllData(new String[] { "type" });
    System.out.println(Arrays.deepToString(allData));
  }
}
