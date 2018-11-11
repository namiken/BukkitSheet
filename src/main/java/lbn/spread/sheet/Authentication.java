package lbn.spread.sheet;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

public class Authentication {
  // もう使わないのでコメントアウト
  // public static Credential authorize() throws Exception {
  // HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
  // JsonFactory jsonFactory = new JacksonFactory();
  // GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
  // .setJsonFactory(jsonFactory).setServiceAccountId(SpreadSheet.ACCOUNT_P12_ID)
  // .setServiceAccountPrivateKeyFromP12File(SpreadSheet.P12FILE).setServiceAccountScopes(SpreadSheet.SCOPES)
  // .build();
  //
  //
  //// boolean ret = credential.refreshToken();
  // credential.refreshToken();
  // // debug dump
  //
  // // debug dump
  //
  // return credential;
  // }

  public static SpreadsheetService getService() throws Exception {
    // System.out.println("service in");

    SpreadsheetService service = new SpreadsheetService(SpreadSheet.APPLICATION_NAME);
    service.setProtocolVersion(SpreadsheetService.Versions.V3);

    FileInputStream jsonKeyFile = new FileInputStream(SpreadSheet.jsonKeyUrl);
    Credential credential = GoogleCredential.fromStream(jsonKeyFile).createScoped(Arrays.asList("https://spreadsheets.google.com/feeds/"));
    credential.refreshToken();
    service.setOAuth2Credentials(credential);

    // debug dump
    // System.out.println("Schema: " + service.getSchema().toString());
    // System.out.println("Protocol: " + service.getProtocolVersion().getVersionString());
    // System.out.println("ServiceVersion: " + service.getServiceVersion());
    //
    // System.out.println("service out");

    return service;
  }

  public static List<SpreadsheetEntry> findAllSpreadsheets(SpreadsheetService service) throws Exception {

    SpreadsheetFeed feed = service.getFeed(SpreadSheet.SPREADSHEET_FEED_URL, SpreadsheetFeed.class);

    List<SpreadsheetEntry> spreadsheets = feed.getEntries();

    // debug dump
    for (SpreadsheetEntry spreadsheet : spreadsheets) {
      System.out.println("title: " + spreadsheet.getTitle().getPlainText());
    }

    return spreadsheets;
  }

  public static SpreadsheetEntry findSpreadsheetByName(SpreadsheetService service, String TestSheet)
      throws Exception {
    SpreadsheetQuery sheetQuery = new SpreadsheetQuery(SpreadSheet.SPREADSHEET_FEED_URL);
    sheetQuery.setTitleQuery(TestSheet);
    SpreadsheetFeed feed = service.query(sheetQuery, SpreadsheetFeed.class);
    SpreadsheetEntry ssEntry = null;
    if (feed.getEntries().size() > 0) {
      ssEntry = feed.getEntries().get(0);
    }
    return ssEntry;
  }

  public static List<WorksheetEntry> FindAllWorkSheet(SpreadsheetService service, SpreadsheetEntry ssEntry)
      throws IOException, ServiceException {

    WorksheetQuery worksheetQuery = new WorksheetQuery(ssEntry.getWorksheetFeedUrl());
    WorksheetFeed feed = service.query(worksheetQuery, WorksheetFeed.class);

    List<WorksheetEntry> allws = feed.getEntries();

    // for (WorksheetEntry allwsheet : allws) {
    // System.out.println("ws title: " + allwsheet.getTitle().getPlainText());
    // }

    return allws;
  }

  public static WorksheetEntry findWorksheetByName(SpreadsheetService service, SpreadsheetEntry ssEntry,
      String sheetName) throws Exception {
    WorksheetQuery worksheetQuery = new WorksheetQuery(ssEntry.getWorksheetFeedUrl());
    worksheetQuery.setTitleQuery(sheetName);
    WorksheetFeed feed = service.query(worksheetQuery, WorksheetFeed.class);
    // 完全一致するものをかえす
    for (WorksheetEntry entry : feed.getEntries()) {
      if (entry.getTitle().getPlainText().equalsIgnoreCase(sheetName)) { return entry; }
    }
    return null;
  }

  public static WorksheetEntry addWorksheet(SpreadsheetService service, SpreadsheetEntry ssEntry, String sheetName,
      int colNum, int rowNum) throws Exception {
    WorksheetEntry wsEntry = new WorksheetEntry();
    wsEntry.setTitle(new PlainTextConstruct(sheetName));
    wsEntry.setColCount(colNum);
    wsEntry.setRowCount(rowNum);
    URL worksheetFeedURL = ssEntry.getWorksheetFeedUrl();
    return service.insert(worksheetFeedURL, wsEntry);
  }

  public static String makeQuery(int minrow, int maxrow, int mincol, int maxcol) {
    String base = "?min-row=MINROW&max-row=MAXROW&min-col=MINCOL&max-col=MAXCOL";
    return base.replaceAll("MINROW", String.valueOf(minrow)).replaceAll("MAXROW", String.valueOf(maxrow))
        .replaceAll("MINCOL", String.valueOf(mincol)).replaceAll("MAXCOL", String.valueOf(maxcol));
  }

  public static void insertDataRow(SpreadsheetService service, WorksheetEntry wsEntry, Map<String, Object> values)
      throws Exception {
    ListEntry dataRow = new ListEntry();

    values.forEach((title, value) -> {
      dataRow.getCustomElements().setValueLocal(title, value.toString());
    });

    URL listFeedUrl = wsEntry.getListFeedUrl();
    service.insert(listFeedUrl, dataRow);

  }

  public static void insertHeadRow(SpreadsheetService service, WorksheetEntry wsEntry, List<String> header,
      String query) throws Exception {

    URL cellFeedUrl = new URI(wsEntry.getCellFeedUrl().toString() + query).toURL();
    CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

    for (int i = 0; i < header.size(); i++) {
      cellFeed.insert(new CellEntry(1, i + 1, header.get(i)));
    }

  }

  public static void updateDataRow(SpreadsheetService service, WorksheetEntry wsEntry, String query,
      Map<String, Object> values) throws Exception {
    if (query == null) { throw new NullPointerException("Query is null"); }

    ListQuery listQuery = new ListQuery(wsEntry.getListFeedUrl());
    listQuery.setSpreadsheetQuery(query);
    ListFeed listFeed = service.query(listQuery, ListFeed.class);
    List<ListEntry> listEntryList = listFeed.getEntries();

    for (ListEntry row : listEntryList) {
      values.forEach((title, value) -> {
        row.getCustomElements().setValueLocal(title, value.toString());
      });

      row.update();
    }
  }

  public static void deleteDataRow(SpreadsheetService service, WorksheetEntry wsEntry, String query) throws IOException, ServiceException {
    if (query == null) { throw new NullPointerException("Query is null"); }
    ListQuery listQuery = new ListQuery(wsEntry.getListFeedUrl());
    listQuery.setSpreadsheetQuery(query);
    ListFeed listFeed = service.query(listQuery, ListFeed.class);
    List<ListEntry> listEntryList = listFeed.getEntries();

    for (ListEntry row : listEntryList) {
      row.delete();
    }
  }

  public static String[][] getDataByQuery(SpreadsheetService service, WorksheetEntry wsEntry, String[] tags, String query)
      throws IOException, ServiceException {
    if (query == null) { throw new NullPointerException("Query is null"); }

    ArrayList<String[]> data = new ArrayList<>();

    ListQuery listQuery = new ListQuery(wsEntry.getListFeedUrl());
    listQuery.setSpreadsheetQuery(query);
    ListFeed listFeed = service.query(listQuery, ListFeed.class);
    List<ListEntry> listEntryList = listFeed.getEntries();

    for (ListEntry listEntry : listEntryList) {
      CustomElementCollection elements = listEntry.getCustomElements();
      String[] lineData = new String[tags.length];
      int i = 0;
      for (String title : tags) {
        lineData[i] = elements.getValue(title);
        i++;
      }
      data.add(lineData);
    }

    return data.toArray(new String[0][0]);
  }

  public static String[][] getAllData(SpreadsheetService service, WorksheetEntry wsEntry, String[] tags)
      throws IOException, ServiceException {
    URL listFeedUrl = wsEntry.getListFeedUrl();
    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

    List<ListEntry> listEntryList = listFeed.getEntries();

    ArrayList<String[]> data = new ArrayList<>();

    for (ListEntry listEntry : listEntryList) {
      CustomElementCollection elements = listEntry.getCustomElements();
      String[] lineData = new String[tags.length];
      int i = 0;
      for (String title : tags) {
        lineData[i] = elements.getValue(title);
        i++;
      }
      data.add(lineData);
    }

    return data.toArray(new String[0][0]);
  }

  public static void main(String[] args) throws Exception {
    System.out.println("main start");

    SpreadsheetService service = getService();

    findAllSpreadsheets(service);
    // スプレッドシートを検索
    String ssName = "TheLowSheet";

    SpreadsheetEntry ssEntry = findSpreadsheetByName(service, ssName);

    // ワークシートを検索
    String wsName = "TestWorkSheet";

    WorksheetEntry newWorksheet = findWorksheetByName(service, ssEntry, wsName);

    // // ワークシートのタイトル名
    // List<String> header = new ArrayList<>();
    // header.add("test1");
    // header.add("test2");
    // header.add("test3");
    // header.add("test4");
    // header.add("test5");
    // header.add("test6");
    //
    // insertHeadRow(service, newWorksheet, header, makeQuery(1, 1, 1, 5));
    //
    // insert
    Map<String, Object> insertValues1 = new HashMap<>();
    insertValues1.put("test1", 1600);
    insertValues1.put("test2", 1200);
    insertValues1.put("test3", 1300);
    insertValues1.put("test4", 1400);
    insertValues1.put("test5", 1500);
    insertValues1.put("test6", 1600);
    //
    insertDataRow(service, newWorksheet, insertValues1);
    //
    // // insert
    // Map<String, Object> insertValues2 = new HashMap<>();
    // insertValues2.put("test1", "2015-09-02");
    // insertValues2.put("test2", 2200);
    // insertValues2.put("test3", 2300);
    // insertValues2.put("test4", 2400);
    // insertValues2.put("test5", 2500);
    // insertValues2.put("test6", 2600);
    //
    // // insertDataRow(service, newWorksheet, insertValues2);
    //
    // // update
    // Map<String, Object> updateValues = new HashMap<>();
    // updateValues.put("test1", "2015-09-01");
    // updateValues.put("test2", 1202);
    // updateValues.put("test3", 1303);
    // updateValues.put("test4", 1404);
    // updateValues.put("test5", 1505);
    // updateValues.put("test6", 1606);
    //
    // System.out.println("main end");
  }

}
