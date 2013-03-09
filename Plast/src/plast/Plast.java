/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author PASHA
 */
public class Plast {
    final static String RESULT_DIR = "..\\res";
    final static String TEMP_DIR = "..\\templates";
    final static String WEB_PAGE_TEMPLATE = TEMP_DIR + "\\" + "template.html";
    final static String WEB_PAGE_TEMP     = TEMP_DIR + "\\" + "index.html";
    final static String LANGS_FILE        = TEMP_DIR + "\\" + "langs.txt";
    
    final static String REMOTE_WEB_PAGE   = "index.htm";
    final static String REMOTE_RESULT_DIR = "history";
    
    
    
    static Vector<String> get_lang_names() {
        return Utils.get_lines_from_file(LANGS_FILE);
    }
    
    static void save_web_page(String wp) {
        Utils.save_file(WEB_PAGE_TEMP, wp);
    }
    
    static String get_template() {
        return Utils.get_file_text(WEB_PAGE_TEMPLATE);
    }
    
    static void save_result(Vector<Integer> sum) {
        String fileName = Utils.get_today_str();
        String text = Utils.ints_to_str(sum);
        Utils.save_file(RESULT_DIR + "\\" + fileName, text);
    }
    
    static void save_ftp() {
        Utils.save_to_ftp(REMOTE_WEB_PAGE, WEB_PAGE_TEMP);
        String fileName = Utils.get_today_str();
        Utils.save_to_ftp(REMOTE_RESULT_DIR + "/" + fileName, RESULT_DIR + "\\" + fileName);
    }
    

    public static void main(String[] args) {
        Vector<String> langs = get_lang_names();
        
        Vector<Integer> hh = HeadHunter.HeadHunter(langs);
        
        Vector<Integer> sum = new Vector<>(hh);
        Vector<Float> res = Utils.toPercent(sum);
        
        for (int i = 0; i < langs.size(); i++) {
            System.out.println(
                    langs.get(i) + ": " + 
                    ((i < sum.size()) ? sum.get(i) : "0") + 
                    String.format(": %2.1f%%", ((i < res.size()) ? 100 * res.get(i) : "0"))
                    );
        }
        
        save_result(sum);
        String template = get_template();
        String wp = Utils.get_web_page(template, langs, sum);
        save_web_page(wp);
        save_ftp();
    }
}
