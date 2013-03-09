/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import org.apache.commons.net.ftp.*;

/**
 *
 * @author PASHA
 */
public class Utils {
    
    final static String FTP_AUTH_FILE = "..\\ftp_auth.txt";
    static String FTP_ADDRESS = "deli.beget.ru";
    static String FTP_LOGIN   = "";
    static String FTP_PASS    = "";
    
    static Vector<String> get_lines_from_file(String fileName) {
        Vector<String> res = new Vector<>();
        try {
            BufferedReader file = new BufferedReader(new FileReader(fileName));
            String row;
            while ((row = file.readLine()) != null) {
                res.add(row);
            }
            file.close();
        } catch (Exception e) {
            return res;
        }
        return res;
    }
    
    static String ints_to_str(Vector<Integer> ints) {
        String res = "";
        for (int x: ints) {
            res += x + "\n";
        }
        return res;
    }
    
    static String get_file_text(String fileName) {
        String res = "";
        Vector<String> vs = get_lines_from_file(fileName);
        for (String tempStr: vs) {
            res += tempStr;
        }
        return res;
    }
    
    static void save_file(String fileName, String text) {
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter(fileName));
            String[] lines = text.split("\n");
            for (String line: lines) {
                file.write(line);
                file.newLine();
            }
            file.close();
        } catch (Exception e) {};
    }
    
    static Boolean update_ftp_auth_data() {
        FTP_LOGIN = "";
        FTP_PASS  = "";
        
        try {
            
            Vector<String> data = get_lines_from_file(FTP_AUTH_FILE);
            if ((data.size() >= 2) && (!data.get(0).isEmpty()) && (!data.get(1).isEmpty())) {
                FTP_LOGIN = data.get(0);
                FTP_PASS  = data.get(1);
            } else {
                
                FTPAuth auth_form = new FTPAuth();
                auth_form.setVisible(true);
                while (auth_form != null && auth_form.isVisible()) {
                    Thread.sleep(1);
                }
                if (FTP_LOGIN.isEmpty() || FTP_PASS.isEmpty()) {
                    return false;
                }
                
            }
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }

    static String get_html(String url) {
        String res = new String();
        try {
            URL myUrl = new URL(url);
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                myUrl.openStream()));
            String line;
            while ((line = in.readLine()) != null)
                res += line + "\n";
            in.close();
        } catch (Exception e) {
            return "";
        }
        return res;
    }
    
    static int get_int(String str) {
        String int_str = "";
        for (int i = 0; i < str.length(); i++) {
            if ((str.charAt(i) >= '0') && (str.charAt(i) <= '9')) {
                int_str += str.charAt(i);
            }
        } 
        try {
            return int_str.equals("") ? 0 : Integer.parseInt(int_str);
        } catch (Exception e) {
            return -1;
        }
    }
    
    static Vector<Integer> sum_vectors(Vector<Integer> v1, Vector<Integer> v2) {
        Vector<Integer> res = new Vector<>();
        for (int i = 0; i < v1.size(); i++) {
            res.add(i, v1.get(i) + v2.get(i));
        }
        return res;
    }
    
    static int elements_sum(Vector<Integer> v) {
        int res = 0;
        for (Integer x: v) {
            res += (x > 0) ? x : 0;
        }
        return res;
    }
    
    static Vector<Float> toPercent(Vector<Integer> v) {
        Vector<Float> res = new Vector<>();
        int sum = elements_sum(v);
        for (Integer x: v) {
            res.add(new Float(x) / sum);
        }
        return res;
    }
    
    static void save_to_ftp(String remoteFile, String localFile) {
        if (!update_ftp_auth_data()) {
            return;
        }
        
        FTPClient ftp = new FTPClient();
        FileInputStream fis = null;
        FTPClientConfig config = new FTPClientConfig();
        ftp.configure(config);
        try {
          int reply;
          ftp.connect(FTP_ADDRESS);
          ftp.login(FTP_LOGIN, FTP_PASS);
          reply = ftp.getReplyCode();
          if(!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            System.exit(1);
          }
          
          fis = new FileInputStream(localFile);
          ftp.storeFile(remoteFile, fis);
          
          ftp.logout();
        } catch(IOException e) {
            // do nothing
        } finally {
          if(ftp.isConnected()) {
            try {
            if (fis != null) {
                fis.close();
            }
              ftp.disconnect();
            } catch(IOException ioe) {
              // do nothing
            }
          }
        }
    }

    static String get_web_page(String template, Vector<String> langs, Vector<Integer> sum) {
        String res = template;
        List<langStatItem> sorted = new ArrayList<>();
        for (int i = 0; i < langs.size(); i++) {
            sorted.add(new langStatItem(langs.elementAt(i), sum.elementAt(i)));
        }
        Collections.sort(sorted, new Comparator<langStatItem>() {
            @Override
            public int compare(langStatItem o1, langStatItem o2) {
              return o2.sum.compareTo(o1.sum);
            }
          });
        int N = (sorted.size() >= 10) ? 10 : sorted.size();
        
        String injection = "";
        int el_sum = elements_sum(sum);
        for (int i = 0; i < N; i++) {
            String per = String.format("%2.1f%%", (new Float(sorted.get(i).sum)*100/el_sum));
            injection += "  <tr>" + "\n";
                injection += String.format(
                        "      <td align = \"center\">%d</td>\n" +
                        "      <td>%s</td>\n" + 
                        "      <td align = \"center\">%s</td>\n",
                        (i+1), sorted.get(i).lang, per);
            injection += "  </tr>" + "\n";
        }
        
        res = res.replace("#top10_russian_table#", injection);
        return res;
    }
    
    static String get_today_str() {
        Date today = new Date();
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(today) + ".txt";
        } catch (Exception e) {
            return "last.txt";
        }
    }
    
}

class langStatItem {
  String lang;
  Integer sum;
  
  langStatItem(String lang, Integer sum) {
    this.lang = lang; this.sum = sum;
  }
  
}