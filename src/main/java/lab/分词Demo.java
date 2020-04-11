package lab;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import java.util.List;

public class 分词Demo {
    public static void main(String[] args) {
        String str = "中华人民共和国成立了！中国人民从此站起来了！";
        List<Term> terms = NlpAnalysis.parse(str).getTerms();
        for (Term term : terms){
//            getNatureStr():获取词性   getRealName()：获取得到的词
            System.out.println(term.getNatureStr()+":"+term.getRealName());
        }
    }
}
