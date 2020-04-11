package lab;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;

public class 详情页Demo {
    public static void main(String[] args) throws IOException {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        String url = "https://so.gushiwen.org/shiwenv_45c396367f59.aspx";
        HtmlPage page = webClient.getPage(url);
        HtmlElement body = page.getBody();
        //题目
        {
            String xpath="//div[@class='cont']/h1/text()";
            Object o = body.getByXPath(xpath).get(0);
            DomText domText = (DomText) o;
            System.out.println(domText.asText());
        }
//朝代
        {
            String xpath="//div[@class='cont']/p[@class='source']/a[1]/text()";
            Object o = body.getByXPath(xpath).get(0);
            DomText domText = (DomText) o;
            System.out.println(domText.asText());
        }
//        作者
        {
            String xpath="//div[@class='cont']/p[@class='source']/a[2]/text()";
            Object o = body.getByXPath(xpath).get(0);
            DomText domText = (DomText) o;
            System.out.println(domText.asText());
        }
//        正文（得到的div元素，和上面情况不一样）
        {
            String xpath="//div[@class='cont']/div[@class='contson']";
            Object o = body.getByXPath(xpath).get(0);
            HtmlElement htmlElement = (HtmlElement) o;
            System.out.println(htmlElement.getTextContent().trim());
        }
    }
}
