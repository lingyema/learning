package lab;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 测试htmlunit
 */
public class HttpUnitDemo {
    public static void main(String[] args) throws IOException {

//        获取无界面浏览器（指定浏览器版本为chrome）
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
//        关闭浏览器的js执行引擎，不再执行网页中的js脚本
        webClient.getOptions().setJavaScriptEnabled(false);
        //        关闭浏览器的css执行引擎，不再执行网页中的css布局
        webClient.getOptions().setCssEnabled(false);
        HtmlPage page = webClient.getPage("https://so.gushiwen.org/gushi/tangshi.aspx");
        System.out.println(page);
        File file = new File("唐诗三百首//列表页.html");
        file.delete();
        page.save(file);


//        如何从html中提取我们需要的信息
        HtmlElement body = page.getBody();
        List<HtmlElement> elements = body.getElementsByAttribute("div", "class", "typecont");
//        for (HtmlElement element:elements){
//            System.out.println(element);
//        }
        HtmlElement element = elements.get(0);
        List<HtmlElement> aElements = element.getElementsByAttribute("a", "target", "_blank");
        for (HtmlElement e : aElements){
            System.out.println(e);
        }
        System.out.println(aElements.size());
        System.out.println(aElements.get(0).getAttribute("href"));
    }
}
