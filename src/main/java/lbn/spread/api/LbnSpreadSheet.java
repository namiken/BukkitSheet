package lbn.spread.api;

import java.io.IOException;
import java.util.HashMap;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

import lbn.spread.sheet.Authentication;

public class LbnSpreadSheet {

  public static final String SPREAD_SHEET_NAME = "TheLowSheet";

  SpreadsheetService service;
  SpreadsheetEntry ssEntry;
  WorksheetEntry wsEntry;

  public static LbnSpreadSheet getInstance(String workSheetName) throws Exception {
    return getInstance(SPREAD_SHEET_NAME, workSheetName);
  }

  public static LbnSpreadSheet getInstance(String sheetName, String workSheetName) throws Exception {
    LbnSpreadSheet lbnSpreadSheet = new LbnSpreadSheet();
    lbnSpreadSheet.service = Authentication.getService();
    lbnSpreadSheet.ssEntry = Authentication.findSpreadsheetByName(lbnSpreadSheet.service, sheetName);
    if (lbnSpreadSheet.ssEntry == null) { return null; }
    lbnSpreadSheet.wsEntry = Authentication.findWorksheetByName(lbnSpreadSheet.service, lbnSpreadSheet.ssEntry, workSheetName);
    if (lbnSpreadSheet.wsEntry == null) { return null; }
    return lbnSpreadSheet;
  }

  protected LbnSpreadSheet() throws Exception {}

  /**
   * 一行追加
   * 
   * @param valuesMap key=title, value=内容
   * @throws Exception
   */
  public void addDataRow(HashMap<String, Object> valuesMap) throws Exception {
    Authentication.insertDataRow(service, wsEntry, valuesMap);
  }

  public void deleteDataRow(String query) throws Exception {
    Authentication.deleteDataRow(service, wsEntry, query);
  }

  public void updateDataRow(HashMap<String, Object> valuesMap, String query) throws Exception {
    Authentication.updateDataRow(service, wsEntry, query, valuesMap);
  }

  public String[][] getAllData(String[] tags) throws IOException, Exception {
    return Authentication.getAllData(service, wsEntry, tags);
  }

  public String[][] getAllDataByQuery(String[] tags, String query) throws IOException, Exception {
    return Authentication.getDataByQuery(service, wsEntry, tags, query);
  }

}
