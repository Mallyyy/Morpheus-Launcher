package launcher.main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JProgressBar;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Toolkit;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.MatteBorder;
import java.awt.SystemColor;
import java.awt.ComponentOrientation;
import java.awt.Component;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Launcher {

  //CLIENT DL CHECK
  private static boolean clientdl = true;

  //OPEN SITE METHOD
  public static void openWebpage(String urlString) {
    try {
      Desktop.getDesktop().browse(new URL(urlString).toURI());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Window Move Methods
  private int tx,ty;
  private void titlebar_mousePressed(MouseEvent m) {
    tx= m.getX();
    ty=m.getY();
  }
  private void titlebar_mouseDragged(MouseEvent m) {
    MorpheusLauncher.setLocation(m.getXOnScreen() - tx, m.getYOnScreen()-ty);
  }

  //Constants
  private JFrame MorpheusLauncher;
  public static JProgressBar progressBar = new JProgressBar();
  public static JButton play = new JButton("");
  static JLabel lblClientUpTo = new JLabel("");

  // Launch the application.
  public static void main(String[] args) throws IOException {
    System.out.println(File.listRoots()[0].getAbsolutePath());
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Launcher window = new Launcher();
          window.MorpheusLauncher.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    });
      RichPresense.initiate();
      play.setEnabled(true);
      lblClientUpTo.setVisible(false);
      clientdl = true;

    if (Launcher.updatedCache()) {
        updatedCache();
      }
      new File(File.listRoots()[0].getAbsolutePath() + "/Morpheus/").mkdir();
      downloadClient("https://morpheus-rsps.com/client/Morpheus.jar", File.listRoots()[0].getAbsolutePath() + "/Morpheus/Morpheus.jar");
    }
  //Create the application.
  public Launcher() {
    initialize();
  }
  public static final boolean FORCE_CACHE_UPDATE = false;
  public static final boolean STOP_CACHE_UPDATES = false;

  public static boolean localHost = true;
  public final static String SERVER_HOST() {
    return localHost ? "localhost" : "127.0.0.1";//15.204.66.46
  }

  public final static String JAVA_DIRECTORY_NAME ="";


  public final static String CACHE_DIRECTORY_NAME = "keystonecache";
  private static final String CACHE_FILE_NAME = "keystonecache.zip";
  private static final String CACHE_URL = "https://morpheus-rsps.com/cache/keystonecache.zip";
  private static final String NEWEST_VERSION_FILE_URL = "https://morpheus-rsps.com/cache/cache_version.txt";
  private static final String CURRENT_VERSION_FILE = "cache_version.txt"; //The location of the local cache_version txt file
  public static boolean UPDATING = true;

  public static void downloadCache() throws IOException {
    URL url = new URL(CACHE_URL);
    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
    httpConn.addRequestProperty("User-Agent", "Mozilla/4.76");
    int responseCode = httpConn.getResponseCode();
    int size = httpConn.getContentLength();

    if (responseCode != HttpURLConnection.HTTP_OK) {
//      downloadCache2();
      return;
    }
    System.out.println("First cache download");


    // always check HTTP response code first
    if (responseCode == HttpURLConnection.HTTP_OK) {
      String fileName = CACHE_FILE_NAME;
      // opens input stream from the HTTP connection
      InputStream inputStream = httpConn.getInputStream();
      String saveFilePath = getCacheDirectory() + File.separator + fileName;

      // opens an output stream to save into file
      FileOutputStream outputStream = new FileOutputStream(saveFilePath);

      int bytesRead = -1;
      byte[] buffer = new byte[4096];
      long startTime = System.currentTimeMillis();
      int downloaded = 0;
      long numWritten = 0;

      if (httpConn.getContentLength() <=  0){
//        downloadCache2();
        return;
      }
      int count;
      double sumCount = 0.0;
      int length = httpConn.getContentLength() > 0 ? httpConn.getContentLength() : 135445935;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
        numWritten += bytesRead;
        downloaded += bytesRead;


        sumCount += bytesRead;
        if (size > 0) {
          int Value = (int) (sumCount / size * 99.0);
          progressBar.setValue(Value);
        }
      }

      outputStream.close();
      inputStream.close();

    } else {
      System.out.println("Cache host replied HTTP code: " + responseCode);
    }
    httpConn.disconnect();
  }
  private static void unzipPartlyArchive(ZipInputStream zin, String s) throws Exception {
    FileOutputStream out = new FileOutputStream(s);
    //drawLoadingText(100, "Unpacking data..");
    byte[] b = new byte[1024];
    int len = 0;

    while ((len = zin.read(b)) != -1) {
      out.write(b, 0, len);
    }
    out.close();
  }
  private static void unzipCache() {
    try {
      final File file = new File(getCacheDirectory() + CACHE_FILE_NAME);
      InputStream in = new BufferedInputStream(new FileInputStream(file));
      ZipInputStream zin = new ZipInputStream(in);
      ZipEntry e;
      //new FinishedCacheDownload();
      while ((e = zin.getNextEntry()) != null) {
        if (e.isDirectory()) {
          (new File(getCacheDirectory() + e.getName())).mkdir();

        } else {

          if (e.getName().equals(file.getName())) {
            unzipPartlyArchive(zin, file.getName());
            break;
          }
          unzipPartlyArchive(zin, getCacheDirectory() + e.getName());
        }

      }

      zin.close();
      file.delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static String getCacheDirectory() {
    String cacheLoc = System.getProperty("user.home") + "/";

    cacheLoc += CACHE_DIRECTORY_NAME + "/";
    File cacheDir = new File(cacheLoc);
    if(!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheLoc;
  }

  public static String getJavaDirectory() {
    String javaPath = System.getProperty("user.dir") + "/";

    javaPath += JAVA_DIRECTORY_NAME + "/";
    File javaDir = new File(javaPath);
    if(!javaDir.exists()) {
      javaDir.mkdir();
    }
    return javaPath;
  }
  //Downloads Client
  public static void downloadClient(String remotePath, String localPath) {
    BufferedInputStream in = null;
    FileOutputStream out = null;

    try {
      URL url = new URL(remotePath);
      URLConnection conn = url.openConnection();
      int size = conn.getContentLength();

      if (size < 0) {
        System.out.println("Could not get the file size");
      } else {
        System.out.println("File size: " + size);
        System.out.println("Finished");
      }

      in = new BufferedInputStream(url.openStream());
      out = new FileOutputStream(localPath);
      byte data[] = new byte[1024];
      int count;
      double sumCount = 0.0;
      play.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/loadingsign.gif")));
      while ((count = in.read(data, 0, 1024)) != -1) {
        out.write(data, 0, count);

        sumCount += count;
        if (size > 0) {
          int Value = (int)(sumCount / size * 100.0);
          progressBar.setValue(Value);
          if (Value == 100){
            lblClientUpTo.setVisible(true);
            clientdl = false;
            play.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            play.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/playbutton.png")));
            progressBar.setVisible(true);
          }

        }
      }

    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    } catch (IOException e2) {
      e2.printStackTrace();
    } finally {
      if (in != null)
        try {
          in.close();
        } catch (IOException e3) {
          e3.printStackTrace();
        }
      if (out != null)
        try {
          out.close();
        } catch (IOException e4) {
          e4.printStackTrace();
        }
    }
  }
  public static boolean cacheDownloadRequired(double newest, double current) {
    return newest != current;
  }
  public static boolean forceUpdateCache() {
    return Launcher.SERVER_HOST().equalsIgnoreCase("localhost") && Launcher.FORCE_CACHE_UPDATE;
  }
  public static boolean updatedCache() {
    try {
      double newest = getNewestVersion();
      double current = getCurrentVersion();
      if (cacheDownloadRequired(newest, current) || forceUpdateCache()) {
        if (Launcher.STOP_CACHE_UPDATES) {
          System.out.println("Stopped a cache update from occuring due to current configuration.");
        } else {
          if (forceUpdateCache()) {
            System.out.println("We are localhost, and being forced to update cache.");
          } else {
            System.out.println("Updated Morpheus Cache. No manual overrides detected, proceeding as normal. Current: " + current + ", Newest: " + newest);
          }
          downloadCache();
          unzipCache();
          setLatestCacheVersion(newest);
        }
      }

              UPDATING = true;
      return true;

    } catch (Exception e) {
      e.printStackTrace();
    }
    UPDATING = true;
    return false;

  }
  public static double getNewestVersion() {
    try {
      System.out.println("First cache version link");
      URL url = new URL(NEWEST_VERSION_FILE_URL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.addRequestProperty("User-Agent", "Mozilla/4.76");
      BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      double version = Double.parseDouble(br.readLine());
      br.close();
      return version;
    } catch (Exception e) {
      System.out.println("HERE SECOND");
      e.printStackTrace();
      return 0.1D;
    }
  }
  public static void setLatestCacheVersion(double newest) throws IOException {
    BufferedWriter bw = new BufferedWriter(new FileWriter(getCacheDirectory() + CURRENT_VERSION_FILE));
    bw.write(String.valueOf(newest));
    bw.close();
  }
  public static double getCurrentVersion() {
    try {
      File file = new File(getCacheDirectory() + CURRENT_VERSION_FILE);
      if (!file.exists()) {
        return 0.0;
      }
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      double version = Double.parseDouble(br.readLine());
      br.close();
      return version;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.1D;
  }
  public static String getPlayercount(){
    try {
      String url = "";
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestProperty("User-Agent", "Mozilla/5.0");
      BufferedReader in = new BufferedReader(
              new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      return response.toString();
    } catch(Exception e) {
      return "";
    }
  }
  //Initialize the contents of the frame.
  private void initialize() {

    try {
      InputStream in = new URL("https://morpheus-rsps.com/images/Latest.png").openStream();
      Files.copy(in, Paths.get(File.listRoots()[0].getAbsolutePath() + "/Morpheus/Latest.png"), StandardCopyOption.REPLACE_EXISTING);

      in = new URL("https://morpheus-rsps.com/images/AlertBox2.png").openStream();
      Files.copy(in, Paths.get(File.listRoots()[0].getAbsolutePath() + "/Morpheus/AlertBox2.png"), StandardCopyOption.REPLACE_EXISTING);

      in = new URL("https://morpheus-rsps.com/images/AlertBox.png").openStream();
      Files.copy(in, Paths.get(File.listRoots()[0].getAbsolutePath() + "/Morpheus/AlertBox.png"), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }


    MorpheusLauncher = new JFrame();
    ComponentResizer cr = new ComponentResizer();
    cr.registerComponent(MorpheusLauncher);
    cr.setMaximumSize(new Dimension(765, 503));
    cr.setMinimumSize(new Dimension(765, 503));
    MorpheusLauncher.setUndecorated(true);
    MorpheusLauncher.getContentPane().setForeground(SystemColor.menu);
    MorpheusLauncher.setBackground(new Color(255, 255, 255));
    MorpheusLauncher.setVisible(true);
    MorpheusLauncher.setIconImage(Toolkit.getDefaultToolkit().getImage(Launcher.class.getResource("/launcher/core/MorpheusM.png")));
    MorpheusLauncher.setTitle("Morpheus Launcher");
    MorpheusLauncher.getContentPane().setName("Morpheus Launcher");
    MorpheusLauncher.setResizable(false);
    MorpheusLauncher.setBounds(100, 100, 765, 503);
    MorpheusLauncher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    MorpheusLauncher.setLocationRelativeTo(null);
    MorpheusLauncher.getContentPane().setLayout(new BoxLayout(MorpheusLauncher.getContentPane(), BoxLayout.X_AXIS));



    JPanel main = new JPanel();
    main.setBorder(null);
    main.setForeground(SystemColor.menu);
    main.setBackground(new Color(250, 235, 215));
    MorpheusLauncher.getContentPane().add(main);
    main.setLayout(null);
    UIManager.put("progressBar.background", Color.ORANGE);
    UIManager.put("progressBar.foreground", Color.BLUE);
    UIManager.put("progressBar.selectionBackground", Color.RED);
    UIManager.put("progressBar.selectionForeground", Color.GREEN);

    JLabel nav1 = new JLabel("");
    nav1.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/dot.png")));
    nav1.setBounds(353, 395, 20, 20);
    main.add(nav1);

    JLabel nav2 = new JLabel("");
    nav2.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/dot.png")));
    nav2.setBounds(372, 395, 20, 20);
    main.add(nav2);

    JLabel nav3 = new JLabel("");
    nav3.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/dot.png")));
    nav3.setBounds(391, 395, 20, 20);
    main.add(nav3);
    nav1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    nav2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    nav3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    lblClientUpTo.setFont(new Font("Arial", Font.PLAIN, 10));
    lblClientUpTo.setForeground(new Color(128, 128, 128));
    lblClientUpTo.setBounds(465, 474, 115, 14);
    main.add(lblClientUpTo);

    JLabel news1 = new JLabel("");
    news1.setIcon(new ImageIcon(File.listRoots()[0].getAbsolutePath() + "/Morpheus/Latest.png"));
    news1.setBounds(0, 0, 765, 503);
    main.add(news1);
    progressBar.setBorderPainted(false);
    progressBar.setStringPainted(true);
    progressBar.setIgnoreRepaint(true);
    progressBar.setInheritsPopupMenu(true);
    progressBar.setOpaque(true);
    progressBar.setAlignmentY(Component.BOTTOM_ALIGNMENT);
    progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
    progressBar.setRequestFocusEnabled(false);
    progressBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    progressBar.setFocusTraversalKeysEnabled(false);
    progressBar.setFocusable(false);
    progressBar.setBorder(new MatteBorder(3, 3, 3, 3, (Color) new Color(52, 52, 52, 255)));
    progressBar.setBackground(new Color(9, 240, 126));
    progressBar.setBounds(26, 431, 450, 49);
    progressBar.setForeground(new Color(39, 40, 84, 255));
    progressBar.setFont(new Font("Yu Gothic UI Semibold", Font.ITALIC, 12));
    main.add(progressBar);
    play.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/loadingsign.gif")));

    play.setBorderPainted(false);
    if (clientdl == true)
      play.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/loadingsign.gif")));
    else
      play.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/playbutton.png")));

    play.setFocusPainted(false);
    play.setBorder(new MatteBorder(1, 1, 0, 1, (Color) new Color(0, 0, 0)));

    play.setBackground(new Color(255, 255, 255));
    play.setAlignmentY(0.0f);


    main.add(play);
    play.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 15));
    play.setBounds(502, 431, 240, 50);
    play.setOpaque(false);
    play.setContentAreaFilled(false);
    play.setBorderPainted(false);

    JLabel CloseButton = new JLabel("");
    CloseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    CloseButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/closebutton.png")));
    CloseButton.setBounds(728, 11, 12, 13);
    main.add(CloseButton);
    JLabel MinButton = new JLabel("");
    MinButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    MinButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/minimizebutton.png")));
    MinButton.setBounds(701, 11, 12, 13);
    main.add(MinButton);

    JLabel StoreButton = new JLabel("");
    StoreButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    StoreButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/Storebutton.png")));
    StoreButton.setBounds(12, 82, 240, 50);
    main.add(StoreButton);

    JLabel VoteButton = new JLabel("");
    VoteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    VoteButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/Votebutton.png")));
    VoteButton.setBounds(262, 82, 240, 50);
    main.add(VoteButton);

    JLabel DiscordButton = new JLabel("");
    DiscordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    DiscordButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/Discordbutton.png")));
    DiscordButton.setBounds(512, 82, 240, 50);
    main.add(DiscordButton);

    JLabel HeaderPage = new JLabel("");
    HeaderPage.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/MorpheusHeader.png")));
    HeaderPage.setBounds(0, 0, 765, 503);
    main.add(HeaderPage);

    JLabel Background = new JLabel("");
    Background.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/MorpheusBackgroundAnimated.gif")));
    Background.setBounds(0, 0, 765, 503);
    main.add(Background);


    //ACTIONS
    nav1.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent arg0) {
        news1.setIcon(new ImageIcon(File.listRoots()[0].getAbsolutePath() + "/Morpheus/Latest.png"));
      }
      @Override
      public void mouseEntered(MouseEvent e) {
        nav1.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/dothover.png")));
      }
      @Override
      public void mouseExited(MouseEvent e) {
        nav1.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/dot.png")));
      }
    });
    nav2.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent arg0) {
        news1.setIcon(new ImageIcon(File.listRoots()[0].getAbsolutePath() + "/Morpheus/AlertBox2.png"));
      }
      @Override
      public void mouseEntered(MouseEvent e) {
        nav2.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/dothover.png")));
      }
      public void mouseExited(MouseEvent e) {
        nav2.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/dot.png")));
      }
    });
    nav3.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent arg0) {
        news1.setIcon(new ImageIcon(File.listRoots()[0].getAbsolutePath() + "/Morpheus/AlertBox.png"));
      }
      @Override
      public void mouseEntered(MouseEvent e) {
        nav3.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/dothover.png")));
      }
      public void mouseExited(MouseEvent e) {
        nav3.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/dot.png")));
      }
    });
    DiscordButton.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e){
        openWebpage("");
      }
      @Override
      public void mouseEntered(MouseEvent e) {
        DiscordButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/Discordhover.png")));
      }
      public void mouseExited(MouseEvent e) {
        DiscordButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/Discordbutton.png")));
      }
    });
    StoreButton.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e){
        openWebpage("");
      }
      @Override
      public void mouseEntered(MouseEvent e) {
        StoreButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/StoreHover.png")));
      }
      public void mouseExited(MouseEvent e) {
        StoreButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/Storebutton.png")));
      }
    });
    VoteButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e){
        openWebpage("");
      }
      @Override
      public void mouseEntered(MouseEvent e) {
        VoteButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/Votehover.png")));
      }
      public void mouseExited(MouseEvent e) {
        VoteButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/Votebutton.png")));
      }
    });
    CloseButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent arg0) {
        File news = new File(File.listRoots()[0].getAbsolutePath() + "/Morpheus/Latest.png");
        if(news.delete()){
          System.out.println(news.getName() + " is deleted!");
        }
        news = new File(File.listRoots()[0].getAbsolutePath() + "/Morpheus/AlertBox2.png");
        if(news.delete()){
          System.out.println(news.getName() + " is deleted!");
        }
        news = new File(File.listRoots()[0].getAbsolutePath() + "/Morpheus/AlertBox.png");
        if(news.delete()){
          System.out.println(news.getName() + " is deleted!");
        }
//        RichPresense.initiateExit();
        System.exit(0);
      }
      @Override
      public void mouseEntered(MouseEvent e) {
        CloseButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/close_hover.png")));
      }
      @Override
      public void mouseExited(MouseEvent e) {
        CloseButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/closebutton.png")));
      }
    });
    MinButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent arg0) {
        MorpheusLauncher.setState(Frame.ICONIFIED);
      }
      @Override
      public void mouseEntered(MouseEvent e) {
        MinButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/minimizehover.png")));
      }
      @Override
      public void mouseExited(MouseEvent e) {
        MinButton.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/minimizebutton.png")));
      }
    });
    play.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e){
        File f = new File(File.listRoots()[0].getAbsolutePath() + "Morpheus\\Morpheus.jar");
        if(f.exists() && !f.isDirectory()) {
          System.out.println("x-opening");

          try {
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", File.listRoots()[0].getAbsolutePath() + "Morpheus\\Morpheus.jar");
            Process p = pb.start();
            System.exit(0);
          } catch (Exception IOexc) {
            System.err.println(IOexc.toString());
          }
        } else {
          lblClientUpTo.setText("Files are missing!");
        }
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        if (clientdl == false)
          play.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/playbuttonhover.png")));
      }
      public void mouseExited(MouseEvent e) {
        if (clientdl == false)
          play.setIcon(new ImageIcon(Launcher.class.getResource("/launcher/core/playbutton.png")));
      }
    });
    main.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        titlebar_mousePressed(e);
      }
    });
    main.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e){
        titlebar_mouseDragged(e);
      }
    });


  }

}


