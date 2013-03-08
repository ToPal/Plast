/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plast;

import com.sun.org.apache.xpath.internal.operations.Equals;
import java.net.URLEncoder;
import java.util.Vector;

/**
 *
 * @author PASHA
 */
public class HeadHunter {
    
    static final String addr_template = "http://hh.ru/applicant/searchvacancyresult.xml?areaId=113&notWithoutSalary=&text=%s&professionalAreaId=1&desireableCompensation=&compensationCurrencyCode=RUR&specializationId=221";
    
    static Vector<Integer> HeadHunter(Vector<String> langs) {
        Vector<Integer> res = new Vector<>();
        
        for (String lang: langs) {
            try {
                String file_text = "";
                String file_name = "langs\\" + lang + ".txt";
                int vac_count = 0;
                String html = "";
                
                //Грязные хаки для конкретной поисковой системы
                if (lang.equalsIgnoreCase("C")) {
                    lang = "(\"C\" OR !\"Си\") NOT !\"1C\" NOT !\"C++\" NOT \"Objective-C\" NOT !\"C#\"";
                } else if (lang.equalsIgnoreCase("Java")) {
                    lang = "Java NOT !JavaScript";
                } else if (lang.equalsIgnoreCase("Delphi")) {
                    lang = "Delphi OR !\"Object pascal\"";
                } else if (lang.equalsIgnoreCase("1C") ||
                            lang.equalsIgnoreCase("1С")) {
                    lang = "\"1С\" OR !\"1C\"";
                    System.out.println(lang);
                }
                
                lang = URLEncoder.encode(lang); //декодируем пробелы и русские символы
                lang = "!" + lang; //запрещаем искать синонимы
                String url = String.format(addr_template, lang);
                file_text = url;
                
                html = Utils.get_html(url);
                file_text += "\n\n\n\n" + html;
                
                if (html.indexOf("resumesearch__result") <= 0) {
                    vac_count = -1;
                } else {
                    
                    html = html.substring(html.indexOf("resumesearch__result"));
                    
                    if ((html.indexOf("</strong>") <= 0) ||
                            html.indexOf("Не найдено ни одной вакансии — измените параметры поиска, ключевые слова или период") > 0) {
                        vac_count = 0;
                    } else {
                        html = html.substring(1, html.indexOf("</strong>"));
                        vac_count = Utils.get_int(html);
                    }
                    
                }
                
                file_text = vac_count + "\n\n\n\n" + file_text;
                Utils.save_file(file_name, file_text);
                
                res.add(vac_count);
                
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        return res;
    }
    
}
